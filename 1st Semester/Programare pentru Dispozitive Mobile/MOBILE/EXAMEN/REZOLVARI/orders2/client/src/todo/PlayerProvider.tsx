import React, { useCallback, useContext, useEffect, useReducer } from 'react';
import PropTypes from 'prop-types';
import { getLogger } from '../core';
import { PlayerProps } from './PlayerProps';
import { getPlayers, newWebSocket} from './PlayerApi';
import { AuthContext } from '../auth';

const log = getLogger('PlayerProvider');

type SetQueryFn = (query: string) => Promise<any>

export interface PlayersState {
  players?: PlayerProps[],
  fetching: boolean,
  fetchingError?: Error | null,
  query: string,
  setQuery?: SetQueryFn,
  offer?: PlayerProps
}

interface ActionProps {
  type: string,
  payload?: any,
}

const initialState: PlayersState = {
  fetching: false,
  query: '!',
};

const FETCH_PLAYERS_STARTED = 'FETCH_PLAYERS_STARTED';
const FETCH_PLAYERS_SUCCEEDED = 'FETCH_PLAYERS_SUCCEEDED';
const FETCH_PLAYERS_FAILED = 'FETCH_PLAYERS_FAILED';
const QUERY_CHANGED = 'QUERY_CHANGED'
const OFFER_CHANGED = 'OFFER_CHANGED'

const reducer: (state: PlayersState, action: ActionProps) => PlayersState =
  (state, { type, payload }) => {
    switch (type) {
      case QUERY_CHANGED:
        return { ...state, query: payload.query }
      case OFFER_CHANGED:
        return { ...state, offer: payload.offer }
      case FETCH_PLAYERS_STARTED:
        return { ...state, fetching: true, fetchingError: null };
      case FETCH_PLAYERS_SUCCEEDED:
        return { ...state, players: payload.players, fetching: false };
      case FETCH_PLAYERS_FAILED:
        return { ...state, fetchingError: payload.error, fetching: false };
      default:
        return state;
    }
  };

export const PlayerContext = React.createContext<PlayersState>(initialState);

interface PlayerProviderProps {
  children: PropTypes.ReactNodeLike,
}

export const PlayerProvider: React.FC<PlayerProviderProps> = ({ children }) => {
  const { token } = useContext(AuthContext);
  const [state, dispatch] = useReducer(reducer, initialState);
  const { players, fetching, fetchingError, query, offer } = state;
  useEffect(getPlayersEffect, [token, query]);
  useEffect(wsEffect, [token]);
  const setQueryCallback = useCallback<SetQueryFn>(setQuery, []);
  const value = { players, fetching, fetchingError, setQuery: setQueryCallback, query, offer };
  log('returns');
  return (
    <PlayerContext.Provider value={value}>
      {children}
    </PlayerContext.Provider>
  );

  async function setQuery(query: string) {
    log(`new query ${query}`);
    dispatch({ type: QUERY_CHANGED, payload: { query: query } });
  }

  function getPlayersEffect() {
    let canceled = false;
    getFilteredMenuItems();
    return () => {
      canceled = true;
    }

    async function getFilteredMenuItems() {
      console.log('token = ' + token)
      console.log('function to get filtered items is called')
      if (!token?.trim()) {
        return;
      }
      try {
        log('Fetching started');
        dispatch({ type: FETCH_PLAYERS_STARTED });

        const q = query? query : '!';
        const menuItems = (await getPlayers(token, q)).slice(0, 5);

        log("query:", q, "items:", menuItems);

        log('fetchPlayers succeeded');
        if (!canceled) {
          dispatch({ type: FETCH_PLAYERS_SUCCEEDED, payload: { players: menuItems } });
        }
      } catch (error) {
        log('fetchPlayers failed');
        dispatch({ type: FETCH_PLAYERS_FAILED, payload: { error } });
      }
    }
  }

  function wsEffect() {
    let canceled = false;
    log('wsEffect - connecting');
    let closeWebSocket: () => void;
    if (token?.trim()) {
      closeWebSocket = newWebSocket(token, message => {
        if (canceled) {
          return;
        }
        log(`ws message, item `, message);
        dispatch({ type: OFFER_CHANGED, payload: { offer: message } });
      });
    }
    return () => {
      log('wsEffect - disconnecting');
      canceled = true;
      closeWebSocket?.();
    }
  }
};

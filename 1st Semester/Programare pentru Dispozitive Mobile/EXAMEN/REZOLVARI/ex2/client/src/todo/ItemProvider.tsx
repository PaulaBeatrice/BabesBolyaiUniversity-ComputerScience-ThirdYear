import React, { useCallback, useContext, useEffect, useReducer } from 'react';
import PropTypes from 'prop-types';
import { getLogger } from '../core';
import { ItemProps } from './ItemProps';
import { newWebSocket, getItem } from './itemApi';
import { AuthContext } from '../auth';

const log = getLogger('ItemProvider');

type SaveItemFn = (item: ItemProps) => Promise<any>;
type SetCorrectFn = (val: number) => void;

export interface ItemsState {
  items?: ItemProps[],
  questionIds?: number[],
  fetching: boolean,
  fetchingError?: Error | null,
  saving: boolean,
  savingError?: Error | null,
  saveItem?: SaveItemFn,
  correct: number,
  setCorrect?: SetCorrectFn
}

interface ActionProps {
  type: string,
  payload?: any,
}

const initialState: ItemsState = {
  fetching: false,
  saving: false,
  correct: 0
};

const FETCH_ITEMS_STARTED = 'FETCH_ITEMS_STARTED';
const FETCH_ITEMS_SUCCEEDED = 'FETCH_ITEMS_SUCCEEDED';
const FETCH_ITEMS_FAILED = 'FETCH_ITEMS_FAILED';
const SAVE_ITEM_STARTED = 'SAVE_ITEM_STARTED';
const SAVE_ITEM_SUCCEEDED = 'SAVE_ITEM_SUCCEEDED';
const SAVE_ITEM_FAILED = 'SAVE_ITEM_FAILED';
const  CORRECT_CHANGED = 'CORRECT_CHANGED';

const reducer: (state: ItemsState, action: ActionProps) => ItemsState =
  (state, { type, payload }) => {
    switch (type) {
      case CORRECT_CHANGED:
        return { ...state, correct: payload.value}
      case FETCH_ITEMS_STARTED:
        return { ...state, fetching: true, fetchingError: null };
      case FETCH_ITEMS_SUCCEEDED:
        return { ...state, items: payload.items, fetching: false };
      case FETCH_ITEMS_FAILED:
        return { ...state, fetchingError: payload.error, fetching: false };
      case SAVE_ITEM_STARTED:
        return { ...state, savingError: null, saving: true };
      case SAVE_ITEM_SUCCEEDED:
        const items = [...(state.items || [])];
        const item = payload;
        const index = items.findIndex(it => it.id === item.id);
        if (index === -1) {
          //items.splice(0, 0, item);
          items.push(item);
        } else {
          items[index] = item;
        }
        return { ...state, items, saving: false };
      case SAVE_ITEM_FAILED:
        return { ...state, savingError: payload.error, saving: false };
      default:
        return state;
    }
  };

export const ItemContext = React.createContext<ItemsState>(initialState);

interface ItemProviderProps {
  children: PropTypes.ReactNodeLike,
}

export const ItemProvider: React.FC<ItemProviderProps> = ({ children }) => {
  const { items: fetchedItems, token, questionIds, fetched } = useContext(AuthContext);
  const [state, dispatch] = useReducer(reducer, initialState);


  useEffect(() => {
    dispatch({ type: FETCH_ITEMS_SUCCEEDED, payload: { items: fetchedItems } });
  }, [fetchedItems]);

  const { items, fetching, fetchingError, saving, savingError, correct } = state;
  const setCorrect = useCallback<SetCorrectFn>(setNewCorrect, []);
  //useEffect(getItemByItemEffect, [token]);
  useEffect(wsEffect, [token, fetched]);

  const value = { items, questionIds, fetching, fetchingError, saving, savingError, correct, setCorrect };
  log('returns');
  return (
    <ItemContext.Provider value={value}>
      {children}
    </ItemContext.Provider>
  );

  function getItemByItemEffect() {
    let canceled = false;
    fetchItemByItem();
    return () => {
      canceled = true;
    }

    async function fetchItemByItem() {
      if (!token?.trim()) {
        return;
      }

      const fetchedItems: ItemProps[] = [];

      try {
        log('fetchItemByItem started');
        dispatch({ type: FETCH_ITEMS_STARTED });

        for (let i = items ? items.length : 0; i < questionIds.length; i++) {
          fetchedItems.push(await getItem(token, questionIds[i]));
        }

        log('fetchItemByItem succeeded');
        if (!canceled) {
          dispatch({ type: FETCH_ITEMS_SUCCEEDED, payload: { items: fetchedItems } });
        }
      } catch (error) {
        log('fetchItemByItem failed');
        dispatch({ type: FETCH_ITEMS_FAILED, payload: { error } });
        dispatch({ type: FETCH_ITEMS_SUCCEEDED, payload: { items: fetchedItems } });    //is this even a good idea?!
      }
    }
  }

  function setNewCorrect(val: number) {
    dispatch({ type: CORRECT_CHANGED, payload: { value: val } });
  }

  function wsEffect() {
    let canceled = false;
    let closeWebSocket: () => void;

    if (!fetched) {
      return () => {
        log('wsEffect - items not fetched');
        canceled = true;
        closeWebSocket?.();
      }
    }

    log('wsEffect - connecting');
    if (token?.trim()) {
      closeWebSocket = newWebSocket(token, message => {
        if (canceled) {
          return;
        }

        log(`ws message, item ${JSON.stringify(message)}`);
        dispatch({ type: SAVE_ITEM_SUCCEEDED, payload: message });
      });
    }
    return () => {
      log('wsEffect - disconnecting');
      canceled = true;
      closeWebSocket?.();
    }
  }
};

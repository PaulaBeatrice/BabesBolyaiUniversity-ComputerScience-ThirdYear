import React, { useCallback, useEffect, useReducer } from 'react';
import PropTypes from 'prop-types';
import { getLogger } from '../core';
import { ItemProps } from './ItemProps';
import { getItems, newWebSocket, updateItem } from './itemApi';

const log = getLogger('ItemProvider');

type SaveItemFn = (item: ItemProps) => Promise<any>;

export interface ItemsState {
  items?: ItemProps[],
  fetching: boolean,
  fetchingError?: Error | null,
  saving: boolean,
  savingError?: Error | null,
  saveItem?: SaveItemFn,
}

interface ActionProps {
  type: string,
  payload?: any,
}

const initialState: ItemsState = {
  fetching: false,
  saving: false,
};

const FETCH_ITEMS_STARTED = 'FETCH_ITEMS_STARTED';
const FETCH_ITEMS_SUCCEEDED = 'FETCH_ITEMS_SUCCEEDED';
const FETCH_ITEMS_FAILED = 'FETCH_ITEMS_FAILED';
const SAVE_ITEM_STARTED = 'SAVE_ITEM_STARTED';
const SAVE_ITEM_SUCCEEDED = 'SAVE_ITEM_SUCCEEDED';
const SAVE_ITEM_FAILED = 'SAVE_ITEM_FAILED';

const reducer: (state: ItemsState, action: ActionProps) => ItemsState =
  (state, { type, payload }) => {
    switch (type) {
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
        //const item = payload.item;
        if (payload.id || payload.id === 0) {
          const item = payload;
          const index = items.findIndex(it => it.id === item.id);
          if (index === -1) {
            items.splice(0, 0, item);
          } else {
            items[index] = item;
          }
        }
        localStorage.setItem("items", JSON.stringify(items));
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
  const [state, dispatch] = useReducer(reducer, initialState);
  let { items, fetching, fetchingError, saving, savingError } = state;
  useEffect(getItemsEffect, []);
  useEffect(wsEffect, []);
  const saveItem = useCallback<SaveItemFn>(saveItemCallback, []);
  const value = { items, fetching, fetchingError, saving, savingError, saveItem };
  log('returns');
  return (
    <ItemContext.Provider value={value}>
      {children}
    </ItemContext.Provider>
  );

  function getItemsEffect() {
    let canceled = false;
    fetchItems();

    // look to resend items to server
    setInterval(resend, 10000);

    return () => {
      canceled = true;
    }

    async function resend() {
      const stringRetry = localStorage.getItem("retry");
      const retry: ItemProps[] = stringRetry? JSON.parse(stringRetry) : [];
      for (let i = retry.length - 1; i >= 0; i--) {
        const item = retry[i];
        try {
            log(`resendItem started: ${JSON.stringify(item)} id: ${item.id}`);
            dispatch({type: SAVE_ITEM_STARTED});
            //const savedItem = await (item.id || item.id === 0 ? updateItem(item) : createItem(item));
            const savedItem = await updateItem(item);
            log('resendItem succeeded');
            dispatch({type: SAVE_ITEM_SUCCEEDED, payload: savedItem/*{ item: savedItem }*/});
            retry.splice(i, 1);
            localStorage.setItem("retry", JSON.stringify(retry));
        } catch (error) {
          log(`resendItem failed: ${error}, i = ${i}`);
          if (error.message === "Request failed with status code 400") {
            retry.splice(i, 1);
            localStorage.setItem("retry", JSON.stringify(retry));
          }
        }
      }
    }

    async function fetchItems() {
      try {
        log('fetchItems started');
        dispatch({ type: FETCH_ITEMS_STARTED });
        const items = await getItems();
        log('fetchItems succeeded');
        if (!canceled) {
          dispatch({ type: FETCH_ITEMS_SUCCEEDED, payload: { items } });
        }
      } catch (error) {
        log('fetchItems failed');
        dispatch({ type: FETCH_ITEMS_FAILED, payload: { error } });
        const itms = localStorage.getItem("items");
        const items = itms? JSON.parse(itms) : [];
        dispatch({ type: FETCH_ITEMS_SUCCEEDED, payload: { items } });
      }
    }
  }

  async function saveItemCallback(item: ItemProps) {
    try {
      log('saveItem started');
      dispatch({ type: SAVE_ITEM_STARTED });
      //const savedItem = await (item.id || item.id === 0 ? updateItem(item) : createItem(item));
      const savedItem = await updateItem(item);
      log('saveItem succeeded');
      dispatch({ type: SAVE_ITEM_SUCCEEDED, payload: savedItem/*{ item: savedItem }*/ });
    } catch (error) {
      log('saveItem failed');
      dispatch({ type: SAVE_ITEM_FAILED, payload: { error } });
      const stringRetry = localStorage.getItem("retry");
      const retry: ItemProps[] = stringRetry? JSON.parse(stringRetry) : [];
      if (retry.findIndex(it => it.id === item.id) === -1) {
        retry.push(item);
      }
      localStorage.setItem("retry", JSON.stringify(retry));
      dispatch({ type: SAVE_ITEM_SUCCEEDED, payload: item/*{ item: savedItem }*/ });
    }
  }

  function wsEffect() {
    let canceled = false;
    log('wsEffect - connecting');
    const closeWebSocket = newWebSocket(message => {
      if (canceled) {
        return;
      }
      // const { event, payload: { item }} = message;
      log(`ws message, item ${JSON.stringify(message)}`);
      if (message) {
        dispatch({ type: SAVE_ITEM_SUCCEEDED, payload: message });
      }
    });
    return () => {
      log('wsEffect - disconnecting');
      canceled = true;
      closeWebSocket();
    }
  }
};

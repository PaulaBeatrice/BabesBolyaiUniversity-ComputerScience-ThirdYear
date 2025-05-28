import React, { useCallback, useContext, useEffect, useReducer, useState } from 'react';
import PropTypes from 'prop-types';
import { getLogger } from '../core';
import { ItemProps } from './ItemProps';
import { createItem, getItems, newWebSocket, updateItem } from './itemApi';
import { AuthContext } from '../auth';
import { Preferences } from '@capacitor/preferences';
import { useNetwork } from '../use/useNetwork';
import { IonToast } from '@ionic/react';


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
        const itemR = payload.item;
        const index = items.findIndex(it => it._id === itemR._id);
          if (index === -1) {
            items.splice(0, 0, itemR);
          } else {
            items[index] = itemR;
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
  const {token} = useContext(AuthContext);
  const [showToast, setShowToast] = useState(false);
  const [state, dispatch] = useReducer(reducer, initialState);
  const { items, fetching, fetchingError, saving, savingError } = state;
  const { networkStatus } = useNetwork();
  useEffect(getItemsEffect,[token,networkStatus]);
  useEffect(wsEffect, [token]);
  useEffect(() => {
    if (!networkStatus.connected) {
      setShowToast(true);
    } else {
      setShowToast(false);
    }
  }, [networkStatus]);
  const saveItem = useCallback<SaveItemFn>(saveItemCallback, [token,networkStatus]);
  const value = { items, fetching, fetchingError, saving, savingError, saveItem };
  
  log('returns');
  return (
    <ItemContext.Provider value={value}>
      {children}
      <IonToast
        isOpen={showToast}
        onDidDismiss={() => setShowToast(false)}
        message="You are offline! The data will be send as soon as you reach connection!"
        duration={3000}
      />
    </ItemContext.Provider>
  );

  function getItemsEffect() {
    let canceled = false;
    if(token){
      if (networkStatus.connected){
        loadFromLocalStorage();
        fetchItems();
      }
      fetchItems();
    }
  
    return () => {
      canceled = true;
    }

    async function fetchItems() {
      try {
        log('fetchItems started');
        dispatch({ type: FETCH_ITEMS_STARTED });
          const items = await getItems(token);
          log('fetchItems succeeded');
          if (!canceled) {
            dispatch({ type: FETCH_ITEMS_SUCCEEDED, payload: { items } });
          }
          Preferences.remove({key:'items'});
            Preferences.set({
              key:'items',
              value : JSON.stringify({items}),
            });
      } catch (error) {
        log('fetchItems failed', error);
        if (!canceled) {
          dispatch({ type: FETCH_ITEMS_FAILED, payload: { error } });
        }
      }
    }

    async function loadFromLocalStorage(){
      const allKeys = await Preferences.keys();
      const keysArray : Array<string> = Object.values(allKeys.keys);
      const filteredKeys = keysArray.filter(key => key.startsWith('item-'));
      if (filteredKeys.length > 0){
         for (const key of filteredKeys){
           const dirtyItemString = await Preferences.get({key});
           if (dirtyItemString && dirtyItemString.value){
             const dirtyItem = JSON.parse(dirtyItemString.value);
             await (dirtyItem._id ? updateItem(token,dirtyItem) : createItem(token,dirtyItem));
             log(`synced dirty item ${key} to server`);
             Preferences.remove({key: key });
             dispatch({type:SAVE_ITEM_SUCCEEDED,payload:{item: dirtyItem}});
           }
         }
      }
    }
  }



  async function saveItemCallback(item: ItemProps) {
    try {
      log('saveItem started');
      dispatch({ type: SAVE_ITEM_STARTED });
      if (networkStatus.connected){
         log('CONECTAT',networkStatus.connected);
          const savedItem = await (item._id ? updateItem(token,item) : createItem(token,item));
          log('saveItem succeeded');
          dispatch({ type: SAVE_ITEM_SUCCEEDED, payload: { item: item } });
      }
      else{
        log('Network is offline. Saving item to local storage...');
        Preferences.set({ key: `item-${item._id}`, value: JSON.stringify(item) });
      }
      
    } catch (error) {
      log('saveItem failed');
      dispatch({ type: SAVE_ITEM_FAILED, payload: { error } });
    }
  }

  function wsEffect() {
    let canceled = false;
    log('wsEffect - connecting');
    let closeWebSocket: ()=>void;
    if (token?.trim()){
      closeWebSocket = newWebSocket(token,message => {
        if (canceled) {
          return;
        }
        const { type, payload:  item } = message;
        log(`ws message, item ${type}`);
        if (type === 'created' || type === 'updated') {
          dispatch({ type: SAVE_ITEM_SUCCEEDED, payload: { item } });
        }
      });
    }
     
    return () => {
      log('wsEffect - disconnecting');
      canceled = true;
      closeWebSocket?.();
    }
  }
};

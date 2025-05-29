import React, {useCallback, useContext, useEffect, useReducer} from 'react';
import PropTypes from 'prop-types';
import { getLogger } from '../core';
import { ItemProps } from './ItemProps';
import { ProductProps } from "./ProductProps";
import {getProductsPage, newWebSocket, uploadItem} from './itemApi';

const log = getLogger('ItemProvider');

type SaveItemFn = (item: ItemProps) => Promise<any>;
type ReDownloadFn = () => any;
type UploadFn = (items?: ItemProps[]) => any;

export interface ItemsState {
  products?: ProductProps[],
  items?: ItemProps[],
  canReDownload: boolean,
  page: number,
  total: number,
  fetching: boolean,
  fetchingError?: Error | null,
  saving: boolean,
  savingError?: Error | null,
  saveItem?: SaveItemFn,
  reDownload?: ReDownloadFn,
  upload?: UploadFn,
  uploading: boolean,
  uploadNr: number,
}

interface ActionProps {
  type: string,
  payload?: any,
}

const initialState: ItemsState = {
  fetching: false,
  saving: false,
  canReDownload: false,
  page: 0,
  total: 0,
  uploading: false,
  uploadNr: 0,
};

const FETCH_ITEMS_STARTED = 'FETCH_ITEMS_STARTED';
const FETCH_ITEMS_SUCCEEDED = 'FETCH_ITEMS_SUCCEEDED';
const FETCH_ITEMS_FAILED = 'FETCH_ITEMS_FAILED';
const SAVE_ITEM_STARTED = 'SAVE_ITEM_STARTED';
const UPLOAD_ITEM_STARTED = 'UPLOAD_ITEM_STARTED';
const UPLOAD_ITEM_SUCCEEDED = 'UPLOAD_ITEM_SUCCEEDED';
const UPLOADED_ITEM = 'UPLOADED_ITEM';
const UPLOAD_ITEM_FAILED = 'UPLOAD_ITEM_FAILED';
const SAVE_ITEM_SUCCEEDED = 'SAVE_ITEM_SUCCEEDED';
const SAVE_ITEM_FAILED = 'SAVE_ITEM_FAILED';
const PAGE_CHANGED = 'PAGE_CHANGED';
const CAN_REDOWNLOAD = 'CAN_REDOWNLOAD';

const reducer: (state: ItemsState, action: ActionProps) => ItemsState =
  (state, { type, payload }) => {
    switch (type) {
      case PAGE_CHANGED:
        localStorage.setItem('page', JSON.stringify(payload.page));
        localStorage.setItem('total', JSON.stringify(payload.total));
        return { ...state, page: payload.page, total: payload.total, canReDownload: false };
      case CAN_REDOWNLOAD:
        return { ... state, canReDownload: true};
      case FETCH_ITEMS_STARTED:
        return { ...state, fetching: true, fetchingError: null };
      case FETCH_ITEMS_SUCCEEDED:
        localStorage.setItem("products", JSON.stringify(payload.items));
        return { ...state, products: payload.items, fetching: false };
      case UPLOAD_ITEM_STARTED:
        return { ...state, uploading: true, uploadNr: 0 };
      case UPLOAD_ITEM_SUCCEEDED:
        const itemss = [...(state.items || [])];
        localStorage.setItem("items", JSON.stringify(itemss));
        return { ...state,items:itemss, uploading: false};
      case UPLOAD_ITEM_FAILED:
        return { ... state, uploading: false }
      case UPLOADED_ITEM:
        const items = [...(state.items || [])];
        //const item = payload.item;
        if (payload.code || payload.code === 0) {
          const item = payload;
          const index = items.findIndex(it => it.code === item.code);
          items[index] = item;
          
        }
        localStorage.setItem("totalUpload", JSON.stringify(items.length));
        localStorage.setItem("items", JSON.stringify(items));
        localStorage.setItem('uploadNr',JSON.stringify(state.uploadNr + 1));
        return { ...state, items ,uploadNr: state.uploadNr + 1 }
      case FETCH_ITEMS_FAILED:
        return { ...state, fetchingError: payload.error, fetching: false, canReDownload: true };
      case SAVE_ITEM_STARTED:
        return { ...state, savingError: null, saving: true };
      case SAVE_ITEM_SUCCEEDED:
        const items1 = [...(state.items || [])];
        //const item = payload.item;
        if (payload.code || payload.code === 0) {
          const item = payload;
          const index = items1.findIndex(it => it.code === item.code);
          if (index === -1) {
            items1.push(item);
          } else {
            items1[index] = item;
          }
        }
        localStorage.setItem("totalUpload", JSON.stringify(items1.length));
        localStorage.setItem("items", JSON.stringify(items1));
        return { ...state, items: items1, saving: false };
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
  let { products, fetching, fetchingError, saving, savingError, page, total, canReDownload, items, uploading, uploadNr } = state;

  if (!items) {
    const v = localStorage.getItem("items");
    items = JSON.parse(v ? v: "[]");
  }
  if (!products){
    const p = localStorage.getItem("products");
    products = JSON.parse(p ? p: "[]");
  }
  
  useEffect(getItemsEffect, []);
  useEffect(wsEffect, []);
  useEffect(() => {
    if (!navigator.onLine) {
      // If offline, load data from local storage
      const storedItems = localStorage.getItem("items");
      const storedProducts = localStorage.getItem("products");
      const storedPage = localStorage.getItem("page");
      const storedTotal = localStorage.getItem("total");
      const storedUploadNr = localStorage.getItem("uploadNr");

      const items = storedItems ? JSON.parse(storedItems) : [];
      const products = storedProducts ? JSON.parse(storedProducts) : [];
      const page = storedPage ? parseInt(storedPage, 10) : 0;
      const total = storedTotal ? parseInt(storedTotal, 10) : 0;
      const uploadNr = storedUploadNr ? parseInt(storedUploadNr, 10) : 0;

      dispatch({
        type: PAGE_CHANGED,
        payload: { page, total },
      });

      dispatch({
        type: FETCH_ITEMS_SUCCEEDED,
        payload: { items: products },
      });

      dispatch({
        type: UPLOADED_ITEM,
        payload: { uploadNr },
      });

      dispatch({
        type: FETCH_ITEMS_SUCCEEDED,
        payload: { items },
      });
    }
  }, []);

  const saveItem = useCallback<SaveItemFn>(saveItemCallback, []);
  const reDownload = useCallback<ReDownloadFn>(getItemsEffect, []);
  const upload = useCallback<UploadFn>(uploadItemCallback, []);

  const value = { products, fetching, fetchingError, saving, savingError, saveItem, page, total, canReDownload, reDownload, items, uploading, uploadNr, upload };

  log('returns');
  return (
    <ItemContext.Provider value={value}>
      {children}
    </ItemContext.Provider>
  );

  function getItemsEffect() {
    let canceled = false;
    fetchItems();

    return () => {
      canceled = true;
    }

    async function fetchItems() {
      let page = 0;
      const items: ProductProps[] = [];
      let totalItems = 0;
    
      try {
        log('fetchItems started');
        dispatch({ type: FETCH_ITEMS_STARTED });
    
        do {
          log('fetching page', page, 'items', items.length);
          const fetchedItems = await getProductsPage(page);
          totalItems = fetchedItems.total;
    
          items.push(...fetchedItems.products);
          page += 1;
    
          log('fetching page', page, 'items', items.length, 'total', totalItems);
          dispatch({ type: PAGE_CHANGED, payload: { page, total: totalItems } });
    
        } while (items.length < totalItems);
    
        log('fetchItems succeeded');
        if (!canceled) {
          dispatch({ type: FETCH_ITEMS_SUCCEEDED, payload: { items } });
        }
      } catch (error) {
        log('fetchItems failed');
        dispatch({ type: FETCH_ITEMS_FAILED, payload: { error } });
      }
    }
  }

  async function saveItemCallback(item: ItemProps) {
    log('saveItem started', item);
    dispatch({ type: SAVE_ITEM_SUCCEEDED, payload: item });
    log('saveItem succeeded');
  }

  // async function uploadItemCallback(items?: ItemProps[]) {
  //   try {
  //     log('uploadItem started', items);
  //     if (items) {
  //       const nonSubmittedItems = items.filter(item => item.state !== 'submitted');
  //       dispatch({ type: UPLOAD_ITEM_STARTED });
  //       for (const item of nonSubmittedItems) {
  //         log('uploading', item);
  //         const uploadedItem = await uploadItem(item);
          
  //         if (uploadedItem){
  //           const updated = {code: uploadedItem.code, number: item.number, state:'submitted'};
  //           dispatch({ type: UPLOADED_ITEM, payload: updated});
  //         }
          
  //       }
  //       dispatch({ type: UPLOAD_ITEM_SUCCEEDED});
  //     }
  //     log('uploadItem succeeded');
  //   } catch (error) {
  //     log('uploadItem failed');
  //     dispatch({ type: UPLOAD_ITEM_FAILED});
  //   }
  // }
  async function uploadItemCallback(items?: ItemProps[]) {
    try {
      log('uploadItem started', items);
      if (items) {
        const nonSubmittedItems = items.filter(item => item.state !== 'submitted');
        dispatch({ type: UPLOAD_ITEM_STARTED });
        for (const item of nonSubmittedItems) {
          log('uploading', item);
          try {
            const uploadedItem = await uploadItem(item);
  
            if (uploadedItem) {
              const updated = { code: uploadedItem.code, number: item.number, state: 'submitted' };
              dispatch({ type: UPLOADED_ITEM, payload: updated });
            }
          } catch (error) {
            // Handle upload failure
            log('uploadItem failed for item', item);
            const failedItem = { ...item, state: 'failed' };
            dispatch({ type: UPLOADED_ITEM, payload: failedItem });
          }
        }
        dispatch({ type: UPLOAD_ITEM_SUCCEEDED });
      }
      log('uploadItem succeeded');
    } catch (error) {
      log('uploadItem failed');
      dispatch({ type: UPLOAD_ITEM_FAILED });
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
      if (message.event === "productsChanged") {
        //getItemsEffect();
        dispatch({ type: CAN_REDOWNLOAD });
      }
    });
    return () => {
      log('wsEffect - disconnecting');
      canceled = true;
      closeWebSocket();
    }
  }
};

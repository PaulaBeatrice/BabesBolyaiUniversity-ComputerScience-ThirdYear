import React, { PropsWithChildren, useEffect, useReducer } from "react";
import { getLogger } from "../core";
import { deleteItem, getItems, newWebSocket, updateItem } from "../api/itemApi";
import { ItemProps } from "./ItemProps";
import { Preferences } from "@capacitor/preferences";

const log = getLogger("ItemProvider");

type SaveItemFn = (item: any) => Promise<any>;

interface Items {
  notes: ItemProps[];
  page: number;
  more: boolean;
}

export interface ItemsState {
  items: Items;
  fetching: boolean;
  fetchingError?: Error | null;
  saving: boolean;
  savingError?: Error | null;
  usedLocal: boolean;
  saveItem?: SaveItemFn;
  fetchNextPage?: () => void;
  fetchPrevPage?: () => void;
  deleteItem?: (id: number) => void;
  page: number;
}

interface ActionProps {
  type: string;
  payload?: any;
}

const initialState: ItemsState = {
  items: { more: false, notes: [], page: 1 },
  fetching: false,
  saving: false,
  usedLocal: false,
  page: 1,
};

const FETCH_ITEMS_STARTED = "FETCH_ITEMS_STARTED";
const FETCH_ITEMS_SUCCEEDED = "FETCH_ITEMS_SUCCEEDED";
const FETCH_ITEMS_FAILED = "FETCH_ITEMS_FAILED";
const SAVE_ITEM_STARTED = "SAVE_ITEM_STARTED";
const SAVE_ITEM_SUCCEEDED = "SAVE_ITEM_SUCCEEDED";
const SAVE_ITEM_FAILED = "SAVE_ITEM_FAILED";
const EXECUTED_LOCAL_OPS = "EXECUTED_LOCAL_OPS";
const SAVE_ITEM_SUCCEDED_OFFLINE = "SAVE_ITEM_SUCCEDED_OFFLINE";
const FETCH_NEXT_PAGE = "FETCH_NEXT_PAGE";
const FETCH_PREV_PAGE = "FETCH_PREV_PAGE";
const DELETE_SUCCEEDED = "DELETE_SUCCEEDED";

const reducer: (state: ItemsState, action: ActionProps) => ItemsState = (
  state,
  { type, payload }
) => {
  switch (type) {
    case FETCH_ITEMS_STARTED:
      return { ...state, fetching: true, fetchingError: null };
    case FETCH_ITEMS_SUCCEEDED:
      return { ...state, items: payload, fetching: false };
    case FETCH_ITEMS_FAILED:
      return { ...state, fetchingError: payload.error, fetching: false };
    case SAVE_ITEM_STARTED:
      return { ...state, savingError: null, saving: true };
    case EXECUTED_LOCAL_OPS:
      return { ...state, usedLocal: false };
    case SAVE_ITEM_SUCCEDED_OFFLINE:
      return { ...state, usedLocal: true };
    case SAVE_ITEM_SUCCEEDED:
      const items = state.items;
      const item = payload;
      const index = items.notes.findIndex((it) => it.id === item.id);
      if (index === -1) {
        items.notes.push(item);
      } else {
        items.notes[index] = item;
      }
      return { ...state, items, saving: false };
    case SAVE_ITEM_FAILED:
      return { ...state, savingError: payload };
    case FETCH_NEXT_PAGE:
      return { ...state, page: state.page + 1 };
    case FETCH_PREV_PAGE:
      return { ...state, page: state.page - 1 };
    case DELETE_SUCCEEDED:
      const itemsBeforeDelete = state.items;
      const notesBeforeDelete = itemsBeforeDelete.notes;
      const notesAfterDelete = notesBeforeDelete.filter(
        (nota) => nota.id != payload
      );
      return {
        ...state,
        items: { ...itemsBeforeDelete, notes: notesAfterDelete },
      };
    default:
      return state;
  }
};

export const ItemContext = React.createContext<ItemsState>(initialState);

export const ItemProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const {
    items,
    fetching,
    fetchingError,
    saving,
    savingError,
    usedLocal,
    page,
  } = state;

  useEffect(getItemsEffect, [page]);
  // useEffect(wsEffect, []);

  const value = {
    items,
    fetching,
    fetchingError,
    saving,
    savingError,
    usedLocal,
    saveItem,
    page,
    fetchNextPage,
    fetchPrevPage,
    deleteItem: deleteItemFn,
  };
  log("returns");
  return <ItemContext.Provider value={value}>{children}</ItemContext.Provider>;

  function fetchNextPage() {
    dispatch({ type: FETCH_NEXT_PAGE });
  }

  function fetchPrevPage() {
    dispatch({ type: FETCH_PREV_PAGE });
  }

  async function deleteItemFn(id: number) {
    try {
      log("deleteItem started");
      dispatch({ type: SAVE_ITEM_STARTED });

      await deleteItem(id);

      dispatch({
        type: DELETE_SUCCEEDED,
        payload: id,
      });
      log("deleteItem succeeded");
    } catch (error: any) {
      const idString = id.toString();
      await Preferences.set({
        key: "deleted_item" + idString,
        value: idString,
      });
      dispatch({ type: SAVE_ITEM_FAILED, payload: error });
    } finally {
      dispatch({ type: EXECUTED_LOCAL_OPS });
    }
  }

  async function saveItem(item: ItemProps) {
    try {
      log("saveItem started");
      dispatch({ type: SAVE_ITEM_STARTED });

      const savedItem = await updateItem(item);

      log("Saving on local storage...");
      const itemJson = JSON.stringify({ item: savedItem });
      if (savedItem.id) {
        await Preferences.set({
          key: savedItem.id?.toString(),
          value: itemJson,
        });
      }

      log("saveItem succeeded");
      dispatch({
        type: SAVE_ITEM_SUCCEEDED,
        payload: savedItem,
      });
    } catch (error: any) {
    } finally {
      dispatch({ type: EXECUTED_LOCAL_OPS });
    }
  }

  function getItemsEffect() {
    let canceled = false;

    fetchItems();
    return () => {
      canceled = true;
    };

    async function fetchItems() {
      try {
        log("fetchItems started");
        dispatch({ type: FETCH_ITEMS_STARTED });
        const items = await getItems(page);

        log("fetchItems succeeded");
        if (!canceled) {
          dispatch({ type: FETCH_ITEMS_SUCCEEDED, payload: items });
        }
      } catch (error) {
        log("fetchItems failed");
        if (!canceled) {
          dispatch({ type: FETCH_ITEMS_FAILED, payload: { error } });
        }
      }
    }
  }

  function wsEffect() {
    let canceled = false;
    log("wsEffect - connecting");
    let closeWebSocket: () => void;
    closeWebSocket = newWebSocket((message) => {
      if (!canceled) {
        dispatch({ type: SAVE_ITEM_SUCCEEDED, payload: message });
      }
    });
    return () => {
      log("wsEffect - disconnecting");
      canceled = true;
      closeWebSocket?.();
    };
  }
};

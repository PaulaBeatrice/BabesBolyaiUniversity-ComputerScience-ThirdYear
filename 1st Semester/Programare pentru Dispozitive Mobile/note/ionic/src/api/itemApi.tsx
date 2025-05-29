import axios from "axios";
import { baseUrl, getLogger } from "../core";
import { ItemProps } from "../entity/ItemProps";

const log = getLogger("itemApi");

const itemUrl = `http://${baseUrl}/note`;

interface ResponseProps<T> {
  data: T;
}

function withLogs<T>(
  promise: Promise<ResponseProps<T>>,
  fnName: string
): Promise<T> {
  log(`${fnName} - started`);
  return promise
    .then((res) => {
      log(`${fnName} - succeeded`);
      return Promise.resolve(res.data);
    })
    .catch((err) => {
      log(`${fnName} - failed`);
      return Promise.reject(err);
    });
}

const config = {
  headers: {
    "Content-Type": "application/json",
  },
};

export const getItems: (pageN: number) => Promise<ItemProps[]> = (pageN) => {
  return withLogs(axios.get(`${itemUrl}?page=${pageN}`), "getItems");
};

export const updateItem: (item: ItemProps) => Promise<ItemProps> = (item) => {
  return withLogs(
    axios.put(`${itemUrl}/${item.id}`, item, config),
    "updateItem"
  );
};

export const deleteItem = (id: number) => {
  return withLogs(axios.delete(itemUrl + "/" + id, config), "deleteItem");
};

interface MessageData {
  event: "inserted" | "deleted";
  note: ItemProps;
}

export const newWebSocket = (onMessage: (data: MessageData) => void) => {
  const ws = new WebSocket(`ws://${baseUrl}`);
  ws.onopen = () => {
    log("web socket onopen");
  };
  ws.onclose = () => {
    log("web socket onclose");
  };
  ws.onerror = (error) => {
    log("web socket onerror", error);
  };
  ws.onmessage = (messageEvent) => {
    log("web socket onmessage");
    onMessage(JSON.parse(messageEvent.data));
  };
  return () => {
    ws.close();
  };
};

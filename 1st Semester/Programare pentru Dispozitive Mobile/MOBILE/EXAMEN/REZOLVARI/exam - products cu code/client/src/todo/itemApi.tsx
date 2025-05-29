import axios from 'axios';
import { getLogger } from '../core';
import { ItemProps } from './ItemProps';
import {ProductProps} from "./ProductProps";

const log = getLogger('itemApi');

const baseUrl = 'localhost:3000';
const productUrl = `http://${baseUrl}/product`;
const itemUrl = `http://${baseUrl}/item`;

interface ResponseProps<T> {
  data: T;
}

function withLogs<T>(promise: Promise<ResponseProps<T>>, fnName: string): Promise<T> {
  log(`${fnName} - started`);
  return promise
    .then(res => {
      log(`${fnName} - succeeded`);
      return Promise.resolve(res.data);
    })
    .catch(err => {
      log(`${fnName} - failed`);
      return Promise.reject(err);
    });
}

const config = {
  headers: {
    'Content-Type': 'application/json'
  }
};

export interface ProductPage {
  total: number;
  page: number;
  products: ProductProps[];
}

export const getProductsPage: (page: number) => Promise<ProductPage> = (page) => {
  return withLogs(axios.get(`${productUrl}?page=${page}`, config), `getProductsPage ${page}`);
}

export const uploadItem: (item: ItemProps) => Promise<ItemProps[]> = item => {
  return withLogs(axios.post(`${itemUrl}`, item, config), 'updateItem');
}

interface MessageData {
  event: string;
}

export const newWebSocket = (onMessage: (data: MessageData) => void) => {
  const ws = new WebSocket(`ws://${baseUrl}`)
  ws.onopen = () => {
    log('web socket onopen');
  };
  ws.onclose = () => {
    log('web socket onclose');
  };
  ws.onerror = error => {
    log('web socket onerror', error);
  };
  ws.onmessage = messageEvent => {
    log('web socket onmessage');
    onMessage(JSON.parse(messageEvent.data));
  };
  return () => {
    ws.close();
  }
}

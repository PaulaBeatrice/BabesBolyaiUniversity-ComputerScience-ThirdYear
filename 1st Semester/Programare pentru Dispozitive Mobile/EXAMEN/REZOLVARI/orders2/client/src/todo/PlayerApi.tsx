import axios from 'axios';
import { authConfig, baseUrl, getLogger, withLogs } from '../core';
import { PlayerProps } from './PlayerProps';

const playerUrl = `http://${baseUrl}/MenuItem`;

export const getPlayers: (token: string, query: string) => Promise<PlayerProps[]> = ( token, query)  => {
  console.log(query);
  return withLogs(axios.get(`${playerUrl}?q=${query}`, authConfig(token)), 'getPlayers');
}

interface MessageData {
  type: string;
  payload: PlayerProps;
}

const log = getLogger('ws');

export const newWebSocket = (token:string, onMessage: (data: MessageData) => void) => {
  const ws = new WebSocket(`ws://${baseUrl}`)
  ws.onopen = () => {
    log('web socket onopen');
    ws.send(JSON.stringify({ type: 'authorization', payload: { token } }));
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

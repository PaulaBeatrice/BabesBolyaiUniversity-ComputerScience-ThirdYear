import axios from 'axios';
import { baseUrl, config, withLogs } from '../core';

const authUrl = `http://${baseUrl}/auth`;

export interface AuthProps {
  token: string;
}

export const login: (table?: string) => Promise<AuthProps> = (table) => {
  return withLogs(axios.post(authUrl, { table }, config), 'login');
}

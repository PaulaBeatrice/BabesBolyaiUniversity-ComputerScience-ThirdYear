import React, { useCallback, useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { getLogger } from '../core';
import { login as loginApi } from './authApi';
import {ItemProps} from "../todo/ItemProps";
import {getItem} from "../todo/itemApi";

const log = getLogger('AuthProvider');

type LoginFn = (id?: string) => void;
type FetchFn = () => void;

export interface AuthState {
  authenticationError: Error | null;
  isAuthenticated: boolean;
  isAuthenticating: boolean;
  login?: LoginFn;
  fetch?: FetchFn;
  pendingAuthentication?: boolean;
  id?: string;
  token: string;
  questionIds: number[];
  fetched: boolean;
  error?: string;
  items: ItemProps[];
}

const initialState: AuthState = {
  isAuthenticated: false,
  isAuthenticating: false,
  authenticationError: null,
  pendingAuthentication: false,
  token: '',
  questionIds: [],
  fetched: false,
  items: [],
};

export const AuthContext = React.createContext<AuthState>(initialState);

interface AuthProviderProps {
  children: PropTypes.ReactNodeLike,
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [state, setState] = useState<AuthState>(initialState);
  const { isAuthenticated, isAuthenticating, authenticationError, pendingAuthentication, token, questionIds, fetched, error, items } = state;
  const login = useCallback<LoginFn>(loginCallback, []);
  const fetch = useCallback<FetchFn>(fetchingEffect, [token, questionIds]);
  useEffect(authenticationEffect, [pendingAuthentication]);
  useEffect(fetchingEffect, [isAuthenticated]);
  const value = { isAuthenticated, login, fetch, isAuthenticating, authenticationError, token, questionIds, fetched, error, items };
  log('render');
  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );

  function loginCallback(id?: string): void {
    log('login');
    setState({
      ...state,
      pendingAuthentication: true,
      id
    });
  }

  function authenticationEffect() {
    let canceled = false;
    authenticate();
    return () => {
      canceled = true;
    }

    async function authenticate() {
      if (!pendingAuthentication) {
        log('authenticate, !pendingAuthentication, return');
        return;
      }
      try {
        log('authenticate...');
        setState({
          ...state,
          isAuthenticating: true,
        });
        const { id } = state;
        const { token, questionIds } = await loginApi(id);
        if (canceled) {
          return;
        }
        log('authenticate succeeded');
        setState({
          ...state,
          token,
          questionIds,
          pendingAuthentication: false,
          isAuthenticated: true,
          isAuthenticating: false,
        });
      } catch (error) {
        if (canceled) {
          return;
        }
        log('authenticate failed');
        setState({
          ...state,
          authenticationError: error,
          pendingAuthentication: false,
          isAuthenticating: false,
        });
      }
    }
  }

  function fetchingEffect() {
    log(`fetching effect start-------------------------------------------${token} ${questionIds.length}`)
    let canceled = false;
    fetchItemByItem();
    return () => {
      canceled = true;
    }

    async function fetchItemByItem() {
      if (!token?.trim()) {
        log('empty token');
        return;
      }

      try {
        log('fetchItemByItem started');

        for (const id of questionIds) {
          if (items.findIndex(itm => itm.id === id) === -1) {
            items.push(await getItem(token, id));
            setState({
              ...state,
              items,
            });
          }
        }

        setState({
          ...state,
          items,
          fetched: true
        });

        log('fetchItemByItem succeeded');
      } catch (error) {
        log('fetchItemByItem failed');
        setState({
          ...state,
          items,
          fetched: false,
          error: error
        });
      }
    }
  }
};

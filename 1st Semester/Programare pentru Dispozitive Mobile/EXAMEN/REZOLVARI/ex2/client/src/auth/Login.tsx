import React, {useContext, useEffect, useState} from 'react';
import { Redirect } from 'react-router-dom';
import { RouteComponentProps } from 'react-router';
import {
  IonButton,
  IonContent,
  IonHeader,
  IonInput,
  IonLabel,
  IonLoading,
  IonPage, IonRow,
  IonTitle,
  IonToolbar
} from '@ionic/react';
import { AuthContext } from './AuthProvider';
import { getLogger } from '../core';
import {ItemContext} from "../todo/ItemProvider";

const log = getLogger('Login');

interface LoginState {
  id?: string;
}

export const Login: React.FC<RouteComponentProps> = ({ history }) => {
  const { isAuthenticated, isAuthenticating, login, fetch, authenticationError, questionIds, fetched, error, items } = useContext(AuthContext);
  const [state, setState] = useState<LoginState>({});
  const { id } = state;
  const handleLogin = () => {
    log('handleLogin...');
    login?.(id);
  };
  const handleRetry = () => {
    log('handleRetry...');
    fetch?.();
  };
  log('render');

  log ("authenticated", isAuthenticated, "items:", items?.length, "questions:", questionIds.length);
  if (isAuthenticated && fetched && !error) {
    return <Redirect to={{ pathname: '/' }} />
  }
  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Login</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent>
        <IonLoading isOpen={isAuthenticating} message="Authenticating..."/>
        {authenticationError && (
          <div>{authenticationError.message || 'Failed to authenticate'}</div>
        )}
        {error && (
            <div>
              <IonLabel>{`Download stopped at ${items.length} / ${questionIds.length}`}</IonLabel>
              <IonButton onClick={handleRetry}>Retry</IonButton>
            </div>
        )}
        {!error && !fetched && questionIds.length !== 0 && (<div>{`Downloading ${items.length} / ${questionIds.length}`}</div>)}
        {!isAuthenticated && (
            <div>
              <IonInput placeholder="ID" value={id} onIonChange={e => setState({...state, id: e.detail.value || ''})}/>
              <IonButton onClick={handleLogin}>Login</IonButton>
            </div>
        )}
      </IonContent>
    </IonPage>
  );
};


import { RouteComponentProps } from "react-router";
import { getLogger } from "../core";
import { IonButton, IonContent, IonHeader, IonIcon, IonInput, IonLoading, IonPage, IonTitle, IonToolbar } from "@ionic/react";
import { useCallback, useContext, useEffect, useState } from "react";
import { AuthContext } from "./AuthProvider";
import { personCircle, logIn } from "ionicons/icons";
import CustomToolbar from '../components/customToolbar';

const log = getLogger('LOGIN');

interface LoginState {
  username?: string;
  password?: string;
}

export const Login: React.FC<RouteComponentProps> = ({ history }) => {
  const { isAuthenticated, isAuthenticating, login, authenticationError } = useContext(AuthContext);
  const [state, setState] = useState<LoginState>({});
  const { username, password } = state;

  const handlePasswordChange = useCallback((e: any) => setState({
    ...state,
    password: e.detail.value || ''
  }), [state]);

  const handleUsernameChange = useCallback((e: any) => setState({
    ...state,
    username: e.detail.value || ''
  }), [state]);

  const handleLogin = useCallback(() => {
    log('handleLogin...');
    login?.(username, password);
  }, [username, password]);

  log('render');

  useEffect(() => {
    if (isAuthenticated) {
      log('redirecting to home');
      history.push('/');
    }
  }, [isAuthenticated, history]);

  return (
    <IonPage>
      <IonHeader>
        {/* <IonToolbar>
          <CustomToolbar title='Login' />
        </IonToolbar> */}
      </IonHeader>
      <IonContent className="ion-padding" title="Enter your login credentials" >
        <div className="ion-text-center">
        <IonIcon icon={personCircle}style={{ fontSize: '6rem' }} color="dark" />
        <IonInput
              label="Username"
              placeholder=" "
              value={username}
              onIonChange={handleUsernameChange}
        />
        <IonInput
          label="Password"
          placeholder=" "
          type="password"
          value={password}
          onIonChange={handlePasswordChange}
        />

          <IonLoading isOpen={isAuthenticating} />
          {authenticationError && (
            <div>{authenticationError.message || 'Failed to authenticate'}</div>
          )}


          <IonButton shape='round' onClick={handleLogin} color='white' style={{ backgroundColor: 'black', color:'white' }}>
            Login
            <IonIcon slot="start" icon={logIn}></IonIcon>
          </IonButton>
        </div>
      </IonContent>
    </IonPage>
  );
};

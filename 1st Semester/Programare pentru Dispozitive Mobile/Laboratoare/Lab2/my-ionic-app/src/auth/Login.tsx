import { RouteComponentProps } from "react-router";
import { getLogger } from "../core";
import { IonButton, IonContent, IonHeader, IonIcon, IonInput, IonLoading, IonPage, IonTitle, IonToolbar } from "@ionic/react";
import { useCallback, useContext, useEffect, useState } from "react";
import { AuthContext } from "./AuthProvider";
import { personCircle, logIn } from "ionicons/icons";

const log = getLogger('LOGIN');

interface LoginState{
    username?: string;
    password?: string;
}

export const Login: React.FC<RouteComponentProps> = ({history}) =>{
    const {isAuthenticated,isAuthenticating,login,authenticationError} = useContext(AuthContext);
    const[state, setState] = useState<LoginState>({});
    const {username,password} = state;
    const handlePasswwordChange = useCallback((e: any) => setState({
        ...state,
        password: e.detail.value || ''
    }),[state]);
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
      }, [isAuthenticated]);
      return (
        <IonPage>
          <IonHeader>
            <IonToolbar>
              <IonTitle>Login</IonTitle>
            </IonToolbar>
          </IonHeader>
          <IonContent className="ion-padding">
            <div className="ion-text-center">
              <IonIcon icon={personCircle}style={{ fontSize: '6rem' }} color="dark" />
            
            <IonInput
              placeholder="Username"
              value={username}
              onIonChange={handleUsernameChange}
            />
            <IonInput
              placeholder="Password"
              type="password"
              value={password}
              onIonChange={handlePasswwordChange}
            />
            {/* <IonLoading isOpen={isAuthenticating} />
            {authenticationError && (
              <div>{authenticationError.message || 'Failed to authenticate'}</div>
            )} */}
            <IonButton shape='round' color="secondary" onClick={handleLogin}>
              Login
              <IonIcon slot="start" icon={logIn}></IonIcon>
            </IonButton>
            </div>
          </IonContent>
        </IonPage>
      );
};

    


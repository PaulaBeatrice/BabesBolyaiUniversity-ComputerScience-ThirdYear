import React, {useContext, useEffect, useState} from 'react';
import {RouteComponentProps} from 'react-router';
import {
  IonCol,
  IonContent,
  IonFab,
  IonFabButton,
  IonHeader,
  IonIcon, IonLabel,
  IonList,
  IonLoading,
  IonPage,
  IonRow,
  IonSearchbar,
  IonTitle,
  IonToolbar
} from '@ionic/react';
import {logOut} from 'ionicons/icons';
import Player from './Player';
import {getLogger} from '../core';
import {PlayerContext,} from './PlayerProvider';
import {AuthContext} from "../auth";

const log = getLogger('PlayerList');

const PlayerList: React.FC<RouteComponentProps> = ({ history }) => {
  let { players, fetching, fetchingError, setQuery, offer } = useContext(PlayerContext);
  const [searchPlayer, setSearchPlayer] = useState<string>('');
  const [showOffer, setShowOffer] = useState(false);
  const { logout } = useContext(AuthContext);

  const handleLogout = () => {
      log('handle logout');
      logout?.();
  }

  async function fetchData() {
    log(`fetch data search: ${searchPlayer}`)
    if (setQuery) {
      await setQuery(searchPlayer);
    }
  }

  useEffect(() => {
    fetchData();
  },[searchPlayer, players]);

  useEffect(() => {
    log("offer changed", offer);
    if (offer) {
      setShowOffer(true);
      setTimeout(() => {setShowOffer(false)}, 3000);
    }
  }, [offer])

  log('render');
  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Menu</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent>
        <IonRow>
            <IonCol>
              <IonSearchbar debounce={2000} onIonChange={ e => setSearchPlayer(e.detail.value || '')}></IonSearchbar>
            </IonCol>
        </IonRow>
        <IonLoading isOpen={fetching} message="Fetching players" />
        {players && (
          <IonList>
            {players.map(({code, name}) =>
                  <Player key={code} code={code} name={name} onEdit={(code => console.log(code))}/>
            )}
          </IonList>
        )}
        {(showOffer && offer) && (
            <div>
              <IonLabel> SPECIAL OFFER! </IonLabel>
              <Player code={offer.code} name={offer.name} onEdit={(code => console.log(code))}/>
            </div>
        )}
        {fetchingError && (
          <div>{fetchingError.message || 'Failed to fetch players'}</div>
        )}
        <IonFab vertical="bottom" horizontal="start" slot="fixed">
          <IonFabButton onClick={() => handleLogout()}>
            <IonIcon icon={logOut} />
          </IonFabButton>
        </IonFab>
      </IonContent>
    </IonPage>
  );
};

export default PlayerList;

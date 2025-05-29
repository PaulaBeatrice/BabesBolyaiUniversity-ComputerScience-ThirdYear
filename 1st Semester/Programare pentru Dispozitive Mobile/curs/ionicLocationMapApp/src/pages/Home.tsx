import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar } from '@ionic/react';
import './Home.css';
import { useMyLocation } from './useMyLocation';
import MyMap from '../components/MyMap';

const Home: React.FC = () => {
  const myLocation = useMyLocation();
  const { latitude: lat, longitude: lng } = myLocation.position?.coords || {}
  console.log('Home');
  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Blank</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent fullscreen>
        <IonHeader collapse="condense">
          <IonToolbar>
            <IonTitle size="large">Blank</IonTitle>
          </IonToolbar>
        </IonHeader>
        <div>My Location is</div>
        <div>latitude: {lat}</div>
        <div>longitude: {lng}</div>
        {lat && lng &&
          <MyMap
            lat={lat}
            lng={lng}
            onMapClick={log('onMap')}
            onMarkerClick={log('onMarker')}
          />}
      </IonContent>
    </IonPage>
  );

  function log(source: string) {
    return (e: any) => console.log(source, e);
  }
};

export default Home;

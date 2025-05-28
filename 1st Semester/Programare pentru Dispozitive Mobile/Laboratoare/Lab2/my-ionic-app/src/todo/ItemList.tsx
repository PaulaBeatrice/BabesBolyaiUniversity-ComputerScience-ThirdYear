import React, { useContext } from 'react';
import { RouteComponentProps } from 'react-router';
import {
  IonButton,
  IonButtons,
  IonContent,
  IonFab,
  IonFabButton,
  IonHeader,
  IonIcon,
  IonList, 
  IonPage,
  IonTitle,
  IonToolbar,

} from '@ionic/react';
import { add, logOut,   } from 'ionicons/icons';
import { getLogger } from '../core';
import { ItemContext } from './ItemProvider';
import Item from './Item';
import { AuthContext } from '../auth';

const log = getLogger('ItemList');


const ItemList: React.FC<RouteComponentProps> = ({ history }) => {
  const { items, fetching, fetchingError } = useContext(ItemContext);
  const { login } = useContext(AuthContext);

  const handleLogout = () => {
       login?.();
  };
  log('render', fetching); 
  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Shopping List</IonTitle>
          <IonButtons slot="end">
            <IonButton fill="outline" onClick={handleLogout}>LogOut</IonButton>

          </IonButtons>
        </IonToolbar>
      </IonHeader>
      <IonContent>
       {/* <IonLoading isOpen={fetching} message="Fetching items" /> */}
        {items && (
          <IonList>
            {items.map(({ _id, title,price,date,sold}) =>
             <Item key={_id} _id={_id} title={title} price={price} date={date} sold = {sold} onEdit={id => history.push(`/book/${id}`)}/>
            )}
          </IonList>
        )}
        {fetchingError && (
          <div>{fetchingError.message || 'Failed to fetch items'}</div>
        )}
        <IonFab vertical="bottom" horizontal="end" slot="fixed">
          <IonFabButton onClick={() => history.push('/book')}>
            <IonIcon icon={add} />
          </IonFabButton>
        </IonFab>
      </IonContent>
    </IonPage>
  );
};

export default ItemList;

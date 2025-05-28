import React, {useContext, useEffect, useState} from 'react';
import { RouteComponentProps } from 'react-router';
import {
  IonButton,
  IonContent,
  IonFab,
  IonFabButton,
  IonHeader,
  IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonItem,
  IonList, IonLoading,
  IonPage, IonSearchbar, IonSelect, IonSelectOption,
  IonTitle,
  IonToolbar
} from '@ionic/react';
import {add, logOut} from 'ionicons/icons';
import Item from './Item';
import { getLogger } from '../core';
import { ItemContext } from './ItemProvider';
import {AuthContext} from "../auth";
import {useNetwork} from "../use/useNetwork";
import CustomToolbar from '../components/customToolbar';
import { IonToast } from '@ionic/react';

const log = getLogger('ItemList');
let MAX_PER_PAGE=10;

const ItemList: React.FC<RouteComponentProps> = ({ history }) => {
  const { items, fetching, fetchingError, savingError } = useContext(ItemContext);
  const {logout}=useContext(AuthContext);
  const { networkStatus } = useNetwork();

  const [end, setEnd] = useState(MAX_PER_PAGE);
  const [start, setStart] = useState(0);


  async function searchNext($event: CustomEvent<void>) {
    setEnd(end+MAX_PER_PAGE);
    await ($event.target as HTMLIonInfiniteScrollElement).complete();
  }

  const [searchText, setSearchText] = useState('');

  const [wasReceived, setWasReceived] = useState('all');
  const [filterStatus, setFilterStatus] = useState('all');

  useEffect(()=>{
    setEnd(MAX_PER_PAGE);
  },[searchText,wasReceived])

  const filteredItems = items?.filter(({ title, sold }) => {
    const bookMatchesSearch = title.toLowerCase().includes(searchText.toLowerCase());

    if (filterStatus === 'all') {
      return bookMatchesSearch;
    } else if (filterStatus === 'sold') {
      return sold && bookMatchesSearch;
    } else if (filterStatus === 'notSold') {
      return !sold && bookMatchesSearch;
    }
    return false;
  });


  return (
    <IonPage>
      <IonHeader>
        <CustomToolbar title='List of Books' />
      </IonHeader>
      <IonContent>
      <IonSearchbar value={searchText} onIonInput={(e) => setSearchText(e.detail.value!)} placeholder='Search by title'/>
      {savingError && (
          <IonToast
                          message="Changes will not be saved until the connection is restored."
                          duration={2000} // Toast will be displayed for 2 seconds
                          position="bottom"
                          color="danger"
                      />
        )}
        {fetchingError && (
            <div>{'No network connection! Changes will not be saved until the connection is restored.'}</div>
        )}
        <IonSelect value={filterStatus} onIonChange={(e) => setFilterStatus(e.detail.value)}>
            <IonSelectOption value="all">All Items</IonSelectOption>
            <IonSelectOption value="sold">Sold Items</IonSelectOption>
            <IonSelectOption value="notSold">Not Sold Items</IonSelectOption>
        </IonSelect>
        {filteredItems && (
          <IonList>
            {filteredItems.map(({ _id, title,price,date: date_added,sold}) =>
             <Item key={_id} 
             _id={_id}
              title={title} 
              price={price}
              date={date_added} sold={sold} onEdit={id => history.push(`/book/${_id}`)} />
            )}
          </IonList>
        )}
        <IonInfiniteScroll threshold="100px" disabled={false}
                           onIonInfinite={(e: CustomEvent<void>) => searchNext(e)}>
          <IonInfiniteScrollContent
              loadingText="Loading more books...">
          </IonInfiniteScrollContent>
        </IonInfiniteScroll>

        <IonFab vertical="bottom" horizontal="end" slot="fixed">
          <IonFabButton onClick={() => history.push('/book')}>
            <IonIcon icon={add}/>
          </IonFabButton>
        </IonFab>
      </IonContent>
    </IonPage>
  );
};

export default ItemList;

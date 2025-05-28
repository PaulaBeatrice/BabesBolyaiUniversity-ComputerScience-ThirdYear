import React, { useCallback, useContext, useEffect, useState } from 'react';
import {
  IonButton,
  IonButtons,
  IonCheckbox,
  IonContent,
  IonHeader,
  IonInput,
  IonLoading,
  IonPage,
  IonTitle,
  IonToolbar
} from '@ionic/react';
import { getLogger } from '../core';
import { ItemContext } from './ItemProvider';
import { RouteComponentProps } from 'react-router';
import { ItemProps } from './ItemProps';

const log = getLogger('ItemEdit');

interface ItemEditProps extends RouteComponentProps<{
  id?: string;
}> {}

const ItemEdit: React.FC<ItemEditProps> = ({ history, match }) => {
  const { items, saving, savingError, saveItem } = useContext(ItemContext);
  const [title, setTitle] = useState('');
  const [price, setPrice] = useState(0); 
  const [sold, setSold] = useState(false); 
  const [item, setItem] = useState<ItemProps>();
  useEffect(() => {
    log('useEffect');
    const routeId = match.params.id || '';
    const item = items?.find(it => it._id === routeId);
    setItem(item);
    if (item) {
      setTitle(item.title);
      setPrice(item.price);
      setSold(item.sold);
    }
  }, [match.params.id, items]);
  const handleSave = () => {
    const editedItem = item ? { ...item, title,price,sold } : { title, price,date: new Date(), sold};
    saveItem && saveItem(editedItem).then(() => history.goBack());
  };
  log('render');
  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Edit</IonTitle>
          <IonButtons slot="end">
            <IonButton fill="outline" shape='round' onClick={handleSave} >
              Save changes
            </IonButton>
          </IonButtons>
        </IonToolbar>
      </IonHeader>
      <IonContent>
          <IonInput label='Title: ' labelPlacement='floating' value={title} onIonChange={e => setTitle(e.detail.value || '')}/>
          <IonInput
            label='Price:   '
            type="number"
            value={price === undefined ? '' : price}
            onIonChange={(e) => {
              const inputValue = e.detail.value;
              if (inputValue !== null && inputValue !== undefined && inputValue !== '') {
                setPrice(parseInt(inputValue));
              } else {
                setPrice(0); 
              }
            }}
          />
           <IonCheckbox labelPlacement="start" checked={sold} onIonChange={e=> setSold(!sold)}>Sold: </IonCheckbox>
       <IonLoading isOpen={saving} />
        {savingError && (
          <div>{savingError.message || 'Failed to save item'}</div>
        )}
      </IonContent>
    </IonPage>
  );
};

export default ItemEdit;

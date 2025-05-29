import React, { useContext, useEffect, useState } from 'react';
import {
  IonButton,
  IonButtons, IonCard,
  IonContent,
  IonHeader, IonItem, IonLabel,
  IonPage, IonRadio, IonRadioGroup, IonRow,
  IonTitle,
  IonToolbar
} from '@ionic/react';
import { getLogger } from '../core';
import { ItemContext } from './ItemProvider';
import { RouteComponentProps } from 'react-router';
import { ItemProps } from './ItemProps';
import {Redirect} from "react-router-dom";

const log = getLogger('ItemEdit');

interface ItemEditProps extends RouteComponentProps<{
  id?: string;
}> {}

const ItemEdit: React.FC<ItemEditProps> = ({ history, match }) => {
  const { items, correct, setCorrect } = useContext(ItemContext);
  const [item, setItem] = useState<ItemProps>();
  const [itemIndex, setItemIndex] = useState(0);
  const [selected, setSelected] = useState(-1);

  useEffect(() => {
    if (items) {
      log('useEffect');
      const routeId = match.params.id || '';
      const itemIdx = items?.findIndex(it => it.id === parseInt(routeId));
      setItem(items[itemIdx]);
      setItemIndex(itemIdx);
    }
  }, [match.params.id, items]);

  useEffect(() => {
    log("selected:", selected);
  }, [selected])

  const handleNext = () => {
    if (selected >= 0 && selected < item!.options.length) {
      if (item!.indexCorrectOption === selected) {
        setCorrect?.(correct + 1);
      }
    }
    if (itemIndex < items!.length - 1)
      history.push(`${items![itemIndex + 1].id}`)
  };

  log('render');

  if (!item || !items) {
    return <Redirect to={{ pathname: '/item/0' }} />
  }

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Question {itemIndex} / {items.length}</IonTitle>
          <IonTitle>Correct answers {correct} / {itemIndex}</IonTitle>
          <IonButtons slot="end">
            {(itemIndex < items.length - 1) &&
              <IonButton onClick={handleNext}>
                Next
              </IonButton>
            }
          </IonButtons>
        </IonToolbar>
      </IonHeader>


      <IonContent>
        <IonCard>
          <IonHeader>{item.text}</IonHeader>
          <IonRadioGroup onIonChange={ (ev) => { setSelected(ev.detail.value? parseInt(ev.detail.value): -1) } }>
            {
              Object.entries(item.options).map(([idx, val]) => (
                  <IonItem key={idx}>
                    <IonLabel>{val}</IonLabel>
                    <IonRadio slot="end" value={idx}></IonRadio>
                  </IonItem>))
            }
          </IonRadioGroup>
        </IonCard>
      </IonContent>
    </IonPage>
  );
};

export default ItemEdit;

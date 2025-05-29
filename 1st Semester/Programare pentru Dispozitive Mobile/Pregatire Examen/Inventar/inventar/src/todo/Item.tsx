import React from 'react';
import { IonItem, IonLabel } from '@ionic/react';
import { ItemProps } from './ItemProps';

const Item: React.FC<ItemProps> = ({ code, number,state }) => {
  return (
    <IonItem>
      <IonLabel>Code: {code}</IonLabel>
      <IonLabel>Quantity: {number}</IonLabel>
      <IonLabel>State: {state}</IonLabel>
    </IonItem>
  );
};

export default Item;

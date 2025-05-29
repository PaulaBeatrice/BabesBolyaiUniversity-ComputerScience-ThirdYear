import React from 'react';
import { IonItem, IonLabel } from '@ionic/react';
import { ItemProps } from './ItemProps';

const Item: React.FC<ItemProps> = ({ code, number }) => {
  return (
    <IonItem>
      <IonLabel>{code}</IonLabel>
      <IonItem>{number}</IonItem>
    </IonItem>
  );
};

export default Item;

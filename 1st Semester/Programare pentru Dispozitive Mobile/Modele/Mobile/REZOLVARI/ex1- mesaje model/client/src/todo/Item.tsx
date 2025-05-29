import React from 'react';
import { IonItem, IonLabel } from '@ionic/react';
import { ItemProps } from './ItemProps';

const Item: React.FC<ItemProps> = ({ id, text, read/*, onEdit*/ }) => {
  return (
    <IonItem>
      <IonLabel>
          {!read && (<h1>{text}</h1>)}
          {read && text}
      </IonLabel>
    </IonItem>
  );
};

export default Item;

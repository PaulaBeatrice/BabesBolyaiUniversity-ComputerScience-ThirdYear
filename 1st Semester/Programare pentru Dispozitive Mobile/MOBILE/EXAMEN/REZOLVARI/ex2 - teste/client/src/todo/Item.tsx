import React from 'react';
import { IonItem, IonLabel } from '@ionic/react';
import { ItemProps } from './ItemProps';

interface ItemPropsExt extends ItemProps {
  onEdit: (_id?: number) => void;
}

const Item: React.FC<ItemPropsExt> = ({ id, text, onEdit }) => {
  return (
    <IonItem onClick={() => onEdit(id)}>
      <IonLabel>{text}</IonLabel>
    </IonItem>
  );
};

export default Item;

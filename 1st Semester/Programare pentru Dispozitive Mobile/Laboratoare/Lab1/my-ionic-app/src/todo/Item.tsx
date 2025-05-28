import React, { memo } from 'react';
import { IonItem, IonLabel } from '@ionic/react';
import { getLogger } from '../core';
import { ItemProps } from './ItemProps';

const log = getLogger('Item');

interface ItemPropsExt extends ItemProps {
  onEdit: (id?: string) => void;
}

const Item: React.FC<ItemPropsExt> = ({ id, title, price, sold, date, onEdit }) => {
  return (
    <IonItem onClick={() => onEdit(id)}>
      <IonLabel>{title}</IonLabel>
      <IonLabel>{price}</IonLabel>
      <IonLabel>{sold ? 'Vândut' : 'Disponibil'}</IonLabel>
      <IonLabel>{date}</IonLabel>
      {/* <IonLabel>{date.getDate()}</IonLabel> */}
      {/* <IonLabel>{date.toLocaleDateString}</IonLabel> */}
      {/* <IonLabel>{date.getFullYear.toString + " " + date.getMonth.toString + " " + date.getDay.toString}</IonLabel> */}
    </IonItem>
  );
};

export default memo(Item);

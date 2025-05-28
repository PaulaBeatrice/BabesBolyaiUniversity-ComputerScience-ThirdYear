import React, { memo } from 'react';
import { IonCard, IonCardContent, IonCheckbox, IonFab, IonFabButton, IonIcon, IonItem, IonLabel } from '@ionic/react';
import { ItemProps } from './ItemProps';
import { pencil } from 'ionicons/icons';

interface ItemPropsExt extends ItemProps {
  onEdit: (id?: string) => void;
}

function formatDate(date: Date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}


const Item: React.FC<ItemPropsExt> = ({ _id, title,price, date,sold, onEdit }) => {
  return (
    <IonCard>
     <IonCardContent>
       <IonItem>
         <IonLabel>Title: {title}</IonLabel>
       </IonItem>
       <IonItem>
          <IonLabel>Price: {price}</IonLabel>
       </IonItem>
       <IonItem>
          <IonLabel>Added on: {formatDate(new Date(date))}</IonLabel>
       </IonItem>
       <IonItem>
          <IonCheckbox labelPlacement="start" disabled ={true} checked={sold}  >Sold:</IonCheckbox>
       </IonItem>
       <IonFab vertical="top" horizontal="end" >
          <IonFabButton color="dark" onClick={() => onEdit(_id)}>
            <IonIcon icon={pencil} />
          </IonFabButton>
        </IonFab>
      </IonCardContent>
    </IonCard>
  );
};

export default Item;

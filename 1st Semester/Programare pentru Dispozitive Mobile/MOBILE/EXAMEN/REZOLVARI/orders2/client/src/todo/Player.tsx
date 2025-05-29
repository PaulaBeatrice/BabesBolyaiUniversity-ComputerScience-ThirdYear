import React, { useCallback } from 'react';
import { IonItem, IonLabel } from '@ionic/react';
import { PlayerProps } from './PlayerProps';

interface PlayerPropsExt extends PlayerProps {
  onEdit: (id?: number) => void;
}

const Player: React.FC<PlayerPropsExt> = ({ code, name , onEdit }) => {
  const handleEdit = useCallback(() => onEdit(code), [code, onEdit]);
  return (
    <IonItem onClick={handleEdit}>
      <IonLabel>{name}</IonLabel>
    </IonItem>
  );
};

export default Player;

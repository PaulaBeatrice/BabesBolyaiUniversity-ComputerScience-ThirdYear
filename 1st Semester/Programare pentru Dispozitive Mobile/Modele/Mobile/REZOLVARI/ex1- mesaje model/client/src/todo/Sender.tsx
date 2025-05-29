import React, { useCallback } from 'react';
import { IonItem, IonLabel } from '@ionic/react';

export interface SenderProps {
    username: string;
    unreadMessages: number;
    onEdit: (username?: string) => void;
}

const Sender: React.FC<SenderProps> = ({ username, unreadMessages, onEdit }) => {
    const handleEdit = useCallback(() => onEdit(username), [username, onEdit]);
    return (
        <IonItem onClick={handleEdit}>
            <IonLabel>{username}</IonLabel>
            <IonLabel>{unreadMessages}</IonLabel>
        </IonItem>
    );
};

export default Sender;
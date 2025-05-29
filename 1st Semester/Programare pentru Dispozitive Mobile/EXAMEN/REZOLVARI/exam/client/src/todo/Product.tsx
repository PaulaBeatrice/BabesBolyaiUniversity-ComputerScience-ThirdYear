import {IonItem, IonLabel, IonRadio} from '@ionic/react';
import {ProductProps} from "./ProductProps";
import React from 'react';

const Product: React.FC<ProductProps> = ({ code, name }) => {
    return (
        <IonItem>
            <IonLabel>{code}</IonLabel>
            <IonLabel>{name}</IonLabel>
            <IonRadio slot="end" value={code}></IonRadio>
        </IonItem>
    );
};

export default Product;
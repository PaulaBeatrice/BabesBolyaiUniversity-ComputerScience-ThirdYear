import React, { useCallback, useContext, useEffect, useState } from 'react';
import {
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonInput,
  IonLabel,
  IonDatetime,
  IonCheckbox,
  IonPage,
  IonTitle,
  IonToolbar,
  IonBackButton,
  IonLoading,
  IonSelect,
  IonSelectOption
} from '@ionic/react';
import { getLogger } from '../core';
import { RouteComponentProps } from 'react-router';
import { BookContext } from './BookProvider';
import { BookProps } from './BookProps';
import styles from './styles.module.css';

const log = getLogger('SaveLogger');

interface BookEditProps extends RouteComponentProps<{
  id?: string;
}> {}

export const BookAdd: React.FC<BookEditProps> = ({ history, match }) => {
  log("book add here");
  const { updating, updateError, addBook } = useContext(BookContext);
  const [title, setTitle] = useState('');
  const [launchDate, setLaunchDate] = useState(new Date());
  const [price, setPrice] = useState(0);
  const [sold, setSold] = useState(false);

  const handleAdd = useCallback(() => {
    const editedBook = { title, launchDate, sold, price };
    addBook && addBook(editedBook).then(() => history.goBack());
  }, [addBook, title, launchDate, sold, price, history]);

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonButtons slot="start">
            <IonBackButton></IonBackButton>
          </IonButtons>
          <IonTitle>Edit</IonTitle>
          <IonButtons slot="end">
            <IonButton onClick={handleAdd}>Add</IonButton>
          </IonButtons>
        </IonToolbar>
      </IonHeader>
      <IonContent className={styles.editContent}>
        <br></br>
        <IonLabel className={styles.editLabel}>Title</IonLabel>
        <IonInput
          value={title}
          placeholder="Enter title"
          onIonChange={(e) => setTitle(e.detail.value || '')}
        />

        <IonLabel className={styles.editLabel}>Launch Date</IonLabel>
        <IonDatetime presentation="date" value={launchDate.toString()} onIonChange={e=>{ setLaunchDate(new Date(Date.parse(e.detail.value?.toString() || new Date(Date.now()).toString())))}}/>       

        <IonLabel className={styles.editLabel}>Sold     </IonLabel>
        <IonCheckbox checked={sold} onIonChange={(e) => setSold(e.detail.checked)} />

        <br></br>
        <br></br>
        <IonLabel className={styles.editLabel}>Price</IonLabel>
        <IonInput
          type="number"
          value={price}
          placeholder="Enter price"
          onIonChange={(e) => setPrice(Number.parseInt(e.detail.value || "0"))}
        />

        <IonLoading isOpen={updating} />
        {updateError && (
          <div className={styles.errorMessage}>{updateError.message || 'Failed to add item'}</div>
        )}
      </IonContent>
    </IonPage>
  );
};

import React, {useContext, useEffect, useState} from 'react';
import { RouteComponentProps } from 'react-router';
import {
  IonButton,
  IonButtons,
  IonContent, IonHeader,
  IonList, IonLoading,
  IonPage, IonRadioGroup, IonSearchbar, IonSplitPane, IonTitle, IonToolbar
} from '@ionic/react';
import Item from './Item';
import Product from "./Product";
import { getLogger } from '../core';
import { ItemContext } from './ItemProvider';
import {ItemProps} from "./ItemProps";
import {ProductProps} from "./ProductProps";
import {queryByDisplayValue} from "@testing-library/react";
import { IonToast } from '@ionic/react';


const log = getLogger('ItemList');

const ItemList: React.FC<RouteComponentProps> = ({ history }) => {
  const { products, items, fetching, fetchingError, saveItem, page, total, canReDownload, reDownload, upload, uploadNr, uploading } = useContext(ItemContext);

  const [searchProduct, setSearchProduct] = useState('');
  const [shownProducts, setShownProducts] = useState<ProductProps[]>([]);
  const [selected, setSelected] = useState(-1);
  const [quantity, setQuantity] = useState(0);
  const [showToast, setShowToast] = useState(false);

  useEffect(() => {
    if (products && searchProduct && !fetching) {
      const filtered = products.filter(el => el.name.indexOf(searchProduct) !== -1).slice(0, 5);
      setShownProducts(filtered);
    }
  }, [searchProduct, products, fetching]);

  useEffect(() => {
    if (selected < 0) return;

    const quantity = prompt("Please enter the quantity:", "1");
    if (!quantity) return;

    const qval = parseInt(quantity);
    if (qval > 0) {
      setQuantity(qval);
    }
  }, [selected]);

  useEffect(() => {
    log('items changed', items);
  }, [items]);

  const addItem = () => {
    saveItem?.({ number: quantity, code: selected });
    //alert("Added " + selected + " qt" + quantity);
  };

  const handleUpload = () => {
    try {
      upload?.(items);
    } catch (error) {
      alert('Upload failed');
    }
  };

  const clearItems = () => {
    items!.length = 0
  };


  log('render');
  return (
    <IonPage>
      <IonSplitPane when="xs" contentId="main">
        <IonContent style ={{ maxWidth: '50%', margin: '0 auto'}}>
          <IonHeader>
            <IonToolbar>
              {fetching && <IonTitle>Downloading {page} / {total / 10}</IonTitle>}
              <IonSearchbar debounce={2000} onIonChange={e => setSearchProduct(e.detail.value || '')}></IonSearchbar>
              <IonButtons slot="end">
                {canReDownload && !fetching && <IonButton onClick={reDownload}>Download</IonButton>}
                {selected >= 0 && <IonButton onClick={addItem}>Add</IonButton>}
              </IonButtons>
            </IonToolbar>
          </IonHeader>
          <IonRadioGroup onIonChange={ev => setSelected(ev.detail.value ? parseInt(ev.detail.value) : -1)}>
            {shownProducts.map(pr => (
              <Product key={pr.code} code={pr.code} name={pr.name} />
            ))}
          </IonRadioGroup>
        </IonContent>

        <div className="ion-page" id="main" style={{ maxWidth: '50%', margin: '0 auto'}}>
          <IonContent>
            <IonHeader>
              <IonToolbar>
                {uploading && (
                  <IonTitle>
                    {uploadNr == items?.length
                      ?` Submitting ${uploadNr} / ${items?.length} `
                      : uploading
                      ? `Submitting ${uploadNr} / ${items?.length}`
                      : (() => {
                          setShowToast(true);
                          return 'Submitted';
                        })()}
                  </IonTitle>
                )}
                <IonToast isOpen={showToast} onDidDismiss={() => setShowToast(false)} message="Submitted" duration={2000} />

                <IonButtons slot="end">
                  {!uploading && items && <IonButton onClick={handleUpload}>Upload</IonButton>}
                </IonButtons>
                <IonButton onClick={clearItems}>Clear</IonButton>
              </IonToolbar>
            </IonHeader>
            {items && items.map(it => <Item key={it.code} number={it.number} code={it.code} state={it.state} />)}
          </IonContent>
        </div>
      </IonSplitPane>
    </IonPage>
  );
};

export default ItemList;
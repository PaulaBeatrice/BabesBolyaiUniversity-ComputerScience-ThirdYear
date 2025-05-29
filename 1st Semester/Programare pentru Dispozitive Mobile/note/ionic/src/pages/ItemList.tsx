import React, { useContext, useState } from "react";
import {
  IonAlert,
  IonButton,
  IonContent,
  IonLoading,
  IonPage,
  IonToast,
} from "@ionic/react";
import Item from "../entity/Item";
import { getLogger } from "../core";
import { ItemContext } from "../entity/ItemProvider";

const log = getLogger("ItemList");

const ItemList: React.FC = () => {
  const {
    items,
    fetching,
    fetchingError,
    usedLocal,
    savingError,
    page,
    fetchNextPage,
    fetchPrevPage,
    deleteItem,
  } = useContext(ItemContext);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [toDeleteId, setToDeleteId] = useState<number>();
  const deletedLocally = !!savingError;

  return (
    <IonPage>
      <IonContent fullscreen className="ion-padding">
        <IonLoading isOpen={fetching} message="Fetching items" />
        {items.notes.map(({ date, text, id }) => (
          <Item
            date={date}
            text={text}
            id={id}
            key={id}
            onClick={(deleteId) => {
              setIsModalOpen(true);
              setToDeleteId(deleteId);
            }}
            deletedLocally={deletedLocally}
            selectedId={toDeleteId}
          />
        ))}
        <IonAlert
          isOpen={isModalOpen}
          onDidDismiss={() => setIsModalOpen(false)}
          buttons={[
            {
              text: "Yes",
              handler: () => {
                deleteItem && toDeleteId && deleteItem(toDeleteId);
              },
            },
          ]}
          header={`Are you sure you want to delete the grade with id ${toDeleteId}?`}
        />
        <IonButton
          onClick={() => {
            if (page > 1) fetchPrevPage && fetchPrevPage();
          }}
        >
          {"<"}
        </IonButton>
        <IonButton
          onClick={() => {
            if (items.more) fetchNextPage && fetchNextPage();
          }}
        >
          {">"}
        </IonButton>
        <div>page: {page}</div>
        {fetchingError && (
          <div>{fetchingError.message || "Failed to fetch items"}</div>
        )}
        <IonToast
          isOpen={usedLocal}
          message={
            "Item was not sent to the server. Waiting on local for a new connection."
          }
          position="bottom"
          buttons={[
            {
              text: "Dismiss",
              role: "cancel",
              handler: () => {
                console.log("More Info clicked");
              },
            },
          ]}
          duration={5000}
        />
      </IonContent>
    </IonPage>
  );
};

export default ItemList;

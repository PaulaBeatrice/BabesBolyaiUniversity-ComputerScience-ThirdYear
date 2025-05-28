import React, { memo } from "react";
import { IonItem, IonLabel, IonNote, IonCheckbox, IonGrid, IonRow, IonCol } from "@ionic/react";
import { getLogger } from "../core";
import { BookProps } from "./BookProps";
import styles from "./styles.module.css";

interface BookPropsExtended extends BookProps {
  onEdit: (_id?: string) => void;
}

const containerStyle = {
  borderBottom: "1px solid #eee", // Adding a border to separate items
  padding: "8px", // Adding padding for better spacing
};

const imageContainerStyle = {
  flex: "0 0 auto",
  marginRight: "16px",
};

const informationContainerStyle = {
  flex: "1",
};

const checkboxContainerStyle = {
  flex: "0 0 auto",
  display: "flex",
  flexDirection: "column", // Adjusting to column layout
  alignItems: "flex-start", // Aligning items to the start of the column
};

const photoStyle = {
  width: "400px", 
  height: "500px",
  borderRadius: "10px", // Rounded corners
};

const BookComponent: React.FC<BookPropsExtended> = ({ _id, title, launchDate, price, sold, webViewPath, isNotSaved, onEdit }) => (  
  <IonItem style={{ borderBottom: "1px solid #eee", padding: "8px", cursor: "pointer" }} onClick={() => onEdit(_id)}>
    <IonGrid style={containerStyle}>
      <IonRow>
        <IonCol size="2" style={imageContainerStyle}>
          {webViewPath && <img style={photoStyle} src={webViewPath} alt={title} />}
        </IonCol>
        <IonCol size="7" style={informationContainerStyle}>
          <IonLabel className={styles.bookLabel}>
            <IonItem>
              <IonLabel>Title: {title}</IonLabel>
            </IonItem>
            <IonItem>
              <IonLabel>Price: {price}</IonLabel>
            </IonItem>
          </IonLabel>
          <IonNote>
            Launched on {launchDate ? new Date(launchDate).toLocaleDateString() : "(Invalid date)"}
          </IonNote>
        </IonCol>
        <IonCol size="3" style={checkboxContainerStyle}>
          <IonItem>
            <IonCheckbox labelPlacement="start" disabled={true} checked={sold} />
            <IonLabel>Sold</IonLabel>
          </IonItem>
        </IonCol>
      </IonRow>
    </IonGrid>
  </IonItem>
);

export default memo(BookComponent);

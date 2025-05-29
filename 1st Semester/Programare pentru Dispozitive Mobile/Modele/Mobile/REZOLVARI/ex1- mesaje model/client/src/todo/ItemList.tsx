import React, {useContext, useEffect, useState} from 'react';
import { RouteComponentProps } from 'react-router';
import {
  IonContent,
  IonList, IonLoading,
  IonPage, IonSplitPane
} from '@ionic/react';
import Item from './Item';
import Sender from "./Sender";
import { getLogger } from '../core';
import { ItemContext } from './ItemProvider';
import {ItemProps} from "./ItemProps";

const log = getLogger('ItemList');

const ItemList: React.FC<RouteComponentProps> = ({ history }) => {
  const { items, fetching, fetchingError, saveItem } = useContext(ItemContext);

  const [sendersMessages, setSendersMessages] = useState<{ [sender: string]: {messages: number, lastUnread: number} }>({});
  const [senderDisplay, setSenderDisplay] = useState("");
  const [displayMessages, setDisplayMessages] = useState<ItemProps[]>([]);

  const [toUpdate, setToUpdate] = useState<ItemProps[]>([]);

  // find all senders
  useEffect(() => {
    log("Find senders");
    if (!items) return;

    const sendMsg: { [sender: string]: {messages: number, lastUnread: number} } = {};

    for (const {sender, read, created} of items) {
      if(!sendMsg[sender]) {
        sendMsg[sender] = {messages: 0, lastUnread: 0};
      }
      if (!read) {
        sendMsg[sender].messages += 1;
        if (sendMsg[sender].lastUnread < created) {
          sendMsg[sender].lastUnread = created;
        }
      }
    }

    log(`senders ${JSON.stringify(sendMsg)}`);
    setSendersMessages(sendMsg);
  }, [items]);

  // populate message show
  useEffect(() => {
    log("Find messages");
    if (!items) return;

    const msgs = items.filter((value) => value.sender === senderDisplay).sort((a, b) => b.created - a.created);

    setDisplayMessages(msgs);

    const u = msgs.filter(m => !(toUpdate.find(value => value.id === m.id))); // not very efficient, but avoids multiple save calls on the same item
    if (u.length > 0)
      setToUpdate(u);
  }, [items, senderDisplay]);

  // mark messages as read
  useEffect(() => {
    if (saveItem) {
      for (const msg of toUpdate) {
        if (!msg.read)
          saveItem({...msg, read: true});
      }
    }
  }, [toUpdate, saveItem]);

  log('render');
  return (
    <IonPage>
        <IonLoading isOpen={fetching} message="Fetching items" />
          <IonSplitPane when="xs" contentId="main">
            <IonList>
              {Object.entries(sendersMessages)
                  .sort(([a, {lastUnread: aLast}], [b, {lastUnread: bLast}]) => {
                    return bLast - aLast;
                  })
                  .map(([sender, {messages}]) => {
                    return <Sender key={sender} username={sender} unreadMessages={messages} onEdit={id => setSenderDisplay(sender) /*history.push(`/item/${id}`)*/ } />
              })}
            </IonList>

            <div className="ion-page" id="main">
              <IonContent>
                <IonList>
                  {displayMessages.map((mes) =>
                  <Item key={mes.id} text={mes.text} created={mes.created} read={mes.read} sender={mes.sender}/>)}
                </IonList>
              </IonContent>
            </div>
          </IonSplitPane>
        {fetchingError && (
          <div>{fetchingError.message || 'Failed to fetch items'}</div>
        )}
        {/*<IonFab vertical="bottom" horizontal="end" slot="fixed">*/}
        {/*  <IonFabButton onClick={() => history.push('/item')}>*/}
        {/*    <IonIcon icon={add} />*/}
        {/*  </IonFabButton>*/}
        {/*</IonFab>*/}
    </IonPage>
  );
};

export default ItemList;

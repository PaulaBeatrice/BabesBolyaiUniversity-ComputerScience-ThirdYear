import { Preferences } from "@capacitor/preferences";
import { useEffect } from "react";

export const usePreferences = () => {
    useEffect(() => {
        runPreferencesUser();
        //runPreferencesNewItem();
        runPreferencesItems();
    },[]);

    function runPreferencesUser(){
        (async () => {
            await Preferences.set({
                key: 'user',
                value: JSON.stringify({
                    username:'a',password:'a',
                })
            });

            const res = await Preferences.get({key:'user'});
            if (res.value){
                console.log('User found', JSON.parse(res.value));
            }else{
                console.log('User not found');
            }

            const {keys} = await Preferences.keys();
            console.log('Keys found', keys);

            await Preferences.remove({key:'user'});
            console.log('Key found after remove',await Preferences.keys());

            await Preferences.clear();
        })();
    }

    function runPreferencesItems(){
        (async () => {
            await Preferences.set({
                key:'items',
                value: JSON.stringify([{
                    
                    _id: 'uniqueItemId123',
                    title: "Book 1",
                    price: 200,
                    date: new Date(),
                    sold: false,
                    
                }])
            });

            const res = await Preferences.get({key:'items'});
            if (res.value){
                console.log('Items found', JSON.parse(res.value));
            }else{
                console.log('Items not found');
            }

            await Preferences.remove({key:'items'});
            console.log('Key found after remove', await Preferences.keys());

            await Preferences.clear();
        })();
    }

    function runPreferencesNewItem(_id:string){
        (async () => {
            await Preferences.set({
                key:`item-${_id}`,
                value: JSON.stringify({
                    
                    _id: 'uniqueItemId123',
                    title: "Book 1",
                    price: 200,
                    date: new Date(),
                    sold: false,
                    
                })
            });

            const res = await Preferences.get({key:`item-${_id}`});
            if (res.value){
                console.log('Item found', JSON.parse(res.value));
            }else{
                console.log('Item not found');
            }

            await Preferences.remove({key:`item-${_id}`});
            console.log('Key found after remove', await Preferences.keys());

            await Preferences.clear();
        })();
    }
};

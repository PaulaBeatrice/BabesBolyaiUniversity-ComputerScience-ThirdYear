export interface BookProps {
    _id?: string;
    title: string;
    launchDate: Date;
    price:number;
    sold: boolean;
    isNotSaved?:boolean;
    latitude?:number;
    longitude?:number;
    webViewPath?: string;
}
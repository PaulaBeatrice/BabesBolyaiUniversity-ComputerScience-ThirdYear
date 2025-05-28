import Router from 'koa-router';
import dataStore from 'nedb-promise';
import { broadcast } from './wss.js';

export class BookStore{
    constructor({filename,autoload}){
        this.store = dataStore({filename,autoload});
    }
    async find(props) {
        return this.store.find(props);
      }
    
      async findOne(props) {
        return this.store.findOne(props);
      }
    
      async insert(item) {
        if (!item.title) { // validation
          throw new Error('Missing name property')
        }else if(!item.price){
            throw new Error('Missing quantity property')
        }
          
          return this.store.insert(item); 
      };
    
      async update(props, item) {
        return this.store.update(props,{$set: item} );
      }
    
      async remove(props) {
        return this.store.remove(props);
      }
}



const bookStore = new BookStore({filename:'./db/items.json',autoload:true});

export const itemRouter = new Router();


itemRouter.get('/', async (ctx) => {
    const userId = ctx.state.user._id;
    ctx.response.body = await bookStore.find({ userId });
    ctx.response.status = 200; // ok
  });

const createItem = async (ctx,item,response) => {
    try{
        const userId = ctx.state.user._id;
        item.userId = userId;
        response.body = await bookStore.insert(item);
        response.status = 201; //created
        broadcast(userId,{type:'created',payload:item});
    }catch(err){
        response.body = {message: err.message};
        response.status = 400;
    }
};


itemRouter.post('/', async ctx => await createItem(ctx,ctx.request.body,ctx.response));

itemRouter.put('/:id', async ctx =>{
  const item = ctx.request.body;
  const id = ctx.params.id;

  const itemId = item._id;
  const response = ctx.response;
  if (itemId && itemId !== id) {
    response.body = { message: 'Param id and body _id should be the same' };
    response.status = 400; // bad request
    return;
  }
  if (!itemId) {
    await createItem(ctx, item, response);
  } else {
    const userId = ctx.state.user._id;
    item.userId = userId;
    const updatedCount = await bookStore.update({ _id: id }, item);
    if (updatedCount === 1) {
      response.body = item;
      response.status = 200; // ok
      broadcast(userId, { type: 'updated', payload: item });
    } else {
      response.body = { message: 'Resource no longer exists' };
      response.status = 405; // method not allowed
    }
  }
});

itemRouter.del('/:id', async(ctx) =>{
    const userId = ctx.state.user._id;
    const item = await itemStore.findOne({ _id: ctx.params.id });
    if (item && userId !== item.userId) {
      ctx.response.status = 403; // forbidden
    } else {
      await itemStore.remove({ _id: ctx.params.id });
      ctx.response.status = 204; // no content
    } 
});


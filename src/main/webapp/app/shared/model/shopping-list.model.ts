import { IUser } from 'app/core/user/user.model';

export const enum State {
    OPEN = 'OPEN',
    WAITING = 'WAITING',
    PICKED = 'PICKED',
    CLOSED = 'CLOSED'
}

export interface IShoppingList {
    id?: number;
    title?: string;
    list?: any;
    state?: State;
    owner?: IUser;
    shopper?: IUser;
}

export class ShoppingList implements IShoppingList {
    constructor(
        public id?: number,
        public title?: string,
        public list?: any,
        public state?: State,
        public owner?: IUser,
        public shopper?: IUser
    ) {}
}

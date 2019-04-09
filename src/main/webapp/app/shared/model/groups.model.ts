import { IUser } from 'app/core/user/user.model';

export interface IGroups {
    id?: number;
    title?: string;
    userList?: any;
    user?: IUser;
}

export class Groups implements IGroups {
    constructor(public id?: number, public title?: string, public userList?: any, public user?: IUser) {}
}

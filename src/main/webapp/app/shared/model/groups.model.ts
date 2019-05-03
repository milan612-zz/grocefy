import { IUser } from 'app/core/user/user.model';

export const enum Status {
    ACTIVE = 'ACTIVE',
    INACTIVE = 'INACTIVE'
}

export interface IGroups {
    id?: number;
    title?: string;
    status?: Status;
    userList?: any;
    users?: IUser[];
}

export class Groups implements IGroups {
    constructor(public id?: number, public title?: string, public status?: Status, public userList?: any, public users?: IUser[]) {}
}

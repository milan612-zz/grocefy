import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GrocefySharedModule } from 'app/shared';
import {
    GroupsComponent,
    GroupsDetailComponent,
    GroupsUpdateComponent,
    GroupsDeletePopupComponent,
    GroupsDeleteDialogComponent,
    groupsRoute,
    groupsPopupRoute
} from './';

const ENTITY_STATES = [...groupsRoute, ...groupsPopupRoute];

@NgModule({
    imports: [GrocefySharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [GroupsComponent, GroupsDetailComponent, GroupsUpdateComponent, GroupsDeleteDialogComponent, GroupsDeletePopupComponent],
    entryComponents: [GroupsComponent, GroupsUpdateComponent, GroupsDeleteDialogComponent, GroupsDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GrocefyGroupsModule {}

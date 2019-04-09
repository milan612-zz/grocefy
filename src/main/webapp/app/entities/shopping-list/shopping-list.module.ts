import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GrocefySharedModule } from 'app/shared';
import {
    ShoppingListComponent,
    ShoppingListDetailComponent,
    ShoppingListUpdateComponent,
    ShoppingListDeletePopupComponent,
    ShoppingListDeleteDialogComponent,
    shoppingListRoute,
    shoppingListPopupRoute
} from './';

const ENTITY_STATES = [...shoppingListRoute, ...shoppingListPopupRoute];

@NgModule({
    imports: [GrocefySharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        ShoppingListComponent,
        ShoppingListDetailComponent,
        ShoppingListUpdateComponent,
        ShoppingListDeleteDialogComponent,
        ShoppingListDeletePopupComponent
    ],
    entryComponents: [
        ShoppingListComponent,
        ShoppingListUpdateComponent,
        ShoppingListDeleteDialogComponent,
        ShoppingListDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GrocefyShoppingListModule {}

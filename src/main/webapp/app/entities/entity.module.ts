import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        RouterModule.forChild([
            {
                path: 'shopping-list',
                loadChildren: './shopping-list/shopping-list.module#GrocefyShoppingListModule'
            },
            {
                path: 'groups',
                loadChildren: './groups/groups.module#GrocefyGroupsModule'
            },
            {
                path: 'shopping-list',
                loadChildren: './shopping-list/shopping-list.module#GrocefyShoppingListModule'
            },
            {
                path: 'groups',
                loadChildren: './groups/groups.module#GrocefyGroupsModule'
            }
            /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
        ])
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GrocefyEntityModule {}

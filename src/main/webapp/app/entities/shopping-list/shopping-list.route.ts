import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ShoppingList } from 'app/shared/model/shopping-list.model';
import { ShoppingListService } from './shopping-list.service';
import { ShoppingListComponent } from './shopping-list.component';
import { ShoppingListDetailComponent } from './shopping-list-detail.component';
import { ShoppingListUpdateComponent } from './shopping-list-update.component';
import { ShoppingListDeletePopupComponent } from './shopping-list-delete-dialog.component';
import { IShoppingList } from 'app/shared/model/shopping-list.model';

@Injectable({ providedIn: 'root' })
export class ShoppingListResolve implements Resolve<IShoppingList> {
    constructor(private service: ShoppingListService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IShoppingList> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<ShoppingList>) => response.ok),
                map((shoppingList: HttpResponse<ShoppingList>) => shoppingList.body)
            );
        }
        return of(new ShoppingList());
    }
}

export const shoppingListRoute: Routes = [
    {
        path: '',
        component: ShoppingListComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ShoppingLists'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: ShoppingListDetailComponent,
        resolve: {
            shoppingList: ShoppingListResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ShoppingLists'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: ShoppingListUpdateComponent,
        resolve: {
            shoppingList: ShoppingListResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ShoppingLists'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: ShoppingListUpdateComponent,
        resolve: {
            shoppingList: ShoppingListResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ShoppingLists'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const shoppingListPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: ShoppingListDeletePopupComponent,
        resolve: {
            shoppingList: ShoppingListResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ShoppingLists'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];

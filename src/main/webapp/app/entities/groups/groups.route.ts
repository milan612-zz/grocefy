import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Groups } from 'app/shared/model/groups.model';
import { GroupsService } from './groups.service';
import { GroupsComponent } from './groups.component';
import { GroupsDetailComponent } from './groups-detail.component';
import { GroupsUpdateComponent } from './groups-update.component';
import { GroupsDeletePopupComponent } from './groups-delete-dialog.component';
import { IGroups } from 'app/shared/model/groups.model';

@Injectable({ providedIn: 'root' })
export class GroupsResolve implements Resolve<IGroups> {
    constructor(private service: GroupsService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IGroups> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Groups>) => response.ok),
                map((groups: HttpResponse<Groups>) => groups.body)
            );
        }
        return of(new Groups());
    }
}

export const groupsRoute: Routes = [
    {
        path: '',
        component: GroupsComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Groups'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: GroupsDetailComponent,
        resolve: {
            groups: GroupsResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Groups'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: GroupsUpdateComponent,
        resolve: {
            groups: GroupsResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Groups'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: GroupsUpdateComponent,
        resolve: {
            groups: GroupsResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Groups'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const groupsPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: GroupsDeletePopupComponent,
        resolve: {
            groups: GroupsResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Groups'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];

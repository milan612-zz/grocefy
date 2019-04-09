import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IShoppingList } from 'app/shared/model/shopping-list.model';
import { ShoppingListService } from './shopping-list.service';
import { IUser, UserService } from 'app/core';

@Component({
    selector: 'jhi-shopping-list-update',
    templateUrl: './shopping-list-update.component.html'
})
export class ShoppingListUpdateComponent implements OnInit {
    shoppingList: IShoppingList;
    isSaving: boolean;

    users: IUser[];

    constructor(
        protected dataUtils: JhiDataUtils,
        protected jhiAlertService: JhiAlertService,
        protected shoppingListService: ShoppingListService,
        protected userService: UserService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ shoppingList }) => {
            this.shoppingList = shoppingList;
        });
        this.userService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
                map((response: HttpResponse<IUser[]>) => response.body)
            )
            .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.shoppingList.id !== undefined) {
            this.subscribeToSaveResponse(this.shoppingListService.update(this.shoppingList));
        } else {
            this.subscribeToSaveResponse(this.shoppingListService.create(this.shoppingList));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IShoppingList>>) {
        result.subscribe((res: HttpResponse<IShoppingList>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackUserById(index: number, item: IUser) {
        return item.id;
    }
}

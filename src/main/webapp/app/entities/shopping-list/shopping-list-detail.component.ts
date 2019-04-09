import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IShoppingList } from 'app/shared/model/shopping-list.model';

@Component({
    selector: 'jhi-shopping-list-detail',
    templateUrl: './shopping-list-detail.component.html'
})
export class ShoppingListDetailComponent implements OnInit {
    shoppingList: IShoppingList;

    constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ shoppingList }) => {
            this.shoppingList = shoppingList;
        });
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }
    previousState() {
        window.history.back();
    }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IShoppingList } from 'app/shared/model/shopping-list.model';
import { ShoppingListService } from './shopping-list.service';

@Component({
    selector: 'jhi-shopping-list-delete-dialog',
    templateUrl: './shopping-list-delete-dialog.component.html'
})
export class ShoppingListDeleteDialogComponent {
    shoppingList: IShoppingList;

    constructor(
        protected shoppingListService: ShoppingListService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.shoppingListService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'shoppingListListModification',
                content: 'Deleted an shoppingList'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-shopping-list-delete-popup',
    template: ''
})
export class ShoppingListDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ shoppingList }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(ShoppingListDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.shoppingList = shoppingList;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/shopping-list', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/shopping-list', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}

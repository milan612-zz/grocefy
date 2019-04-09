import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IGroups } from 'app/shared/model/groups.model';
import { GroupsService } from './groups.service';

@Component({
    selector: 'jhi-groups-delete-dialog',
    templateUrl: './groups-delete-dialog.component.html'
})
export class GroupsDeleteDialogComponent {
    groups: IGroups;

    constructor(protected groupsService: GroupsService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.groupsService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'groupsListModification',
                content: 'Deleted an groups'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-groups-delete-popup',
    template: ''
})
export class GroupsDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ groups }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(GroupsDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.groups = groups;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/groups', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/groups', { outlets: { popup: null } }]);
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

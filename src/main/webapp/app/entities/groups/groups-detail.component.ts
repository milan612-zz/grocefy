import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IGroups } from 'app/shared/model/groups.model';

@Component({
    selector: 'jhi-groups-detail',
    templateUrl: './groups-detail.component.html'
})
export class GroupsDetailComponent implements OnInit {
    groups: IGroups;

    constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ groups }) => {
            this.groups = groups;
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

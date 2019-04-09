/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { GrocefyTestModule } from '../../../test.module';
import { GroupsDeleteDialogComponent } from 'app/entities/groups/groups-delete-dialog.component';
import { GroupsService } from 'app/entities/groups/groups.service';

describe('Component Tests', () => {
    describe('Groups Management Delete Component', () => {
        let comp: GroupsDeleteDialogComponent;
        let fixture: ComponentFixture<GroupsDeleteDialogComponent>;
        let service: GroupsService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [GrocefyTestModule],
                declarations: [GroupsDeleteDialogComponent]
            })
                .overrideTemplate(GroupsDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(GroupsDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(GroupsService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});

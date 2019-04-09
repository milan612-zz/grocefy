/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { GrocefyTestModule } from '../../../test.module';
import { GroupsUpdateComponent } from 'app/entities/groups/groups-update.component';
import { GroupsService } from 'app/entities/groups/groups.service';
import { Groups } from 'app/shared/model/groups.model';

describe('Component Tests', () => {
    describe('Groups Management Update Component', () => {
        let comp: GroupsUpdateComponent;
        let fixture: ComponentFixture<GroupsUpdateComponent>;
        let service: GroupsService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [GrocefyTestModule],
                declarations: [GroupsUpdateComponent]
            })
                .overrideTemplate(GroupsUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(GroupsUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(GroupsService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Groups(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.groups = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Groups();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.groups = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});

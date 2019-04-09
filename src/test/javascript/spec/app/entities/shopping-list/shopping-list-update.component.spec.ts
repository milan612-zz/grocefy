/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { GrocefyTestModule } from '../../../test.module';
import { ShoppingListUpdateComponent } from 'app/entities/shopping-list/shopping-list-update.component';
import { ShoppingListService } from 'app/entities/shopping-list/shopping-list.service';
import { ShoppingList } from 'app/shared/model/shopping-list.model';

describe('Component Tests', () => {
    describe('ShoppingList Management Update Component', () => {
        let comp: ShoppingListUpdateComponent;
        let fixture: ComponentFixture<ShoppingListUpdateComponent>;
        let service: ShoppingListService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [GrocefyTestModule],
                declarations: [ShoppingListUpdateComponent]
            })
                .overrideTemplate(ShoppingListUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(ShoppingListUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ShoppingListService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new ShoppingList(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.shoppingList = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new ShoppingList();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.shoppingList = entity;
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

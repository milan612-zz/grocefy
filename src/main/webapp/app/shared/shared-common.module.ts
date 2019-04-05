import { NgModule } from '@angular/core';

import { GrocefySharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [GrocefySharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [GrocefySharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class GrocefySharedCommonModule {}

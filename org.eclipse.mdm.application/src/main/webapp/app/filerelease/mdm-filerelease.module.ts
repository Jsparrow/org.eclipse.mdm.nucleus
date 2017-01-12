import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMFilereleaseComponent } from './mdm-filerelease.component';
import { MDMFilereleaseCreateComponent } from './mdm-filerelease-create.component';
import { MDMFilereleaseDisplayComponent } from './mdm-filerelease-display.component';

@NgModule({
  imports: [
    MDMCoreModule
  ],
  declarations: [
    MDMFilereleaseComponent,
    MDMFilereleaseCreateComponent,
    MDMFilereleaseDisplayComponent,
  ],
  exports: [
    MDMFilereleaseComponent,
    MDMFilereleaseCreateComponent,
    MDMFilereleaseDisplayComponent,
  ]
})
export class MDMFilereleaseModule {
}

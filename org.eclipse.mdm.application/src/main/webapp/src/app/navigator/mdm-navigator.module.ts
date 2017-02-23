import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMNavigatorComponent } from './mdm-navigator.component';

@NgModule({
  imports: [
    MDMCoreModule
  ],
  declarations: [
    MDMNavigatorComponent
  ],
  exports: [
    MDMNavigatorComponent
  ]
})
export class MDMNavigatorModule {
}

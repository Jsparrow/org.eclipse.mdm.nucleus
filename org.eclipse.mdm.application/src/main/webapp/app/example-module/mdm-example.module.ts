import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMExampleComponent } from './mdm-example.component';

@NgModule({
  imports: [
    MDMCoreModule
  ],
  declarations: [
    MDMExampleComponent
  ]
})
export class MDMExampleModule {}

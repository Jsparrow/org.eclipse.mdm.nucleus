import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMExampleComponent } from './mdm-example.component';
import { MDMExampleRoutingModule } from './mdm-example-routing.module';

@NgModule({
  imports: [
    MDMCoreModule,
    MDMExampleRoutingModule
  ],
  declarations: [
    MDMExampleComponent
  ]
})
export class MDMExampleModule {}

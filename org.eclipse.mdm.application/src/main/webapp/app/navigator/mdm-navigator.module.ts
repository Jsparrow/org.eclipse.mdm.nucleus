import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMNavigatorComponent } from './mdm-navigator.component';
import { MDMNodeProviderComponent } from './mdm-node-provider.component';

@NgModule({
  imports: [
    MDMCoreModule
  ],
  declarations: [
    MDMNavigatorComponent,
    MDMNodeProviderComponent,
  ],
  exports: [
    MDMNavigatorComponent
  ]
})
export class MDMNavigatorModule {
}

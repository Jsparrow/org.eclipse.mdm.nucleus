import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMNavigatorViewRoutingModule } from './mdm-navigator-view-routing.module';

import { MDMNavigatorViewComponent } from './mdm-navigator-view.component';

import { MDMNavigatorModule } from '../navigator/mdm-navigator.module';
import { MDMModulesModule } from '../modules/mdm-modules.module';
import { MDMBasketModule } from '../basket/mdm-basket.module';

@NgModule({
  imports: [
    MDMCoreModule,
    MDMNavigatorViewRoutingModule,
    MDMNavigatorModule,
    MDMModulesModule,
    MDMBasketModule
  ],
  declarations: [
    MDMNavigatorViewComponent
  ],
  exports: [
    MDMNavigatorViewRoutingModule
  ]
})
export class MDMNavigatorViewModule {
}

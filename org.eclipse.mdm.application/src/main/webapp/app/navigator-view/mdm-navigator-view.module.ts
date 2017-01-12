import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMNavigatorViewRoutingModule } from './mdm-navigator-view-routing.module';

import { MDMNavigatorViewComponent } from './mdm-navigator-view.component';

import { MDMNavigatorModule } from '../navigator/mdm-navigator.module';
import { MDMDetailModule } from '../details/mdm-detail.module';
import { MDMFilereleaseModule } from '../filerelease/mdm-filerelease.module';
import { ModulesModule } from '../modules/modules.module';

@NgModule({
  imports: [
    MDMCoreModule,
    MDMNavigatorViewRoutingModule,
    MDMNavigatorModule,
    ModulesModule
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

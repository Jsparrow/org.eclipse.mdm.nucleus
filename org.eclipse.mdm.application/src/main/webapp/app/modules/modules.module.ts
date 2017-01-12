import { NgModule } from '@angular/core';


import { ModulesComponent } from './modules.component';
import { ModulesRoutingModule } from './modules-routing.module';

import { MDMCoreModule } from '../core/mdm-core.module';
import { MDMDetailModule } from '../details/mdm-detail.module';
import { MDMSearchModule } from '../search/mdm-search.module';
import { MDMFilereleaseModule } from '../filerelease/mdm-filerelease.module';
import { DummyModuleComponent } from '../dummy/dummy-module.component';

@NgModule({
  imports: [
    MDMCoreModule,
    ModulesRoutingModule,
    MDMDetailModule,
    MDMSearchModule,
    MDMFilereleaseModule,
  ],
  declarations: [
    ModulesComponent,
    DummyModuleComponent
  ],
  exports: [
    ModulesComponent,
  ]
})
export class ModulesModule {
}

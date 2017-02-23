import { NgModule } from '@angular/core';

import { MDMModulesComponent } from './mdm-modules.component';
import { MDMModulesRoutingModule } from './mdm-modules-routing.module';

import { MDMCoreModule } from '../core/mdm-core.module';
import { MDMDetailModule } from '../details/mdm-detail.module';
import { MDMSearchModule } from '../search/mdm-search.module';
import { MDMFilereleaseModule } from '../filerelease/mdm-filerelease.module';
import { MDMExampleModule } from '../example-module/mdm-example.module';

@NgModule({
  imports: [
    MDMCoreModule,
    MDMModulesRoutingModule,
    MDMDetailModule,
    MDMSearchModule,
    MDMFilereleaseModule,
    MDMExampleModule
  ],
  declarations: [
    MDMModulesComponent
  ],
  exports: [
    MDMModulesComponent,
  ]
})
export class MDMModulesModule {}

import { NgModule } from '@angular/core';

import { MDMDetailRoutingModule } from './mdm-detail-routing.module';

import { MDMCoreModule } from '../core/mdm-core.module';
import { MDMFilereleaseModule } from '../filerelease/mdm-filerelease.module';

import { MDMDetailComponent } from './mdm-detail.component';
import { MDMDetailViewComponent } from './mdm-detail-view.component';
import { MDMDescriptiveDataComponent } from './mdm-detail-descriptive-data.component';
import { DetailViewService } from './detail-view.service';


@NgModule({
  imports: [
    MDMDetailRoutingModule,
    MDMCoreModule,
    MDMFilereleaseModule
  ],
  declarations: [
    MDMDetailComponent,
    MDMDetailViewComponent,
    MDMDescriptiveDataComponent,
  ],
  exports: [
    MDMDetailComponent
  ],
  providers: [
    DetailViewService
  ]
})
export class MDMDetailModule {
}

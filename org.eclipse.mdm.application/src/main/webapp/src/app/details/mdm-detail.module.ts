/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

import { NgModule } from '@angular/core';

import { MDMDetailRoutingModule } from './mdm-detail-routing.module';

import { MDMCoreModule } from '../core/mdm-core.module';
// import { MDMFilereleaseModule } from '../filerelease/mdm-filerelease.module';

import { MDMDetailComponent } from './mdm-detail.component';
import { MDMDetailViewComponent } from './mdm-detail-view.component';
import { MDMDescriptiveDataComponent } from './mdm-detail-descriptive-data.component';
import { DetailViewService } from './detail-view.service';
import { SensorComponent } from './sensor.component';
import { ContextService } from './context.service';

@NgModule({
  imports: [
    MDMDetailRoutingModule,
    MDMCoreModule,
    // MDMFilereleaseModule
  ],
  declarations: [
    MDMDetailComponent,
    MDMDetailViewComponent,
    MDMDescriptiveDataComponent,
    SensorComponent,
  ],
  exports: [
    MDMDetailComponent
  ],
  providers: [
    DetailViewService,
    ContextService
  ]
})
export class MDMDetailModule {
}

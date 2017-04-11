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
import { RouterModule, Routes } from '@angular/router';

import { MDMDetailComponent } from './mdm-detail.component';
import { MDMDetailViewComponent } from './mdm-detail-view.component';
import { MDMDescriptiveDataComponent } from './mdm-detail-descriptive-data.component';
import { SensorComponent } from './sensor.component';

const detailRoutes: Routes = [
  { path: '',  component: MDMDetailComponent, children: [
    { path: '', redirectTo: 'general', pathMatch: 'full' },
    { path: 'general',  component: MDMDetailViewComponent },
    { path: 'sensors',  component: SensorComponent },
    { path: ':context', component: MDMDescriptiveDataComponent }
  ]}
];

@NgModule({
  imports: [
    RouterModule.forChild(detailRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class MDMDetailRoutingModule {}

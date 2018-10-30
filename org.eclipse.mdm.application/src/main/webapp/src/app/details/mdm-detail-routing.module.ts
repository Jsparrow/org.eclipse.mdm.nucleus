/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


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

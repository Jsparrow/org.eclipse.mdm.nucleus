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

import { MDMModulesComponent } from '../modules/mdm-modules.component';
import { MDMSearchComponent } from '../search/mdm-search.component';

const moduleRoutes: Routes = [
  { path: '', component: MDMModulesComponent, children: [
    { path: '', redirectTo: 'details', pathMatch: 'full' },
    { path: 'details', loadChildren: '../details/mdm-detail.module#MDMDetailModule'},
    { path: 'search', component: MDMSearchComponent },
  ]}
];

@NgModule({
  imports: [
    RouterModule.forChild(moduleRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class MDMModulesRoutingModule {}

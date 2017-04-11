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

import { MDMNavigatorViewComponent } from './mdm-navigator-view.component';
import { MDMDetailComponent } from '../details/mdm-detail.component';
import { MDMSearchComponent } from '../search/mdm-search.component';
import { MDMModulesComponent } from '../modules/mdm-modules.component';

const navigatorViewRoutes: Routes = [
  { path: 'navigator', component: MDMNavigatorViewComponent,
    children: [
      { path: '**', component: MDMModulesComponent },
      { path: 'details', component: MDMDetailComponent},
      { path: 'modules', component: MDMModulesComponent, children: [
        { path: 'details', component: MDMDetailComponent},
        { path: 'filerelease', component: MDMDetailComponent},
        { path: 'search',  component: MDMSearchComponent }
      ]}
    ]}
];

@NgModule({
  imports: [
    RouterModule.forChild(navigatorViewRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class MDMNavigatorViewRoutingModule {}

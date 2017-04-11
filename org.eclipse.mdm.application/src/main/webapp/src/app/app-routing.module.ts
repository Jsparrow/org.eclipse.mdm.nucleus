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

import { NgModule, Component } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MDMNavigatorViewComponent } from './navigator-view/mdm-navigator-view.component';
import { AdminModulesComponent } from './administration/admin-modules.component';

const appRoutes: Routes = [
  { path: '', redirectTo: 'navigator', pathMatch: 'full' },
  { path: 'navigator', component: MDMNavigatorViewComponent, loadChildren: './modules/mdm-modules.module#MDMModulesModule' },
  { path: 'administration', component: AdminModulesComponent, loadChildren: './administration/admin.module#AdminModule' }
];

@NgModule({
  imports: [
    RouterModule.forRoot(appRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {}

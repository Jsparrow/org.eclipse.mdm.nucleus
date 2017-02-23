import { NgModule, Component } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MDMNavigatorViewComponent } from './navigator-view/mdm-navigator-view.component';
import { AdminModulesComponent } from './administration/admin-modules.component';

const appRoutes: Routes = [
  { path: '', redirectTo: 'navigator', pathMatch: 'full' },
  { path: 'navigator', component: MDMNavigatorViewComponent, loadChildren: './modules/mdm-modules.module#MDMModulesModule' },
  { path: 'administration', component: AdminModulesComponent/*, loadChildren: './administration/admin.module#AdminModule'*/ }
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

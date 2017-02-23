import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MDMModulesComponent } from '../modules/mdm-modules.component';
import { MDMSearchComponent } from '../search/mdm-search.component';
import { MDMFilereleaseComponent } from '../filerelease/mdm-filerelease.component';

const moduleRoutes: Routes = [
  { path: '', component: MDMModulesComponent, children: [
    { path: '', redirectTo: 'details', pathMatch: 'full' },
    { path: 'details', loadChildren: '../details/mdm-detail.module#MDMDetailModule'},
    { path: 'search', component: MDMSearchComponent },
    { path: 'filerelease', component: MDMFilereleaseComponent },
    { path: 'example', loadChildren: '../example-module/mdm-example.module#MDMExampleModule'},
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

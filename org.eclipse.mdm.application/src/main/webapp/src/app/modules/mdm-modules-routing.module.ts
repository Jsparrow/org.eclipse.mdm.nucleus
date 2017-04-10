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

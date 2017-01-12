import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ModulesComponent } from '../modules/modules.component';

import { MDMDetailComponent }    from '../details/mdm-detail.component';
import { MDMSearchComponent } from '../search/mdm-search.component';
import { MDMFilereleaseComponent } from '../filerelease/mdm-filerelease.component';
import { DummyModuleComponent } from '../dummy/dummy-module.component';

import { MDMDetailModule } from '../details/mdm-detail.module';

const moduleRoutes: Routes = [
  { path: '', component: ModulesComponent, children: [
    { path: '', redirectTo: 'details', pathMatch: 'full' },
    { path: 'details', loadChildren: '../details/mdm-detail.module#MDMDetailModule'},
    { path: 'search', component: MDMSearchComponent },
    { path: 'filerelease', component: MDMFilereleaseComponent },
    { path: 'dummy', component: DummyModuleComponent }
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
export class ModulesRoutingModule {}

import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MDMExampleComponent } from './mdm-example.component';

const moduleRoutes: Routes = [
  { path: '', component: MDMExampleComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(moduleRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class MDMExampleRoutingModule {}

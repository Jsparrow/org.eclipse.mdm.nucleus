import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PreferenceComponent } from './preference.component';

const moduleRoutes: Routes = [
  { path: '',
      children: [
        { path: '', redirectTo: 'system', pathMatch: 'full' },
        { path: ':scope', component: PreferenceComponent}
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

export class AdminRoutingModule {}

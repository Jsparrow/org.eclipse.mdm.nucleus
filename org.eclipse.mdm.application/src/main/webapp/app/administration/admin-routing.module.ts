import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AdminModulesComponent } from './admin-modules.component';
import { AdminSystemComponent } from './admin-system.component';
import { AdminSourceComponent } from './admin-source.component';
import { AdminUserComponent } from './admin-user.component';

const moduleRoutes: Routes = [
  { path: '',
      children: [
        { path: '', redirectTo: 'system', pathMatch: 'full' },
        { path: 'system', component: AdminSystemComponent},
        { path: 'source', component: AdminSourceComponent },
        { path: 'user', component: AdminUserComponent }
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

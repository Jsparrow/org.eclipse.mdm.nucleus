import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MDMDetailComponent } from './mdm-detail.component';
import { MDMDetailViewComponent } from './mdm-detail-view.component';
import { MDMDescriptiveDataComponent } from './mdm-detail-descriptive-data.component';

const detailRoutes: Routes = [
  { path: '',  component: MDMDetailComponent, children: [
    { path: '', redirectTo: 'general', pathMatch: 'full' },
    { path: 'general',  component: MDMDetailViewComponent },
    { path: ':context', component: MDMDescriptiveDataComponent }
  ]}
];

@NgModule({
  imports: [
    RouterModule.forChild(detailRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class MDMDetailRoutingModule {}

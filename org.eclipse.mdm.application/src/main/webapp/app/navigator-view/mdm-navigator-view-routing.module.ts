import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MDMNavigatorViewComponent }    from './mdm-navigator-view.component';
import { MDMDetailComponent }    from '../details/mdm-detail.component';
import { MDMSearchComponent }    from '../search/mdm-search.component';
import { ModulesComponent }    from '../modules/modules.component';

const navigatorViewRoutes: Routes = [
  { path: 'navigator', component: MDMNavigatorViewComponent,
    //loadChildren: '../modules/modules.module#ModulesModule'}
    children: [
      { path: '**', component: ModulesComponent },
      { path: 'details', component: MDMDetailComponent},
      { path: 'modules', component: ModulesComponent, children: [
        { path: 'details', component: MDMDetailComponent},
        { path: 'filerelease', component: MDMDetailComponent},
        { path: 'search',  component: MDMSearchComponent }
      ]}
    ]}
  // loadChildren: '../details/mdm-detail.module#MDMDetailModule'}
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

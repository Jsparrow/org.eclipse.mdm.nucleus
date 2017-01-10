import { RouterModule, Routes } from '@angular/router';

import { MDMMenuComponent } from './navigator/mdm-menu.component';
import { MDMDetailComponent } from './details/mdm-detail.component';
import { MDMSearchComponent } from './search/mdm-search.component';

const routes: Routes = [
  { path: '', redirectTo: 'mdmmenu', pathMatch: 'full' },
  {path: 'mdmmenu', component: MDMMenuComponent},
  {path: 'details', component: MDMDetailComponent},
  {path: 'search', component: MDMSearchComponent},
];

export const routing = RouterModule.forRoot(routes);

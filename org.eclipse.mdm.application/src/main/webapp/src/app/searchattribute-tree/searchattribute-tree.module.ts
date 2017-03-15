import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';
import { MDMNavigatorModule } from '../navigator/mdm-navigator.module';
import { MDMSearchModule } from '../search/mdm-search.module';


import { SearchattributeTreeService } from './searchattribute-tree.service';
import { SearchattributeTreeComponent } from './searchattribute-tree.component';

@NgModule({
  imports: [
    MDMCoreModule,
    MDMNavigatorModule,
    // MDMSearchModule
  ],
  declarations: [
    SearchattributeTreeComponent,
  ],
  exports: [
    SearchattributeTreeComponent,
  ],
  providers: [
    SearchattributeTreeService
  ]
})
export class SearchattributeTreeModule {
}

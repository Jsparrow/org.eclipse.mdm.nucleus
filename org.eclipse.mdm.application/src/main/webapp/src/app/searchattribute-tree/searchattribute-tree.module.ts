import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';
import { MDMNavigatorModule } from '../navigator/mdm-navigator.module';

import { SearchattributeTreeComponent } from './searchattribute-tree.component';

@NgModule({
  imports: [
    MDMCoreModule,
    MDMNavigatorModule,
  ],
  declarations: [
    SearchattributeTreeComponent,
  ],
  exports: [
    SearchattributeTreeComponent,
  ]
})
export class SearchattributeTreeModule {
}

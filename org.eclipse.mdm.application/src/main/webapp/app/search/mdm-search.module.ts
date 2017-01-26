import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMSearchComponent } from './mdm-search.component';
import { SearchConditionComponent } from './search-condition.component';
import { MDMFullTextSearchComponent } from './mdm-full-text-search.component';
import { DynamicForm } from './dynamic-form.component';

import { TableViewModule } from '../tableview/tableview.module';

@NgModule({
  imports: [
    MDMCoreModule,
    TableViewModule
  ],
  declarations: [
    MDMSearchComponent,
    SearchConditionComponent
  ],
  exports: [
    MDMSearchComponent
  ]
})
export class MDMSearchModule {
}

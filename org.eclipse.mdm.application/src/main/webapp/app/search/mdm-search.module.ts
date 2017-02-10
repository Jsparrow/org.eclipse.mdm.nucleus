import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMSearchComponent } from './mdm-search.component';
import { SearchConditionComponent } from './search-condition.component';
import { DynamicForm } from './dynamic-form.component';

import { TableViewModule } from '../tableview/tableview.module';
import {SearchService} from './search.service';
import {SearchDeprecatedService} from './search-deprecated.service';
import {FilterService} from './filter.service';

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
  ],
  providers: [
    SearchService,
    FilterService,
    SearchDeprecatedService
  ],
})
export class MDMSearchModule {
}

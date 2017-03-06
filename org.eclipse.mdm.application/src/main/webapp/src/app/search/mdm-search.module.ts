import { NgModule } from '@angular/core';

import { DatePipe } from '@angular/common';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMSearchComponent } from './mdm-search.component';
import { SearchConditionComponent } from './search-condition.component';
import { SearchDatepickerComponent } from './search-datepicker.component';
import { EditSearchFieldsComponent } from './edit-searchFields.component';

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
    SearchConditionComponent,
    SearchDatepickerComponent,
    EditSearchFieldsComponent
  ],
  exports: [
    MDMSearchComponent,
    EditSearchFieldsComponent
  ],
  providers: [
    SearchService,
    FilterService,
    SearchDeprecatedService,
    DatePipe
  ],
})
export class MDMSearchModule {
}

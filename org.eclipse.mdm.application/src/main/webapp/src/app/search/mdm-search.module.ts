/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

import { NgModule } from '@angular/core';
import { DatePipe } from '@angular/common';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMSearchComponent } from './mdm-search.component';
import { SearchConditionComponent } from './search-condition.component';
import { SearchDatepickerComponent } from './search-datepicker.component';
import { EditSearchFieldsComponent } from './edit-searchFields.component';

import { TableViewModule } from '../tableview/tableview.module';
import {SearchService} from './search.service';
import {FilterService} from './filter.service';

import {SearchattributeTreeModule} from '../searchattribute-tree/searchattribute-tree.module';
import {AutoCompleteModule} from 'primeng/primeng';

@NgModule({
  imports: [
    MDMCoreModule,
    TableViewModule,
    SearchattributeTreeModule,
    AutoCompleteModule
  ],
  declarations: [
    MDMSearchComponent,
    SearchConditionComponent,
    SearchDatepickerComponent,
    EditSearchFieldsComponent,
  ],
  exports: [
    MDMSearchComponent,
    EditSearchFieldsComponent
  ],
  providers: [
    SearchService,
    FilterService,
    DatePipe
  ],
})
export class MDMSearchModule {
}

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

import { MDMCoreModule } from '../core/mdm-core.module';

import { TableviewComponent } from './tableview.component';
import { EditViewComponent } from './editview.component';
import { ViewComponent } from './view.component';
import {SearchattributeTreeModule} from '../searchattribute-tree/searchattribute-tree.module';

import {DataTableModule, SharedModule} from 'primeng/primeng';

@NgModule({
  imports: [
    MDMCoreModule,
    SearchattributeTreeModule,
    DataTableModule,
    SharedModule
  ],
  declarations: [
    TableviewComponent,
    EditViewComponent,
    ViewComponent
  ],
  exports: [
    TableviewComponent,
    EditViewComponent,
    ViewComponent,
  ],
  providers: [
  ]
})
export class TableViewModule {
}

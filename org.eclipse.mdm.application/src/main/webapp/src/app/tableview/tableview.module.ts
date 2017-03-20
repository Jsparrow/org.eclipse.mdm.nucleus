import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { TableviewComponent } from './tableview.component';
import { EditViewComponent } from './editview.component';
import { ViewComponent } from './view.component';
import { ViewService } from './tableview.service';
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
    ViewComponent
  ],
  providers: [
    ViewService
  ]
})
export class TableViewModule {
}

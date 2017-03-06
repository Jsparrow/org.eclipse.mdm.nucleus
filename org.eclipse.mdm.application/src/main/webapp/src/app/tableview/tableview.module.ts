import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { TableviewComponent } from './tableview.component';
import { EditViewComponent } from './editview.component';
import { ViewComponent } from './view.component';
import { ViewService } from './tableview.service';
import { Ng2PaginationModule } from 'ng2-pagination';

@NgModule({
  imports: [
    MDMCoreModule,
    Ng2PaginationModule
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

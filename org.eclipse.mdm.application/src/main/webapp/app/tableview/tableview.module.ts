import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { TableviewComponent } from './tableview.component';
import { EditViewComponent } from './editview.component';
import { ViewService } from './tableview.service';


@NgModule({
  imports: [
    MDMCoreModule,
  ],
  declarations: [
    TableviewComponent,
    EditViewComponent,
  ],
  exports: [
    TableviewComponent,
    EditViewComponent
  ],
  providers: [
    ViewService
  ]
})
export class TableViewModule {
}

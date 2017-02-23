import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { TableviewComponent } from './tableview.component';
import { EditViewComponent } from './editview.component';
import { ViewComponent } from './view.component';
import { ViewService } from './tableview.service';

@NgModule({
  imports: [
    MDMCoreModule,
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

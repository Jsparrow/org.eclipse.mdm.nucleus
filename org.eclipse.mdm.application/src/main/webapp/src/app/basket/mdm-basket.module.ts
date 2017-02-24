import { NgModule } from '@angular/core';

import { MDMBasketComponent } from './mdm-basket.component';

import { MDMCoreModule } from '../core/mdm-core.module';
import { TableViewModule } from '../tableview/tableview.module';

@NgModule({
  imports: [
    MDMCoreModule,
    TableViewModule
  ],
  declarations: [ MDMBasketComponent ],
  exports: [ MDMBasketComponent],
})
export class MDMBasketModule { }

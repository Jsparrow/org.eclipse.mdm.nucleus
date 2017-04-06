import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';

import { Ng2BootstrapModule } from 'ng2-bootstrap';
import { TypeaheadModule } from 'ng2-bootstrap';
import { DatepickerModule } from 'ng2-bootstrap';
import { TabsModule } from 'ng2-bootstrap/tabs';
import { PositioningService } from 'ng2-bootstrap/positioning';
import { ComponentLoaderFactory } from 'ng2-bootstrap/component-loader';

import { DropdownMultiselectModule } from 'ng2-dropdown-multiselect';
import { TreeModule, DataTableModule, SharedModule, ContextMenuModule, GrowlModule } from 'primeng/primeng';
import { PropertyService } from './property.service';
import { PreferenceService } from './preference.service';

import { MDMNotificationComponent } from './mdm-notification.component';

@NgModule({
  imports: [
    HttpModule,
    FormsModule,
    CommonModule,
    Ng2BootstrapModule,
    TabsModule.forRoot(),
    TypeaheadModule.forRoot(),
    DatepickerModule.forRoot(),
    DropdownMultiselectModule,
    TreeModule,
    DataTableModule,
    SharedModule,
    ContextMenuModule,
    GrowlModule
  ],
  declarations: [
    MDMNotificationComponent
  ],
  exports: [
    CommonModule,
    FormsModule,
    Ng2BootstrapModule,
    DropdownMultiselectModule,
    TreeModule,
    DataTableModule,
    SharedModule,
    ContextMenuModule,
    GrowlModule,
    MDMNotificationComponent
  ],
  providers: [
      PositioningService,
      ComponentLoaderFactory,

      PropertyService,
      PreferenceService
    ],
})
export class MDMCoreModule { }

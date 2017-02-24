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
import { RlTagInputModule } from 'angular2-tag-input';
import { TreeModule } from 'primeng/primeng';

import { PropertyService } from './property.service';
import { PreferenceService } from './preference.service';
import { FilterService } from './filter.service';

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
    RlTagInputModule,
    TreeModule
  ],
  declarations: [  ],
  exports: [
    CommonModule,
    FormsModule,
    Ng2BootstrapModule,
    DropdownMultiselectModule,
    RlTagInputModule,
    TreeModule
  ],
  providers: [
      PositioningService,
      ComponentLoaderFactory,

      PropertyService,
      PreferenceService,
      FilterService
    ],
})
export class MDMCoreModule { }

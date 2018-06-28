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
import { OverwriteDialogComponent } from './overwrite-dialog.component';

import { TranslationPipe } from '../localization/translation.pipe';

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
    MDMNotificationComponent,
    OverwriteDialogComponent,
    TranslationPipe
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
    MDMNotificationComponent,
    OverwriteDialogComponent,
    TranslationPipe
  ],
  providers: [
      PositioningService,
      ComponentLoaderFactory,

      PropertyService,
      PreferenceService
    ],
})
export class MDMCoreModule { }

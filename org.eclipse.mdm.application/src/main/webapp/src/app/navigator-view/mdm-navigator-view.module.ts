/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


import { NgModule } from '@angular/core';

import { MDMCoreModule } from '../core/mdm-core.module';

import { MDMNavigatorViewRoutingModule } from './mdm-navigator-view-routing.module';

import { MDMNavigatorViewComponent } from './mdm-navigator-view.component';

import { MDMNavigatorModule } from '../navigator/mdm-navigator.module';
import { MDMModulesModule } from '../modules/mdm-modules.module';
import { MDMBasketModule } from '../basket/mdm-basket.module';
import { SplitPaneModule } from 'ng2-split-pane/lib/ng2-split-pane';

@NgModule({
  imports: [
    MDMCoreModule,
    MDMNavigatorViewRoutingModule,
    MDMNavigatorModule,
    MDMModulesModule,
    MDMBasketModule,
    SplitPaneModule
  ],
  declarations: [
    MDMNavigatorViewComponent
  ],
  exports: [
    MDMNavigatorViewRoutingModule
  ]
})
export class MDMNavigatorViewModule {
}

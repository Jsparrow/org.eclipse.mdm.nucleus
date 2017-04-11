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

import { MDMNavigatorViewRoutingModule } from './mdm-navigator-view-routing.module';

import { MDMNavigatorViewComponent } from './mdm-navigator-view.component';

import { MDMNavigatorModule } from '../navigator/mdm-navigator.module';
import { MDMModulesModule } from '../modules/mdm-modules.module';
import { MDMBasketModule } from '../basket/mdm-basket.module';

// import { SplitPaneModule } from 'ng2-split-pane/lib/ng2-split-pane';

@NgModule({
  imports: [
    MDMCoreModule,
    MDMNavigatorViewRoutingModule,
    MDMNavigatorModule,
    MDMModulesModule,
    MDMBasketModule,
    // SplitPaneModule
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

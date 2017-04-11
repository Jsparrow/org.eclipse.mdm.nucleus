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

import { MDMNavigatorComponent } from './mdm-navigator.component';

@NgModule({
  imports: [
    MDMCoreModule
  ],
  declarations: [
    MDMNavigatorComponent
  ],
  exports: [
    MDMNavigatorComponent
  ]
})
export class MDMNavigatorModule {
}

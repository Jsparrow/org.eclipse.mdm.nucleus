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
import { MDMExampleComponent } from './mdm-example.component';
import { MDMExampleRoutingModule } from './mdm-example-routing.module';

@NgModule({
  imports: [
    MDMCoreModule,
    MDMExampleRoutingModule
  ],
  declarations: [
    MDMExampleComponent
  ]
})
export class MDMExampleModule {}

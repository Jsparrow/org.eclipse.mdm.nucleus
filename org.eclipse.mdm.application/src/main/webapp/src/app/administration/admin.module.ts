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
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ComponentLoaderFactory } from 'ngx-bootstrap/component-loader';

import { PreferenceService } from '../core/preference.service';
import { MDMCoreModule } from '../core/mdm-core.module';

import { AdminModulesComponent } from './admin-modules.component';
import { AdminRoutingModule } from './admin-routing.module';
import { PreferenceComponent } from './preference.component';
import { EditPreferenceComponent } from './edit-preference.component';

@NgModule( {
    imports: [
        AdminRoutingModule,
        MDMCoreModule,
        FormsModule,
        ReactiveFormsModule
    ],
    declarations: [
        PreferenceComponent,
        EditPreferenceComponent,
        AdminModulesComponent,
    ],
    exports: [
        AdminModulesComponent,
    ],
    providers: [
        ComponentLoaderFactory,
        PreferenceService
    ],
})
export class AdminModule { }

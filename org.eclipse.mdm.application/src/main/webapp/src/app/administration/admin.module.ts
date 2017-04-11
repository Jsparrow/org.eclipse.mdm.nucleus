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
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ComponentLoaderFactory } from 'ng2-bootstrap/component-loader';

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

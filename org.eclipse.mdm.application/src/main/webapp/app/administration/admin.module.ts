import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';


import { ComponentLoaderFactory } from 'ng2-bootstrap/component-loader';

import { PreferenceService } from '../core/preference.service';

import { AdminModulesComponent } from './admin-modules.component';
import { MDMCoreModule } from '../core/mdm-core.module';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminSourceComponent } from './admin-source.component';
import { AdminSystemComponent } from './admin-system.component';
import { AdminUserComponent } from './admin-user.component';
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
        AdminSystemComponent,
        AdminSourceComponent,
        AdminUserComponent,
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

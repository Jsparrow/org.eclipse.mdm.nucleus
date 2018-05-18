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

import { BrowserModule } from '@angular/platform-browser';
import { NgModule, ErrorHandler } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppRoutingModule } from './app-routing.module';
import { NoticeComponent } from './notice.component';

import { DialogModule } from 'primeng/primeng';
import { MDMCoreModule} from './core/mdm-core.module';
import { MDMNavigatorViewModule } from './navigator-view/mdm-navigator-view.module';
import { AdminModule } from './administration/admin.module';

import { AppComponent } from './app.component';

import {NodeService} from './navigator/node.service';
import {BasketService} from './basket/basket.service';
import {LocalizationService} from './localization/localization.service';
import {FilereleaseService} from './filerelease/filerelease.service';
import {NavigatorService} from './navigator/navigator.service';
import {QueryService} from './tableview/query.service';
import {ViewService} from './tableview/tableview.service';
import {NodeproviderService} from './navigator/nodeprovider.service';
import { SearchattributeTreeComponent } from './searchattribute-tree/searchattribute-tree.component';
import { MDMNotificationService } from './core/mdm-notification.service';
import { MDMErrorHandler } from './core/mdm-error-handler';
import { HttpErrorHandler } from './core/http-error-handler';

@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    AppRoutingModule,
    MDMCoreModule,
    MDMNavigatorViewModule,
    AdminModule,
    DialogModule
  ],
  declarations: [
    AppComponent,
    NoticeComponent
  ],
  providers: [
    NodeService,
    LocalizationService,
    FilereleaseService,
    BasketService,
    NavigatorService,
    QueryService,
    NodeproviderService,
    MDMNotificationService,
    ViewService,
    HttpErrorHandler,
    { provide: ErrorHandler, useClass: MDMErrorHandler }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

}

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppRoutingModule } from './app-routing.module';

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
import {NodeproviderService} from './navigator/nodeprovider.service';

@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    AppRoutingModule,
    MDMCoreModule,
    MDMNavigatorViewModule,
    AdminModule
  ],
  declarations: [
    AppComponent,
  ],
  providers: [
    NodeService,
    LocalizationService,
    FilereleaseService,
    BasketService,
    NavigatorService,
    QueryService,
    NodeproviderService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
    
    }

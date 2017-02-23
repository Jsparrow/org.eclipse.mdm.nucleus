import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { MDMNavigatorViewComponent } from './navigator-view/mdm-navigator-view.component';
import { MDMNavigatorComponent } from './navigator/mdm-navigator.component';

import { MDMBasketComponent } from './basket/mdm-basket.component';

import { AppRoutingModule } from './app-routing.module';

import { Ng2BootstrapModule } from 'ng2-bootstrap';
import { TabsModule } from 'ng2-bootstrap/tabs';
import { PositioningService } from 'ng2-bootstrap/positioning';
import { ComponentLoaderFactory } from 'ng2-bootstrap/component-loader';
import { TypeaheadModule } from 'ng2-bootstrap';
import { DatepickerModule } from 'ng2-bootstrap';

import { AdminModule } from './administration/admin.module';
import { MDMCoreModule} from './core/mdm-core.module';
import { MDMBasketModule } from './basket/mdm-basket.module';

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
    Ng2BootstrapModule,
    TabsModule.forRoot(),
    AdminModule,
    MDMCoreModule,
    MDMBasketModule,
    TypeaheadModule.forRoot(),
    DatepickerModule.forRoot()
  ],
  declarations: [
    AppComponent,
    MDMNavigatorViewComponent,
    MDMNavigatorComponent
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
export class AppModule { }

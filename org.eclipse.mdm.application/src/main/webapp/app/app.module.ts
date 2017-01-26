import { NgModule, ApplicationRef } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';

import { MDMComponent } from './mdm.component';

import { MDMNavigatorViewComponent } from './navigator-view/mdm-navigator-view.component';
import { MDMNavigatorComponent } from './navigator/mdm-navigator.component';
import { MDMNodeProviderComponent } from './navigator/mdm-node-provider.component';

import { MDMBasketComponent } from './basket/mdm-basket.component';

import { AppRoutingModule } from './app-routing.module';
import { removeNgStyles, createNewHosts } from '@angularclass/hmr';

import { Ng2BootstrapModule } from 'ng2-bootstrap';
import { TabsModule } from 'ng2-bootstrap/tabs';
import { PositioningService } from 'ng2-bootstrap/positioning';
import { ComponentLoaderFactory } from 'ng2-bootstrap/component-loader';
import { TypeaheadModule } from 'ng2-bootstrap';

import {NodeService} from './navigator/node.service';
import {BasketService} from './basket/basket.service';
import {LocalizationService} from './localization/localization.service';
import {FilereleaseService} from './filerelease/filerelease.service';

import { AdminModule } from './administration/admin.module';
import { MDMCoreModule} from './core/mdm-core.module';
import { MDMBasketModule } from './basket/mdm-basket.module';

import {NavigatorService} from './navigator/navigator.service';
import {QueryService} from './tableview/query.service';

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
    TypeaheadModule.forRoot()
  ],
  declarations: [
    MDMComponent,
    MDMNavigatorViewComponent,
    MDMNavigatorComponent,
    MDMNodeProviderComponent
  ],
  providers: [
    NodeService, LocalizationService, FilereleaseService, BasketService, NavigatorService, QueryService
  ],
  bootstrap: [MDMComponent]
})
export class AppModule {
  constructor(public appRef: ApplicationRef) {}
  hmrOnInit(store) {
    console.log('HMR store', store);
  }
  hmrOnDestroy(store) {
    let cmpLocation = this.appRef.components.map(cmp => cmp.location.nativeElement);
    // recreate elements
    store.disposeOldHosts = createNewHosts(cmpLocation);
    // remove styles
    removeNgStyles();
  }
  hmrAfterDestroy(store) {
    // display new elements
    store.disposeOldHosts();
    delete store.disposeOldHosts;
  }
}

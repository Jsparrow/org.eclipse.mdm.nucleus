import { NgModule, ApplicationRef } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { MDMComponent } from './mdm.component';

import { MDMNavigatorViewModule } from './navigator-view/mdm-navigator-view.module';

import { MDMBasketComponent } from './basket/mdm-basket.component';

import { MDMCoreModule } from './core/mdm-core.module';

import { TableViewModule } from './tableview/tableview.module';

import { AppRoutingModule } from './app-routing.module';

import { removeNgStyles, createNewHosts } from '@angularclass/hmr';

import {NodeService} from './navigator/node.service';
import {BasketService} from './basket/basket.service';
import {LocalizationService} from './localization/localization.service';
import {FilereleaseService} from './filerelease/filerelease.service';

import { MDMNavigatorViewComponent } from './navigator-view/mdm-navigator-view.component';
import { MDMNavigatorComponent } from './navigator/mdm-navigator.component';
import { MDMDetailComponent }    from './details/mdm-detail.component';
import { MDMSearchComponent } from './search/mdm-search.component';
import { ModulesComponent } from './modules/modules.component';
import { MDMNodeProviderComponent } from './navigator/mdm-node-provider.component';

@NgModule({
  imports: [
    BrowserModule,
    AppRoutingModule,
    //MDMNavigatorViewModule,
    MDMCoreModule,
    TableViewModule
  ],
  declarations: [
    MDMComponent,
    MDMBasketComponent,

    MDMNodeProviderComponent,
    MDMNavigatorViewComponent,
    MDMNavigatorComponent,
    MDMDetailComponent,
    MDMSearchComponent,
    ModulesComponent

  ],
  providers: [NodeService, LocalizationService, FilereleaseService, BasketService],
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

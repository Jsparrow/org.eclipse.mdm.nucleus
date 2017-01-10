import { NgModule, ApplicationRef } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';

import { MDMComponent } from './mdm.component';
import { ModulesComponent } from './modules/modules.component';

import { MDMMenuComponent } from './navigator/mdm-menu.component';
import { MDMNavigatorComponent } from './navigator/mdm-navigator.component';
import { MDMNodeProviderComponent } from './navigator/mdm-node-provider.component';

import { MDMBasketComponent } from './basket/mdm-basket.component';

import { MDMDetailComponent } from './details/mdm-detail.component';
import { MDMDetailViewComponent } from './details/mdm-detail-view.component';
import { MDMDescriptiveDataComponent } from './details/mdm-detail-descriptive-data.component';

import { MDMSearchComponent } from './search/mdm-search.component';

import { MDMFilereleaseComponent } from './filerelease/mdm-filerelease.component';
import { MDMFilereleaseCreateComponent } from './filerelease/mdm-filerelease-create.component';
import { MDMFilereleaseDisplayComponent } from './filerelease/mdm-filerelease-display.component';

import { TableviewComponent } from './tableview/tableview.component';
import { EditViewComponent } from './tableview/editview.component';

import { DummyModuleComponent } from './dummy/dummy-module.component';

import { routing } from './app.routing';

import { removeNgStyles, createNewHosts } from '@angularclass/hmr';

import { Ng2BootstrapModule } from 'ng2-bootstrap';
import { PositioningService } from 'ng2-bootstrap/positioning';
import { ComponentLoaderFactory } from 'ng2-bootstrap/component-loader';

@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    routing,
    Ng2BootstrapModule
  ],
  declarations: [
    MDMComponent,
    ModulesComponent,
    MDMMenuComponent,
    MDMNavigatorComponent,
    MDMNodeProviderComponent,
    MDMBasketComponent,
    MDMDetailComponent,
    MDMDetailViewComponent,
    MDMDescriptiveDataComponent,
    MDMSearchComponent,
    MDMFilereleaseComponent,
    MDMFilereleaseCreateComponent,
    MDMFilereleaseDisplayComponent,
    TableviewComponent,
    EditViewComponent,
    DummyModuleComponent
  ],
  providers: [
    // ApiService
    PositioningService,
    ComponentLoaderFactory
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

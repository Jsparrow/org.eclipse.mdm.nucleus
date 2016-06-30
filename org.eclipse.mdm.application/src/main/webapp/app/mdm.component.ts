// *******************************************************************************
//   * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
//   * All rights reserved. This program and the accompanying materials
//   * are made available under the terms of the Eclipse Public License v1.0
//   * which accompanies this distribution, and is available at
//   * http://www.eclipse.org/legal/epl-v10.html
//   *
//   * Contributors:
//   * Dennis Schroeder - initial implementation
//   *******************************************************************************
import {Component, OnInit, ViewContainerRef} from '@angular/core';
import {HTTP_PROVIDERS} from '@angular/http';
import {Routes, Router, ROUTER_DIRECTIVES} from '@angular/router';

import {MDMMenuComponent} from './navigator/mdm-menu.component';
import {MDMSearchComponent} from './search/mdm-search.component';
import {MDMFullTextSearchComponent} from './search/mdm-full-text-search.component';
import {MDMFilereleaseComponent} from './filerelease/mdm-filerelease.component';

import {NodeService} from './navigator/node.service';
import {BasketService} from './basket/basket.service';
import {LocalizationService} from './localization/localization.service';
import {PropertyService} from './properties';
import {FilereleaseService} from './filerelease/filerelease.service';

@Component({
  selector: 'mdm-web',
  template: require('../templates/mdm.component.html'),
  directives: [ROUTER_DIRECTIVES],
  providers: [HTTP_PROVIDERS, NodeService, LocalizationService, FilereleaseService, PropertyService, BasketService]
})
@Routes([
  {path: '/mdmmenu', component: MDMMenuComponent},
  {path: '/mdmsearch', component: MDMSearchComponent},
  {path: '/mdmfulltextsearch', component: MDMFullTextSearchComponent},
  {path: '/mdmapproval', component: MDMFilereleaseComponent}
])
export class MDMComponent implements OnInit{
  brand = 'openMDM5 Web';
  _comp: string = "MDMMenu";
  viewContainerRef;

  constructor(private router: Router,
              viewContainerRef:ViewContainerRef){
                this.viewContainerRef = viewContainerRef;
              }

  ngOnInit(){
    this.router.navigate(['/mdmmenu'])
  }

  activate(comp: string){
    this._comp = comp;
  }
  isActive(comp: string){
    if (comp === this._comp) {
      return 'active';
    }
  }
}

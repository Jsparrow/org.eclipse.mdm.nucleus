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
import {Component} from 'angular2/core';
import {HTTP_PROVIDERS} from 'angular2/http';
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';

import {MDMMenuComponent} from './mdm-menu.component';
import {NodeService} from './node.service';
import {LocalizationService} from './localization.service';
import {PropertyService} from './properties';

@Component({
  selector: 'mdm-web',
  templateUrl: 'templates/mdm.component.html',
  directives: [ROUTER_DIRECTIVES],
  providers: [ROUTER_PROVIDERS, HTTP_PROVIDERS, NodeService, LocalizationService, PropertyService]
})
@RouteConfig([
  { path: '/mdmmenu/...', name: 'MDMMenu', component: MDMMenuComponent, useAsDefault: true }
])
export class MDMComponent {
  brand = 'openMDM5 Web';
  _comp: string = "MDMMenu";

  activate(comp: string){
    this._comp = comp;
  }
  isActive(comp: string){
    if comp === this._comp {
      return 'active';
    }
  }
}

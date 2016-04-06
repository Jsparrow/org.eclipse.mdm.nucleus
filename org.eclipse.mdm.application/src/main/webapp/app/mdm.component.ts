// Copyright (c) 2016 Gigatronik Ingolstadt GmbH
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
import {Component} from 'angular2/core';
import {HTTP_PROVIDERS} from 'angular2/http';
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';

import {MDMNavigatorComponent} from './mdm-navigator.component';
import {NodeService} from './node.service';
import {LocalizationService} from './localization.service';

@Component({
  selector: 'mdm-web',
  template: `<nav class="navbar navbar-default">
    <div class="container-fluid">
      <div class="navbar-header">
        <a class="navbar-brand" href="#">{{title}}</a>
      </div>
      <ul class="nav navbar-nav">
        <li class="active"><a [routerLink]="['Navigator']"> MDMNavigator</a></li>
      </ul>
    </div>
  </nav>
  <router-outlet></router-outlet>`,
  directives: [ROUTER_DIRECTIVES],
  providers: [ROUTER_PROVIDERS, HTTP_PROVIDERS, NodeService, LocalizationService]
})
@RouteConfig([
  { path: '/navigator/...', name: 'Navigator', component: MDMNavigatorComponent, useAsDefault: true }
])
export class MDMComponent {
  title = 'openMDM5 Web';
}

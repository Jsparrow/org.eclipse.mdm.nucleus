// Copyright (c) 2016 Gigatronik Ingolstadt GmbH
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
import {Component, Input} from 'angular2/core';
import {HTTP_PROVIDERS} from 'angular2/http';
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';

// import {MDMDetailViewComponent} from './mdm-detail-view.component';
import {Node} from './node';
import {ActionService} from './action.service';

@Component({
  selector: 'mdm-detail',
  template: `<nav class="navbar navbar-default">
    <div class="container-fluid">
      <ul class="nav navbar-nav">
        <li [ngClass]="isActive('DetailView')"><a [routerLink]="['DetailView']" (click)="activate('DetailView')"> Attributes</a></li>
      </ul>
    </div>
  </nav>
  <div class="container-fluid">
    <router-outlet></router-outlet>
  </div>
  `,
  directives: [ROUTER_DIRECTIVES],
  providers: []
})
export class MDMDetailComponent {
  _comp: string = 'DetailView';

  activate(comp: string){
    this._comp = comp;
  }
  isActive(comp: string){
    if comp === this._comp {
      return 'active';
    }
  }
}

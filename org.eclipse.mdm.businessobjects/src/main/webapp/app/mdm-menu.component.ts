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
import {RouteConfig} from 'angular2/router';

import {Node} from './node';

import {MDMNodeProviderComponent} from './mdm-node-provider.component';

import {MDMNavigatorComponent} from './mdm-navigator.component';
import {MDMSearchComponent} from './mdm-search.component';
import {MDMBasketComponent} from './mdm-basket.component';

import {MDMDetailComponent} from './mdm-detail.component';
import {MDMDetailViewComponent} from './mdm-detail-view.component';
import {MDMDescriptiveDataComponent} from './mdm-detail-descriptive-data.component';

@RouteConfig([
  { path: '/detailView', component: MDMDetailViewComponent, name: 'DetailView', useAsDefault: true },
  { path: '/descriptiveData', component: MDMDescriptiveDataComponent name: 'DescriptiveData'}
])
@Component({
  selector: 'mdm-menu',
  templateUrl: 'templates/mdm-menu.component.html',
  directives [MDMNavigatorComponent, MDMSearchComponent, MDMBasketComponent, MDMDetailComponent],
  providers: []
})
export class MDMMenuComponent{
  selectedNode: Node

  updateSelectedNode(arg) {
    this.selectedNode = arg
  }
}

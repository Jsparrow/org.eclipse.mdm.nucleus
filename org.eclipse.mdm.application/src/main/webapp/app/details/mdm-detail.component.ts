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
import {Component, Input} from '@angular/core';
import {HTTP_PROVIDERS} from '@angular/http';
import {RouteConfig, ROUTER_DIRECTIVES} from '@angular/router-deprecated';

import {Node} from '../navigator/node';

import {MDMDetailViewComponent} from './mdm-detail-view.component';
import {MDMDescriptiveDataComponent} from './mdm-detail-descriptive-data.component';

@Component({
  selector: 'mdm-detail',
  template: require('../../templates/details/mdm-detail.component.html'),
  directives: [ROUTER_DIRECTIVES, MDMDetailViewComponent, MDMDescriptiveDataComponent],
  providers: []
})
export class MDMDetailComponent {
  @Input() selectedNode: Node;

  brand: string = 'Details';
  _comp: string = 'DetailView';

  activate(comp: string){
    this._comp = comp;
  }
  isActive(comp: string){
    if (comp === this._comp) {
      return 'active';
    }
  }
}

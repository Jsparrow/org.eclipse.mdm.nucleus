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
import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {OnActivate, RouteSegment, Router, Routes} from '@angular/router';
import { ACCORDION_DIRECTIVES } from 'ng2-bootstrap/ng2-bootstrap';

import {Node} from './node';
import {MDMNodeProviderComponent} from './mdm-node-provider.component';

import {MDMNavigatorComponent} from './mdm-navigator.component';
import {MDMBasketComponent} from '../basket/mdm-basket.component';
import {MDMDetailComponent} from '../details/mdm-detail.component';
import {MDMSearchComponent} from '../search/mdm-search.component';

@Component({
  selector: 'mdm-menu',
  templateUrl: 'templates/navigator/mdm-menu.component.html',
  styles: [
    '.panel-body {padding: 0px;}',
    '.list-group {margin-bottom: 0px;}',
    '.list-group-item:first-child {border-top-left-radius: 0px; border-top-right-radius: 0px;}',
    '.list-group-item:last-child {border-bottom-right-radius: 0px; border-bottom-left-radius: 0px; border-bottom-style: none;}'
  ],
  directives: [MDMNavigatorComponent, MDMBasketComponent, MDMDetailComponent, ACCORDION_DIRECTIVES],
  providers: [],
  encapsulation: ViewEncapsulation.None
})
@Routes([
  {path: '/details', component: MDMDetailComponent},
  {path: '/search', component: MDMSearchComponent}
])
export class MDMMenuComponent implements OnInit{
  selectedNode: Node
  closeOther:boolean = false;
  navigator:string = "Navigator";
  basket:string = "Basket";

  constructor(private router: Router){}

  ngOnInit(){
  }

  updateSelectedNode(arg) {
    this.selectedNode = arg
  }
}

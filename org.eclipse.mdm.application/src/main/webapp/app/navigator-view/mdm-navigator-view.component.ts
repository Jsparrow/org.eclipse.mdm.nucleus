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
import {Component, ViewEncapsulation} from '@angular/core';

import { DropdownModule, AccordionConfig, DropdownConfig } from 'ng2-bootstrap';

import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';
import {MDMNodeProviderComponent} from '../navigator/mdm-node-provider.component';

import {MDMNavigatorComponent} from '../navigator/mdm-navigator.component';
import {MDMBasketComponent} from '../basket/mdm-basket.component';
import {MDMDetailComponent} from '../details/mdm-detail.component';
import {MDMSearchComponent} from '../search/mdm-search.component';
import {ModulesComponent} from '../modules/modules.component';

@Component({
  selector: 'mdm-navigator-view',
  templateUrl: 'mdm-navigator-view.component.html',
  styles: [
    '.panel-body {padding: 0px;}',
    '.list-group {margin-bottom: 0px;}',
    '.list-group-item { white-space: nowrap; }',
    '.list-group-item:first-child {border-top-left-radius: 0px; border-top-right-radius: 0px;}',
    '.list-group-item:last-child {border-bottom-right-radius: 0px; border-bottom-left-radius: 0px; border-bottom-style: none;}'
  ],
  providers: [DropdownConfig, AccordionConfig],
  encapsulation: ViewEncapsulation.None
})
export class MDMNavigatorViewComponent {
  selectedNode: Node = new Node;
  activeNode: Node;
  closeOther: boolean = false;
  navigator: string = 'Navigation';
  basket: string = 'Warenkorb';
  activeNodeprovider: any;
  _comp: string = 'Navigation';
  subscription: any;

  constructor(private nodeService: NodeService) {}

  updateSelectedNode(node: Node) {
    this.selectedNode = node;
  }
  updateActiveNode(node: Node) {
    this.activeNode = node;
  }
  activateNodeProvider(nodeprovider: any) {
    this.nodeService.setActiveNodeprovider(nodeprovider);
  }

  getNodeproviders() {
    return this.nodeService.getNodeproviders();
  }

  ngOnInit() {
    this.activeNodeprovider = this.nodeService.getActiveNodeprovider();
    this.subscription = this.nodeService.nodeProviderChanged
        .subscribe(np => this.activeNodeprovider = np);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  activate(comp: string) {
    this._comp = comp;
  }

  isActive(comp: string) {
    if (comp === this._comp) {
      return 'active';
    }
  }

  isDropActive(comp: string) {
    if (comp === this._comp) {
      return 'open ';
    }
  }
}

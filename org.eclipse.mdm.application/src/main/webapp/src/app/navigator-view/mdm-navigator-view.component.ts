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
import {NodeproviderService} from '../navigator/nodeprovider.service';

@Component({
  selector: 'mdm-navigator-view',
  templateUrl: 'mdm-navigator-view.component.html',
  styleUrls: [ './mdm-navigator-view.component.css' ],
  providers: [DropdownConfig, AccordionConfig],
  encapsulation: ViewEncapsulation.None
})
export class MDMNavigatorViewComponent {
  selectedNode = new Node;
  activeNode: Node;
  closeOther = false;
  navigator = 'Navigation';
  basket = 'Warenkorb';
  activeNodeprovider: any;
  _comp = 'Navigation';
  subscription: any;

  constructor(private nodeProviderService: NodeproviderService) {}

  updateSelectedNode(node: Node) {
    this.selectedNode = node;
  }
  updateActiveNode(node: Node) {
    this.activeNode = node;
  }
  activateNodeProvider(nodeprovider: any) {
    this.nodeProviderService.setActiveNodeprovider(nodeprovider);
  }

  getNodeproviders() {
    return this.nodeProviderService.getNodeproviders();
  }

  ngOnInit() {
    this.activeNodeprovider = this.nodeProviderService.getActiveNodeprovider();
    this.subscription = this.nodeProviderService.nodeProviderChanged
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

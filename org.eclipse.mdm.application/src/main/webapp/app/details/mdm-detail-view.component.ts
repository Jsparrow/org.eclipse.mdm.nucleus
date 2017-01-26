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
import {Component, OnInit} from '@angular/core';

import { ModalDirective } from 'ng2-bootstrap';

import {MDMItem} from '../core/mdm-item';
import {Node} from '../navigator/node';
import {BasketService} from '../basket/basket.service';

import {Release, FilereleaseService} from '../filerelease/filerelease.service';

import {Localization} from '../localization/localization';
import {LocalizationService} from '../localization/localization.service';

import {MDMFilereleaseCreateComponent} from '../filerelease/mdm-filerelease-create.component';
import {NavigatorService} from '../navigator/navigator.service';

@Component({
  selector: 'mdm-detail-view',
  templateUrl: 'mdm-detail-view.component.html'
})
export class MDMDetailViewComponent implements OnInit {
  selectedNode: Node;

  locals: Localization[] = [];

  constructor(private localService: LocalizationService,
              private basketService: BasketService,
              private navigatorService: NavigatorService) {}

  ngOnInit() {
    this.navigatorService.selectedNodeChanged
        .subscribe(node => this.selectedNode = node);
  }

  add2Basket() {
    if (this.selectedNode) {
      this.basketService.add(new MDMItem(this.selectedNode.sourceName, this.selectedNode.type, this.selectedNode.id));
    }
  }

  isShopable() {
    if (this.selectedNode && this.selectedNode.name !== undefined) { return false; }
    return true;
  }
  isReleasable() {
    if (this.selectedNode && this.selectedNode.sourceType === 'TestStep') { return false; }
    return true;
  }

  getTrans(type: string, attr: string) {
    return this.localService.getTranslation(type, attr);
  }
  getAttributesForDisplay() {
    if (this.selectedNode.attributes !== undefined) {
      return this.selectedNode.attributes.filter(a => a.name !== 'MimeType').filter(a => a.name !== 'Sortindex');
    }

    return this.selectedNode.attributes;
  }
}

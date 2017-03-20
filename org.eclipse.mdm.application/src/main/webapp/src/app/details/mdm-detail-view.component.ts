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

import {Input, Component, OnInit, ViewChild, OnDestroy} from '@angular/core';

import { ModalDirective } from 'ng2-bootstrap';

import {MDMItem} from '../core/mdm-item';
import {Node, Attribute} from '../navigator/node';
import {BasketService} from '../basket/basket.service';

import {Release, FilereleaseService} from '../filerelease/filerelease.service';

import {Localization} from '../localization/localization';
import {LocalizationService} from '../localization/localization.service';

import {MDMFilereleaseCreateComponent} from '../filerelease/mdm-filerelease-create.component';
import {NavigatorService} from '../navigator/navigator.service';

import { DetailViewService } from './detail-view.service';

@Component({
  selector: 'mdm-detail-view',
  templateUrl: 'mdm-detail-view.component.html'
})
export class MDMDetailViewComponent implements OnInit, OnDestroy {
  selectedNode: Node;
  @Input() displayAttributes: Attribute[];

  locals: Localization[] = [];
  subscription: any;

  @ViewChild('lgModal')
  lgModal: ModalDirective;

  constructor(private localService: LocalizationService,
              private basketService: BasketService,
              private navigatorService: NavigatorService,
              private detailViewService: DetailViewService) {}

  ngOnInit() {
    this.onSelectedNodeChange(this.navigatorService.getSelectedNode());
    this.subscription = this.navigatorService.selectedNodeChanged
        .subscribe(node => this.onSelectedNodeChange(node));
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  onSelectedNodeChange(node: Node) {
    if (node) {
      this.selectedNode = node;
      this.displayAttributes = this.detailViewService.getAttributesToDisplay(this.selectedNode);
    }
  }

  add2Basket() {
    if (this.selectedNode) {
      this.basketService.add(new MDMItem(this.selectedNode.sourceName, this.selectedNode.type, this.selectedNode.id));
    }
  }

  isShopable() {
    if (this.selectedNode && this.selectedNode.name !== undefined && this.selectedNode.type !== 'Environment') {
      return false;
    }
    return true;
  }

  isReleasable() {
    if (this.selectedNode && this.selectedNode.sourceType === 'TestStep') { return false; }
    return true;
  }

  getTrans(type: string, attr: string) {
    return this.localService.getTranslation(type, attr);
  }
}

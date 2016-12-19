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
import {ControlGroup, Control, Validators} from '@angular/common';

import {Node} from '../navigator/node';
import {NodeService} from '../navigator/node.service';
import {BasketService} from '../basket/basket.service';

import {Release, FilereleaseService} from '../filerelease/filerelease.service';

import {Localization} from '../localization/localization';
import {LocalizationService} from '../localization/localization.service';

import {MODAL_DIRECTVES, BS_VIEW_PROVIDERS} from 'ng2-bootstrap/ng2-bootstrap';
import {MDMFilereleaseCreateComponent} from '../filerelease/mdm-filerelease-create.component';

@Component({
  selector: 'mdm-detail-view',
  template: require('../../templates/details/mdm-detail-view.component.html'),
  directives: [MODAL_DIRECTVES, MDMFilereleaseCreateComponent],
  providers: [],
  viewProviders: [BS_VIEW_PROVIDERS],
  inputs: []
})

export class MDMDetailViewComponent{
  @Input() selectedNode: Node;
  locals: Localization[] = [];

  constructor(private _nodeService: NodeService,
              private _loaclService: LocalizationService,
              private _basketService: BasketService,
              private _releaseService: FilereleaseService){}

  add2Basket(){
    if (this.selectedNode){
      this._basketService.addNode(this.selectedNode);
    }
  }

  isShopable(){
    if (this.selectedNode.name != undefined){return false}
    return true
  }
  isReleasable(){
    if (this.selectedNode.sourceType == 'TestStep'){return false}
    return true
  }

  getTrans(type: string, attr: string){
    return this._loaclService.getTranslation(type, attr)
  }
  getAttributesForDisplay() {
    if (this.selectedNode.attributes != undefined) {
      return this.selectedNode.attributes.filter(a => a.name != 'MimeType').filter(a => a.name != 'Sortindex');
    }

    return this.selectedNode.attributes;
  }
}

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
import {Component, Input, OnChanges, SimpleChange} from '@angular/core';

import {Node} from '../navigator/node';
import {NodeService} from '../navigator/node.service';
import {BasketService} from '../basket/basket.service';

import {Localization} from '../localization/localization';
import {LocalizationService} from '../localization/localization.service';

@Component({
  selector: 'mdm-detail-view',
  template: require('../../templates/details/mdm-detail-view.component.html'),
  directives: [],
  providers: [],
  inputs: []
})

export class MDMDetailViewComponent{
  @Input() selectedNode: Node;
  locals: Localization[] = [];
  errorMessage: string;

  constructor(private _nodeService: NodeService,
              private _loaclService: LocalizationService,
              private _basketService: BasketService){}

  add2Basket(){
    if (this.selectedNode){
      this._basketService.addNode(this.selectedNode);
    }
  }

  isActive(){
    if (this.selectedNode){return}
    return "disabled"
  }

  getTrans(type: string, attr: string){
    return this._loaclService.getTranslation(type, attr)
  }
}

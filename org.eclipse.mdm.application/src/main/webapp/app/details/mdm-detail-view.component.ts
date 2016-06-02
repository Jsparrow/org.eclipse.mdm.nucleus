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

import {Localization} from '../localization';
import {LocalizationService} from '../localization.service';

@Component({
  selector: 'mdm-detail-view',
  templateUrl: 'templates/details/mdm-detail-view.component.html',
  directives: [],
  providers: [],
  inputs: []
})

export class MDMDetailViewComponent implements OnChanges{
  @Input() selectedNode: Node;
  locals: Localization[] = [];
  errorMessage: string;

  constructor(private _nodeService: NodeService,
              private _loaclService: LocalizationService,
              private _basketService: BasketService){}

  ngOnChanges(){
    this.locals = [];
    if (this.selectedNode){
      this._loaclService.getLocalization(this.selectedNode).subscribe(
        locals => this.locals = locals,
        error => this.errorMessage = <any>error);
    }
  }

  add2Basket(){
    if (this.selectedNode){
      this._basketService.addNode(this.selectedNode);
    }
  }

  getTrans(attr: string){
    let pos = this.locals.map(function(e) { return e.name; }).indexOf(attr);
    if (pos !== -1) {
      return this.locals[pos].localizedName
    }
    return attr;
  }
}

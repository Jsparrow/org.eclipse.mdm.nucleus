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
import {Component, Input, Output, EventEmitter} from '@angular/core';
import {Node} from '../navigator/node';
import {BasketService} from './basket.service';

@Component({
  selector: 'mdm-basket',
  template: require('../../templates/basket/mdm-basket.component.html'),
  styles: ['.remove {color:black; cursor: pointer; float: right}'],
  directives: [],
  providers: []
})
export class MDMBasketComponent {
  @Output() onSelect = new EventEmitter<Node>();
  @Output() onActive = new EventEmitter<Node>();
  @Input() activeNode: Node;

  constructor(private _basketService : BasketService){}

  isActive(node){
    if (this.activeNode == node) {
      return "active"
    }
  }

  removeNode(node){
    this._basketService.removeNode(node);
  }

  selectNode(node){
    this.activeNode = node;
    this.onActive.emit(node);
    this.onSelect.emit(node);
  }
}
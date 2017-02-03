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
import {Component, Input, Output, EventEmitter, OnInit, ViewChild } from '@angular/core';
import {Node} from '../navigator/node';
import {BasketNode, BasketService} from './basket.service';
import {TableviewComponent} from '../tableview/tableview.component';

import { ModalDirective } from 'ng2-bootstrap';

@Component({
  selector: 'mdm-basket',
  templateUrl: 'mdm-basket.component.html',
  styles: ['.remove {color:black; cursor: pointer; float: right}'],
  providers: []
})
export class MDMBasketComponent {
  @ViewChild('lgLoadModal') public childLoadModal: ModalDirective;
  @ViewChild('lgSaveModal') public childSaveModal: ModalDirective;
  @Output() onSelect = new EventEmitter<Node>();
  @Output() onActive = new EventEmitter<Node>();
  @Input() activeNode: Node;
  basketNodes: Node[];
  baskets: BasketNode[];
  selectedBasket: BasketNode;
  basketName: string = '';

  constructor(private _basketService: BasketService) {
  }

  ngOnInit() {
    this._basketService.getBasketNodes().then(baskets => this.setNodes(baskets));
    this._basketService.nodesChanged$.subscribe(nodes => this.basketName = '');
  }

  setNodes(baskets: BasketNode[]) {
    this.basketNodes = this._basketService.Nodes;
    this.baskets = baskets;
    this.selectedBasket = this.baskets[0];
  }

  saveBasket() {
    let index = this.baskets.indexOf(this.selectedBasket);
    this._basketService.saveNodes(this.basketNodes, this.basketName)
             .then(saved => this._basketService.getBasketNodes())
             .then(baskets => { this.baskets = baskets; this.selectedBasket = baskets[index]; });
    this.childSaveModal.hide();
  }

  loadBasket() {
    this.basketNodes.length = 0;
    this.selectedBasket.nodes.forEach(nodes => this.basketNodes.push(nodes));
    this.basketName = this.selectedBasket.name;
    this.childLoadModal.hide();
  }

  clearBasket() {
    this.basketNodes.length = 0;
    this.basketName = '';
  }

  showLoadModal() {
    this.childLoadModal.show();
  }

  showSaveModal() {
    this.childSaveModal.show();
  }

  selectBasket(basket) {
    this.selectedBasket = basket;
  }

}

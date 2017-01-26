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
import {MDMItem} from '../core/mdm-item';

import {BasketService, Basket} from './basket.service';
import {TableviewComponent} from '../tableview/tableview.component';
import {ViewComponent} from '../tableview/view.component';

import { View } from '../tableview/tableview.service';

import { ModalDirective } from 'ng2-bootstrap';

import {QueryService, Query, SearchResult, Row, Filter} from '../tableview/query.service';

@Component({
  selector: 'mdm-basket',
  templateUrl: 'mdm-basket.component.html',
  styles: ['.remove {color:black; cursor: pointer; float: right}'],
  providers: []
})
export class MDMBasketComponent {
  @Output() onActive = new EventEmitter<Node>();
  @Output() onSelect = new EventEmitter<Node>();
  @Input() activeNode: Node;

  basketName: string = '';
  basketContent: SearchResult = new SearchResult();

  baskets: Basket[] = [];
  selectedBasket: Basket;

  selectedView: View;

  @ViewChild(ViewComponent)
  private viewComponent: ViewComponent;
  @ViewChild('lgLoadModal')
  private childLoadModal: ModalDirective;
  @ViewChild('lgSaveModal')
  private childSaveModal: ModalDirective;

  constructor(private _basketService: BasketService, private queryService: QueryService) {
  }

  ngOnInit() {
    this.setItems(this._basketService.items);

    this.viewComponent.onViewSelected.subscribe(view => this.selectedView = view);
    this._basketService.itemsAdded$.subscribe(items => this.addItems(items));
    this._basketService.itemsRemoved$.subscribe(items => this.removeItems(items));
  }

  setItems(items: MDMItem[]) {
    if (this.selectedView) {
      this.queryService.queryItems(items, this.selectedView.cols.map(c => c.type + '.' + c.name))
        .forEach(q => q.subscribe(r => this.basketContent.rows = r.rows));
    }
  }

  addItems(items: MDMItem[]) {
    if (this.selectedView) {
      this.queryService.queryItems(items, this.selectedView.cols.map(c => c.type + '.' + c.name))
        .forEach(q => q.subscribe(r => this.addData(r.rows)));
    }
  }

  removeItems(items: MDMItem[]) {
    items.forEach(item =>
      this.basketContent.rows = this.basketContent.rows.filter(row =>
        !(row.source === item.source && row.type === item.type && +row.id === item.id)));
  }


  setView(view: View) {
    console.log('setView', view);
  }

  saveBasket() {
    this._basketService.saveBasketWithName(this.basketName);
    this.childSaveModal.hide();
  }

  loadBasket(basket: Basket) {
    this.selectedBasket = basket;
    this.setItems(basket.items);
    this.childLoadModal.hide();
  }

  loadBaskets() {
    this._basketService.getBaskets().then(baskets => this.baskets = baskets);
  }

  clearBasket() {
    this.basketContent = new SearchResult();
    this.basketName = '';
  }

  showLoadModal() {
    this.loadBaskets();
    this.childLoadModal.show();
  }

  showSaveModal() {
    this.childSaveModal.show();
  }

  private addData(rows: Row[]) {
    rows.forEach(row => this.basketContent.rows.push(row));
  }
}

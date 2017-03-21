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
import { DomSanitizer } from '@angular/platform-browser';
import {Node} from '../navigator/node';
import {MDMItem} from '../core/mdm-item';

import {BasketService, Basket} from './basket.service';
import { NavigatorService } from '../navigator/navigator.service';

import {TableviewComponent} from '../tableview/tableview.component';
import {ViewComponent} from '../tableview/view.component';

import { View } from '../tableview/tableview.service';

import { ModalDirective } from 'ng2-bootstrap';

import {QueryService, Query, SearchResult, Row, Filter} from '../tableview/query.service';
import {MenuItem} from 'primeng/primeng';

import * as FileSaver from 'file-saver';

@Component({
  selector: 'mdm-basket',
  templateUrl: 'mdm-basket.component.html',
  styleUrls: ['./mdm-basket.component.css'],
  providers: []
})
export class MDMBasketComponent implements OnInit {
  @Output() onActive = new EventEmitter<Node>();
  @Output() onSelect = new EventEmitter<Node>();
  @Input() activeNode: Node;

  basketName = 'basket';
  basketContent: SearchResult = new SearchResult();

  baskets: Basket[] = [];
  selectedBasket: Basket;

  contextMenuItems: MenuItem[] = [
      {label: 'Selektion aus Warenkorb entfernen', icon: 'glyphicon glyphicon-remove', command: (event) => this.removeSelected() }
  ];

  @ViewChild(TableviewComponent)
  tableViewComponent: TableviewComponent;
  @ViewChild(ViewComponent)
  viewComponent: ViewComponent;
  @ViewChild('lgLoadModal')
  childLoadModal: ModalDirective;
  @ViewChild('lgSaveModal')
  childSaveModal: ModalDirective;

  constructor(private _basketService: BasketService,
              private queryService: QueryService,
              private navigatorService: NavigatorService,
              private sanitizer: DomSanitizer) {
  }

  removeSelected() {
    this.tableViewComponent.selectedRows.map(r => r.getItem()).forEach(i => this._basketService.remove(i));
  }

  ngOnInit() {
    this.setItems(this._basketService.items);

    this._basketService.itemsAdded$.subscribe(items => this.addItems(items));
    this._basketService.itemsRemoved$.subscribe(items => this.removeItems(items));
  }

  setItems(items: MDMItem[]) {
    this.basketContent.rows = [];
    this.addItems(items);
  }

  addItems(items: MDMItem[]) {
    if (this.viewComponent.selectedView) {
      this.queryService.queryItems(items, this.viewComponent.selectedView.columns.map(c => c.type + '.' + c.name))
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

  loadBasket(basket?: Basket) {
    if (basket === undefined) {
      basket = this.selectedBasket;
      if (this.selectedBasket === undefined) {
        return;
      }
    }
    this.basketName = basket.name;
    this.setItems(basket.items);
    this._basketService.setItems(basket.items);
    this.childLoadModal.hide();
  }

  loadBaskets() {
    this._basketService.getBaskets().subscribe(baskets => this.baskets = baskets);
  }

  clearBasket() {
    this.basketContent = new SearchResult();
    this._basketService.removeAll();
    this.basketName = 'basket';
  }

  showLoadModal() {
    this.selectedBasket = undefined;
    this.loadBaskets();
    this.childLoadModal.show();
  }

  showSaveModal() {
    this.basketName = '';
    this.childSaveModal.show();
  }

  downloadBasket() {
    console.log('!!!!!!!!!!!!!!!!!!!!!!!!!')
    let downloadContent = new Basket(this.basketName, this._basketService.getItems());
    let blob = new Blob([JSON.stringify(downloadContent)], {
         type: 'application/json'
     });
    FileSaver.saveAs(blob, this.basketName + '.json');
  }

  onUploadChange(event) {
    this.onUploadEvent(event.target);
  }

  toggleSelect(basket: Basket) {
    this.selectedBasket = this.selectedBasket === basket ? undefined : basket;
  }

  isDownloadDisabled() {
    return this.basketContent.rows.length <= 0;
  }

  private onUploadEvent(inputValue: any) {
    let file = inputValue.files[0];
    let reader = new FileReader();
    reader.onloadend = (obj) => {
      let upload = JSON.parse(reader.result);
      this.loadBasket(upload);
    };
    reader.readAsText(file);
  }

  private addData(rows: Row[]) {
    rows.forEach(row => this.basketContent.rows.push(row));
  }
}

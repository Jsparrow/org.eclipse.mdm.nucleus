/*******************************************************************************
*  Original work: Copyright (c) 2016 Gigatronik Ingolstadt GmbH                *
*  Modified work: Copyright (c) 2017 Peak Solution GmbH                        *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Dennis Schroeder - initial implementation                                   *
*  Matthias Koller, Johannes Stamm - additional client functionality           *
*******************************************************************************/

import { Component, Input, Output, EventEmitter, OnInit, ViewChild } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ModalDirective } from 'ng2-bootstrap';

import { BasketService, Basket} from './basket.service';

import { MDMItem} from '../core/mdm-item';
import { OverwriteDialogComponent } from '../core/overwrite-dialog.component';
import { NavigatorService } from '../navigator/navigator.service';
import { Node} from '../navigator/node';
import { TableviewComponent } from '../tableview/tableview.component';
import { ViewComponent } from '../tableview/view.component';
import { View } from '../tableview/tableview.service';
import { QueryService, Query, SearchResult, Row, Filter } from '../tableview/query.service';

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

  basketName = '';
  basketContent: SearchResult = new SearchResult();
  basket = 'Warenkorb';

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
  @ViewChild(OverwriteDialogComponent)
  private overwriteDialogComponent: OverwriteDialogComponent;

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
    this.viewComponent.viewChanged$.subscribe(() => this.setItems(this._basketService.items));
  }

  onViewClick(e: Event) {
    e.stopPropagation();
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

  saveBasket(e: Event) {
    e.stopPropagation();
    if (this.baskets.find(f => f.name === this.basketName) !== undefined) {
      this.childSaveModal.hide();
      this.overwriteDialogComponent.showOverwriteModal('ein Warenkorb').subscribe(ovw => this.saveBasket2(ovw));
    } else {
      this.saveBasket2(true);
    }
  }

  saveBasket2(save: boolean) {
    if (save) {
      this._basketService.saveBasketWithName(this.basketName);
      this.childSaveModal.hide();
    } else {
      this.childSaveModal.show();
    }
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

  clearBasket(e: Event) {
    e.stopPropagation();
    this.basketContent = new SearchResult();
    this._basketService.removeAll();
    this.basketName = '';
  }

  showLoadModal(e: Event) {
    e.stopPropagation();
    this.selectedBasket = undefined;
    this.loadBaskets();
    this.childLoadModal.show();
  }

  showSaveModal(e: Event) {
    e.stopPropagation();
    this.basketName = this.selectedBasket ? this.selectedBasket.name : '';
    this.childSaveModal.show();
  }

  downloadBasket(e: Event) {
    e.stopPropagation();
    let downloadContent = new Basket(this.basketName, this._basketService.getItems());
    let blob = new Blob([JSON.stringify(downloadContent)], {
         type: 'application/json'
     });
    FileSaver.saveAs(blob, this.basketName + '.json');
  }

  onUploadChange(event: Event) {
    this.onUploadEvent(event.target);
  }

  onUploadClick(e: Event) {
    e.stopPropagation();
  }

  toggleSelect(basket: Basket) {
    this.selectedBasket = this.selectedBasket === basket ? undefined : basket;
  }

  isDownloadDisabled() {
    return this.basketContent.rows.length <= 0;
  }

  private onUploadEvent(fileInput: any) {
    let file = fileInput.files[0];
    let reader = new FileReader();
    reader.onloadend = (event) => {
      let upload = JSON.parse(reader.result);
      this.loadBasket(upload);
      fileInput.value = '';
    };
    reader.readAsText(file);
  }

  private addData(rows: Row[]) {
    rows.forEach(row => this.basketContent.rows.push(row));
    this.tableViewComponent.customSort({
      'field': this.tableViewComponent.view.getSortField(),
      'order': this.tableViewComponent.view.getSortOrder()
    });
  }
}

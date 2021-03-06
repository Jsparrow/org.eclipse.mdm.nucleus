/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


import { Component, Input, Output, EventEmitter, OnInit, ViewChild } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ModalDirective } from 'ngx-bootstrap';

import { BasketService, Basket} from './basket.service';

import { MDMItem} from '../core/mdm-item';
import { OverwriteDialogComponent } from '../core/overwrite-dialog.component';
import { NavigatorService } from '../navigator/navigator.service';
import { Node} from '../navigator/node';
import { NodeService } from '../navigator/node.service';
import { TableviewComponent } from '../tableview/tableview.component';
import { ViewComponent } from '../tableview/view.component';
import { View } from '../tableview/tableview.service';
import { QueryService, Query, SearchResult, Row, Filter } from '../tableview/query.service';

import { serialize, deserialize } from 'class-transformer';

import {MenuItem} from 'primeng/primeng';
import * as FileSaver from 'file-saver';

import {MDMNotificationService} from '../core/mdm-notification.service';
import {Observable} from 'rxjs/Observable';

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

  readonly LblBasket = 'Warenkorb';
  readonly LblExistingBasketNames = 'Vorhandene Warenkörbe';
  readonly LblLoad = 'Laden';
  readonly LblLoadBasket = 'Warenkorb öffnen';
  readonly LblSave = 'Speichern';
  readonly LblSaveBasketAs = 'Warenkorb speichern als';
  readonly TtlClearShoppingBasket = 'Warenkorb leeren';
  readonly TtlClose = 'Schließen';
  readonly TtlDownloadShoppingBasket = 'Warenkorb herunterladen';
  readonly TtlLoadShoppingBasket = 'Warenkorb öffnen';
  readonly TtlNoNameSet = 'Kein Name ausgewählt';
  readonly TtlSaveShoppingBasket = 'Warenkorb speichern';
  readonly TtlUploadShoppingBasket = 'Warenkorb hochladen';

  basketName = '';
  basketContent: SearchResult = new SearchResult();
  basket = 'Warenkorb';

  baskets: Basket[] = [];
  selectedBasket: Basket;
  environments: Node[];

  public selectedRow: string;
  public lazySelectedRow: string;

  _currentChange: any;

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
  overwriteDialogComponent: OverwriteDialogComponent;

  constructor(private _basketService: BasketService,
              private queryService: QueryService,
              private navigatorService: NavigatorService,
              private sanitizer: DomSanitizer,
              private NodeService: NodeService,
              private notificationService: MDMNotificationService) {
  }

  removeSelected() {
    this.tableViewComponent.selectedRows.map(r => r.getItem()).forEach(i => this._basketService.remove(i));
  }

  ngOnInit() {
    this.NodeService.getRootNodes().subscribe(
      envs => this.environments = envs,
      error => this.notificationService.notifyError('Quellen kann nicht geladen werden.', error)
    );
    this.setItems(this._basketService.items);
    this._basketService.itemsAdded$.subscribe(
      items => this.addItems(items),
      error => this.notificationService.notifyError('Auswahl kann nicht hinzugefügt werden.', error)
    );
    this._basketService.itemsRemoved$.subscribe(
      items => this.removeItems(items),
      error => this.notificationService.notifyError('Auswahl kann nicht entfernt werden.', error)
    );
    this.viewComponent.viewChanged$.subscribe(
      () => this.setItems(this._basketService.items),
      error => this.notificationService.notifyError('Ansicht kann nicht ausgewählt werden.', error)
    );
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
        .forEach(q => q.subscribe(
          r => this.addData(r.rows),
          error => this.notificationService.notifyError('Items können nicht zum Warenkorb hinzugefügt werden', error)
        )
      );
    }
  }

  removeItems(items: MDMItem[]) {
    items.forEach(item =>
      this.basketContent.rows = this.basketContent.rows.filter(row =>
        !(row.source === item.source && row.type === item.type && row.id === item.id)));
  }

  setView(view: View) {
    console.log('setView', view);
  }

  saveBasket(e: Event) {
    e.stopPropagation();
    if (this.baskets.find(f => f.name === this.basketName) !== undefined) {
      this.childSaveModal.hide();
      this.overwriteDialogComponent.showOverwriteModal('ein Warenkorb').subscribe(
        needSave => this.saveBasket2(needSave),
        error => {
          this.saveBasket2(false);
          this.notificationService.notifyError('Ein Fehler ist aufgetreten. Der Warenkorb wurde nicht gespeichert.', error);
        });
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
    this._basketService.getBaskets().subscribe(
      baskets => this.baskets = baskets,
      error => this.notificationService.notifyError('Warenkorb kann nicht geladen werden.', error));
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
    this.loadBaskets();
    this.basketName = this.selectedBasket ? this.selectedBasket.name : '';
    this.childSaveModal.show();
  }

  downloadBasket(e: Event) {
    e.stopPropagation();
    let downloadContent = new Basket(this.basketName, this._basketService.getItems());

    this._basketService.getBasketAsXml(downloadContent).combineLatest(
      this._basketService.getFileExtension(),
      (xml, fileExtension) => this.saveXml(xml, fileExtension)
    ).subscribe();
  }

  saveXml(xml: string, fileExtension: string) {
    let blob = new Blob([xml], {
         type: 'application/xml'
    });

    if (this.basketName && this.basketName.trim().length !== 0) {
      FileSaver.saveAs(blob, this.basketName + '.' + fileExtension);
    } else {
      FileSaver.saveAs(blob, 'warenkorb.' + fileExtension);
    }
  }

  saveJson(basket: Basket) {
    let blob = new Blob([serialize(basket)], {
         type: 'application/json'
     });
    if (this.basketName && this.basketName.trim().length !== 0) {
      FileSaver.saveAs(blob, this.basketName + '.json');
    } else {
      FileSaver.saveAs(blob, 'warenkorb.json');
    }
  }

  onUploadChange(event: Event) {
    this._currentChange = event.target;
    this.onUploadEvent(this._currentChange);
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

  getSaveBtnTitle() {
    return this.basketName ? this.TtlSaveShoppingBasket : this.TtlNoNameSet;
  }

  private onUploadEvent(fileInput: any) {
    if (fileInput.files[0]) {
      let file = fileInput.files[0];
      let reader = new FileReader();
      reader.onloadend = (event) => {
        let upload: Basket = deserialize(Basket, reader.result);
        this.loadBasket(upload);
        fileInput.value = '';
    };
    reader.readAsText(file);
  }
  }

  private addData(rows: Row[]) {
    rows.forEach(row => this.basketContent.rows.push(row));
    this.tableViewComponent.customSort({
      'field': this.tableViewComponent.view.getSortField(),
      'order': this.tableViewComponent.view.getSortOrder()
    });
  }

  onRowSelect(e: any) {
    if (this.lazySelectedRow !== e.data) {
      this.selectedRow = e.data;
      this.basketName = e.data.name;
    } else {
      this.selectedRow = undefined;
      this.basketName = '';
    }
    this.lazySelectedRow = this.selectedRow;
  }
}

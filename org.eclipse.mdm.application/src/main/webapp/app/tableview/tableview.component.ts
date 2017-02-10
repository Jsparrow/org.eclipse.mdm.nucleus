import { Component, Input, ViewChild, OnInit, OnChanges, SimpleChanges } from '@angular/core';

import { PreferenceView, View, ViewColumn, ViewService } from './tableview.service';

import {FilterService} from '../search/filter.service';

import {BasketService} from '../basket/basket.service';
import { Node } from '../navigator/node';
import {MDMItem} from '../core/mdm-item';

import { EditViewComponent } from './editview.component';

import {PreferenceService} from '../core/preference.service';
import {Preference} from '../core/preference.service';
import {QueryService, Query, SearchResult, Row} from './query.service';

@Component({
  selector: 'mdm-tableview',
  templateUrl: 'tableview.component.html',
  providers: [ViewService],
  styleUrls: [ './tableview.component.css']
})
export class TableviewComponent implements OnInit, OnChanges {
  @Input() view: View;

  @Input() nodes: Node[];
  @Input() items: MDMItem[];
  @Input() results: SearchResult;
  @Input() isShopable = false;
  @Input() isRemovable = false;

  results1: SearchResult;

  @ViewChild(EditViewComponent)
  private editViewComponent: EditViewComponent;

  constructor(private viewService: ViewService,
    private basketService: BasketService,
    private _pref: PreferenceService,
    private queryService: QueryService) {
  }

  ngOnInit() {
    /*
    if (this.items) {
      this.loadData(this.items);
    }
    */
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['view']) {
      this.view = changes['view'].currentValue;
    }
    /*
    if (changes['items']) {
      this.loadData(this.items);
    }
    */
  }
/*
  loadData(items: MDMItem[]) {
    if (this.view) {
      this.queryService.queryItems(items, this.view.cols.map(c => c.type + '.' + c.name)).subscribe(r => this.results = r);
    }
  }*/

  getData(row: Row, col: ViewColumn) {
    let resultColumn = row.columns.find(c => c.type === col.type && c.attribute === col.name);

    if (resultColumn) {
      return resultColumn.value;
    }
  }

  nodeDataProvider(node: Node, col: ViewColumn) {
    if (node.type !== col.type) {
      return '-';
    } else {
      for (let index in node.attributes) {
        if (node.attributes[index].name === col.name) {
          return node.attributes[index].value;
        }
      }
    }
    return '-';
  }

  functionalityProvider(isShopable: boolean, row: Row) {
    if (isShopable) {
      this.basketService.add(new MDMItem(row.source, row.type, +row.id));
    } else {
      this.basketService.remove(new MDMItem(row.source, row.type, +row.id));
    }
  }

  getNodeClass(type: string) {
    switch (type) {
      case 'Test':
        return 'test';
      case 'TestStep':
        return 'teststep';
      case 'Measurement':
      case 'MeaResult':
        return 'measurement';
      case 'ChannelGroup':
      case 'SubMatrix':
        return 'channelgroup';
      case 'Channel':
      case 'MeaQuantity':
        return 'channel';
    }
  }
}

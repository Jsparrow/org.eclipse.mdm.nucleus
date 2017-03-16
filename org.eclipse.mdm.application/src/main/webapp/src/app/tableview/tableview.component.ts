import { Component, Input, ViewChild, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';

import { PreferenceView, View, ViewColumn, ViewService } from './tableview.service';
import { NavigatorService } from '../navigator/navigator.service';
import { FilterService } from '../search/filter.service';

import { BasketService} from '../basket/basket.service';
import { Node } from '../navigator/node';
import { MDMItem } from '../core/mdm-item';

import { EditViewComponent } from './editview.component';

import { PreferenceService } from '../core/preference.service';
import { Preference } from '../core/preference.service';
import { QueryService, Query, SearchResult, Row } from './query.service';

import {DataTableModule,SharedModule,ContextMenuModule,MenuItem} from 'primeng/primeng';
import {Type, Exclude, plainToClass, serialize, deserialize} from 'class-transformer';

export class TestItem {
  name: string;
  constructor(name: string) {
    this.name = name;
  }
}

@Component({
  selector: 'mdm-tableview',
  templateUrl: 'tableview.component.html',
  styleUrls: [ './tableview.component.css']
})
export class TableviewComponent implements OnInit, OnChanges {

  @Input() view: View;
  @Input() results: SearchResult;
  @Input() isShopable = false;
  @Input() isRemovable = false;

  items: MenuItem[] = [
              {label: 'In Baum zeigen', icon: 'glyphicon glyphicon-remove', command: (event) => this.openInTree(this.menuSelectedRow, null)},
              {label: 'In Warenkorb legen', icon: 'glyphicon glyphicon-shopping-cart', command: (event) => this.selectedRows.forEach(r => this.basketService.add(r.getItem()))}
          ];

  menuSelectedRow: Row;
  selectedRows: Row[] = [];


  p: any;
  activeItems: MDMItem[] = [];
  public static readonly pageSize = 5;

  constructor(private viewService: ViewService,
    private basketService: BasketService,
    private _pref: PreferenceService,
    private queryService: QueryService,
    private navigatorService: NavigatorService) {
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['view']) {
      this.view = changes['view'].currentValue;
    }
  }

  onRowClick(event: any) {
    let index = this.selectedRows.findIndex(row => row === event.data);
    if (index >= 0) {
      this.selectedRows.splice(index);
    } else {
      this.selectedRows.push(event.data);
    }
  }

  onContextMenuSelect(event: any) {
    this.menuSelectedRow = event.data;
  }

  customSort(event: any) {
    let comparer = function (row1: Row, row2: Row): number {
      let value1 = row1.getColumn(event.field) || '';
      let value2 = row2.getColumn(event.field) || '';

      if (value1 < value2) {
        return event.order;
      }
      else if (value1 > value2) {
        return -1 * event.order;
      }
      else {
        return 0;
      }
    };

    this.results.rows.sort(comparer);
  }

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
    let item = row.getItem();
    console.log(item);
    if (isShopable) {
      this.basketService.add(item);
    } else {
      this.basketService.remove(item);
    }
  }

  getNodeClass(type: string) {
    switch (type) {
      case 'StructureLevel':
        return 'pool';
      case 'MeaResult':
        return 'measurement';
      case 'SubMatrix':
        return 'channelgroup';
      case 'MeaQuantity':
        return 'channel';
      default:
        return type.toLowerCase();
    }
  }

  getRowTitle(row: Row) {
    return [row.source, row.type, row.id].join('/');
  }

  isActive(row: Row) {
    let item = row.getItem();
    return this.activeItems.findIndex(ai => ai.equals(item)) === -1 ? '' : 'active';
  }

  onClickRow(row: Row, event: MouseEvent) {
    if (event.shiftKey && this.activeItems.length > 0) {
      let lastItem = this.activeItems[this.activeItems.length - 1];
      let lastIndex = this.results.rows.findIndex(r => r.getItem().equals(lastItem));
      let thisIndex = this.results.rows.findIndex(r => r.getItem().equals(row.getItem()));

      this.results.rows.slice(Math.min(lastIndex, thisIndex), Math.max(lastIndex, thisIndex) + 1)
            .map(r => r.getItem())
            .forEach(item => {
              if (this.activeItems.findIndex(i => i.equals(item)) === -1) {
                this.activeItems.push(item);
              }
            });
    } else if (event.ctrlKey) {
        this.selectRow(row);
    } else {
      if ( this.activeItems.length > 1 || (this.activeItems.length !== 0 && !row.getItem().equals(this.activeItems[0]))) {
        this.activeItems = [];
      }
      this.selectRow(row);
    }
  }

  selectRow(row: Row) {
    let item = row.getItem();
    let index = this.activeItems.findIndex(ai => ai.equals(item));
    if (index === -1) {
      this.activeItems.push(item);
    } else {
      this.activeItems.splice(index, 1);
    }
  }

  openInTree(row: Row, event: Event) {
    this.navigatorService.fireOnOpenInTree(row.getItem());
  }
}

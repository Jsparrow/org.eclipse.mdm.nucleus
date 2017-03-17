import { Component, Input, ViewChild, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';

import { PreferenceView, View, ViewColumn, ViewService, Style } from './tableview.service';
import { NavigatorService } from '../navigator/navigator.service';
import { FilterService } from '../search/filter.service';

import { BasketService} from '../basket/basket.service';
import { Node } from '../navigator/node';
import { MDMItem } from '../core/mdm-item';

import { EditViewComponent } from './editview.component';

import { PreferenceService } from '../core/preference.service';
import { Preference } from '../core/preference.service';
import { QueryService, Query, SearchResult, Row } from './query.service';

import {DataTableModule, SharedModule, ContextMenuModule, MenuItem} from 'primeng/primeng';
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
  styleUrls: ['./tableview.component.css']
})
export class TableviewComponent implements OnInit {

  public static readonly pageSize = 5;

  @Input() view: View;
  @Input() results: SearchResult;
  @Input() isShopable = false;
  @Input() isRemovable = false;

  @Input() menuItems: MenuItem[] = [];

  menuSelectedRow: Row;
  selectedRows: Row[] = [];
  buttonColumns = 3;

  constructor(private viewService: ViewService,
    private basketService: BasketService,
    private _pref: PreferenceService,
    private queryService: QueryService,
    private navigatorService: NavigatorService) {
  }

  ngOnInit() {
    this.menuItems.push({
      label: 'In Baum zeigen',
      icon: 'glyphicon glyphicon-tree-conifer',
      command: (event) => this.openInTree()
    });
    this.menuItems.push({
      label: 'Selektion zurücksetzen',
      icon: 'glyphicon glyphicon-unchecked',
      command: (event) => this.selectedRows = []
    });
  }

  onContextMenuSelect(event: any) {
    this.menuSelectedRow = event.data;
  }

  onColResize(event: any) {
    this.view.columns[event.element.cellIndex - this.buttonColumns].style = {'width': event.element.clientWidth.toString() + 'px'};
  }

  onColReorder(event: any) {
    let dragIndex = event.dragIndex > this.buttonColumns ? event.dragIndex - this.buttonColumns : 0;
    let dropIndex = event.dropIndex > this.buttonColumns ? event.dragIndex - this.buttonColumns : 0;

    let tmp = this.view.columns[dragIndex ];
    this.view.columns[dragIndex] = this.view.columns[dropIndex];
    this.view.columns[dropIndex] = tmp;
  }

  customSort(event: any) {
    let comparer = function(row1: Row, row2: Row): number {
      let value1 = row1.getColumn(event.field) || '';
      let value2 = row2.getColumn(event.field) || '';

      if (value1 < value2) {
        return event.order;
      } else if (value1 > value2) {
        return -1 * event.order;
      } else {
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

  onRowClick(e: any) {
    let row: Row = e.data;
    let event: MouseEvent = e.originalEvent;
    if (event.shiftKey && this.selectedRows.length > 0) {
      let lastRow = this.selectedRows[this.selectedRows.length - 1];
      let lastIndex = this.results.rows.findIndex(r => r.equals(lastRow));
      let thisIndex = this.results.rows.findIndex(r => r.equals(row));
      if (this.selectedRows.findIndex(sr => sr.equals(row)) > -1) {
      } else {
        let min = Math.min(lastIndex, thisIndex);
        let max = Math.max(lastIndex, thisIndex);
        this.results.rows.slice(min, max + 1)
          .forEach(r => {
            if (this.selectedRows.findIndex(sr => sr.equals(r)) === -1) {
              this.selectedRows.push(r);
            }
          });
      }
    } else if (event.ctrlKey) {
      this.selectRow(row);
    } else {
      if (this.selectedRows.length > 1 || (this.selectedRows.length !== 0 && !row.equals(this.selectedRows[0]))) {
        this.selectedRows = [];
      }
      this.selectRow(row);
    }
  }

  selectRow(row: Row) {
    let index = this.selectedRows.findIndex(ai => ai.equals(row));
    if (index === -1) {
      this.selectedRows.push(row);
    } else {
      this.selectedRows.splice(index, 1);
    }
  }

  openInTree() {
    if (this.selectedRows && this.selectedRows.length === 0) {
      this.navigatorService.fireOnOpenInTree([ this.menuSelectedRow.getItem() ]);
    } else {
      this.navigatorService.fireOnOpenInTree(this.selectedRows.map(r => r.getItem()));
    }

  }
}

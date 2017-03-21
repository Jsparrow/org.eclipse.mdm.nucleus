import { Component, Input, ViewChild, OnInit, OnChanges, SimpleChanges} from '@angular/core';

import { View} from './tableview.service';
import { NavigatorService } from '../navigator/navigator.service';

import { BasketService} from '../basket/basket.service';

import { EditViewComponent } from './editview.component';
import { SearchResult, Row } from './query.service';

import {DataTableModule, SharedModule, ContextMenuModule, MenuItem} from 'primeng/primeng';

@Component({
  selector: 'mdm-tableview',
  templateUrl: 'tableview.component.html',
  styleUrls: ['./tableview.component.css']
})
export class TableviewComponent implements OnInit, OnChanges {

  public static readonly pageSize = 5;
  readonly buttonColumns = 3;

  @Input() view: View;
  @Input() results: SearchResult;
  @Input() isShopable = false;
  @Input() isRemovable = false;
  @Input() menuItems: MenuItem[] = [];

  menuSelectedRow: Row;
  selectedRows: Row[] = [];

  constructor(private basketService: BasketService,
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

  ngOnChanges(changes: SimpleChanges) {
    if (changes['results'] && this.view) {
        this.customSort({ 'field': this.view.getSortField(), 'order': this.view.getSortOrder() });
    }
  }

  onContextMenuSelect(event: any) {
    this.menuSelectedRow = event.data;
  }

  onColResize(event: any) {
    let index = event.element.cellIndex - this.buttonColumns;
    if (index > -1) {
      this.view.columns[index].style = { 'width': event.element.clientWidth.toString() + 'px' };
    }
  }

  onColReorder(event: any) {
    let dragIndex = event.dragIndex > this.buttonColumns ? event.dragIndex - this.buttonColumns : 0;
    let dropIndex = event.dropIndex > this.buttonColumns ? event.dropIndex - this.buttonColumns : 0;

    let tmp = this.view.columns[dragIndex];
    this.view.columns[dragIndex] = this.view.columns[dropIndex];
    this.view.columns[dropIndex] = tmp;
  }

  onSort(event: any) {
    let a = event.field.split('.');
    this.view.setSortOrder(a[0], a[1], event.order);
  }

  customSort(event: any) {
    let comparer = function(row1: Row, row2: Row): number {
      let value1 = row1.getColumn(event.field) || '';
      let value2 = row2.getColumn(event.field) || '';

      if (!isNaN(<any>value1) && !isNaN(<any>value2)) {
        if (value1 === value2) {
          return 0;
        } else {
          return (+value1 < +value2 ? -1 : 1) * event.order;
        }
      } else {
        return value1.localeCompare(value2) * -event.order;
      }
    };

    this.results.rows.sort(comparer);
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

  openInTree(row?: Row) {
    if (row) {
      this.selectedRows = [row];
    }
    if (this.selectedRows && this.selectedRows.length === 0 && this.menuSelectedRow) {
      this.navigatorService.fireOnOpenInTree([this.menuSelectedRow.getItem()]);
    } else if (row) {
      this.navigatorService.fireOnOpenInTree([row.getItem()]);
    } else {
      this.navigatorService.fireOnOpenInTree(this.selectedRows.map(r => r.getItem()));
    }
  }
}

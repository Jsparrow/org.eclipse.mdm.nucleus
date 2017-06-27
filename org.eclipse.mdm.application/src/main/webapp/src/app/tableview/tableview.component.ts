/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

import { Component, Input, ViewChild, OnInit, OnChanges, SimpleChanges} from '@angular/core';

import { View, ViewColumn} from './tableview.service';
import { NavigatorService } from '../navigator/navigator.service';
import { SearchAttribute } from '../search/search.service';
import { BasketService} from '../basket/basket.service';

import { EditViewComponent } from './editview.component';
import { SearchResult, Row } from './query.service';
import { Node } from '../navigator/node';
import { NodeService } from '../navigator/node.service';

import {DataTableModule, SharedModule, ContextMenuModule, MenuItem} from 'primeng/primeng';
import {MDMNotificationService} from '../core/mdm-notification.service';

@Component({
  selector: 'mdm-tableview',
  templateUrl: 'tableview.component.html',
  styleUrls: ['./tableview.component.css']
})
export class TableviewComponent implements OnInit, OnChanges {

  public static readonly pageSize = 5;
  readonly buttonColumns = 3;

  readonly TtlAddToBasket = 'Zum Warenkorb hinzufügen';
  readonly TtlOpenInTree = 'Öffnen in';
  readonly TtlRemoveFromBasket = 'Aus dem Warenkorb entfernen';

  @Input() view: View;
  @Input() results: SearchResult;
  @Input() isShopable = false;
  @Input() isRemovable = false;
  @Input() menuItems: MenuItem[] = [];
  @Input() selectedEnvs: Node[];
  @Input() searchAttributes: { [env: string]: SearchAttribute[] };
  @Input() environments: Node[];

  public menuSelectedRow: Row;
  public selectedRows: Row[] = [];
  public columnsToShow: ViewColumn[];
  public readonly buttonColStyle = {'width': '3%'};
  public btnColHidden = false;

  constructor(private basketService: BasketService,
              private navigatorService: NavigatorService,
              private nodeService: NodeService,
              private notificationService: MDMNotificationService) {
  }

  ngOnInit() {
    this.menuItems.push({
      label: 'Im Baum zeigen',
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
    if (changes['selectedEnvs'] || changes['view']) {
      this.updateColumnsToShow();
    }
  }

  updateColumnsToShow() {
    if (this.view && this.selectedEnvs && this.selectedEnvs.length > 0 && this.searchAttributes[this.selectedEnvs[0].sourceName]) {
      let relevantCols: string[] = [];
      this.selectedEnvs.forEach(env =>
        relevantCols = relevantCols.concat(this.searchAttributes[env.sourceName].map(sa =>
          sa.boType.toLowerCase() + sa.attrName.toLowerCase())
        )
      );
      relevantCols = Array.from(new Set(relevantCols));

      this.view.columns.filter(col => {
        if (relevantCols.findIndex(ct => ct === col.type.toLowerCase() + col.name.toLowerCase()) > -1) {
          col.hidden = false;
        } else {
          col.hidden = true;
        }
        this.btnColHidden = false;
      });
    } else if (this.view && this.selectedEnvs && this.selectedEnvs.length === 0) {
      this.btnColHidden = true;
      this.view.columns.forEach(vc => vc.hidden = true);
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
    return this.TtlOpenInTree + ': ' + [NodeService.mapSourceNameToName(this.environments, row.source), row.type, row.id].join('/');
  }

  getIconTitle() {
    return this.isShopable ? this.TtlAddToBasket : this.TtlRemoveFromBasket;
  }
  onRowClick(e: any) {
    let row: Row = e.data;
    this.nodeService.getNodeFromItem(row.getItem()).subscribe(
      node => this.navigatorService.fireSelectedNodeChanged(node),
      error => this.notificationService.notifyError('Knoten konnte nicht berechnet werden.', error)
    );
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

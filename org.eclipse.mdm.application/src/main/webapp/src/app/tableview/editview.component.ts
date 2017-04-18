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

import {Component, Input, ViewChild, OnInit, OnDestroy, Output, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';

import {View, ViewColumn, ViewService} from './tableview.service';
import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';
import {SearchService, SearchAttribute} from '../search/search.service';
import {SearchattributeTreeComponent} from '../searchattribute-tree/searchattribute-tree.component';
import {MDMNotificationService} from '../core/mdm-notification.service';

import {TreeNode} from 'primeng/primeng';
import {ModalDirective} from 'ng2-bootstrap';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';
import {Observable} from 'rxjs/Observable';
import {classToClass} from 'class-transformer';

@Component({
  selector: 'edit-view',
  templateUrl: './editview.component.html',
  styles: ['.remove {color:black; cursor: pointer; float: right}', '.icon { cursor: pointer; margin: 0px 5px; }']
})
export class EditViewComponent implements OnInit {
  @ViewChild('lgModal') public childModal: ModalDirective;
  @ViewChild(SearchattributeTreeComponent) tree: SearchattributeTreeComponent;

  environments: Node[] = [];
  isReadOnly = false;
  currentView: View = new View();
  searchAttributes: { [env: string]: SearchAttribute[] } = {};
  typeAheadValues: {label: string, group: string, attribute: SearchAttribute }[] = [];

  selectedAttribute: SearchAttribute;

  @Output()
  coloumnsSubmitted = new EventEmitter<View>();

  constructor(private nodeService: NodeService,
    private viewService: ViewService,
    private searchService: SearchService,
    private notificationService: MDMNotificationService) { }

  ngOnInit() {
    this.tree.onNodeSelect$.subscribe(node => this.selectNode(node));

    this.nodeService.getNodes()
      .subscribe(
        envs => {
          this.searchService.loadSearchAttributesStructured(envs.map(e => e.sourceName))
            .map(attrs => attrs['measurements'])
            .subscribe(attrs => { this.searchAttributes = attrs; this.environments = envs; });
      });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['searchAttributes'] || changes['environments']) {
      let ar = Object.keys(this.searchAttributes)
          .map(env => this.searchAttributes[env])
          .reduce((acc, value) => acc.concat(value), <SearchAttribute[]> [])
          .map(sa => { return { 'label': sa.boType + '.' + sa.attrName, 'group': sa.boType, 'attribute': sa }; });

      this.typeAheadValues = this.uniqueBy(ar, p => p.label);
    }
  }

  showDialog(currentView: View) {
    this.currentView = classToClass(currentView);
    this.isNameReadOnly();
    this.childModal.show();
    return this.coloumnsSubmitted;
  }

  closeDialog() {
    this.childModal.hide();
  }

  remove(col: ViewColumn) {
    this.currentView.columns = this.currentView.columns.filter(c => c !== col);
  }

  isLast(col: ViewColumn) {
    return this.currentView.columns.indexOf(col) === this.currentView.columns.length - 1;
  }

  isFirst(col: ViewColumn) {
    return this.currentView.columns.indexOf(col) === 0;
  }

  moveUp(col: ViewColumn) {
    if (!this.isFirst(col)) {
      let oldIndex = this.currentView.columns.indexOf(col);
      let otherCol = this.currentView.columns[oldIndex - 1];
      this.currentView.columns[oldIndex] = otherCol;
      this.currentView.columns[oldIndex - 1] = col;
    }
  }

  moveDown(col: ViewColumn) {
    if (!this.isLast(col)) {
      let oldIndex = this.currentView.columns.indexOf(col);
      let otherCol = this.currentView.columns[oldIndex + 1];
      this.currentView.columns[oldIndex] = otherCol;
      this.currentView.columns[oldIndex + 1] = col;
    }
  }

  selectNode(node: TreeNode) {
    if (node.type !== 'attribute') {
      return;
    }
    this.pushViewCol(new ViewColumn(node.parent.label, node.label));
  }

  pushViewCol(viewCol: ViewColumn) {
    if (viewCol && this.currentView.columns.findIndex(c => viewCol.equals(c)) === -1 ) {
      this.currentView.columns.push(viewCol);
    } else {
      this.notificationService.notifyInfo('Das Attribut wurde bereits ausgewählt!', 'Info');
    }
  }

  isAsc(col: ViewColumn) {
    return col.sortOrder === 1;
  }
  isDesc(col: ViewColumn) {
    return col.sortOrder === -1;
  }
  isNone(col: ViewColumn) {
    return col.sortOrder === null;
  }

  toggleSort(col: ViewColumn) {
    if (col.sortOrder === null) {
      this.currentView.setSortOrder(col.type, col.name, 1);
    } else if (col.sortOrder === 1) {
      this.currentView.setSortOrder(col.type, col.name, -1);
    } else if (col.sortOrder === -1) {
      this.currentView.setSortOrder(col.type, col.name, null);
    }
  }

  private uniqueBy<T>(a: T[], key: (T) => any) {
    let seen = {};
    return a.filter(function(item) {
      let k = key(item);
      return seen.hasOwnProperty(k) ? false : (seen[k] = true);
    });
  }

  public typeaheadOnSelect(match: TypeaheadMatch) {
    this.pushViewCol(new ViewColumn(match.item.attribute.boType, match.item.attribute.attrName));
    this.selectedAttribute = undefined;
  }

  private isNameReadOnly() {
    return this.isReadOnly = (this.currentView.name === '') ? false : true;
  }

  applyChanges() {
    this.coloumnsSubmitted.emit(this.currentView);
    this.closeDialog();
  }
}

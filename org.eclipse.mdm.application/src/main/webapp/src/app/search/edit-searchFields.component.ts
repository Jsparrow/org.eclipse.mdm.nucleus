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


import {Component, ViewChild, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnInit} from '@angular/core';

import {SearchDefinition, SearchAttribute, SearchLayout} from './search.service';
import {FilterService, SearchFilter, Condition, Operator} from './filter.service';
import {NodeService} from '../navigator/node.service';

import {Node} from '../navigator/node';
import {MDMItem} from '../core/mdm-item';
import {SearchattributeTreeComponent} from '../searchattribute-tree/searchattribute-tree.component';

import {TypeaheadMatch} from 'ngx-bootstrap/typeahead';
import {ModalDirective} from 'ngx-bootstrap';
import {TreeNode} from 'primeng/primeng';
import {MDMNotificationService} from '../core/mdm-notification.service';
import {classToClass} from 'class-transformer';

export class SearchField {
  group: string;
  attribute: string;

  constructor(group?: string, attribute?: string) {
    this.group = group || '';
    this.attribute = attribute || '';
  }

  equals(searchField: SearchField) {
    return (searchField.group === this.group && searchField.attribute === this.attribute);
  }
}

@Component({
  selector: 'edit-searchFields',
  templateUrl: 'edit-searchFields.component.html',
})
export class EditSearchFieldsComponent implements OnChanges, OnInit {

  readonly LblApplyChanges = 'Änderungen übernehmen';
  readonly LblSearchFieldEditor = 'Suchfeld Editor';
  readonly LblSelectedSearchAttributes = 'Ausgewählte Suchattribute';
  readonly TtlClose = 'Schließen';
  readonly TtlRemove = 'Entfernen';

  @ViewChild('lgEditSearchFieldsModal') public childModal: ModalDirective;
  @ViewChild(SearchattributeTreeComponent) tree: SearchattributeTreeComponent;

  @Input() environments: Node[];
  @Input() searchAttributes: { [env: string]: SearchAttribute[] } = {};
  typeAheadValues: { label: string, group: string, attribute: SearchAttribute }[] = [];

  @Output()
  conditionsSubmitted = new EventEmitter<Condition[]>();

  layout: SearchLayout = new SearchLayout;
  conditions: Condition[] = [];
  selectedSearchAttribute: SearchAttribute;

  subscription: any;

  constructor(private filterService: FilterService,
    private notificationService: MDMNotificationService) { }

  ngOnInit() {
      this.tree.onNodeSelect$.subscribe(
        node => this.nodeSelect(node),
        error => this.notificationService.notifyError('Ausgewählter Knoten kann nicht aktualisiert werden.', error)
      );
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['searchAttributes'] || changes['environments']) {
      let ar = Object.keys(this.searchAttributes)
          .map(env => this.searchAttributes[env])
          .reduce((acc, value) => acc.concat(value), <SearchAttribute[]> [])
          .map(sa => { return { 'label': sa.boType + '.' + sa.attrName, 'group': sa.boType, 'attribute': sa }; });

      this.typeAheadValues = this.uniqueBy(ar, p => p.label);
    }
    if (changes['environments']) {
      this.conditionUpdated();
    }
  }

  show(conditions?: Condition[]) {
    if (conditions) {
      this.conditions = classToClass(conditions);
    } else {
      this.conditions = [];
    }
    this.conditionUpdated();
    this.childModal.show();
    return this.conditionsSubmitted;
  }

  conditionUpdated() {
    let envs = (this.environments || []).map(node => node.sourceName);
    this.layout = SearchLayout.createSearchLayout(envs, this.searchAttributes, this.conditions);
  }

  nodeSelect(node: TreeNode) {
    if (node && node.type === 'attribute') {
      let sa = <SearchAttribute>node.data;
      this.pushCondition(new Condition(sa.boType, sa.attrName, Operator.EQUALS, [], sa.valueType));
      this.conditionUpdated();
    }
  }

  removeCondition(condition: Condition) {
    let index = this.conditions.findIndex(c => condition.type === c.type && condition.attribute === c.attribute);
    this.conditions.splice(index, 1);
    this.conditionUpdated();
  }

  addSearchFields() {
    this.childModal.hide();
    this.conditionsSubmitted.emit(this.conditions);
  }

  public typeaheadOnSelect(match: TypeaheadMatch) {
    this.pushCondition(new Condition(match.item.attribute.boType,
      match.item.attribute.attrName, Operator.EQUALS, [], match.item.attribute.valueType));
    this.conditionUpdated();
    this.selectedSearchAttribute = undefined;
  }

  pushCondition(condition: Condition) {
    if (this.conditions.find(c => condition.type === c.type && condition.attribute === c.attribute)) {
      this.notificationService.notifyInfo('Das Suchfeld wurde bereits ausgewählt!', 'Info');
    } else {
      this.conditions.push(condition);
      this.conditionUpdated();
    }
  }

  mapSourceNameToName(sourceName: string) {
    return NodeService.mapSourceNameToName(this.environments, sourceName);
  }

  isLast(col: Condition) {
    let sourceName = this.layout.getSourceName(col);
    if (sourceName) {
      let conditionsInSameSource = this.layout.getConditions(sourceName);
      return conditionsInSameSource.indexOf(col) === conditionsInSameSource.length - 1;
    }
  }

  isFirst(col: Condition) {
    let sourceName = this.layout.getSourceName(col);
    if (sourceName) {
      let conditionsInSameSource = this.layout.getConditions(sourceName);
      return conditionsInSameSource.indexOf(col) === 0;
    }
  }

  moveUp(condition: Condition) {
    if (!this.isFirst(condition)) {
      let sourceName = this.layout.getSourceName(condition);
      if (sourceName) {
        let conditionsInSameSource = this.layout.getConditions(sourceName);

        let oldIndex = conditionsInSameSource.indexOf(condition);
        let otherCondition = conditionsInSameSource[oldIndex - 1];
        this.swap(condition, otherCondition);
      }
    }
  }

  moveDown(condition: Condition) {
    if (!this.isLast(condition)) {
      let sourceName = this.layout.getSourceName(condition);
      if (sourceName) {
        let conditionsInSameSource = this.layout.getConditions(sourceName);

        let oldIndex = conditionsInSameSource.indexOf(condition);
        let otherCondition = conditionsInSameSource[oldIndex + 1];
        this.swap(condition, otherCondition);
      }
    }
  }

  private swap(condition1: Condition, condition2: Condition) {
    let index1 = this.conditions.findIndex(c => c.type === condition1.type && c.attribute === condition1.attribute);
    let index2 = this.conditions.findIndex(c => c.type === condition2.type && c.attribute === condition2.attribute);

    let tmp = this.conditions[index1];
    this.conditions[index1] = this.conditions[index2];
    this.conditions[index2] = tmp;
    this.conditionUpdated();
  }

  private uniqueBy<T>(a: T[], key: (T) => any) {
    let seen = {};
    return a.filter(function(item) {
      let k = key(item);
      return seen.hasOwnProperty(k) ? false : (seen[k] = true);
    });
  }
}

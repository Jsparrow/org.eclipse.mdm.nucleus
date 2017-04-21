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

import {Component, ViewChild, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnInit} from '@angular/core';

import {SearchDefinition, SearchAttribute, SearchLayout} from './search.service';
import {FilterService, SearchFilter, Condition, Operator} from './filter.service';
import {NodeService} from '../navigator/node.service';

import {Node} from '../navigator/node';
import {MDMItem} from '../core/mdm-item';
import {SearchattributeTreeComponent} from '../searchattribute-tree/searchattribute-tree.component';

import {IDropdownItem, IMultiselectConfig} from 'ng2-dropdown-multiselect';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';
import {ModalDirective} from 'ng2-bootstrap';
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
      this.tree.onNodeSelect$.subscribe(node => this.nodeSelect(node));
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
  
  private uniqueBy<T>(a: T[], key: (T) => any) {
    let seen = {};
    return a.filter(function(item) {
      let k = key(item);
      return seen.hasOwnProperty(k) ? false : (seen[k] = true);
    });
  }
}

import {Component, ViewChild, Input, Output, EventEmitter} from '@angular/core';

import {SearchDefinition, SearchAttribute, SearchLayout} from './search.service';
import {FilterService, SearchFilter, Condition, Operator} from './filter.service';
import {NodeService} from '../navigator/node.service';
import {SearchattributeTreeService} from '../searchattribute-tree/searchattribute-tree.service';

import {Node} from '../navigator/node';
import {MDMItem} from '../core/mdm-item';
import {SearchattributeTreeComponent} from '../searchattribute-tree/searchattribute-tree.component';

import {IDropdownItem, IMultiselectConfig} from 'ng2-dropdown-multiselect';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';
import {ModalDirective} from 'ng2-bootstrap';
import {TreeNode} from 'primeng/primeng';
import {MDMNotificationService} from '../core/mdm-notification.service';

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
export class EditSearchFieldsComponent {

  @ViewChild('lgEditSearchFieldsModal') public childModal: ModalDirective;

  @Input() environments: Node[];
  @Input() searchableFields: { label: string, group: string, attribute: SearchAttribute }[] = [];

  @Output()
  conditionsSubmitted = new EventEmitter<Condition[]>();

  conditions: Condition[] = [];
  selectedSearchAttribute: SearchAttribute;

  constructor(private filterService: FilterService,
              private treeService: SearchattributeTreeService,
              private notificationService: MDMNotificationService) { }

  show(conditions?: Condition[]) {
    if (conditions) {
      this.conditions = conditions;
    } else {
      this.conditions = [];
    }
    this.treeService.onNodeSelect$.subscribe(node => this.nodeSelect(node));
    this.childModal.show();
    return this.conditionsSubmitted;
  }

  nodeSelect(node: TreeNode) {
    if (node.type === 'attribute') {
      let sa = <SearchAttribute>node.data;
      this.pushSearchField(new Condition(sa.boType, sa.attrName, Operator.EQUALS, [], sa.valueType));
    }
  }

  removeCondition(condition: Condition) {
    let index = this.conditions.findIndex(c => condition.type === c.type && condition.attribute === c.attribute);
    this.conditions.splice(index, 1);
  }

  addSearchFields() {
    this.childModal.hide();
    this.conditionsSubmitted.emit(this.conditions);
  }

  public typeaheadOnSelect(match: TypeaheadMatch) {
    this.pushSearchField(new Condition(match.item.attribute.boType, match.item.attribute.attrName, Operator.EQUALS, [], match.item.attribute.valueType));
    this.selectedSearchAttribute = undefined;
  }

  pushSearchField(condition: Condition) {
    if (this.conditions.find(c => condition.type === c.type && condition.attribute === c.attribute)) {
      this.notificationService.notifyInfo('Das Suchfeld wurde bereits ausgewählt!', 'Info');
    } else {
      this.conditions.push(condition);
    }
  }
}

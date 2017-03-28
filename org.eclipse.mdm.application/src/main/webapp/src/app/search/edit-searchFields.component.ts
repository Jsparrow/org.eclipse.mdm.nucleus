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
  @Input() searchAttributes: SearchAttribute[] = [];
  typeAheadValues: { label: string, group: string, attribute: SearchAttribute }[] = [];

  @Output()
  conditionsSubmitted = new EventEmitter<Condition[]>();

  conditions: Condition[] = [];
  selectedSearchAttribute: SearchAttribute;

  subscription: any;

  constructor(private filterService: FilterService,
    private notificationService: MDMNotificationService) { }

  ngOnInit() {
      this.tree.onNodeSelect$.subscribe(node => this.nodeSelect(node));
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['searchAttributes']) {
      let ar = [this.searchAttributes.map(sa => {
        return { 'label': sa.boType + '.' + sa.attrName, 'group': sa.boType, 'attribute': sa };
      })];
      this.typeAheadValues = ar.map(sa => this.uniqueBy(sa, p => p.label))[0];
    }
  }

  show(conditions?: Condition[]) {
    if (conditions) {
      this.conditions = classToClass(conditions);
    } else {
      this.conditions = [];
    }
    this.childModal.show();
    return this.conditionsSubmitted;
  }

  nodeSelect(node: TreeNode) {
    if (node && node.type === 'attribute') {
      let sa = <SearchAttribute>node.data;
      this.pushCondition(new Condition(sa.boType, sa.attrName, Operator.EQUALS, [], sa.valueType));
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
    this.pushCondition(new Condition(match.item.attribute.boType,
      match.item.attribute.attrName, Operator.EQUALS, [], match.item.attribute.valueType));
    this.selectedSearchAttribute = undefined;
  }

  pushCondition(condition: Condition) {
    if (this.conditions.find(c => condition.type === c.type && condition.attribute === c.attribute)) {
      this.notificationService.notifyInfo('Das Suchfeld wurde bereits ausgewählt!', 'Info');
    } else {
      this.conditions.push(condition);
    }
  }

  private uniqueBy<T>(a: T[], key: (T) => any) {
    let seen = {};
    return a.filter(function(item) {
      let k = key(item);
      return seen.hasOwnProperty(k) ? false : (seen[k] = true);
    });
  }
}

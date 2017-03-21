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

  nodes: TreeNode[] = [];
  searchFields: SearchField[] = [];
  selectedAttribute: SearchField;

  constructor(private filterService: FilterService,
              private treeService: SearchattributeTreeService,
              private notificationService: MDMNotificationService) { }

  show(conditions?: Condition[]) {
    if (conditions) {
      this.searchFields = conditions.map(cond => this.mapCondToSf(cond));
    } else {
      this.searchFields = [];
    }
    this.treeService.onNodeSelect$.subscribe(node => this.nodeSelect(node));
    this.childModal.show();
    return this.conditionsSubmitted;
  }

  mapCondToSf(cond: Condition) {
    return <SearchField>{ group: cond.type, attribute: cond.attribute };
  }

  nodeSelect(node: TreeNode) {
    if (node.type !== 'attribute') {
      return;
    }
    if (this.searchFields.findIndex(sf => (sf.group === node.parent.label && sf.attribute === node.label)) === -1) {
      this.pushSearchField(new SearchField(node.parent.label, node.label));
    }
  }

  removeSearchField(searchField: { group: string, attribute: string }) {
    let index = this.searchFields.findIndex(sf => sf.group === searchField.group && sf.attribute === searchField.attribute);
    this.searchFields.splice(index, 1);
  }

  addSearchFields() {
    let conditions = this.searchFields
       .map(sf => new Condition(sf.group, sf.attribute, Operator.EQUALS, [], this.getValueType(sf.attribute)));
    this.childModal.hide();
    this.conditionsSubmitted.emit(conditions);
  }

  getValueType(typ: string) {
    return typ.toLowerCase().indexOf('date') === 0
      || typ.toLowerCase().indexOf('date') === typ.length - 4
      || typ.indexOf('Date') !== -1
      || typ.toLowerCase().indexOf('_date') !== -1
      || typ.toLowerCase().indexOf('date_') !== -1
      ? 'date' : 'string';
  }

  public typeaheadOnSelect(match: TypeaheadMatch) {
    this.pushSearchField(new SearchField(match.item.attribute.boType, match.item.attribute.attrName));
    this.selectedAttribute = undefined;
  }

  pushSearchField(searchField: SearchField) {
    if (searchField && this.searchFields.findIndex(c => searchField.equals(c)) === -1 ) {
      this.searchFields.push(searchField);
    } else {
      this.notificationService.notifyInfo('Das Suchfeld wurde bereits ausgewählt!', 'Info');
    }
  }
}

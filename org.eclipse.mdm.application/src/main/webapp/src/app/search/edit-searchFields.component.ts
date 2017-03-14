import {Component, ViewChild, Input} from '@angular/core';

import {SearchService, SearchDefinition, SearchAttribute, SearchLayout} from './search.service';
import {FilterService, SearchFilter, Condition, Operator} from './filter.service';

import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';
import {MDMItem} from '../core/mdm-item';

import {IDropdownItem, IMultiselectConfig} from 'ng2-dropdown-multiselect';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';
import {ModalDirective} from 'ng2-bootstrap';
import {TreeModule, TreeNode} from 'primeng/primeng';


export class SearchField {
  group: string;
  attribute: string;

  constructor(group: string, attribute: string) {
    this.group = group;
    this.attribute = attribute;
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

  filter: SearchFilter = new SearchFilter('New Filter', [], '*', '', []);
  @Input() searchableFields: { label: string, group: string, attribute: SearchAttribute }[] = [];

  nodes: TreeNode[] = [];
  searchFields: SearchField[] = [];
  needSave: boolean;
  selectedAttribute: any;

  constructor(private searchService: SearchService,
    private filterService: FilterService,
    private nodeService: NodeService) { }

  show(filter: SearchFilter) {
    this.nodeService.getNodes().subscribe(nodes => this.nodes = nodes.map(n => this.mapNode(n)));
    this.filter = filter;
    this.needSave = false;
    this.childModal.show();
    this.searchFields = this.filter.conditions.map(cond => <SearchField>{ group: cond.type, attribute: cond.attribute });
  }

  loadNodes(event) {
    if (event.node) {
      event.node.children = this.getChildren(event.node);
    }
  }

  mapStringToTreeNode(strg: string, type: string, leaf: boolean) {
    return <TreeNode>{
      label: strg,
      leaf: leaf,
      type: type
    };
  }

  mapNode(node: Node) {
    let item = new MDMItem(node.sourceName, node.type, +node.id);

    return <TreeNode>{
      label: node.name,
      leaf: false,
      data: item
    };
  }

  getChildren(node: TreeNode): TreeNode[] {
    if (node.data) {
      return this.getSearchableGroups(node.data.source).map(g => this.mapStringToTreeNode(g, 'group', false));
    } else {
      return this.getSearchableAttributes(node.label).map(g => this.mapStringToTreeNode(g, 'attribute', true));
    }
  }

  getSearchableGroups(env: string) {
    let distinctGroupArray = [];
    let attributesPerEnv = this.searchService.groupByEnv(this.searchableFields.map(sf => sf.attribute));
    attributesPerEnv[env].map(attr => attr.boType)
      .forEach(boType => {
        if (distinctGroupArray.findIndex(bt => bt === boType) === -1) {
          distinctGroupArray.push(boType);
        }
      });
    return distinctGroupArray.sort((a, b) => a < b ? -1 : 1);
  }

  getSearchableAttributes(group: string) {
    let distinctAttrArray = [];
    this.searchableFields.filter(sf => sf.group === group)
      .map(sf => sf.attribute.attrName)
      .forEach(attr => {
        if (distinctAttrArray.findIndex(a => a === attr) === -1) {
          distinctAttrArray.push(attr);
        }
      });
    return distinctAttrArray.sort((a, b) => a < b ? -1 : 1);
  }

  nodeSelect(event) {
    if (event.node.type !== 'attribute') {
      return;
    }
    if (this.searchFields.findIndex(sf => (sf.group === event.node.parent.label && sf.attribute === event.node.label)) === -1) {
      this.searchFields.push(new SearchField(event.node.parent.label, event.node.label));
    }
  }

  removeSearchField(searchField: { group: string, attribute: string }) {
    let index = this.searchFields.findIndex(sf => sf.group === searchField.group && sf.attribute === searchField.attribute);
    this.searchFields.splice(index, 1);
  }

  addSearchFields() {
    this.filter.conditions = this.searchFields
      .map(sf => new Condition(sf.group, sf.attribute, Operator.EQUALS, [], this.getValueType(sf.attribute)));
    this.needSave = true;
    this.childModal.hide();
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
    this.searchFields.push(new SearchField(match.item.attribute.boType, match.item.attribute.attrName));
  }

}

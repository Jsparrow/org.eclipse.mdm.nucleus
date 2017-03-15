import {Component, OnInit, Input} from '@angular/core';

import {SearchService, SearchDefinition, SearchAttribute, SearchLayout} from '../search/search.service';
import {SearchattributeTreeService} from './searchattribute-tree.service';

import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';
import {MDMItem} from '../core/mdm-item';

import {IDropdownItem, IMultiselectConfig} from 'ng2-dropdown-multiselect';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';
import {ModalDirective} from 'ng2-bootstrap';
import {TreeModule, TreeNode} from 'primeng/primeng';

@Component({
  selector: 'searchattribute-tree',
  templateUrl: './searchattribute-tree.component.html',
  styleUrls: ['./searchattribute-tree.component.css']
})
export class SearchattributeTreeComponent implements OnInit {

  @Input() searchableFields: { label: string, group: string, attribute: SearchAttribute }[] = [];

  nodes: TreeNode[] = [];
  selectedAttribute: any;

  constructor(private searchService: SearchService,
    private nodeService: NodeService,
    private treeService: SearchattributeTreeService) { }

  ngOnInit() {
    this.nodeService.getNodes().subscribe(nodes => this.nodes = nodes.map(n => this.mapNode(n)));
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
    this.treeService.fireOnNodeSelect(event.node);
  }

}

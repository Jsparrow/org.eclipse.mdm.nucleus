import {Component, OnInit, Input, OnChanges, SimpleChanges, EventEmitter} from '@angular/core';

import {SearchService, SearchDefinition, SearchAttribute, SearchLayout} from '../search/search.service';

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
export class SearchattributeTreeComponent implements OnChanges {

  @Input() environments: Node[];
  @Input() searchAttributes: SearchAttribute[];

  lastClickTime = 0;
  nodes: TreeNode[] = [];
  selectedAttribute: { label: string, group: string, attribute: SearchAttribute };
  public onNodeSelect$ = new EventEmitter<TreeNode>();

  constructor(private searchService: SearchService,
    private nodeService: NodeService) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['searchAttributes'] && this.environments && this.environments.length > 0) {
      this.nodes = this.environments.map(n => this.mapRootNode(n));
    }

    if (changes['environments'] && this.environments && this.environments.length > 0) {
      this.nodes = this.environments.map(n => this.mapRootNode(n));
    }
  }

  // onDblClick(e: any) {
  //   let label = e.target.childNodes[0].data;
  // }

  mapRootNode(node: Node) {
    let item = new MDMItem(node.sourceName, node.type, +node.id);

    return <TreeNode>{
      label: node.name,
      leaf: false,
      type: 'env',
      data: item
    };
  }

  loadNodes(event: any) {
    if (event.node) {
      event.node.children = this.getChildren(event.node);
    }
  }

  mapType(group: { boType: string, attributes: SearchAttribute[] }) {
    return <TreeNode>{
      label: group.boType,
      leaf: false,
      type: 'group',
      data: group.attributes
    };
  }

  mapAttribute(attribute: SearchAttribute) {
    return <TreeNode>{
      label: attribute.attrName,
      leaf: true,
      type: 'attribute',
      data: attribute
    };
  }

  getChildren(node: TreeNode): TreeNode[] {
    if (node.type === 'env') {
      return this.getSearchableGroups(node.data.source)
        .sort((a, b) => a.boType.localeCompare(b.boType))
        .map(g => this.mapType(g));
    } else if (node.type === 'group') {
      return (<SearchAttribute[]> node.data)
        .sort((a, b) => a.attrName.localeCompare(b.attrName))
        .map(a => this.mapAttribute(a));
    } else {
      return [];
    }
  }

  getSearchableGroups(env: string): { boType: string, attributes: SearchAttribute[] }[] {
    let distinctGroupArray: { boType: string, attributes: SearchAttribute[] }[] = [];

    let attributesPerEnv = this.searchService.groupByEnv(this.searchAttributes);
    attributesPerEnv[env].forEach(attribute => {
      let item = distinctGroupArray.find(p => p.boType === attribute.boType);
      if (item && item.attributes.every(a => a.attrName !== attribute.attrName)) {
        item.attributes.push(attribute);
      } else if (!item) {
        distinctGroupArray.push({ boType: attribute.boType, attributes: [attribute]});
      }
    });

    return distinctGroupArray;
  }

  nodeSelect(event) {
    this.onNodeSelect$.emit(event.node);
    if (event.originalEvent.timeStamp - this.lastClickTime < 300) {
      if (!event.node.expanded && !event.node.children) {
        this.loadNodes(event);
      }
      event.node.expanded = !event.node.expanded;
    }
    this.lastClickTime = event.originalEvent.timeStamp;
  }
}

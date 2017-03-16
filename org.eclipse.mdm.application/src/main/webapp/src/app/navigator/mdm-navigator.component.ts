import {Component, OnInit} from '@angular/core';
import {TreeModule, TreeNode} from 'primeng/primeng';

import {MDMItem} from '../core/mdm-item';
import {Node} from '../navigator/node';
import {NodeService} from '../navigator/node.service';
import {QueryService} from '../tableview/query.service';
import {NodeproviderService} from './nodeprovider.service';
import {NavigatorService} from './navigator.service';


@Component({
  selector: 'mdm-navigator',
  templateUrl: './mdm-navigator.component.html',
  styles: [
    ':host >>> .ui-tree { display: inline-block; width: 100%; height: 100%; }',
    ':host >>> .ui-tree .ui-treenode .ui-treenode-content .ui-treenode-label { padding: 2px 3px; }',
    ':host >>> .ui-tree .ui-treenode .ui-treenode-content .ui-treenode-icon { margin: 0.2em 0; width: 15px; }'
  ]
})
export class MDMNavigatorComponent implements OnInit {

  selectedNodes: TreeNode[] = [];
  nodes: TreeNode[] = [];

  options = {
    getChildren: (node: TreeNode) => {
      return this.getChildren(node);
    }
  };

  constructor(private nodeService: NodeService,
    private queryService: QueryService,
    private nodeproviderService: NodeproviderService,
    private navigatorService: NavigatorService) {
  }

  ngOnInit() {
    this.reloadTree();
    this.nodeproviderService.nodeProviderChanged
      .subscribe(np => this.reloadTree());

    this.navigatorService.selectedNodeChanged
      .subscribe(node => this.selectNode(node));

    this.navigatorService.onOpenInTree
      .subscribe(item => this.openInTree(item));
  }

  nodeSelect(event) {
    this.navigatorService.setSelectedItem(event.node.data);
  }

  loadNode(event) {
    if (event.node && event.node.children === undefined) {
      return this.getChildren(event.node).then(nodes => event.node.children = nodes);
    }
  }

  reloadTree() {
    this.nodeService.getRootNodes().subscribe(n => {
      this.nodes = n.map(node => this.mapNode(node));
    });
  }

  onFocus(event: { eventName: string, node: TreeNode }) {
    this.navigatorService.setSelectedItem(event.node.data.item);
  }

  getNodeClass(item: MDMItem) {
    return 'icon ' + item.type.toLowerCase();
  }

  mapNode(node: Node) {
    let item = new MDMItem(node.sourceName, node.type, +node.id);

    return <TreeNode>{
      label: node.name,
      leaf: false,
      data: item,
      icon: this.getNodeClass(item)
    };
  }

  getChildren(node: TreeNode) {
    return this.nodeService.getNodesByUrl(this.nodeproviderService.getQueryForChildren(node.data))
      .map(nodes => nodes.map(n => this.mapNode(n)))
      .toPromise();
  }

  selectNode(event) {

  }

  typeToUrl(type: string) {
    switch (type) {
      case 'StructureLevel':
        return 'pools';
      case 'MeaResult':
        return 'measurements';
      case 'SubMatrix':
        return 'channelgroups';
      case 'MeaQuantity':
        return 'channels';
      default:
        return type.toLowerCase() + 's';
    }
  }

  openInTree(item: MDMItem) {
    this.selectedNodes = []
    let pathTypes = this.nodeproviderService.getPathTypes(item.type);
    if (pathTypes.length === 0) {
      alert('Items of this type are not displayed in the current Tree!');
    } else {
      let env = this.nodes.find(e => item.source === e.data.source);
      env.expanded = true;
      this.openChildrenRecursive(item, env, pathTypes, 1);
    }
  }

  openChildrenRecursive(item: MDMItem, current: TreeNode, pathTypes: string[], iii: number) {
    if (current.children) {
      this.expander(item, current, pathTypes, iii);
    } else {
      this.getChildren(current)
        .then(n => current.children = n)
        .then(() => {
          this.expander(item, current, pathTypes, iii);
        });
    }
  }

  expander(item: MDMItem, current: TreeNode, pathTypes: string[], iii: number) {
    let expandList: number[] = [];
    this.nodeService.searchNodes('filter=' + item.type + '.Id eq ' + item.id,
      item.source, this.typeToUrl(pathTypes[iii]))
      .subscribe(nodes => {
        expandList = nodes.map(n => n.id);
        current.children.filter(node => expandList.findIndex(n => n === node.data.id) > -1)
          .forEach(node => {
            if (++iii < pathTypes.length) {
              node.expanded = true;
              this.openChildrenRecursive(item, node, pathTypes, iii);
              this.scrollToSelectionPrimeNgDataTable(node);
            } else {
              this.selectedNodes.push(node);
              this.scrollToSelectionPrimeNgDataTable(node);
            }
          });
      });
  }

  /**
   * PrimeNG does not support scroll to view. This methods implements a
   * workaround by using HTML element IDs.
   * @see https://github.com/primefaces/primeng/issues/1278
   */
  scrollToSelectionPrimeNgDataTable(node: TreeNode) {
    let list = document.querySelectorAll('p-tree span#' + this.getId(node));

    if (list && list.length > 0) {
        list.item(0).scrollIntoView();
    }
  }

  getId(node: TreeNode) {
    return 'node_' + node.data.source + '_' + node.data.type + '_' + node.data.id;
  }
}

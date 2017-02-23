import {Component, ViewChild, OnInit} from '@angular/core';
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

  selectedFile: any;
  nodes = [];

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

  nodeSelect(event) {
    this.navigatorService.setSelectedItem(event.node.data);
  }

  loadNode(event) {
    if(event.node) {
      return this.getChildren(event.node).then(nodes => event.node.children = nodes);
    }
  }

  ngOnInit() {

    this.reloadTree();
    this.nodeproviderService.nodeProviderChanged
      .subscribe(np => this.reloadTree());

    this.navigatorService.selectedNodeChanged
      .subscribe(node => this.selectNode(node));

  }

  reloadTree() {
    this.nodeService.getRootNodes().subscribe(n => {
      this.nodes = n.map(node => this.mapNode(node));
    });
  }

  onFocus(event: { eventName: string, node: TreeNode}) {
     this.navigatorService.setSelectedItem(event.node.data.item);
  }

  getNodeClass(item: MDMItem) {
      return 'icon ' + item.type.toLowerCase();
  }

  mapNode(node: Node) {
    let item = new MDMItem(node.sourceName, node.type, +node.id);

    return <TreeNode> {
      label: node.name,
      leaf: false,
      data: item,
      icon: this.getNodeClass(item)
    }
  }

  getChildren(node: TreeNode) {
    return this.nodeService.getNodesByUrl(this.nodeproviderService.getQueryForChildren(node.data))
      .map(nodes => nodes.map(n => this.mapNode(n)))
      .toPromise();
  }

  selectNode(node: Node) {
    // TODO
  }
}

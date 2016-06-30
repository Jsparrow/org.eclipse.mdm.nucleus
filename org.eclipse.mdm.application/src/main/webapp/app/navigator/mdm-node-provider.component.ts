// *******************************************************************************
//   * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
//   * All rights reserved. This program and the accompanying materials
//   * are made available under the terms of the Eclipse Public License v1.0
//   * which accompanies this distribution, and is available at
//   * http://www.eclipse.org/legal/epl-v10.html
//   *
//   * Contributors:
//   * Dennis Schroeder - initial implementation
//   *******************************************************************************
import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';

import {Node} from './node';
import {NodeService} from './node.service';

@Component({
  selector: 'mdm-node-provider',
  template: require('../../templates/navigator/mdm-node-provider.component.html'),
  directives: [MDMNodeProviderComponent],
  providers: [],
  inputs: ['rootNode', 'indent']
})
export class MDMNodeProviderComponent implements OnInit {
  @Output() selectingNode = new EventEmitter<Node>();
  @Output() onActive = new EventEmitter<Node>();
  @Input() activeNode: Node;

  rootNode: Node;
  indent: number;

  openNode: Node;
  nodes: Node[];
  errorMessage: string;
  selectedNode: Node;

  constructor(
    private _nodeService: NodeService) {}

  ngOnInit(){
    this.getNodes();
  }

  getNodes(){
    if (this.rootNode.type !== "Channel") {
      this._nodeService.getNodes(this.rootNode).subscribe(
        nodes => this.nodes = nodes,
        error => this.errorMessage = <any>error);
    }
  }
  onOpenNode(node:Node){
    if (this.openNode === node && this.openNode.active) {
      this.openNode.active = false;
      return
    }
    if (this.openNode) {
      this.openNode.active = false;
    }
    this.openNode = node;
    this.openNode.active = true;
  }
  updateActiveNode(node){
    this.activeNode = node
    this.onActive.emit(node)
  }
  onSelectNode(node){
    this.selectingNode.emit(node);
  }
  updateSelectedNode(node) {
    this.selectedNode = node
    this.activeNode = node
    this.onActive.emit(node)
    this.selectingNode.emit(node)
  }
  isActive(node){
    if (this._nodeService.compareNode(this.activeNode, node)) {return "active"}
  }
  isOpen(node: Node){
    if (node.active) {
      return "glyphicon glyphicon-chevron-down"
    } else {
      return "glyphicon glyphicon-chevron-right"
    }
  }
  getMargin(){
    return this.indent + 10;
  }
}
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
import {ACCORDION_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';

import {Node} from './node';
import {NodeService} from './node.service';

import {MDMNodeProviderComponent} from './mdm-node-provider.component';

import '../../templates/navigator/navigator.css';

@Component({
  selector: 'mdm-navigator',
  template: require('../../templates/navigator/mdm-navigator.component.html'),
  directives: [MDMNodeProviderComponent, ACCORDION_DIRECTIVES],
  providers: []
})
export class MDMNavigatorComponent implements OnInit {
  @Output() selectingNode = new EventEmitter<Node>();
  @Output() onActive = new EventEmitter<Node>();
  @Input() activeNode: Node;

  openNode: Node;
  selectedNode: Node;
  nodes: Node[];
  errorMessage: string;

  constructor(
    private _nodeService: NodeService) {}

  getNodes(){
    let node: Node;
    this._nodeService.getNodes(node).subscribe(
      nodes => this.nodes = nodes,
      error => this.errorMessage = <any>error);
  }
  ngOnInit(){
    this.getNodes();
  }
  onOpenNode(node: Node) {
    this.activateNode(node);
  }
  updateSelectedNode(node) {
    this.selectedNode = node
    this.activeNode = node
    this.onActive.emit(node)
    this.selectingNode.emit(node)
  }
  updateActiveNode(node){
    this.activeNode = node
    this.onActive.emit(node)
  }
  onSelectNode(node){
    this.selectingNode.emit(node);
    this.onActive.emit(node);
  }
  private activateNode(node: Node){
    if (this.openNode === node && this.openNode.active) {
      this.openNode.active = false;
      return
    }
    if (this.openNode) {
      this.openNode.active = true;
    }
    this.openNode = node;
    this.openNode.active = true;
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
    return 10;
  }
  
  getNodeClass(){
    return "icon environment";
  }
}

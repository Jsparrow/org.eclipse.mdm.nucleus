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

@Component({
  selector: 'mdm-navigator',
  templateUrl: 'templates/navigator/mdm-navigator.component.html',
  directives: [MDMNodeProviderComponent, ACCORDION_DIRECTIVES],
  providers: []
})
export class MDMNavigatorComponent implements OnInit {
  @Output() selectingNode = new EventEmitter();

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
    updateSelectedNode(arg) {
      this.selectedNode = arg
      this.selectingNode.emit(arg)
    }
    onSelectNode(node){
      this.selectingNode.emit(node);
    }
    private activateNode(node: Node){
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
    isActive(node: Node){
      if (node.active) {
        return "glyphicon glyphicon-chevron-down"
      } else {
        return "glyphicon glyphicon-chevron-right"
      }
    }
    getMargin(){
      return 10;
    }
}

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
import {Component, OnInit, Input, Output, EventEmitter} from 'angular2/core';
import {Router, RouteConfig} from 'angular2/router';

import {Node} from './node';
import {NodeService} from './node.service';

import {MDMNodeProviderComponent} from './mdm-node-provider.component';

// import {MDMDetailComponent} from './mdm-detail.component';
// import {MDMDetailViewComponent} from './mdm-detail-view.component';
// import {MDMDescriptiveDataComponent} from './mdm-detail-descriptive-data.component';

@Component({
  selector: '[mdm-navigator]',
  templateUrl: 'templates/mdm-navigator.component.html',
  directives [MDMNodeProviderComponent],
  providers: []
})
export class MDMNavigatorComponent implements OnInit {
  @Output() selectingNode = new EventEmitter();

  openNode: Node;
  actions: Actions[];
  nodes: Node[];
  errorMessage: string;

  constructor(
    private _nodeService: NodeService
    private _router: Router) {}

    getNodes(){
      this._nodeService.getNodes().subscribe(
        nodes => this.nodes = nodes
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
      if node.active {
        return "glyphicon glyphicon-chevron-down"
      } else {
        return "glyphicon glyphicon-chevron-right"
      }
    }
    getMargin(){
      return 10;
    }
}

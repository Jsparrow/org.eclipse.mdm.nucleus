// Copyright (c) 2016 Gigatronik Ingolstadt GmbH
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
import {Component, OnInit} from 'angular2/core';
import {Router, RouteConfig} from 'angular2/router';

import {Node} from './node';
import {NodeService} from './node.service';

import {MDMNodeProviderComponent} from './mdm-node-provider.component';


import {MDMDetailComponent} from './mdm-detail.component';
import {MDMDetailViewComponent} from './mdm-detail-view.component';

@Component({
  selector: 'mdm-navigator',
  template: `<div class="container-fluid">
   <div class="row">
     <div class="col-sm-3">
       <div class="panel-group" style="height: 90vh; overflow-y: auto">
         <div class="panel panel-default">
           <div class="panel-heading">
             <h4 class="panel-title">
               <a data-toggle="collapse" href="#root"><span class="glyphicon glyphicon-th-list"></span> Navigator</a>
             </h4>
           </div>
           <div id="root" class="panel-collapse collapse">
             <ul class="list-group">
               <template ngFor #node [ngForOf]="nodes">
                 <li class="list-group-item"><span style="cursor: pointer;" [style.margin-left.px]="getMargin()" [ngClass]="isActive(node)" (click)="onOpenNode(node)"></span> <a style="color:black; cursor: pointer;" (click)="onSelectNode(node)">{{node.name}}</a></li>
                 <div *ngIf="node.active" class="panel-collapse">
                   <ul class="list-group"><mdm-node-provider  [rootNode]="openNode" [margin]="getMargin()">Loading...</mdm-node-provider></ul>
                 </div>
               </template>
             </ul>
           </div>
         </div>
       </div>
     </div>
     <div class="col-sm-9">
      <mdm-detail></mdm-detail>
     </div>
   </div>
  </div>`,
  directives [MDMNodeProviderComponent, MDMDetailComponent, MDMDetailViewComponent],
  providers: []
})
@RouteConfig([
  { path: '/detailView', component: MDMDetailViewComponent, name: 'DetailView', useAsDefault: true }
])
export class MDMNavigatorComponent implements OnInit {
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
    onSelectNode(node){
      this._nodeService.setSelectedNode(node)
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

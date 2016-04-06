// Copyright (c) 2016 Gigatronik Ingolstadt GmbH
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
import {Component} from 'angular2/core';

import {Node} from './node';
import {NodeService} from './node.service';

import {Action} from './action';
import {ActionService} from './action.service';

@Component({
  selector: 'mdm-detail-view',
  template: `
  <table class="table table-hover">
    <thead>
      <tr>
        <th>Attribute</th>
        <th>Value</th>
        <th>Unit</th>
      </tr>
    </thead>
    <tbody *ngIf="_nodeService.selectedNode">
      <tr *ngFor="#attr of _nodeService.selectedNode.attributes">
        <td>{{getTrans(attr.name)}}</td>
        <td>{{attr.value}}</td>
        <td>{{attr.unit}}</td>
      </tr>
    </tbody>
  </table>
  `,
  directives [],
  providers: [],
  inputs: []
})

export class MDMDetailViewComponent {
  constructor(private _nodeService: NodeService){}

  getTrans(attr: string){
    let pos = this._nodeService.locals.map(function(e) { return e.name; }).indexOf(attr);
    if pos !== -1 {
      return this._nodeService.locals[pos].localizedName;
    }
    return attr;
  }
}

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
import {Component, Input, OnChanges} from 'angular2/core';

import {NodeService} from './node.service';
import {ContextService} from './context.service';

@Component({
  selector: 'mdm-detail-context',
  templateUrl: 'templates/mdm-detail-descriptive-data.component.html',
  providers: [ContextService]
})

export class MDMDescriptiveDataComponent implements OnChanges{
  @Input() selectedNode: Node;

  constructor(private _nodeService: NodeService
              private _contextService: ContextService){}

  _diff: boolean = false;
  contexts: [];
  errorMessage: string;
  status: string = "loading...";

  ngOnChanges(changes: {[propName: string]: SimpleChange}){
    this.getContext(changes['selectedNode'].currentValue);
  }

  getContext(node: Node){
    this.contexts = undefined;
    if (node && (node.type.toLowerCase() == "measurement" || node.type.toLowerCase() == "teststep")) {
      this.status = "loading...";
      this._contextService.getContext(node).subscribe(
        contexts => this.contexts = contexts,
        error => this.errorMessage = <any>error);
    } else {
      this.status = "no context data available"
    }
  }

  getTrans(attr: string){
    let pos = this._nodeService.locals.map(function(e) { return e.name; }).indexOf(attr);
    if pos !== -1 {
      return this._nodeService.locals[pos].localizedName
    }
    return attr;
  }

  diffToggle(){
    this._diff = !this._diff
  }
  diff(attr1: string, attr2: string){
    if (attr1 !== attr2 && this._diff) {
      return 'danger';
    }
  }
}

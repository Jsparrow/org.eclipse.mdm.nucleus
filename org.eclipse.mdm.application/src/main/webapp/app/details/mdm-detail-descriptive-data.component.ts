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
import {Component, Input, OnChanges, SimpleChange} from '@angular/core';
import {TAB_DIRECTIVES, ACCORDION_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';

import {NodeService} from '../navigator/node.service';
import {ContextService} from './context.service';
import {Context} from './context';
import {Node} from '../navigator/node';

@Component({
  selector: 'mdm-detail-context',
  template: require('../../templates/details/mdm-detail-descriptive-data.component.html'),
  providers: [ContextService],
  directives: [TAB_DIRECTIVES, ACCORDION_DIRECTIVES]
})

export class MDMDescriptiveDataComponent implements OnChanges{
  @Input() selectedNode: Node;

  constructor(private _nodeService: NodeService,
              private _contextService: ContextService){}

  _diff: boolean = false;
  contexts: Context[];
  errorMessage: string;
  status: string = "loading...";

  uut:string = "UnitUnderTest";
  te:string = "TestEquipment";
  ts:string = "TestSequence";

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
      this.status = "keine Beschreibende Daten vorhanden"
    }
  }

  getTrans(attr: string){
    let pos = this._nodeService.locals.map(function(e) { return e.name; }).indexOf(attr);
    if (pos !== -1) {
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

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
import {Component, Input, onChange} from 'angular2/core';

import {Node} from './node';
import {NodeService} from './node.service';

import {Localization} from './localization';
import {LocalizationService} from './localization.service';

import {Action} from './action';
import {ActionService} from './action.service';

@Component({
  selector: 'mdm-detail-view',
  templateUrl: 'templates/mdm-detail-view.component.html',
  directives [],
  providers: [],
  inputs: []
})

export class MDMDetailViewComponent implements onChange{
  @Input() selectedNode: Node;
  locals: Localization[] = [];

  constructor(private _nodeService: NodeService
              private _loaclService: LocalizationService){}

  ngOnChanges(changes: {[propName: string]: SimpleChange}){
    this.locals = [];
    if this.selectedNode{
      this._loaclService.getLocalization(this.selectedNode).subscribe(
        locals => this.locals = locals
        error => this.errorMessage = <any>error);
    }
  }

  getTrans(attr: string){
    let pos = this.locals.map(function(e) { return e.name; }).indexOf(attr);
    if pos !== -1 {
      return this.locals[pos].localizedName
    }
    return attr;
  }
}

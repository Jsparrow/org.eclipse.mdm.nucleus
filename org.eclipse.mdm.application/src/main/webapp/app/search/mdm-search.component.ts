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
import {Component} from '@angular/core'
import {DynamicForm} from './dynamic-form.component';

import {DROPDOWN_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';

import {SearchService} from './search.service';
import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';


@Component({
  selector: 'mdm-search',
  templateUrl: 'templates/search/mdm-search.component.html',
  directives: [DynamicForm, DROPDOWN_DIRECTIVES],
  providers:  [SearchService]
})
export class MDMSearchComponent {
  searches:any[] = []
  definitions:any
  groups:any[] = []
  envs:Node[]
  selectedEnv: string = ''
  type: string = ''
  errorMessage: string

  constructor(private service: SearchService,
              private nodeservice: NodeService) {
    this.definitions = service.getDefinitions();
    let node: Node;
    this.nodeservice.getNodes(node).subscribe(
      nodes => this.setEvns(nodes),
      error => this.errorMessage = <any>error);
  }

  setEvns(envs){
    this.envs = envs
    if (envs[0]) {
      this.selectedEnv = envs[0].sourceName
      this.selected(this.definitions.options[0].value)
    }
  }

  selectEnv(env){
    this.selectedEnv = env;
  }

  selected(type:string){
    this.type = type
    this.service.getSearches(type, this.selectedEnv).then(defs => this.groupBy(defs))
  }

  groupBy(defs){
    this.searches = defs
    let groups = []
    defs.forEach(function(obj){
      let str = obj.key
      let a = str.split(".")

      var i = groups.map(function(x) {return x.name; }).indexOf(a[0]);
      if (i == -1) {
        groups.push({name:a[0], items:[obj]})
      } else {
        groups[i].items.push(obj)
      }
    })
    this.groups = groups
  }
}

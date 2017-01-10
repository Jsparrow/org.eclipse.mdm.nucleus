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

import {SearchService} from './search.service';
import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';


@Component({
  selector: 'mdm-full-text-search',
  templateUrl: 'mdm-full-text-search.component.html',
  providers:  [SearchService]
})
export class MDMFullTextSearchComponent {
  nodes : Node[] = []
  envs : Node[] = []
  errorMessage : string = ""
  selectedEnv : Node[] = []

  constructor(private service: NodeService){
    let node: Node;
    this.service.getNodes(node).subscribe(
      nodes => this.setEvns(nodes),
      error => this.errorMessage = <any>error);
  }

  setEvns(envs){
    this.envs = envs
    this.selectedEnv = envs.slice()
  }

  selectEnv(env: string){
    let i = this.selectedEnv.map(function(e){return e.sourceName}).indexOf(env)
    if (i > -1) {
        this.selectedEnv.splice(i, 1)
    } else {
      let e = this.envs.map(function(e){return e.sourceName}).indexOf(env)
      this.selectedEnv.push(this.envs[e])
    }
  }

  onSubmit(query: string){
    this.nodes = []
    for (let i in this.selectedEnv) {
      this.search(query, this.selectedEnv[i].sourceName)
    }
  }

  search(query: string, env: string){
    this.service.searchFT(query, env).subscribe(
      nodes => this.nodes = this.nodes.concat(nodes),
      error => this.errorMessage = <any>error
    );
  }
}

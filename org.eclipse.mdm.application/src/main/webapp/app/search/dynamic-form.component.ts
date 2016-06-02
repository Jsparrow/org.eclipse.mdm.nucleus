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
import {Component, Input, OnInit, OnChanges, SimpleChange} from '@angular/core';
import {ControlGroup} from '@angular/common';

import {ACCORDION_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';

import {SearchBase} from './search-base';
import {SearchService} from './search.service';
import {SearchControlService} from './search-control.service';
import {DynamicFormSearchComponent} from './dynamic-form-search.component';
import {Node} from '../navigator/node';
import {NodeService} from '../navigator/node.service';
import {BasketService} from '../basket/basket.service';


@Component({
  selector:'dynamic-form',
  templateUrl:'templates/search/dynamic-form.component.html',
  directives: [DynamicFormSearchComponent, ACCORDION_DIRECTIVES],
  providers:  [SearchControlService]
})
export class DynamicForm implements OnChanges, OnInit{
  @Input() searches: SearchBase<any>[] = [];
  @Input() groups = [];
  @Input() env: string;
  @Input() type: string;
  form: ControlGroup;
  nodes: Node[] = [];
  errorMessage: string;

  constructor(private scs: SearchControlService,
              private service: NodeService,
              private basket: BasketService) {}

  ngOnInit(){
    this.form = this.scs.toControlGroup(this.searches);
  }
  ngOnChanges(){
    this.form = this.scs.toControlGroup(this.searches)
  }
  onSubmit() {
    this.getResults(this.form.value)
  }

  getResults(values) {
    let query = "filter="
    for (var prop in values){
      if (values.hasOwnProperty(prop)){
        if (values[prop]){
          query = query + prop + " eq " + values[prop] + " and "
        }
      }
    }
    if (query == "filter=") {
      return
    }
    this.service.serachNodes(query.slice(0,-5), this.env, this.type).subscribe(
      nodes => this.nodes = nodes,
      error => this.errorMessage = <any>error);
  }

  add2Basket(node: Node){
    this.basket.addNode(node);
  }
}

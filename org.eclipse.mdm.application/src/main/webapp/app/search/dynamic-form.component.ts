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
import {FormGroup} from '@angular/forms';
import {MDMItem} from '../core/mdm-item';
import {SearchBase} from './search-base';
import {SearchService} from './search.service';
import {SearchControlService} from './search-control.service';
import {DynamicFormSearchComponent} from './dynamic-form-search.component';
import {Node} from '../navigator/node';
import {NodeService} from '../navigator/node.service';
import {BasketService} from '../basket/basket.service';
import {LocalizationService} from '../localization/localization.service';


@Component({
  selector:'dynamic-form',
  templateUrl: 'dynamic-form.component.html',
  providers:  [SearchControlService]
})
export class DynamicForm implements OnChanges, OnInit{
  @Input() groups = [];
  @Input() env: Node[];
  @Input() type: string;
  form: FormGroup;
  nodes: Node[] = [];
  errorMessage: string;

  constructor(private scs: SearchControlService,
              private service: NodeService,
              private basket: BasketService,
              private localservice : LocalizationService) {}

  ngOnInit(){
    this.form = this.scs.toControlGroup(this.groups);
  }
  ngOnChanges(){
    this.form = this.scs.toControlGroup(this.groups);
  }
  onSubmit() {
    this.nodes = []
    this.getResults(this.form.value);
  }

  hasActiveItems(group) {
    let g = this.groups.map(function(x){return x.name}).indexOf(group)
    let i = this.groups[g].items.map(function(x){
      return x.active
    }).filter(function(v){
      return v == true
    })
    if (i.length > 0){return true}
    return false
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
    for (let i in this.env){
      this.service.searchNodes(query.slice(0,-5), this.env[i].sourceName, this.type).subscribe(
        nodes => this.nodes = this.nodes.concat(nodes),
        error => this.errorMessage = <any>error);
    }
  }
  add2Basket(node: Node){
    this.basket.add(new MDMItem(node.sourceName, node.type, node.id));
  }

  getTrans(label: string){
    return this.localservice.getTranslation(label, "")
  }
}

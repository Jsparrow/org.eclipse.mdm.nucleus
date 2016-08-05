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

import {DROPDOWN_DIRECTIVES, ACCORDION_DIRECTIVES, TYPEAHEAD_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {MODAL_DIRECTVES, BS_VIEW_PROVIDERS} from 'ng2-bootstrap/ng2-bootstrap';

import {SearchService} from './search.service';
import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';
import {LocalizationService} from '../localization/localization.service';

@Component({
  selector: 'mdm-search',
  template: require('../../templates/search/mdm-search.component.html'),
  directives: [DynamicForm, DROPDOWN_DIRECTIVES, MODAL_DIRECTVES, ACCORDION_DIRECTIVES, TYPEAHEAD_DIRECTIVES],
  providers:  [SearchService],
  viewProviders: [BS_VIEW_PROVIDERS]
})
export class MDMSearchComponent {
  definitions:any
  ungrouped:any[] = []
  groups:any[] = []
  selectedGroups:any[] = []
  envs:Node[] = []
  selectedEnv: Node[] = []
  type: any = {label: "Versuchen"}
  errorMessage: string

  constructor(private service: SearchService,
              private nodeservice: NodeService,
              private localservice: LocalizationService) {
    this.definitions = service.getDefinitions();
    let node: Node;
    this.nodeservice.getNodes(node).subscribe(
      nodes => this.setEvns(nodes),
      error => this.errorMessage = <any>error);
  }

  setEvns(envs){
    this.envs = envs
    for (let i = 0; i < envs.length; i++) {
      this.selectedEnv.push(envs[i])
    }
    this.selectDef(this.definitions.options[0])
  }

  selectEnv(env: string){
    let i = this.selectedEnv.map(function(e){return e.sourceName}).indexOf(env)
    if (i > -1) {
        this.selectedEnv.splice(i, 1)
    } else {
      let e = this.envs.map(function(e){return e.sourceName}).indexOf(env)
      this.selectedEnv.push(this.envs[e])
    }
    this.selectDef(this.type)
  }

  selectItem(item){
    let a = item.key.split(".")
    let g = this.groups.map(function(x) {return x.name; }).indexOf(a[0]);
    let i = this.groups[g].items.indexOf(item)
    if (this.groups[g].items[i].active) {
      this.groups[g].items[i].active = false
    } else {
      this.groups[g].items[i].active = true
    }
  }

  selectDef(type:any){
    this.type = type
    this.groups = []
    this.ungrouped = []
    for (let i = 0; i < this.selectedEnv.length; i++){
      this.service.getSearches(type.value, this.selectedEnv[i].sourceName).then(defs => this.groupBy(defs))
    }
  }

  public typeaheadOnSelect(e:any):void {
    this.selectItem(e.item)
  }

  private arrayUnique(source, target) {
    source.forEach(function(item) {
      let i = target.map(function(x) {return x.key}).indexOf(item.key)
      if (i != -1) {return}
      target.push(item)
    })
    return target
  }

  groupBy(defs){
    this.ungrouped = this.arrayUnique(defs, this.ungrouped)
    this.transTypeAHead()
    let groups = this.groups.slice()
    defs.forEach(function(obj){
      let str = obj.key
      let a = str.split(".")
      let g = groups.map(function(x) {return x.name; }).indexOf(a[0]);
      if (g == -1) {
        groups.push({name:a[0], items:[obj]})
      } else {
        let i = groups[g].items.map(function(x) {return x.key}).indexOf(obj.key)
        if (i != -1) {return}
        groups[g].items.push(obj)
      }
    })
    this.groups = groups
  }

  isActive(item) {
    if (item.active) { return "active"}
    return
  }

  transTypeAHead(){
    for(let i in this.ungrouped){
      if(this.ungrouped[i].labelt){continue}
      let a = this.ungrouped[i].label.split(".")
      this.ungrouped[i].labelt = this.getTrans(a[0]) + "." + this.getTrans(this.ungrouped[i].label)
    }
  }

  getTrans(label: string){
    let type = "",
    comp = ""
    if (label.includes(".")){
      let a = label.split(".")
      type = a[0]
      comp = a[1]
    } else {
      type = label
    }
    return this.localservice.getTranslation(type, comp)
  }
}

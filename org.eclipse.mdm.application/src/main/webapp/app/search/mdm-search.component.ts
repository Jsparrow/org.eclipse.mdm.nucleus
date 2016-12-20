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
import {FilterService, SearchFilter, Condition} from './filter.service';
import {NodeService} from '../navigator/node.service';
import {BasketService} from '../basket/basket.service';
import {LocalizationService} from '../localization/localization.service';

import {Node} from '../navigator/node';

import {TableviewComponent} from '../tableview/tableview.component';

@Component({
  selector: 'mdm-search',
  template: require('../../templates/search/mdm-search.component.html'),
  styles: [ 'table.searchdefinition td { vertical-align: middle; padding: 4px 8px; } '],
  directives: [DynamicForm, DROPDOWN_DIRECTIVES, MODAL_DIRECTVES, ACCORDION_DIRECTIVES, TYPEAHEAD_DIRECTIVES, TableviewComponent],
  providers:  [SearchService, FilterService],
  viewProviders: [BS_VIEW_PROVIDERS],
  inputs: []
})
export class MDMSearchComponent {

  nodes : Node[] = []
  definitions:any
  ungrouped:any[] = []
  groups:any[] = []
  selectedGroups:any[] = []
  envs:Node[] = []
  selectedEnv: Node[] = []
  type: any = {label: "Ergebnistyp wählen"}
  errorMessage: string
  filters: SearchFilter[];
  selectedFilter: SearchFilter;

  isAdvancedSearchOpen: boolean = false;
  isSearchResultsOpen: boolean = false;

  constructor(private searchService: SearchService,
              private filterService: FilterService,
              private nodeService: NodeService,
              private localService: LocalizationService,
              private basketService: BasketService) {
    this.definitions = searchService.getDefinitions();
    this.filters = filterService.getFilters();
    let node: Node;
    this.nodeService.getNodes(node).subscribe(
      nodes => this.setEvns(nodes),
      error => this.errorMessage = <any>error);
  }

  ngOnInit() {
    this.filters = this.filterService.getFilters();
    this.selectedFilter = this.filters[0];
    this.filterService.filterChanged$.subscribe(filter => this.onFilterChanged(filter));
  }

  onFilterChanged(filter: SearchFilter) {
    this.filters = this.filterService.getFilters();
    this.selectedFilter = filter;
  }

  setEvns(envs){
    this.envs = envs
    for (let i = 0; i < envs.length; i++) {
      this.selectedEnv.push(envs[i])
    }
    this.selectDef(this.definitions.options[0])
  }

  selectEnv(env: Node){
    this.selectedEnv.push(env);
    this.selectDef(this.type)
  }
  
  onSubmit(query: string){
    this.nodes = []
    for (let i in this.selectedEnv) {
      this.search(query, this.selectedEnv[i].sourceName)
    }
  }

  search(query: string, env: string){
    this.nodeService.searchFT(query, env).subscribe(
      nodes => { this.nodes = this.nodes.concat(nodes); this.isSearchResultsOpen = true; },
      error => this.errorMessage = <any>error
    );
  }

  selectFilter(filter: SearchFilter) {
    this.filterService.setSelectedFilter(filter);
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
      this.searchService.getSearches(type.value, this.selectedEnv[i].sourceName).then(defs => this.groupBy(defs))
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
    return this.localService.getTranslation(type, comp)
  }
  
  add2Basket(node: Node){
    if (node){
      this.basketService.addNode(node);
    }
  }

  selectConditionOperator(condition: Condition, operator: string){
    condition.operator = operator;
  }
}

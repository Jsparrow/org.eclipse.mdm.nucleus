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
import {Component, ViewChild} from '@angular/core';
import {DynamicForm} from './dynamic-form.component';

import {SearchService, SearchDefinition, SearchAttribute} from './search.service';
import {DropdownSearch} from './search-dropdown';
import {SearchBase} from './search-base';

import {FilterService, SearchFilter, Condition, Operator} from './filter.service';
import {NodeService} from '../navigator/node.service';
import {BasketService} from '../basket/basket.service';
import {QueryService, Query, SearchResult, Filter} from '../tableview/query.service';

import {LocalizationService} from '../localization/localization.service';

import {Node} from '../navigator/node';
import {MDMItem} from '../core/mdm-item';

import {TableviewComponent} from '../tableview/tableview.component';
import {ViewComponent} from '../tableview/view.component';

import {View} from '../tableview/tableview.service';
import { IDropdownItem, IMultiselectConfig  } from 'ng2-dropdown-multiselect';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';

@Component({
  selector: 'mdm-search',
  templateUrl: 'mdm-search.component.html',
  providers:  [SearchService, FilterService, QueryService],
  inputs: []
})
export class MDMSearchComponent {

  filters: SearchFilter[];
  selectedFilter: SearchFilter;
  selectedView: View;

  // environments: Node[] = [];
  // type: SearchDefinition;
  searchText: string = '';

  searchableFields: SearchBase<any>[] = [];

  definitions: SearchDefinition[];

  errorMessage: string;

  results: SearchResult;

  isAdvancedSearchOpen: boolean = false;
  isSearchResultsOpen: boolean = false;

  currentSearch: any = {};
  public dropdownModel: IDropdownItem[];
  public dropdownConfig: IMultiselectConfig = { showCheckAll: false, showUncheckAll: false };

  @ViewChild(ViewComponent)
  private viewComponent: ViewComponent;

  @ViewChild(TableviewComponent)
  private tableViewComponent: TableviewComponent;

  constructor(private searchService: SearchService,
              private queryService: QueryService,
              private filterService: FilterService,
              private nodeService: NodeService,
              private localService: LocalizationService,
              private basketService: BasketService) {

              }
  ngOnInit() {
    this.definitions = this.searchService.getDefinitionsSimple();

    this.filters = this.filterService.getFilters();
    this.selectedFilter = this.filters[0];


    this.filterService.filterChanged$.subscribe(filter => this.onFilterChanged(filter));
    this.viewComponent.onViewSelected.subscribe(view => this.selectedView = view);

    this.nodeService.getNodes().subscribe(
      nodes => this.setEnvironments(nodes),
      error => this.errorMessage = <any>error);

    this.dropdownModel = [];
  }

  onConditionChanged(condition: Condition) {
    console.log(condition);
    this.calcCurrentSearch();
  }

  onFilterChanged(filter: SearchFilter) {
    this.filters = this.filterService.getFilters();
    this.selectedFilter = filter;
  }

  setEnvironments(environments: Node[]) {
    this.selectedFilter.environments = environments.map(e => e.sourceName);
    this.dropdownModel = environments.map(env => <IDropdownItem> { id: env.sourceName, label: env.name, selected: true });
    this.selectResultType(this.definitions[0]);
    this.calcCurrentSearch();
  }

  selectResultType(type: any) {
    this.selectedFilter.resultType = type.type;
    this.updateSearches();
  }

  getSearchDefinition(type: string) {
    return this.definitions.find(def => def.type === type);
  }

  updateSearches() {
    let type = this.getSearchDefinition(this.selectedFilter.resultType).value;

    this.selectedFilter.environments.forEach(env =>
      this.searchService.getSearches(type, env).then(defs => this.searchableFields = defs));

    let map = this.searchService.get(this.selectedFilter.environments, type);
  }

  isEnvSelected(env) {
    return this.dropdownModel.find(item => item.id === env.sourceName && item.selected);
  }

  onSearch() {
    let type = this.getSearchDefinition(this.selectedFilter.resultType).value;
    this.searchService.getSearchAttributesPerEnvs(this.selectedFilter.environments, type)
      .subscribe(attrs => this.search(attrs));
  }

  search(attrs: SearchAttribute[]) {
    let query = this.filterService.convertToQuery(this.selectedFilter, attrs, this.selectedView);
    this.queryService.query(query).subscribe(
      result => {
        this.results = <SearchResult> result;
        this.isSearchResultsOpen = true;
      },
      error => this.errorMessage = <any>error
    );
  }

  calcCurrentSearch() {
    let type = this.getSearchDefinition(this.selectedFilter.resultType).value;
    let conditions = this.selectedFilter.conditions;

    this.searchService.getSearchAttributesPerEnvs(this.selectedFilter.environments, type)
      .subscribe(attrs => this.setCurrentSearch(this.selectedFilter.environments, conditions, attrs));
  }

  setCurrentSearch(envs: string[], conditions: Condition[], attrs: SearchAttribute[]) {
    this.currentSearch = this.filterService.env2Conditions(envs, conditions, this.filterService.groupByEnv(attrs));
    console.log(this.currentSearch);
  }

  getEnvs(currentSearch: any) {
    return Object.keys(currentSearch) || [];
  }

  selectFilter(filter: SearchFilter) {
    this.filterService.setSelectedFilter(filter);
  }

  addToFilter(field: string) {
    let splitted = field.split('.');

    let type = splitted[0];
    let attribute = splitted[1];

    let condition = new Condition(type, attribute, Operator.EQUALS, []);

    this.selectedFilter.conditions.push(condition);
    this.calcCurrentSearch();
  }

  public isAttributeAvailableInEnv(envName: string, type: string, attribute: string) {

    return true;
  }

  public typeaheadOnSelect(match: TypeaheadMatch) {
    let item: SearchBase<any> = match.item;
    this.addToFilter(item.key);
  }

  isActive(item) {
    return item.active ? 'active' : '';
  }

  add2Basket(node: Node) {
    if (node) {
      this.basketService.add(new MDMItem(node.sourceName, node.type, node.id));
    }
  }

  selectConditionOperator(condition: Condition, operator: Operator) {
    condition.operator = operator;
  }

  getOperators() {
    return Operator.values();
  }

  getOperatorName(op: Operator) {
    return Operator.toString(op);
  }

  removeCondition(value: [string, Condition]) {
    this.selectedFilter.conditions = this.selectedFilter.conditions
        .filter(c => !(c.type === value[1].type && c.attribute === value[1].attribute));

    this.calcCurrentSearch();
  }

  resetFilter() {
    this.filters = this.filterService.getFilters();
    this.selectedFilter = this.filters.find(f => f.name === this.selectedFilter.name);
  }
}

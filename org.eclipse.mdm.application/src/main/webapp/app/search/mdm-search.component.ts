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

import {SearchService, SearchDefinition, SearchAttribute, SearchLayout} from './search.service';
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

import { ModalDirective } from 'ng2-bootstrap';

@Component({
  selector: 'mdm-search',
  templateUrl: 'mdm-search.component.html',
})
export class MDMSearchComponent {

  filters: SearchFilter[] = [];
  selectedFilter: SearchFilter = new SearchFilter('No filter selected', [], '*', '', []);
  selectedView: View;
  filterName = '';

  searchableFields: { label: string, group: string, attribute: SearchAttribute }[] = [];

  definitions: SearchDefinition[];

  errorMessage: string;

  results: SearchResult;

  isAdvancedSearchOpen = false;
  isSearchResultsOpen = false;

  layout: SearchLayout = new SearchLayout;
  public dropdownModel: IDropdownItem[];
  public dropdownConfig: IMultiselectConfig = { showCheckAll: false, showUncheckAll: false };


  @ViewChild(ViewComponent)
  private viewComponent: ViewComponent;

  @ViewChild(TableviewComponent)
  private tableViewComponent: TableviewComponent;

  @ViewChild('lgSaveModal')
  private childSaveModal: ModalDirective;

  constructor(private searchService: SearchService,
              private queryService: QueryService,
              private filterService: FilterService,
              private nodeService: NodeService,
              private localService: LocalizationService,
              private basketService: BasketService) {

              }
  ngOnInit() {
    this.definitions = this.searchService.getDefinitionsSimple();
    this.filterService.getFilters().subscribe(
      filters => { this.filters = filters;
                   this.filters.push(new SearchFilter('No filter selected', [], '*', '', []));
                   this.selectedFilter = this.filters.find(f => f.name === 'Standard');
                   if (this.selectedFilter === undefined) {
                      this.selectedFilter = this.filters.find(f => f.name === 'No filter selected');
                   }
                   this.getEnvironments();
                 }
    );

    this.filterService.filterChanged$.subscribe(filter => this.onFilterChanged(filter));
    this.viewComponent.onViewSelected.subscribe(view => this.selectedView = view);

    this.dropdownModel = [];
  }

  onConditionChanged(condition: Condition) {
    this.calcCurrentSearch();
  }

  onFilterChanged(filter: SearchFilter) {
    this.getEnvironments();
    this.selectedFilter = filter;
  }

  getEnvironments() {
    this.nodeService.getNodes().subscribe(
      nodes => this.setEnvironments(nodes),
      error => this.errorMessage = <any>error);
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

    this.searchableFields = [];

    this.selectedFilter.environments.forEach(env =>
      this.searchService.loadSearchAttributes(type, env)
        .map(attrs => attrs.map(sa => {
          return { 'label': sa.boType + '.' + sa.attrName, 'group': sa.boType, 'attribute': sa };
      }))
      .subscribe(attrs => this.searchableFields = this.searchableFields.concat(attrs)));
  }

  onSearch() {
    let type = this.getSearchDefinition(this.selectedFilter.resultType).value;
    this.searchService.getSearchAttributesPerEnvs(this.selectedFilter.environments, type)
      .subscribe(attrs => this.search(attrs));
  }

  search(attrs: SearchAttribute[]) {
    let query = this.searchService.convertToQuery(this.selectedFilter, attrs, this.selectedView);
    this.queryService.query(query).subscribe(
      result => {
        this.results = <SearchResult> result;
        this.isSearchResultsOpen = true;
      },
      error => this.errorMessage = <any>error
    );
  }

  calcCurrentSearch() {
    let environments = this.selectedFilter.environments;
    let conditions = this.selectedFilter.conditions;
    let type = this.getSearchDefinition(this.selectedFilter.resultType).value;

    this.searchService.getSearchLayout(environments, conditions, type)
        .subscribe(l => this.layout = l);
  }

  getEnvs(currentSearch: any) {
    return Object.keys(currentSearch) || [];
  }

  selectFilter(filter: SearchFilter) {
    this.filterService.setSelectedFilter(filter);
  }

  resetFilter() {
    this.onFilterChanged(this.filters.find(f => f.name === 'No filter selected'));
  }

  saveFilter() {
    this.selectedFilter.name = this.filterName;
    this.filterService.saveFilter(this.selectedFilter);
    this.childSaveModal.hide();
  }

  addCondition(field: SearchAttribute) {
    let condition = new Condition(field.boType, field.attrName, Operator.EQUALS, [], field.valueType);

    this.selectedFilter.conditions.push(condition);
    this.calcCurrentSearch();
  }

  removeCondition(condition: Condition) {
    this.selectedFilter.conditions = this.selectedFilter.conditions
        .filter(c => !(c.type === condition.type && c.attribute === condition.attribute));

    this.calcCurrentSearch();
  }

  public typeaheadOnSelect(match: TypeaheadMatch) {
    this.addCondition(match.item.attribute);
  }

  selected2Basket() {
    this.tableViewComponent.activeItems.forEach(item => this.basketService.add(item));
  }

  showSaveModal() {
    this.childSaveModal.show();
  }
}

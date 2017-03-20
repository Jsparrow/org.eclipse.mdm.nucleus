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
import {Component, ViewChild, OnInit, Input} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {SearchService, SearchDefinition, SearchAttribute, SearchLayout} from './search.service';
import {DropdownSearch} from './search-dropdown';
import {SearchBase} from './search-base';

import {FilterService, SearchFilter, Condition, Operator} from './filter.service';
import {NodeService} from '../navigator/node.service';
import {BasketService} from '../basket/basket.service';
import {QueryService, Query, SearchResult, Filter} from '../tableview/query.service';

import {LocalizationService} from '../localization/localization.service';
import {MDMNotificationService} from '../core/mdm-notification.service';

import {Node} from '../navigator/node';
import {MDMItem} from '../core/mdm-item';

import {TableviewComponent} from '../tableview/tableview.component';
import {ViewComponent} from '../tableview/view.component';

import {View} from '../tableview/tableview.service';
import {IDropdownItem, IMultiselectConfig} from 'ng2-dropdown-multiselect';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';

import {ModalDirective} from 'ng2-bootstrap';

import {TreeModule, TreeNode, MenuItem} from 'primeng/primeng';
import {EditSearchFieldsComponent} from './edit-searchFields.component';
import {classToClass} from 'class-transformer';

@Component({
  selector: 'mdm-search',
  templateUrl: 'mdm-search.component.html',
})
export class MDMSearchComponent implements OnInit {

  maxResults = 10;

  filters: SearchFilter[] = [];
  currentFilter: SearchFilter = new SearchFilter('No filter selected', [], 'Test', '', []);
  filterName = '';

  environments: Node[];
  searchableFields: { label: string, group: string, attribute: SearchAttribute }[] = [];

  definitions: SearchDefinition[];

  results: SearchResult;

  isAdvancedSearchOpen = false;
  isSearchResultsOpen = false;

  layout: SearchLayout = new SearchLayout;
  public dropdownModel: IDropdownItem[] = [];
  public dropdownConfig: IMultiselectConfig = { showCheckAll: false, showUncheckAll: false };

  searchFields: {group: string, attribute: string}[] = [];

  subscription: any;

  contextMenuItems: MenuItem[] = [
    { label: 'In Warenkorb legen', icon: 'glyphicon glyphicon-shopping-cart', command: (event) => this.addSelectionToBasket() }
  ];

  @ViewChild(ViewComponent)
  viewComponent: ViewComponent;

  @ViewChild(TableviewComponent)
  private tableViewComponent: TableviewComponent;

  @ViewChild('lgSaveModal')
  childSaveModal: ModalDirective;

  @ViewChild(EditSearchFieldsComponent)
  private editSearchFieldsComponent: EditSearchFieldsComponent;

  constructor(private searchService: SearchService,
    private queryService: QueryService,
    private filterService: FilterService,
    private nodeService: NodeService,
    private localService: LocalizationService,
    private notificationService: MDMNotificationService,
    private basketService: BasketService) { }

  ngOnInit() {
    // Load contents for environment selection
    this.nodeService.getNodes()
        .do(envs => this.environments = envs)
        .do(nodes => this.loadSearchAttributes(nodes.map(env => env.sourceName)))
        .map(nodes => nodes.map(env => <IDropdownItem> { id: env.sourceName, label: env.name, selected: true }))
        .subscribe(
          dropDownItems => {
            this.dropdownModel = dropDownItems;
            this.selectedEnvironmentsChanged(dropDownItems);
          },
          error => this.notificationService.notifyError('Datenquellen konnten nicht geladen werden!', error));

    this.searchService.getDefinitionsSimple()
        .subscribe(defs => this.definitions = defs);

    this.loadFilters('Standard');

    this.filterService.filterChanged$.subscribe(filter => this.onFilterChanged(filter));

  }

  selectedEnvironmentsChanged(items: IDropdownItem[]) {
    this.currentFilter.environments = items.filter(item => item.selected).map(item => item.id);
    this.calcCurrentSearch();
  }

  loadFilters(defaultFilterName: string) {
    this.filterService.getFilters()
      .defaultIfEmpty([ this.currentFilter ])
      .subscribe(filters => {
          this.filters = filters;
          this.selectFilterByName(defaultFilterName);
        }
    );
  }

  selectFilterByName(defaultFilterName: string) {
    this.selectFilter(this.filters.find(f => f.name === defaultFilterName));
  }

  removeSearchField(searchField: { group: string, attribute: string }) {
    let index = this.searchFields.findIndex(sf => sf.group === searchField.group && sf.attribute === searchField.attribute);
    this.searchFields.splice(index, 1);
  }

  selectResultType(type: any) {
    this.currentFilter.resultType = type.type;
  }

  getSearchDefinition(type: string) {
    return this.definitions.find(def => def.type === type);
  }

  onSearch() {
    let type = this.getSearchDefinition(this.currentFilter.resultType).value;
    this.searchService.getSearchAttributesPerEnvs(this.currentFilter.environments, type)
      .subscribe(attrs => this.search(attrs));
  }

  search(attrs: SearchAttribute[]) {
    let query = this.searchService.convertToQuery(this.currentFilter, attrs, this.viewComponent.selectedView);
    this.queryService.query(query)
      .do(result => this.generateWarningsIfMaxResultsReached(result))
      .subscribe(result => {
        this.results = result;
        this.isSearchResultsOpen = true;
      },
      error => this.notificationService.notifyError('Suchanfrage konnte nicht bearbeitet werden!', error)
    );
  }

  generateWarningsIfMaxResultsReached(result: SearchResult) {
    let resultsPerSource = result.rows
      .map(r => r.source)
      .reduce((prev, item) => { (prev[item]) ? prev[item] += 1 : prev[item] = 1; return prev; }, {});

    Object.keys(resultsPerSource)
      .filter(source => resultsPerSource[source] > this.maxResults)
      .forEach(source => this.notificationService.notifyWarn(
          'Zu viele Suchergebnisse',
          `Es werden die ersten ${this.maxResults} Suchergebnisse aus ${source}
          angezeigt. Bitte schränken Sie die Suchanfrage weiter ein.`));
  }

  calcCurrentSearch() {
    let environments = this.currentFilter.environments;
    let conditions = this.currentFilter.conditions;
    let type = this.getSearchDefinition(this.currentFilter.resultType).value;

    this.searchService.getSearchLayout(environments, conditions, type)
      	.subscribe(l => this.layout = l);
  }

  onFilterChanged(filter: SearchFilter) {
    this.currentFilter = classToClass(filter);

    this.dropdownModel.forEach(item => item.selected = (this.currentFilter.environments.findIndex(i => i === item.id) >= 0));

    this.calcCurrentSearch();
  }

  selectFilter(filter: SearchFilter) {
    this.filterService.setSelectedFilter(filter);
  }

  resetConditions() {
    this.currentFilter.conditions.forEach(cond => cond.value = []);
    this.onFilterChanged(this.currentFilter);
  }

  saveFilter() {
    let filter = this.currentFilter;
    filter.name = this.filterName;
    this.filterService.saveFilter(filter).subscribe();
    this.loadFilters(filter.name);
    this.childSaveModal.hide();
  }

  addCondition(field: SearchAttribute) {
    let condition = new Condition(field.boType, field.attrName, Operator.EQUALS, [], field.valueType);

    this.currentFilter.conditions.push(condition);
    this.calcCurrentSearch();
  }

  removeCondition(condition: Condition) {
    this.currentFilter.conditions = this.currentFilter.conditions
        .filter(c => !(c.type === condition.type && c.attribute === condition.attribute));

    this.calcCurrentSearch();
  }

  typeaheadOnSelect(match: TypeaheadMatch) {
    this.addCondition(match.item.attribute);
  }

  selected2Basket() {
    this.tableViewComponent.selectedRows.forEach(row => this.basketService.add(row.getItem()));
  }

  showSaveModal() {
    this.childSaveModal.show();
  }

  showSearchFieldsEditor(filter?: SearchFilter) {
    this.editSearchFieldsComponent.show(filter).subscribe(f => this.selectFilter(f));
  }

  addSelectionToBasket() {
    this.basketService.addAll(this.tableViewComponent.selectedRows.map(row => row.getItem()));
  }

  private loadSearchAttributes(environments: string[]) {
    let searchDef = this.getSearchDefinition(this.currentFilter.resultType);
    if (searchDef === undefined) {
      return;
    }

    Observable.forkJoin(environments.map(env => this.searchService.loadSearchAttributes(searchDef.value, env)))
      .map(attrs => <SearchAttribute[]> [].concat.apply([], attrs))
      .map(attrs => attrs.map(sa => { return { 'label': sa.boType + '.' + sa.attrName, 'group': sa.boType, 'attribute': sa }; }))
      .subscribe(attrs => this.searchableFields = attrs);
  }

  private uniqueBy<T>(a: T[], key: (T) => any) {
    let seen = {};
    return a.filter(function(item) {
      let k = key(item);
      return seen.hasOwnProperty(k) ? false : (seen[k] = true);
    });
  }
}

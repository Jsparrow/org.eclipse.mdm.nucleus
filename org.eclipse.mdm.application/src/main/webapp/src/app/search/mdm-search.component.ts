/*******************************************************************************
*  Original work: Copyright (c) 2016 Gigatronik Ingolstadt GmbH                *
*  Modified work: Copyright (c) 2017 Peak Solution GmbH                        *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Dennis Schroeder - initial implementation                                   *
*  Matthias Koller, Johannes Stamm - additional client functionality           *
*******************************************************************************/

import {Component, ViewChild, OnInit, Input, OnDestroy, OnChanges, SimpleChanges} from '@angular/core';
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
import {TypeaheadMatch} from 'ngx-bootstrap/typeahead';

import {ModalDirective} from 'ngx-bootstrap';
import {AccordionPanelComponent} from 'ngx-bootstrap/accordion';

import {TreeModule, TreeNode, DataTableModule, SharedModule, ContextMenuModule, MenuItem} from 'primeng/primeng';
import {EditSearchFieldsComponent} from './edit-searchFields.component';
import {OverwriteDialogComponent} from '../core/overwrite-dialog.component';

import {classToClass, plainToClass, serialize, deserialize} from 'class-transformer';
import {SelectItem} from 'primeng/primeng';

@Component({
  selector: 'mdm-search',
  templateUrl: 'mdm-search.component.html',
})
export class MDMSearchComponent implements OnInit, OnDestroy {


  readonly LblAdvancedSearch = 'Erweiterte Suche';
  readonly LblExistingFilterNames = 'Vorhandene Suchfilter';
  readonly LblFilter = 'Suchfilter';
  readonly LblResultType = 'Ergebnistyp';
  readonly LblResults = 'Ergebnisse';
  readonly LblSave = 'Speichern';
  readonly LblSaveFilterAs = 'Suchfilter speichern unter';
  readonly LblSearch = 'Suche';
  readonly LblSource = 'Quelle';
  readonly TtlDeleteFilter = 'Suchfilter löschen';
  readonly TtlDisableAdvancedSearch = 'Erweiterte Suche deaktivieren';
  readonly TtlEditSearchFields = 'Suchfilter bearbeiten';
  readonly TtlEnableAdvancedSearch = 'Erweiterte Suche aktivieren';
  readonly TtlNewSearchFields = 'Neuen Suchfilter anlegen';
  readonly TtlNoNameSet = 'Name nicht gesetzt!';
  readonly TtlResetSearchConditions = 'Suchkriterien zurücksetzen';
  readonly TtlSaveFilter = 'Suchfilter speichern';
  readonly TtlSaveSearchFilter = 'Suchfilter speichern';
  readonly TtlSelectionToBasket = 'Auswahl zum Warenkorb hinzufügen';
  readonly TtlSelectFilter = 'Suchfilter auswählen';
  readonly TtlSelectResultType = 'Ergebnisstyp auswählen';
  readonly TtlSelectSource = 'Quellen auswählen';
  readonly TtlClearSearchResults = 'Suchergebnisliste leeren';

  maxResults = 1000;

  filters: SearchFilter[] = [];
  currentFilter: SearchFilter;
  filterName = '';

  environments: Node[];
  selectedEnvironments: Node[] = [];

  definitions: SearchDefinition[] = [];

  results: SearchResult = new SearchResult();
  allSearchAttributes: { [type: string]: { [env: string]: SearchAttribute[] }} = {};
  allSearchAttributesForCurrentResultType: { [env: string]: SearchAttribute[] } = {};

  isAdvancedSearchOpen = false;
  isAdvancedSearchActive = true;
  isSearchResultsOpen = false;

  layout: SearchLayout = new SearchLayout;

  public dropdownModel: SelectItem[] = [];
  public selectedEnvs: string[] = [];

  searchFields: { group: string, attribute: string }[] = [];

  subscription: any;
  searchExecuted = false;

  selectedRow: SearchFilter;
  lazySelectedRow: SearchFilter;
  loading = false;

  contextMenuItems: MenuItem[] = [
    { label: 'In Warenkorb legen', icon: 'glyphicon glyphicon-shopping-cart', command: (event) => this.addSelectionToBasket() }
  ];

  @ViewChild(ViewComponent)
  viewComponent: ViewComponent;

  @ViewChild(TableviewComponent)
  tableViewComponent: TableviewComponent;

  @ViewChild('lgSaveModal')
  childSaveModal: ModalDirective;

  @ViewChild(EditSearchFieldsComponent)
  editSearchFieldsComponent: EditSearchFieldsComponent;

  @ViewChild(OverwriteDialogComponent)
  overwriteDialogComponent: OverwriteDialogComponent;

  @ViewChild('advancedSearch')
  advancedSearchPanel: AccordionPanelComponent;

  @ViewChild('searchResults')
  searchResultsPanel: AccordionPanelComponent;

  constructor(private searchService: SearchService,
    private queryService: QueryService,
    private filterService: FilterService,
    private nodeService: NodeService,
    private localService: LocalizationService,
    private notificationService: MDMNotificationService,
    private basketService: BasketService) { }

  ngOnInit() {
    this.currentFilter = this.filterService.EMPTY_FILTER;

    this.nodeService.getNodes()
      .flatMap(envs => Observable.combineLatest([
        Observable.of(envs),
        this.searchService.loadSearchAttributesStructured(envs.map(env => env.sourceName)),
        this.filterService.getFilters().defaultIfEmpty([this.currentFilter]),
        this.searchService.getDefinitionsSimple()
      ])).subscribe(
        ([envs, attrs, filters, definitions]) => this.init(envs, attrs, filters, definitions),
        error => this.notificationService.notifyError('Datenquellen können nicht geladen werden.', error)
      );

    // event handlers
    this.viewComponent.viewChanged$.subscribe(
      () => this.onViewChanged(),
      error => this.notificationService.notifyError('Ansicht kann nicht aktualisiert werden.', error)
    );
  }

  ngOnDestroy() {
     this.saveState();
  }

  init(envs: Node[], attrs: { [type: string]: { [env: string]: SearchAttribute[] }}, filters: SearchFilter[], definitions: SearchDefinition[]) {
    this.environments = envs
    this.allSearchAttributes = attrs;
    this.filters = filters;
    this.definitions = definitions;

    this.dropdownModel = envs.map(env => <SelectItem>{ value: env.sourceName, label: env.name });
    this.selectedEnvs = envs.map(env => env.sourceName);

    this.updateSearchAttributesForCurrentResultType();
    this.selectedEnvironmentsChanged();

    this.loadState();
  }

  loadState() {

    this.results = deserialize(SearchResult, sessionStorage.getItem('mdm-search.searchResult')) || new SearchResult();
    this.selectFilter(deserialize(SearchFilter, sessionStorage.getItem('mdm-search.currentFilter')) || this.filterService.EMPTY_FILTER);
    this.isAdvancedSearchActive = !('false' == sessionStorage.getItem('mdm-search.isAdvancedSearchActive'));
    this.advancedSearchPanel.isOpen = !('false' == sessionStorage.getItem('mdm-search.isAdvancedSearchOpen'));
    this.searchResultsPanel.isOpen = !('false' == sessionStorage.getItem('mdm-search.isSearchResultsOpen'));
  }

  saveState() {
    sessionStorage.setItem('mdm-search.isSearchResultsOpen', serialize(this.searchResultsPanel.isOpen));
    sessionStorage.setItem('mdm-search.isAdvancedSearchOpen', serialize(this.advancedSearchPanel.isOpen));
    sessionStorage.setItem('mdm-search.currentFilter', serialize(this.currentFilter));
    sessionStorage.setItem('mdm-search.searchResult', serialize(this.results));
    sessionStorage.setItem('mdm-search.isAdvancedSearchActive', this.isAdvancedSearchActive.toString());
  }

  onViewClick(e: Event) {
    e.stopPropagation();
  }

  onCheckBoxClick(event: any) {
    event.stopPropagation();
    this.isAdvancedSearchActive = event.target.checked;
  }

  onViewChanged() {
    if (this.searchExecuted) {
      this.onSearch();
    }
  }

  selectedEnvironmentsChanged() {
    this.currentFilter.environments = this.selectedEnvs;
    if (this.environments) {
      let envs = this.environments.filter(env =>
        this.currentFilter.environments.find(envName => envName === env.sourceName));

      if (envs.length === 0) {
        this.selectedEnvironments = this.environments;
      } else {
        this.selectedEnvironments = envs;
      }
    }
    this.calcCurrentSearch();
  }

  loadFilters() {
    this.filters = [];
    this.filterService.getFilters()
      .defaultIfEmpty([this.currentFilter])
      .subscribe(
        filters => this.filters = this.filters.concat(filters),
        error => this.notificationService.notifyError('Suchfilter kann nicht geladen werden.', error)
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
    this.updateSearchAttributesForCurrentResultType();
  }

  updateSearchAttributesForCurrentResultType() {
    if (this.allSearchAttributes.hasOwnProperty(this.getSelectedDefinition())) {
      this.allSearchAttributesForCurrentResultType = this.allSearchAttributes[this.getSelectedDefinition()];
    }
  }

  getSearchDefinition(type: string) {
    return this.definitions.find(def => def.type === type);
  }

  getSelectedDefinition() {
    let def = this.getSearchDefinition(this.currentFilter.resultType);
    if (def) {
      return def.value;
    }
  }

  onSearch() {
    let query;
    this.loading = true;
    this.isSearchResultsOpen = true;
    if (this.isAdvancedSearchActive) {
      query = this.searchService.convertToQuery(this.currentFilter, this.allSearchAttributes, this.viewComponent.selectedView);
    } else {
      let filter = classToClass(this.currentFilter);
      filter.conditions = [];
      query = this.searchService.convertToQuery(filter, this.allSearchAttributes, this.viewComponent.selectedView);
    }
    this.queryService.query(query)
      .do(result => this.generateWarningsIfMaxResultsReached(result))
      .subscribe(
        result => {
          this.results = result;
          this.isSearchResultsOpen = true;
          this.searchExecuted = true;
          this.loading = false;
        },
        error => this.notificationService.notifyError('Suchanfrage kann nicht bearbeitet werden.', error)
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
    let type = this.getSelectedDefinition();
    this.layout = SearchLayout.createSearchLayout(environments, this.allSearchAttributesForCurrentResultType, conditions);
  }

  selectFilter(filter: SearchFilter) {
    this.currentFilter = classToClass(filter);
    this.selectedEnvs = this.currentFilter.environments;
    this.updateSearchAttributesForCurrentResultType();
    this.selectedEnvironmentsChanged();
    this.calcCurrentSearch();
  }

  resetConditions(e: Event) {
    e.stopPropagation();
    this.currentFilter.conditions.forEach(cond => cond.value = []);
    this.selectFilter(this.currentFilter);
  }

  clearResultlist(e: Event) {
    e.stopPropagation();
    this.results = new SearchResult();
  }

  deleteFilter(e: Event) {
    e.stopPropagation();
    if (this.currentFilter.name === this.filterService.NO_FILTER_NAME
          || this.currentFilter.name === this.filterService.NEW_FILTER_NAME) {
      this.notificationService
        .notifyInfo('Kein Filter ausgewählt.',
        'Der Vorgang konnte nicht durchgeführt werden, da kein gespeicherter Filter zum Löschen ausgewählt wurde.');
    } else {
      this.layout = new SearchLayout;
      this.filterService.deleteFilter(this.currentFilter.name).subscribe(
        () => {
          this.loadFilters();
          this.selectFilter(new SearchFilter(this.filterService.NO_FILTER_NAME, [], 'Test', '', []));
        },
        error => this.notificationService.notifyError('Filter kann nicht gelöscht werden', error)
      );
    }
  }

  saveFilter(e: Event) {
    e.stopPropagation();
    if (this.filters.find(f => f.name === this.filterName) !== undefined) {
      this.childSaveModal.hide();
      this.overwriteDialogComponent.showOverwriteModal('ein Filter').subscribe(
        needSave => this.saveFilter2(needSave),
        error => {
          this.saveFilter2(false);
          this.notificationService.notifyError('Suchfilter kann nicht gespeichert werden', error);
        }
      );
    } else {
      this.saveFilter2(true);
    }
  }

  saveFilter2(save: boolean) {
    if (save) {
      let filter = this.currentFilter;
      filter.name = this.filterName;
      this.filterService.saveFilter(filter).subscribe(
        () => {
          this.loadFilters();
          this.selectFilter(filter);
        },
        error => this.notificationService.notifyError('Suchfilter kann nicht gespeichert werden', error)
      );
      this.childSaveModal.hide();
    } else {
      this.childSaveModal.show();
    }
  }

  removeCondition(condition: Condition) {
    this.currentFilter.conditions = this.currentFilter.conditions
      .filter(c => !(c.type === condition.type && c.attribute === condition.attribute));

    this.calcCurrentSearch();
  }

  selected2Basket(e: Event) {
    e.stopPropagation();
    this.tableViewComponent.selectedRows.forEach(row => this.basketService.add(row.getItem()));
  }

  showSaveModal(e: Event) {
    e.stopPropagation();
    if (this.currentFilter.name === this.filterService.NO_FILTER_NAME
          	|| this.currentFilter.name === this.filterService.NEW_FILTER_NAME) {
      this.filterName = '';
    } else {
      this.filterName = this.currentFilter.name;
    }
    this.childSaveModal.show();
  }

  showSearchFieldsEditor(e: Event, conditions?: Condition[]) {
    e.stopPropagation();
    this.editSearchFieldsComponent.show(conditions).subscribe(
      conds => {
        if (!conditions) {
          let filter = new SearchFilter(this.filterService.NEW_FILTER_NAME, this.currentFilter.environments, 'Test', '', conds);
          this.selectFilter(filter);
        }
        this.currentFilter.conditions = conds;
        this.calcCurrentSearch();
      },
      error => this.notificationService.notifyError('Suchfeldeditor kann nicht angezeigt werden.', error)
    );
  }

  addSelectionToBasket() {
    this.basketService.addAll(this.tableViewComponent.selectedRows.map(row => row.getItem()));
  }

  mapSourceNameToName(sourceName: string) {
    return NodeService.mapSourceNameToName(this.environments, sourceName);
  }

  getSaveFilterBtnTitle () {
    return this.filterName ? this.TtlSaveFilter : this.TtlNoNameSet;
  }

  getAdvancedSearchCbxTitle() {
    return this.isAdvancedSearchActive ? this.TtlDisableAdvancedSearch : this.TtlEnableAdvancedSearch;
  }

/*
  private loadSearchAttributes(environments: string[]) {
    this.searchService.loadSearchAttributesStructured(environments)
      .subscribe(
        attrs => { this.allSearchAttributes = attrs; this.updateSearchAttributesForCurrentResultType(); },
        error => this.notificationService.notifyError('Attribute konnten nicht geladen werden!', error));
  }
*/
  onRowSelect(e: any) {
    if (this.lazySelectedRow !== e.data) {
      this.selectedRow = e.data;
      this.filterName = e.data.name;
    } else {
      this.selectedRow = undefined;
      this.filterName = '';
    }
    this.lazySelectedRow = this.selectedRow;
  }
}

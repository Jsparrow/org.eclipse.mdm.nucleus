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
import {IDropdownItem, IMultiselectConfig} from 'ng2-dropdown-multiselect';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';

import {ModalDirective} from 'ng2-bootstrap';

import {TreeModule, TreeNode, MenuItem} from 'primeng/primeng';
import {EditSearchFieldsComponent} from './edit-searchFields.component';
import {OverwriteDialogComponent} from '../core/overwrite-dialog.component';

import {classToClass} from 'class-transformer';

@Component({
  selector: 'mdm-search',
  templateUrl: 'mdm-search.component.html',
})
export class MDMSearchComponent implements OnInit, OnDestroy {


  readonly LblAdvancedSearch = 'Erweiterte Suche';
  readonly LblFilter = 'Filter';
  readonly LblResultType = 'Ergebnistyp';
  readonly LblResults = 'Ergebnisse';
  readonly LblSave = 'Speichern';
  readonly LblSaveFilterAs = 'Filter speichern als';
  readonly LblSearch = 'Suche';
  readonly LblSource = 'Quelle';
  readonly TtlDeleteFilter = 'Filter löschen';
  readonly TtlDisableAdvancedSearch = 'Erweiterte Suche deaktivieren';
  readonly TtlEditSearchFields = 'Suchfilter bearbeiten';
  readonly TtlEnableAdvancedSearch = 'Erweiterte Suche aktivieren';
  readonly TtlNewSearchFields = 'Neuen Suchfilter anlegen';
  readonly TtlNoNameSet = 'Name nicht gesetzt!';
  readonly TtlResetSearchConditions = 'Suchkriterien zurücksetzen';
  readonly TtlSaveFilter = 'Filter speichern';
  readonly TtlSaveSearchFilter = 'Suchfilter speichern';
  readonly TtlSelectionToBasket = 'Auswahl zum Warenkorb hinzufügen';


  maxResults = 100;

  filters: SearchFilter[] = [];
  currentFilter: SearchFilter;
  filterName = '';

  environments: Node[];
  selectedEnvironments: Node[];

  definitions: SearchDefinition[];

  results: SearchResult;
  searchAttributes: SearchAttribute[];
  allSearchAttributes: { [type: string]: { [env: string]: SearchAttribute[] }} = {};
  allSearchAttributesForCurrentResultType: { [env: string]: SearchAttribute[] } = {};

  isAdvancedSearchOpen = false;
  isSearchResultsOpen = false;

  layout: SearchLayout = new SearchLayout;
  public dropdownModel: IDropdownItem[] = [];
  public dropdownConfig: IMultiselectConfig = { showCheckAll: false, showUncheckAll: false };

  searchFields: { group: string, attribute: string }[] = [];

  subscription: any;
  isBoxChecked = true;
  searchExecuted = false;

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

  @ViewChild(OverwriteDialogComponent)
  private overwriteDialogComponent: OverwriteDialogComponent;

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
      .map(nodes => nodes.map(env => <IDropdownItem>{ id: env.sourceName, label: env.name, selected: true }))
      .subscribe(
      dropDownItems => {
        this.dropdownModel = dropDownItems;
        this.selectedEnvironmentsChanged(dropDownItems);
      },
      error => this.notificationService.notifyError('Datenquellen konnten nicht geladen werden!', error));

    this.searchService.getDefinitionsSimple()
      .subscribe(defs => this.definitions = defs);

    if ( this.filterService.currentFilter.name === this.filterService.NO_FILTER_NAME ) {
      this.loadFilters('Standard');
    }
    this.filterService.filterChanged$.subscribe(filter => this.onFilterChanged(filter));
    this.viewComponent.viewChanged$.subscribe(() => this.onViewChanged());

    this.selectFilter(this.filterService.currentFilter);
  }

  ngOnDestroy() {
     this.filterService.currentFilter = this.currentFilter;
  }

  onViewClick(e: Event) {
    e.stopPropagation();
  }

  onCheckBoxClick(event: any) {
    event.stopPropagation();
    this.isBoxChecked = event.target.checked;
  }

  onViewChanged() {
    if (this.searchExecuted) {
      this.onSearch();
      this.searchExecuted = true;
    }
  }
  selectedEnvironmentsChanged(items: IDropdownItem[]) {
    this.currentFilter.environments = items.filter(item => item.selected).map(item => item.id);
    if (this.environments) {
      this.selectedEnvironments = this.environments.filter(env =>
        this.currentFilter.environments.find(envName => envName === env.sourceName));
    }
    this.calcCurrentSearch();
  }

  loadFilters(defaultFilterName: string) {
    this.filterService.getFilters()
      .defaultIfEmpty([this.currentFilter])
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
    if (this.isBoxChecked) {
      query = this.searchService.convertToQuery(this.currentFilter, this.allSearchAttributes, this.viewComponent.selectedView);
    } else {
      let filter = classToClass(this.currentFilter);
      filter.conditions = [];
      query = this.searchService.convertToQuery(filter, this.allSearchAttributes, this.viewComponent.selectedView);
    }
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
    console.log(this.currentFilter);
    this.dropdownModel.forEach(item => item.selected = (this.currentFilter.environments.findIndex(i => i === item.id) >= 0));
    this.selectedEnvironmentsChanged(this.dropdownModel);
    this.calcCurrentSearch();
  }

  selectFilter(filter: SearchFilter) {
    this.filterService.setSelectedFilter(filter);
  }

  resetConditions(e: Event) {
    e.stopPropagation();
    this.currentFilter.conditions.forEach(cond => cond.value = []);
    this.onFilterChanged(this.currentFilter);
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
        () => this.loadFilters(this.filterService.NO_FILTER_NAME),
        () => this.notificationService
          .notifyError('Server Error.', 'Der Vorgang konnte nicht durchgeführt werden, da ein Serverfehler aufgetreten ist.')
      );
    }
  }

  saveFilter(e: Event) {
    e.stopPropagation();
    if (this.filters.find(f => f.name === this.filterName) !== undefined) {
      this.childSaveModal.hide();
      this.overwriteDialogComponent.showOverwriteModal('ein Filter').subscribe(ovw => this.saveFilter2(ovw));
    } else {
      this.saveFilter2(true);
    }
  }

  saveFilter2(save: boolean) {
    if (save) {
      let filter = this.currentFilter;
      filter.name = this.filterName;
      this.filterService.saveFilter(filter).subscribe(
        () => this.loadFilters(filter.name)
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
    this.editSearchFieldsComponent.show(conditions).subscribe(conds => {
      if (!conditions) {
        let filter = new SearchFilter(this.filterService.NEW_FILTER_NAME, this.currentFilter.environments, 'Test', '', conds);
        this.selectFilter(filter);
      }
      this.currentFilter.conditions = conds;
      this.calcCurrentSearch();
    });
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
    return this.isBoxChecked ? this.TtlDisableAdvancedSearch : this.TtlEnableAdvancedSearch;
  }

  private loadSearchAttributes(environments: string[]) {
    this.searchService.loadSearchAttributesStructured(environments)
      .subscribe(attrs => { this.allSearchAttributes = attrs; this.updateSearchAttributesForCurrentResultType(); });
  }
}

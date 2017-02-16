import { Component, Input, Output, ViewChild, EventEmitter } from '@angular/core';

import { ModalDirective } from 'ng2-bootstrap';

import { View, ViewColumn, SortOrder, ViewService } from './tableview.service';
import { LocalizationService } from '../localization/localization.service';

import {SearchService, SearchAttribute} from '../search/search.service';
import {SearchDeprecatedService} from '../search/search-deprecated.service';

import {FilterService, SearchFilter} from '../search/filter.service';
import {NodeService} from '../navigator/node.service';
import {BasketService} from '../basket/basket.service';
import { Node } from '../navigator/node';
import {MDMItem} from '../core/mdm-item';

@Component({
  selector: 'edit-view',
  templateUrl: './editview.component.html',
  providers: [SearchDeprecatedService, FilterService],
  styles: ['.remove {color:black; cursor: pointer; float: right}', '.icon { cursor: pointer; margin: 0px 5px; }']
})
export class EditViewComponent {
  @ViewChild('lgModal') public childModal: ModalDirective;
  @Input() prefViews;

  nodes: Node[] = [];
  definitions: any;
  ungrouped: any[] = [];
  groups: any[] = [];
  selectedGroups: any[] = [];
  envs: Node[] = [];
  selectedEnv: Node[] = [];
  type: any = { label: 'Ergebnistyp wählen' };
  errorMessage: string;

  filters: SearchFilter[] = [];
  selectedFilter: any = {name: 'Filter wählen'};

  typeaheadQuery = '';
  isReadOnly = false;
  private currentView: View = new View();

  constructor(private searchDeprecatedService: SearchDeprecatedService,
              private filterService: FilterService,
              private nodeService: NodeService,
              private viewService: ViewService,
              private localService: LocalizationService,
              private basketService: BasketService) {

  }

  loadData() {
    this.definitions = this.searchDeprecatedService.getDefinitions();
    this.filterService.getFilters().then(filters => this.filters = filters);
    let node: Node;
    this.nodeService.getNodes(node).subscribe(
      nodes => this.setEvns(nodes),
      error => this.errorMessage = <any>error);
  }

  setEvns(envs) {
    this.envs = envs;
    for (let i = 0; i < envs.length; i++) {
      this.selectedEnv.push(envs[i]);
    }
    this.selectDef(this.definitions.options[0]);
  }

  selectDef(type: any) {
    this.type = type;
    this.groups = [];
    this.ungrouped = [];
    for (let i = 0; i < this.selectedEnv.length; i++) {
      this.searchDeprecatedService.getSearches(type.value, this.selectedEnv[i].sourceName).then(defs => this.groupBy(defs));
    }
  }


  showDialog(currentView: View) {
    this.loadData();
    this.currentView = <View> JSON.parse(JSON.stringify(currentView));
    this.isNameReadOnly(currentView);
    this.typeaheadQuery = '';
    this.childModal.show();
  }

  closeDialog() {
    this.childModal.hide();
  }

  save() {
    if (this.checkViews(this.currentView) && this.isReadOnly === false) {
      alert('Name schon vorhanden!');
    } else {
      this.viewService.saveView(this.currentView);
      this.closeDialog();
    }
  }

  remove(col: ViewColumn) {
    this.currentView.columns = this.currentView.columns.filter(c => c !== col);
  }

  isLast(col: ViewColumn) {
    return this.currentView.columns.indexOf(col) === this.currentView.columns.length - 1;
  }

  isFirst(col: ViewColumn) {
    return this.currentView.columns.indexOf(col) === 0;
  }

  isAsc(col: ViewColumn) {
    return col.sort === SortOrder.Asc;
  }
  isDesc(col: ViewColumn) {
    return col.sort === SortOrder.Desc;
  }
  isNone(col: ViewColumn) {
    return col.sort === SortOrder.None;
  }

  toggleSort(col: ViewColumn) {
    if (col.sort === SortOrder.None) {
      col.sort = SortOrder.Asc;
    } else if (col.sort === SortOrder.Asc) {
      col.sort = SortOrder.Desc;
    } else if (col.sort === SortOrder.Desc) {
      col.sort = SortOrder.None;
    }
  }

  moveUp(col: ViewColumn) {
    if (!this.isFirst(col)) {
      let oldIndex = this.currentView.columns.indexOf(col);
      let otherCol = this.currentView.columns[oldIndex - 1];
      this.currentView.columns[oldIndex] = otherCol;
      this.currentView.columns[oldIndex - 1] = col;
    }
  }

  moveDown(col: ViewColumn) {
    if (!this.isLast(col)) {
      let oldIndex = this.currentView.columns.indexOf(col);
      let otherCol = this.currentView.columns[oldIndex + 1];
      this.currentView.columns[oldIndex] = otherCol;
      this.currentView.columns[oldIndex + 1] = col;
    }
  }

  selectItem(item) {
    let a = item.key.split('.');
    let g = this.groups.map(function(x) {return x.name; }).indexOf(a[0]);
    let i = this.groups[g].items.indexOf(item);
    if (this.groups[g].items[i].active) {
      this.groups[g].items[i].active = false;
    } else {
      this.groups[g].items[i].active = true;
    }

    this.currentView.columns.push(new ViewColumn(a[0], a[1], SortOrder.None));
  }

  groupBy(defs) {
    this.ungrouped = this.arrayUnique(defs, this.ungrouped);
    this.ungrouped.sort(this.compare);
    let groups = this.groups.slice();
    defs.forEach(function(obj) {
      let str = obj.key;
      let a = str.split('.');
      let g = groups.map(function(x) {return x.name; }).indexOf(a[0]);
      if (g === -1) {
        groups.push({name: a[0], items: [obj]});
      } else {
        let i = groups[g].items.map(function(x) { return x.key; }).indexOf(obj.key);
        if (i !== -1) { return; }
        groups[g].items.push(obj);
      }
    });

    this.groups = groups.sort((g1, g2) => {
      if (g1.name.toLowerCase() > g2.name.toLowerCase()) {
          return 1;
      }

      if (g1.name.toLowerCase() < g2.name.toLowerCase()) {
          return -1;
      }

      return 0;
    });
  }

  isActive(item) {
    if (item.active) { return 'active'; }
    return;
  }

  getTrans(label: string) {
    let type = '',
    comp = '';
    if (label.includes('.')) {
      let a = label.split('.');
      type = a[0];
      comp = a[1];
    } else {
      type = label;
    }
    return this.localService.getTranslation(type, comp);
  }

  add2Basket(node: Node) {
    if (node) {
      this.basketService.add(new MDMItem(node.sourceName, node.type, node.id));
    }
  }

  public typeaheadOnSelect(e: any): void {
    this.selectItem(e.item);
    this.typeaheadQuery = '';
  }

  private arrayUnique(source, target) {
    source.forEach(function(item) {
      let i = target.map(function(x) { return x.key; }).indexOf(item.key);
      if (i !== -1) { return; }
      target.push(item);
    });
    return target;
  }

  private compare(g1, g2) {
      if (g1.label.toLowerCase() > g2.label.toLowerCase()) {
        return 1;
      }
      if (g1.label.toLowerCase() < g2.label.toLowerCase()) {
        return -1;
      }
  }

  private isNameReadOnly(currentView: View) {
    return this.isReadOnly = (currentView.name === '') ? false : true;
  }

  private checkViews(currentView: View) {
    return this.prefViews.findIndex(pv => currentView.name === pv.view.name) >= 0;
  }
}

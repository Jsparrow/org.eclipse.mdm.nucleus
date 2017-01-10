import { Component, Input, Output, ViewChild, EventEmitter } from '@angular/core';

import { ModalDirective } from 'ng2-bootstrap';

import { View, Col, SortOrder, ViewService } from './tableview.service';
import { LocalizationService } from '../localization/localization.service';

import {SearchService} from '../search/search.service';
import {FilterService} from '../search/filter.service';
import {NodeService} from '../navigator/node.service';
import {BasketService} from '../basket/basket.service';
import { Node } from '../navigator/node';

@Component({
  selector: 'edit-view',
  templateUrl: './editview.component.html',
  providers: [SearchService, FilterService, NodeService],
  styles: ['.remove {color:black; cursor: pointer; float: right}', '.icon { cursor: pointer; margin: 0px 5px; }']
})
export class EditViewComponent {
  @Input() selectedView: View;
  @ViewChild('lgModal') public childModal: ModalDirective;
  // @Output() onViewChanged = new EventEmitter<View>();


  nodes: Node[] = [];
  definitions: any;
  ungrouped: any[] = [];
  groups: any[] = [];
  selectedGroups: any[] = [];
  envs: Node[] = [];
  selectedEnv: Node[] = [];
  type: any = { label: 'Ergebnistyp wählen' };
  errorMessage: string;
  filters: any;
  selectedFilter: any = {name: 'Filter wählen'};

  private currentView: View = new View('', []);

  constructor(private searchService: SearchService,
              private filterService: FilterService,
              private nodeService: NodeService,
              private viewService: ViewService,
              private localService: LocalizationService,
              private basketService: BasketService) {
    this.definitions = searchService.getDefinitions();
    this.filters = filterService.getFilters();
    let node: Node;
    this.nodeService.getNodes(node).subscribe(
      nodes => this.setEvns(nodes),
      error => this.errorMessage = <any>error);
  }

  showDialog() {
    this.currentView = <View> JSON.parse(JSON.stringify(this.selectedView));
    this.childModal.show();
  }

  closeDialog() {
    this.childModal.hide();
  }

  save() {
    this.viewService.saveView(this.currentView);
    this.closeDialog();
  }

  remove(col: Col) {
    this.currentView.cols = this.currentView.cols.filter(c => c !== col);
  }

  isLast(col: Col) {
    return this.currentView.cols.indexOf(col) === this.currentView.cols.length - 1;
  }

  isFirst(col: Col) {
    return this.currentView.cols.indexOf(col) === 0;
  }

  isAsc(col: Col) {
    return col.sort === SortOrder.Asc;
  }
  isDesc(col: Col) {
    return col.sort === SortOrder.Desc;
  }
  isNone(col: Col) {
    return col.sort === SortOrder.None;
  }

  toggleSort(col: Col) {
    if (col.sort === SortOrder.None) {
      col.sort = SortOrder.Asc;
    } else if (col.sort === SortOrder.Asc) {
      col.sort = SortOrder.Desc;
    } else if (col.sort === SortOrder.Desc) {
      col.sort = SortOrder.None;
    }
  }

  moveUp(col: Col) {
    if (!this.isFirst(col)) {
      let oldIndex = this.currentView.cols.indexOf(col);
      let otherCol = this.currentView.cols[oldIndex - 1];
      this.currentView.cols[oldIndex] = otherCol;
      this.currentView.cols[oldIndex - 1] = col;
    }
  }

  moveDown(col: Col) {
    if (!this.isLast(col)) {
      let oldIndex = this.currentView.cols.indexOf(col);
      let otherCol = this.currentView.cols[oldIndex + 1];
      this.currentView.cols[oldIndex] = otherCol;
      this.currentView.cols[oldIndex + 1] = col;
    }
  }

  setEvns(envs) {
    this.envs = envs;
    for (let i = 0; i < envs.length; i++) {
      this.selectedEnv.push(envs[i]);
    }
    this.selectDef(this.definitions.options[0]);
  }

  selectEnv(env: string) {
    let i = this.selectedEnv.map(function(e){ return e.sourceName; }).indexOf(env);
    if (i > -1) {
        this.selectedEnv.splice(i, 1);
    } else {
      let e = this.envs.map(function(e){ return e.sourceName; }).indexOf(env);
      this.selectedEnv.push(this.envs[e]);
    }
    this.selectDef(this.type);
  }

  onSubmit(query: string) {
    this.nodes = [];
    for (let i in this.selectedEnv) {
      if (this.selectedEnv.hasOwnProperty(i)) {
        this.search(query, this.selectedEnv[i].sourceName);
      }
    }
  }

  search(query: string, env: string) {
    this.nodeService.searchFT(query, env).subscribe(
      nodes => this.nodes = this.nodes.concat(nodes),
      error => this.errorMessage = <any>error
    );
  }

  selectFilter(filter: string) {
    let f = this.filters.map(function(e){ return e.name; }).indexOf(filter);
    this.selectedFilter = this.filters[f];
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

    this.currentView.cols.push(new Col(a[0], a[1], SortOrder.None));
  }

  selectDef(type: any) {
    this.type = type;
    this.groups = [];
    this.ungrouped = [];
    for (let i = 0; i < this.selectedEnv.length; i++) {
      this.searchService.getSearches(type.value, this.selectedEnv[i].sourceName).then(defs => this.groupBy(defs));
    }
  }

  groupBy(defs) {
    this.ungrouped = this.arrayUnique(defs, this.ungrouped);
    this.transTypeAHead();
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

  transTypeAHead() {
    for (let i in this.ungrouped) {
      if (this.ungrouped[i].labelt) { continue; }
      let a = this.ungrouped[i].label.split('.');
      this.ungrouped[i].labelt = this.getTrans(a[0]) + '.' + this.getTrans(this.ungrouped[i].label);
    }
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
      this.basketService.addNode(node);
    }
  }

  public typeaheadOnSelect(e: any): void {
    this.selectItem(e.item);
  }

  private arrayUnique(source, target) {
    source.forEach(function(item) {
      let i = target.map(function(x) { return x.key; }).indexOf(item.key);
      if (i !== -1) { return; }
      target.push(item);
    });
    return target;
  }

}

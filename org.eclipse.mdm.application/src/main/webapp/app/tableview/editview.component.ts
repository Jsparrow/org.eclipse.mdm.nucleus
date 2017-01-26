import { Component, Input, Output, ViewChild, EventEmitter } from '@angular/core';

import { ModalDirective } from 'ng2-bootstrap';

import { View, Col, SortOrder, ViewService } from './tableview.service';
import { LocalizationService } from '../localization/localization.service';

import {SearchService} from '../search/search.service';
import {FilterService} from '../search/filter.service';
import {NodeService} from '../navigator/node.service';
import {BasketService} from '../basket/basket.service';
import { Node } from '../navigator/node';
import {MDMItem} from '../core/mdm-item';

@Component({
  selector: 'edit-view',
  templateUrl: './editview.component.html',
  providers: [SearchService, FilterService, NodeService],
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
  filters: any;
  selectedFilter: any = {name: 'Filter wählen'};
  typeaheadQuery: string = '';
  isReadOnly: boolean = false;
  private currentView: View = new View();

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

  showDialog(currentView: View) {
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

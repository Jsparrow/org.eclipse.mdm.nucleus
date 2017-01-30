import { Component, Input, ViewChild, OnInit } from '@angular/core';

import { View, Col, ViewService } from './tableview.service';

import {FilterService} from '../search/filter.service';

import {BasketService} from '../basket/basket.service';
import { Node } from '../navigator/node';
import { EditViewComponent } from './editview.component';

import {PreferenceService} from '../core/preference.service';
import {Preference} from '../core/preference.service';

@Component({
  selector: 'mdm-tableview',
  templateUrl: 'tableview.component.html',
  providers: [ViewService],
  styles: ['.remove {color:black; cursor: pointer; float: right}']
})
export class TableviewComponent implements OnInit {
  views: View[] = [];
  selectedView: View;
  emptyView: View = new View();
  groupedViews: any[] = [];

  @Input() nodes: Node[];
  @Input() isShopable: boolean = false;
  @Input() isRemovable: boolean = false;

  @ViewChild(EditViewComponent)
  private editViewComponent: EditViewComponent;

  constructor(private viewService: ViewService,
    private basketService: BasketService,
    private _pref: PreferenceService) {
  }

  ngOnInit() {
    this.viewService.getViews().then(views => this.setView(views));
    this.viewService.viewsChanged$.subscribe(view => this.onViewChanged(view));
  }

  setView(views: View[]) {
    this.views = views;
    this.selectedView = this.views[0];
    this.getGroupedView(views);
  }

  onViewChanged(view: View) {
    this.viewService.getViews().then(views => this.views = views);
    this.getGroupedView(this.views);
    this.selectedView = view;
  }

  editSelectedView() {
    this.editViewComponent.showDialog(this.selectedView);
  }

  newView() {
    this.editViewComponent.showDialog(this.emptyView);
  }

  selectView(view: View) {
    this.selectedView = view;
  }

  nodeDataProvider(node: Node, col: Col) {
    if (node.type !== col.type) {
      return '-';
    } else {
      for (let index in node.attributes) {
        if (node.attributes[index].name === col.name) {
          return node.attributes[index].value;
        }
      }
    }
    return '-';
  }

  functionalityProvider(isShopable: boolean, node: Node) {
    if (isShopable) {
        this.add2Basket(node);
    } else {
        this.removeNode(node);
    }
  }


  add2Basket(node: Node) {
    if (node) {
      this.basketService.addNode(node);
    }
  }
  removeNode(node) {
    this.basketService.removeNode(node);
  }

  getGroupedView(views: View[]) {
    this.groupedViews = [];
    for (let i = 0; i < views.length; i++) {
      let pushed = false;
      for (let j = 0; j < this.groupedViews.length; j++) {
        if (views[i].scope === this.groupedViews[j].scope) {
          this.groupedViews[j].view.push(views[i]);
          pushed = true;
        }
      }
      if (pushed === false) { this.groupedViews.push({scope: views[i].scope, view: [views[i]]}); }
    }
  }
}

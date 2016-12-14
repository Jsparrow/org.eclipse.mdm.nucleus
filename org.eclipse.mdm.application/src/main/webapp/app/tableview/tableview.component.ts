import { Component, Input } from '@angular/core'

import { DROPDOWN_DIRECTIVES, ACCORDION_DIRECTIVES, TYPEAHEAD_DIRECTIVES } from 'ng2-bootstrap/ng2-bootstrap';
import { MODAL_DIRECTVES, BS_VIEW_PROVIDERS } from 'ng2-bootstrap/ng2-bootstrap';

import { View, Col, ViewService } from './tableview.service';
import { LocalizationService } from '../localization/localization.service';

import {BasketService} from '../basket/basket.service';
import { Node } from '../navigator/node';

@Component({
  selector: 'mdm-tableview',
  template: require('../../templates/tableview/tableview.component.html'),
  directives: [DROPDOWN_DIRECTIVES, MODAL_DIRECTVES, ACCORDION_DIRECTIVES, TYPEAHEAD_DIRECTIVES],
  providers: [ViewService],
  viewProviders: [BS_VIEW_PROVIDERS],
  styles: ['.remove {color:black; cursor: pointer; float: right}']
})
export class TableviewComponent {
  views: View[]
  selectedView: View
  @Input() nodes: Node[]
  @Input() isShopable: boolean = false
  @Input() isRemovable: boolean = false
  
  constructor(private viewService: ViewService, private basketService : BasketService) {
    this.views = viewService.getViews();
    this.selectedView = this.views[0];
  }
  
  selectView(view: View) {
    this.selectedView = view;
  }

  nodeDataProvider(node: Node, col: Col) {
    if (node.type != col.type) {
      return "-"
    } else {
      for (let index in node.attributes) {
        if (node.attributes[index].name == col.name) {
          return node.attributes[index].value
        }
      }
    }
    return "-"
  }
  
  functionalityProvider(isShopable: boolean,node: Node){
    if (isShopable){
        this.add2Basket(node)}
    else{
        this.removeNode(node)}
    }
    
  
  add2Basket(node: Node){
    if (node){
      this.basketService.addNode(node);
    }
  }
  removeNode(node){
    this.basketService.removeNode(node);
  }
}
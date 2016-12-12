import {Component, Input} from '@angular/core'

import {DROPDOWN_DIRECTIVES, ACCORDION_DIRECTIVES, TYPEAHEAD_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {MODAL_DIRECTVES, BS_VIEW_PROVIDERS} from 'ng2-bootstrap/ng2-bootstrap';

import {View, ViewService} from './tableview.service';
import {LocalizationService} from '../localization/localization.service';

@Component({
  selector: 'mdm-tableview',
  template: require('../../templates/tableview/tableview.component.html'),
  directives: [ DROPDOWN_DIRECTIVES, MODAL_DIRECTVES, ACCORDION_DIRECTIVES, TYPEAHEAD_DIRECTIVES],
  providers:  [ViewService],
  viewProviders: [BS_VIEW_PROVIDERS]
})
export class TableviewComponent {
  views: View[]
  selectedView: View
  @Input() nodes: Node[]
  
  constructor(private viewService: ViewService) {
    this.views = viewService.getViews();
    this.selectedView = this.views[0];
  }
  
  selectView(view: View) {
    this.selectedView = view;
  }
}
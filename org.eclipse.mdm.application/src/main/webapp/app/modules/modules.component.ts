
import {Component, Input} from '@angular/core';

import {Node} from '../navigator/node';

import {MDMDetailComponent} from '../details/mdm-detail.component';
import {MDMSearchComponent} from '../search/mdm-search.component';
import {MDMFilereleaseComponent} from '../filerelease/mdm-filerelease.component';
import {DummyModuleComponent} from '../dummy/dummy-module.component';

@Component({
  selector: 'modules',
  templateUrl: 'modules.component.html',
  providers: []
})
export class ModulesComponent {
  @Input() selectedNode: Node;

  brand: string = 'Modules';
  _comp: string = 'Details';

  activate(comp: string) {
    this._comp = comp;
  }
  isActive(comp: string) {
    if (comp === this._comp) {
      return 'active';
    }
  }
}

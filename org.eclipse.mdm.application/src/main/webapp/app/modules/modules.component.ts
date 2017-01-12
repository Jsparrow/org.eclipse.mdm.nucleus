
import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';

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

  brand: string = 'Modules';

  constructor(private router: Router) {

  }
}

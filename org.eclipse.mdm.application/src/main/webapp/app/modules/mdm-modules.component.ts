
import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'modules',
  templateUrl: 'mdm-modules.component.html',
  providers: []
})
export class MDMModulesComponent {

  brand: string = 'Modules';

  constructor(private router: Router) {}

  getLinks() {
    return [
      { name: 'Details', path: 'details'},
      { name: 'MDM Suche', path: 'search'},
      { name: 'Freigabeübersicht', path: 'filerelease'},
      { name: 'Example Module', path: 'example'},
    ];
  }
}

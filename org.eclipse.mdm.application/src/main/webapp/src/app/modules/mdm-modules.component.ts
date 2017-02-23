
import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'modules',
  templateUrl: 'mdm-modules.component.html',
  providers: []
})
export class MDMModulesComponent {

  brand = 'Modules';
  links = [
    { name: 'Details', path: 'details'},
    { name: 'MDM Suche', path: 'search'},
    { name: 'Freigabeübersicht', path: 'filerelease'},
    { name: 'Example Module', path: 'example'},
  ];
  constructor(private router: Router) {}
}

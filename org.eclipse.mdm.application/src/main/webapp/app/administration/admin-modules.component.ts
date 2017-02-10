import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'admin-modules',
  templateUrl: 'admin-modules.component.html',
  providers: []
})
export class AdminModulesComponent {

  brand = 'Scope';

  constructor(private router: Router) {}

    getLinks() {
        return [
          { name: 'System', path: 'system'},
          { name: 'Source', path: 'source'},
          { name: 'User', path: 'user'}
        ];
      }
}

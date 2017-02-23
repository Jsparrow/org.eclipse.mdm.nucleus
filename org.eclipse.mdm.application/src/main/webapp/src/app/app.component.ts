import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  brand = 'openMDM5 Web';
  links = [
      { name: 'Navigator', path: '/navigator' },
      { name: 'Administration', path: '/administration' }
  ];
}

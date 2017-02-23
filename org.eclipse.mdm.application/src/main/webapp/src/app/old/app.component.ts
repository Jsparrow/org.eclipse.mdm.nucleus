

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  brand = 'openMDM5 Web';
  links = [
      { name: 'Navigator', path: '/navigator' },
      { name: 'Administration', path: '/administration' }
  ];
}

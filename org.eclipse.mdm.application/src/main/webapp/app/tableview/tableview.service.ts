import {Injectable, EventEmitter} from '@angular/core';
import {Http} from '@angular/http';

import {PreferenceService, Preference} from '../core/preference.service';

export class View {
  name: string;
  columns: ViewColumn[];

  constructor(name?: string, cols?: ViewColumn[]) {
    this.name = name || '';
    this.columns = cols || [];
  }
}
export class PreferenceView {
  scope: string;
  view: View;

  constructor(scope?: string, view?: View) {
    this.scope = scope || '';
    this.view = view || new View();
  }
}
export enum SortOrder {
  None,
  Asc,
  Desc
}
export class ViewColumn {
  type: string;
  name: string;
  sort: SortOrder = SortOrder.None;

  constructor(type: string, name: string, sort: SortOrder) {
    this.type = type;
    this.name = name;
    this.sort = sort;
  }
  isNone() {
    return this.sort === SortOrder.None;
  }
  isAsc() {
    return this.sort === SortOrder.Asc;
  }
  isDesc() {
    return this.sort === SortOrder.Desc;
  }
}

@Injectable()
export class ViewService {
  public viewsChanged$ = new EventEmitter<View>();
  private views: View[] = [];

  constructor(private http: Http,
              private prefService: PreferenceService) {
  }

  getViews(): Promise<PreferenceView[]> {
    return this.prefService.getPreference('', 'tableview.view.').then(preferences => this.preparePrefs(preferences));
  }

  preparePrefs(prefs: Preference[]) {
    return prefs.map(p => new PreferenceView(p.scope, JSON.parse(p.value)));
  }

  saveView(view: View) {
    let pref = new Preference();
    pref.value = JSON.stringify(view);
    pref.key = 'tableview.view.' + view.name;
    pref.scope = 'User';
    this.prefService.savePreference(pref).then(saved => this.viewsChanged$.emit(view));
    }
}

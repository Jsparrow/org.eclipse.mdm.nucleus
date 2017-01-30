import {Injectable, EventEmitter} from '@angular/core';
import {Http} from '@angular/http';

import {PropertyService} from '../core/property.service';
import {PreferenceService} from '../core/preference.service';
import {Preference} from '../core/preference.service';

export class View {
  id: number;
  scope: string;
  source: string;
  user: string;
  name: string;
  cols: Col[];

  constructor(id?: number, scope?: string, source?: string, user?: string, name?: string, cols?: Col[]) {
    this.id = id || null;
    this.scope = scope || null;
    this.source = source || null;
    this.user = user || null;
    this.name = name || '';
    this.cols = cols || [];
  }
}
export enum SortOrder {
  None,
  Asc,
  Desc
}
export class Col {
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
              private _prop: PropertyService,
              private _pref: PreferenceService) {
  }

  getViews(): Promise<View[]> {
    return this._pref.getPreference('', 'tableview.view.').then(preferences => this.preparePrefs(preferences));
  }

  preparePrefs(prefs: Preference[]) {
    let views: View[] = [];
    for (let i = 0; i < prefs.length; i++) {
      views.push(JSON.parse(prefs[i].value));
      views[i].id = prefs[i].id;
      views[i].scope = prefs[i].scope;
      views[i].source = prefs[i].source;
      views[i].user = prefs[i].user;
    }
    return views;
  }

  saveView(view: View) {
    let pref = new Preference();
    pref.value = JSON.stringify(view);
    pref.key = 'tableview.view.' + view.name;
    pref.scope = 'User';
    this._pref.savePreference(pref).then(saved => this.viewsChanged$.emit(view));
    }
}

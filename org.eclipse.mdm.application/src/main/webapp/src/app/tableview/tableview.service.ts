import { Injectable, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { PreferenceService, Preference } from '../core/preference.service';
import { Type, Exclude, plainToClass, serialize, deserialize } from 'class-transformer';

export class View {
  name: string;
  @Type(() => ViewColumn)
  columns: ViewColumn[] = [];

  constructor(name?: string, cols?: ViewColumn[]) {
    this.name = name || '';
    this.columns = cols || [];
  }
}
export class PreferenceView {
  scope: string;
  @Type(() => View)
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

export class Style {
  [field: string]: string
}

export class ViewColumn {
  type: string;
  name: string;
  @Type(() => Style)
  style: Style;

  constructor(type: string, name: string, style?: Style) {
    this.type = type;
    this.name = name;
    this.style = style;
  }

  equals(vc: ViewColumn) {
    return this.type === vc.type && this.name === vc.name;
  }
}

@Injectable()
export class ViewService {
  public viewsChanged$ = new EventEmitter<View>();
  readonly preferencePrefix = 'tableview.view.';
  private views: View[] = [];

  private defaultPrefViews =  [ new PreferenceView('System', new View('Standard', [new ViewColumn('Test', 'Name')])) ];

  constructor(private prefService: PreferenceService) {
  }

  getViews() {
    return this.prefService.getPreference('', this.preferencePrefix)
        .map(preferences => preferences.map(p => new PreferenceView(p.scope, deserialize(View, p.value))))
        .filter(prefViews => !(prefViews === undefined || prefViews.length === 0))
        .defaultIfEmpty(this.defaultPrefViews);
  }

  saveView(view: View) {
    const pref = new Preference();
    pref.value = serialize(view);
    pref.key = this.preferencePrefix + view.name;
    pref.scope = 'User';
    this.prefService.savePreference(pref).subscribe(saved => this.viewsChanged$.emit(view));
  }
}

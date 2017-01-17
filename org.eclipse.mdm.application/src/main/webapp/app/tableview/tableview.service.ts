import {Injectable, EventEmitter} from '@angular/core';
import {Http} from '@angular/http';

import {PropertyService} from '../core/property.service';

export class View {
  name: string;
  cols: Col[];

  constructor(name: string, cols: Col[]) {
    this.name = name;
    this.cols = cols;
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
  private views: View[];

  constructor(private http: Http,
              private _prop: PropertyService) {
    this.views = [
      new View('Standard', [
        new Col('Test', 'Name', SortOrder.Asc),
        new Col('TestStep', 'Name', SortOrder.None)]),
      new View('Test', [
        new Col('Test', 'Name', SortOrder.None),
        new Col('Test', 'Beschreibung', SortOrder.None),
        new Col('Test', 'MimeType', SortOrder.None)])
      ];
  }

  getViews() {
    return this.views;
  }

  saveView(view: View) {
    this.views.push(view);
    this.viewsChanged$.emit(view);
  }
}

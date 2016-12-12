import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {PropertyService} from '../properties'

export class View {
  name: string;
  cols: Col[];
  
  constructor(name: string, cols: Col[]) {
    this.name = name;
    this.cols = cols;
  }
}

class Col {
  name: string;
  constructor(name: string) {
    this.name = name;
  }
}
@Injectable()
export class ViewService {

  private views : View[]
  
  constructor(private http: Http,
              private _prop: PropertyService) {
    this.views = [new View("Standard", [new Col("Col1"), new Col("Col2")]), new View("Test", [new Col("Test1"), new Col("Test2"), new Col("Test3")])];
  }
              
  getViews() {
    return this.views;
  }
}
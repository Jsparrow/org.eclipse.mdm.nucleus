import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {SearchBase} from './search-base';
import {DynamicForm} from './dynamic-form.component';
import {TextboxSearch} from './search-textbox';
import {DropdownSearch} from './search-dropdown';

import {PropertyService} from '../properties'
import {Node} from '../navigator/node';

class SearchFilter {
  name: string;
  constructor(name: string) {
    this.name = name;
  }
}

@Injectable()
export class FilterService {

  private filters : SearchFilter[]
  
  constructor(private http: Http,
              private _prop: PropertyService) {
    this.filters = [new SearchFilter("Standard"), new SearchFilter("Test")];
  }
              
  getFilters() {
    return this.filters;
  }
}
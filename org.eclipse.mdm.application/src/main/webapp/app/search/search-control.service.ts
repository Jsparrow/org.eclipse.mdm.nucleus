import {Injectable} from '@angular/core';
import {ControlGroup, FormBuilder, Validators} from '@angular/common';
import {SearchBase} from './search-base';

@Injectable()
export class SearchControlService {
  constructor(private fb:FormBuilder){ }

  toControlGroup(searches:SearchBase<any>[] ) {
    let group = {};

    searches.forEach(search => {
      group[search.key] = search.required ? [search.value || '', Validators.required] : [search.value || ''];
    });
    return this.fb.group(group);
  }
}

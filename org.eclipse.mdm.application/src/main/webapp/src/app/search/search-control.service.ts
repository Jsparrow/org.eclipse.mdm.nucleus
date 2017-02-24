// *******************************************************************************
//   * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
//   * All rights reserved. This program and the accompanying materials
//   * are made available under the terms of the Eclipse Public License v1.0
//   * which accompanies this distribution, and is available at
//   * http://www.eclipse.org/legal/epl-v10.html
//   *
//   * Contributors:
//   * Dennis Schroeder - initial implementation
//   *******************************************************************************
import {Injectable} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {SearchBase} from './search-base';

@Injectable()
export class SearchControlService {
  constructor(private fb: FormBuilder) { }

  toControlGroup(searches) {
    let group = {};
    for (let i = 0; i < searches.length; i++) {
      searches[i].items.forEach(search => {
        group[search.key] = search.required ? [search.value || '', Validators.required] : [search.value || ''];
      });
    }
    return this.fb.group(group);
  }
}

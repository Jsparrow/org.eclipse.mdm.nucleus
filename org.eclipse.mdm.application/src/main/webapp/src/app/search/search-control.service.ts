/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


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

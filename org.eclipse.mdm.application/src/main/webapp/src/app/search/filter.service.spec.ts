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


import { BaseRequestOptions, Http, HttpModule, Response, ResponseOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { TestBed, async, inject } from '@angular/core/testing';
import {PreferenceService, Preference, Scope} from '../core/preference.service';
import {PropertyService} from '../core/property.service';

import {Condition, Operator, OperatorUtil, FilterService, SearchFilter} from './filter.service';

class TestPreferenceService {
  getPreferenceForScope(scope: string, key?: string): Observable<Preference[]> {
    return Observable.of([
    {
      id: 1,
      key: 'filter.nodes.test',
      scope: Scope.USER,
      source: null,
      user: 'testUser',
      value: '{"conditions":[],"name":"TestFilter","environments":[],"resultType":"Test","fulltextQuery":""}'
    }
  ]);
  }
}

describe('OperatorUtil', () => {

  describe('toString()', () => {
    it('should return associated string', () => {
      expect(OperatorUtil.toString(Operator.EQUALS)).toMatch('=');
      expect(OperatorUtil.toString(Operator.LESS_THAN)).toMatch('<');
      expect(OperatorUtil.toString(Operator.GREATER_THAN)).toMatch('>');
      expect(OperatorUtil.toString(Operator.LIKE)).toMatch('like');
    });
  });

  describe('toFilterString()', () => {
    it('should return associated filterstring', () => {
      expect(OperatorUtil.toFilterString(Operator.EQUALS)).toMatch('eq');
      expect(OperatorUtil.toFilterString(Operator.LESS_THAN)).toMatch('lt');
      expect(OperatorUtil.toFilterString(Operator.GREATER_THAN)).toMatch('gt');
      expect(OperatorUtil.toFilterString(Operator.LIKE)).toMatch('lk');
    });
  });
});

describe('FilterService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        FilterService,
        PropertyService,
        {
          provide: PreferenceService,
          useClass: TestPreferenceService
        },
      ]
    });

  });

  describe('getFilters()', () => {

    it('should return array of filters from preference',  async(inject([FilterService], (service) => {
      let filters = [new SearchFilter('TestFilter', [], 'Test', '', [])];

      service.getFilters().subscribe(f => expect(f).toEqual(filters));
    })));
  });

  describe('filterToPreference()', () => {

    it('should return preference holding input filter',  async(inject([FilterService], (service) => {
      let filter = new SearchFilter('TestFilter', [], 'Test', '', []);
      let pref = service.filterToPreference(filter);

      expect(pref.scope).toEqual(Scope.USER);
      expect(pref.key).toMatch('filter.nodes.TestFilter');
      expect(pref.value).toEqual('{"conditions":[],"name":"TestFilter","environments":[],"resultType":"Test","fulltextQuery":""}');
    })));

    it('should return preference holding input filter',  async(inject([FilterService], (service) => {
      let pref = service.filterToPreference(undefined);

      expect(pref.scope).toEqual(Scope.USER);
      expect(pref.key).toMatch('filter.nodes.');
      expect(pref.value).toEqual(undefined);
    })));
  });
});

import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs';

import { ComponentFixture, TestBed } from '@angular/core/testing';

import {FilterService, Condition, Operator} from './filter.service';
import {SearchAttribute} from './search.service';

describe ( 'Filter service', () => {

  let service: FilterService;
  beforeEach(() => { service = new FilterService(null, null); });

  describe('group()', () => {
    it('group conditions', () => {
      let cond1 = new Condition('Test', 'Name', Operator.EQUALS, []);
      let cond2 = new Condition('Vehicle', 'Name', Operator.EQUALS, []);

      let attributes = {
        'env1': [
          new SearchAttribute('env1', 'Test', 'Name'),
          new SearchAttribute('env1', 'Vehicle', 'Name'),
        ],
        'env2': [
          new SearchAttribute('env2', 'Test', 'Name'),
          new SearchAttribute('env2', 'Uut', 'Name'),
        ]
      };

      let conditionsPerEnv = service.group([cond1, cond2], attributes);

      expect(Object.keys(conditionsPerEnv).length)
        .toBe(3);

      expect(conditionsPerEnv['Test.Name'])
        .toEqual(['env1', 'env2']);

      expect(conditionsPerEnv['Vehicle.Name'])
        .toEqual(['env1']);

      expect(conditionsPerEnv['Uut.Name'])
        .toEqual(['env2']);
    });
  });

  describe('env2Conditions()', () => {
    it('group conditions', () => {
      let cond1 = new Condition('Test', 'Name', Operator.EQUALS, []);
      let cond2 = new Condition('Vehicle', 'Name', Operator.EQUALS, []);

      let attributes = {
        'env1': [
          new SearchAttribute('env1', 'Test', 'Name'),
          new SearchAttribute('env1', 'Vehicle', 'Name'),
        ],
        'env2': [
          new SearchAttribute('env2', 'Test', 'Name'),
          new SearchAttribute('env2', 'Uut', 'Name'),
        ]
      };

      let env2Conditions = service.env2Conditions(['env1', 'env2'], [cond1, cond2], attributes);

      expect(Object.keys(env2Conditions).length)
        .toBe(2);

      expect(env2Conditions['Global'])
        .toEqual([cond1]);

      expect(env2Conditions['env1'])
        .toEqual([cond2]);

    });
  });
  describe('convert()', () => {
    it('simple', () => {
      let cond1 = new Condition('Test', 'Name', Operator.LIKE, ['PBN*']);
      let cond2 = new Condition('Vehicle', 'Name', Operator.EQUALS, ['car']);

      let attributes = [
          new SearchAttribute('env1', 'Test', 'Name'),
          new SearchAttribute('env1', 'Vehicle', 'Name'),
          new SearchAttribute('env2', 'Test', 'Name'),
          new SearchAttribute('env2', 'Uut', 'Name'),
        ];

      let filter = service.convertEnv('env1', [cond1, cond2], attributes, 'test');

      expect(filter.sourceName).toEqual('env1');
      expect(filter.filter).toEqual('Test.Name lk PBN* and Vehicle.Name eq car');
      expect(filter.searchString).toEqual('test');

    });
  });
});

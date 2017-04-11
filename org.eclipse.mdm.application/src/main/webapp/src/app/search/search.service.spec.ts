/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

// import { DebugElement } from '@angular/core';
// import { By } from '@angular/platform-browser';
// import { Observable } from 'rxjs/Observable';
//
// import { ComponentFixture, TestBed } from '@angular/core/testing';
//
// import {SearchService, SearchAttribute} from './search.service';
// import {Condition, Operator} from './filter.service';
//
// describe ( 'SearchService', () => {
//
//   let service: SearchService;
//   beforeEach(() => { service = new SearchService(null, null, null); });
//
//   describe('group()', () => {
//     it('should group conditions', () => {
//       let cond1 = new Condition('Test', 'Name', Operator.EQUALS, []);
//       let cond2 = new Condition('Vehicle', 'Name', Operator.EQUALS, []);
//
//       let attributes = {
//         'env1': [
//           new SearchAttribute('env1', 'Test', 'Name'),
//           new SearchAttribute('env1', 'Vehicle', 'Name'),
//         ],
//         'env2': [
//           new SearchAttribute('env2', 'Test', 'Name'),
//           new SearchAttribute('env2', 'Uut', 'Name'),
//         ]
//       };
//
//       let conditionsPerEnv = service.group(attributes);
//
//       expect(Object.keys(conditionsPerEnv).length)
//         .toBe(3);
//
//       expect(conditionsPerEnv['Test.Name'])
//         .toEqual(['env1', 'env2']);
//
//       expect(conditionsPerEnv['Vehicle.Name'])
//         .toEqual(['env1']);
//
//       expect(conditionsPerEnv['Uut.Name'])
//         .toEqual(['env2']);
//     });
//   });
//
//   describe('createSearchLayout()', () => {
//     it('should create a searchLayout object from conditions', () => {
//       let cond1 = new Condition('Test', 'Name', Operator.EQUALS, []);
//       let cond2 = new Condition('Vehicle', 'Name', Operator.EQUALS, []);
//
//       let attributes = {
//         'env1': [
//           new SearchAttribute('env1', 'Test', 'Name'),
//           new SearchAttribute('env1', 'Vehicle', 'Name'),
//         ],
//         'env2': [
//           new SearchAttribute('env2', 'Test', 'Name'),
//           new SearchAttribute('env2', 'Uut', 'Name'),
//         ]
//       };
//
//       let searchLayout = service.createSearchLayout(['env1', 'env2'], [cond1, cond2], attributes);
//
//       expect(searchLayout.getEnvironments())
//         .toEqual(['Global', 'env1']);
//
//       expect(searchLayout.getConditions('Global'))
//         .toEqual([cond1]);
//
//       expect(searchLayout.getConditions('env1'))
//         .toEqual([cond2]);
//
//     });
//   });
//   describe('convert()', () => {
//     it('should convert conditions to filter string', () => {
//       let cond1 = new Condition('Test', 'Name', Operator.LIKE, ['PBN*']);
//       let cond2 = new Condition('Vehicle', 'Name', Operator.EQUALS, ['car']);
//
//       let attributes = [
//           new SearchAttribute('env1', 'Test', 'Name'),
//           new SearchAttribute('env1', 'Vehicle', 'Name'),
//           new SearchAttribute('env2', 'Test', 'Name'),
//           new SearchAttribute('env2', 'Uut', 'Name'),
//         ];
//
//       let filter = service.convertEnv('env1', [cond1, cond2], attributes, 'test');
//
//       expect(filter.sourceName).toEqual('env1');
//       expect(filter.filter).toEqual('Test.Name lk PBN* and Vehicle.Name eq car');
//       expect(filter.searchString).toEqual('test');
//
//     });
//   });
// });

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


import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { Observable } from 'rxjs/Observable';

import { ComponentFixture, TestBed, inject } from '@angular/core/testing';

import { HttpModule } from '@angular/http';
import { PropertyService } from '../core/property.service';
import { NodeproviderService } from './nodeprovider.service';
import {NodeService} from '../navigator/node.service';
import {PreferenceService, Preference, Scope} from '../core/preference.service';
import {MDMNotificationService} from '../core/mdm-notification.service';
import { QueryService } from '../tableview/query.service';
import {MDMItem} from '../core/mdm-item';
import {HttpErrorHandler} from '../core/http-error-handler';

declare function require(path: string): any;
const defaultNodeProvider = require('../navigator/defaultnodeprovider.json');

class TestPreferenceService {
  getPreferenceForScope( scope: string, key?: string ): Observable<Preference[]> {
    let p = new Preference();
    p.value = JSON.stringify(defaultNodeProvider);
    return Observable.of([p]);
  }
}

describe ( 'NodeproviderService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        PropertyService,
        {
          provide: PreferenceService,
          useClass: TestPreferenceService
        },
        NodeproviderService,
        MDMNotificationService,
        QueryService,
        HttpErrorHandler,
        NodeService]
    });
  });

  it('getSubNodeprovider', inject([NodeproviderService], (nodeproviderService) => {
      let item = new MDMItem('MDMNVH', 'Project', 'id1');
      let query = nodeproviderService.getSubNodeprovider(item);

      expect(query).toEqual('/pools?filter=Project.Id eq \'{Project.Id}\'');
  }));

  it('getSubNodeprovider not found', inject([NodeproviderService], (nodeproviderService) => {
      let item = new MDMItem('MDMNVH', 'xxx', 'id1');
      let query = nodeproviderService.getSubNodeprovider(item, defaultNodeProvider);

      expect(query).toEqual(undefined);
  }));

  it('replace', inject([NodeproviderService], (nodeproviderService) => {
      let item = new MDMItem('MDMNVH', 'Project', 'id1');
      let query = nodeproviderService.replace('/pools?filter=Project.Id eq \'{Project.Id}\'', item);

      expect(query).toEqual('/MDMNVH/pools?filter=Project.Id eq \'id1\'');
  }));
});

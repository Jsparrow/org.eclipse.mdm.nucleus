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

import {Injectable, EventEmitter} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {MDMNotificationService} from '../core/mdm-notification.service';
import {PreferenceService, Preference, Scope} from '../core/preference.service';
import {QueryService, Query} from '../tableview/query.service';
import {PropertyService} from '../core/property.service';
import {MDMItem} from '../core/mdm-item';

declare function require(path: string): any;
const defaultNodeProvider = require('./defaultnodeprovider.json');

@Injectable()
export class NodeproviderService {

  public nodeProviderChanged: EventEmitter<any> = new EventEmitter<any>();
  private nodeproviders = [defaultNodeProvider];

  private activeNodeprovider: any = this.nodeproviders[0];

  constructor(private http: Http,
              private _prop: PropertyService,
              private queryService: QueryService,
              private preferenceService: PreferenceService,
              private notificationService: MDMNotificationService) {
      this.loadNodeproviders();
  }

  setActiveNodeprovider(nodeprovider: any) {
    this.activeNodeprovider = nodeprovider;
    this.nodeProviderChanged.emit(this.activeNodeprovider);
  }

  getActiveNodeprovider() {
    return this.activeNodeprovider;
  }

  getNodeproviders() {
    return this.nodeproviders;
  }

  loadNodeproviders() {
    this.preferenceService.getPreferenceForScope(Scope.SYSTEM, 'nodeprovider.')
      .map(prefs => prefs.map(p => JSON.parse(p.value)))
      .subscribe(
        nodeproviders => this.nodeproviders = nodeproviders,
        error => this.notificationService.notifyError('Nodeprovider kann nicht aus den Einstellungen geladen werden.', error)
      );
  }

  getQueryForChildren(item: MDMItem) {
    return this.replace(this.getSubNodeprovider(item), item);
  }

  getSubNodeprovider(item: MDMItem) {
    let current = this.activeNodeprovider;
    do {
      if (current.type === item.type && current.children) {
        return current.children.query;
      } else {
        current = current.children;
      }
    }
    while (current);

    return current;
  }

replace(query: string, item: MDMItem) {
    return '/' + item.source + query.replace(/{(\w+)\.(\w+)}/g, function(match, type, attr) {

      if (type !== item.type) {
        this.notificationService.notifyWarn('Typ ' + type + ' wird nicht unterstützt! Es sollte Typ ' + item.type + ' verwendet werden.');
      }

      if (attr === 'Id') {
        return '' + item.id;
      }
    });
}

  getPathTypes(type: string): string[] {
    let current = this.activeNodeprovider;
    let ancestorList: string[] = [];
    while (current) {
        ancestorList.push(current.type);
        if (current.type === type) {
          return ancestorList;
        }
        current = current.children;
   }
    return [];
  }
}

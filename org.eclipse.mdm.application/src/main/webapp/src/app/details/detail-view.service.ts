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


import { Injectable} from '@angular/core';
import { Preference, PreferenceService, Scope } from '../core/preference.service';
import { MDMNotificationService } from '../core/mdm-notification.service';

import { Node, Attribute } from '../navigator/node';

@Injectable()
export class DetailViewService {

    ignoreAttributesPrefs: Preference[] = [];

    constructor (private preferenceService: PreferenceService,
                 private notificationService: MDMNotificationService) {
      this.preferenceService.getPreference('ignoredAttributes')
          .subscribe(
            prefs => this.ignoreAttributesPrefs = this.ignoreAttributesPrefs.concat(prefs),
            error => this.notificationService.notifyWarn('Einstellung für zu ignorirende Attribute kann nicht geladen werden.', error));
    }

    getAttributesToDisplay(node: Node) {
        let filterList = this.getFilters(node.sourceName)
          .map(p => { let splitted = p.split('.'); return { type: splitted[0], attribute: splitted[1]}; })
          .filter(p => p.type === node.type || p.type === '*')
          .map(p => p.attribute);

        return this.getFilteredAttributes(node.attributes, filterList);
    }

    getFilters(source: string): string[] {
      if (this.ignoreAttributesPrefs.length > 0) {
      return this.ignoreAttributesPrefs
        .filter(p => p.scope !== Scope.SOURCE || p.source === source)
        .sort(Preference.sortByScope)
        .map(p => this.parsePreference(p))
        .reduce((acc, value) => acc.concat(value), []);
      } else {
        return [];
      }
    }

    private parsePreference(pref: Preference) {
      try {
          return <string[]> JSON.parse(pref.value);
      } catch (e) {
          this.notificationService.notifyError('Einstellungen für die zu ignorierenden Attribute ist fehlerhaft.', e);
          return [];
      }
    }

    private processFilter(prefList: string[], type: string) {
      return prefList.filter(p => p.split('.')[0] === type || p.split('.')[0] === '*')
        .map(p => p.split('.')[1]);
    }

    private getFilteredAttributes(attributes: Attribute[], filter: string[]) {
        if (filter.indexOf('*') !== -1) {
            return [];
        } else {
            return attributes.filter(attr => filter.indexOf(attr.name ) === -1);
        }
    }
}

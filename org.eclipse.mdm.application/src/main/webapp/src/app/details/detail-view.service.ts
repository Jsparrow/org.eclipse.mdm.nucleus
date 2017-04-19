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

import { Injectable} from '@angular/core';
import { Preference, PreferenceService, Scope } from '../core/preference.service';

import { Node, Attribute } from '../navigator/node';

@Injectable()
export class DetailViewService {

    ignoreAttributesPrefs: Preference[] = [];

    constructor (private preferenceService: PreferenceService) {
      this.preferenceService.getPreference('ignoredAttributes')
          .subscribe( prefs => this.ignoreAttributesPrefs = this.ignoreAttributesPrefs.concat(prefs));
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
          console.log('Preference for ignored attributes is corrupted.\n', pref, e);
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

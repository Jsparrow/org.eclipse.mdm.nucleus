import { Injectable} from '@angular/core';
import { Preference, PreferenceService } from '../core/preference.service';

import { Node, Attribute } from '../navigator/node';

@Injectable()
export class DetailViewService {

    ignoreAttributesPrefs: Preference[] = [];

    constructor (private preferenceService: PreferenceService) {
      this.preferenceService.getPreference('source', 'ignoredAttributes')
          .subscribe( prefs => this.ignoreAttributesPrefs = this.ignoreAttributesPrefs.concat(prefs));
    }

    getAttributesToDisplay(node: Node) {
        let filterList = this.getFilterPreference(node.sourceName);
        filterList = this.processFilter(filterList, node.type);
        return this.getFilteredAttributes(node.attributes, filterList);
    }

    private getFilterPreference(source: string): string[] {
        let pref = this.ignoreAttributesPrefs.find(p => p.source === source);
        let prefList: string[] = [];
        if (pref) {
            try {
                prefList = JSON.parse(pref.value);
            } catch (e) {
                console.log('Preference for ignored attributes is corrupted.\n', pref);
            }
        }
        return prefList;
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

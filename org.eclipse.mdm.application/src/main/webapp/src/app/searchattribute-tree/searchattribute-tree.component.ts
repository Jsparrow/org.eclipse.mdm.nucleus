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

import {Component, OnInit, Input, OnChanges, SimpleChanges, EventEmitter} from '@angular/core';

import {SearchService, SearchDefinition, SearchAttribute, SearchLayout} from '../search/search.service';
import { Preference, PreferenceService } from '../core/preference.service';
import {NodeService} from '../navigator/node.service';
import {Node} from '../navigator/node';
import {MDMItem} from '../core/mdm-item';

import {IDropdownItem, IMultiselectConfig} from 'ng2-dropdown-multiselect';
import {TypeaheadMatch} from 'ng2-bootstrap/typeahead';
import {ModalDirective} from 'ng2-bootstrap';
import {TreeModule, TreeNode} from 'primeng/primeng';

@Component({
  selector: 'searchattribute-tree',
  templateUrl: './searchattribute-tree.component.html'
})
export class SearchattributeTreeComponent implements OnChanges, OnInit {

  @Input() environments: Node[];
  @Input() searchAttributes: { [env: string]: SearchAttribute[] } = {};
  ignoreAttributesPrefs: Preference[] = [];

  lastClickTime = 0;
  nodes: TreeNode[] = [];
  selectedAttribute: { label: string, group: string, attribute: SearchAttribute };
  public onNodeSelect$ = new EventEmitter<TreeNode>();

  constructor(private searchService: SearchService,
    private nodeService: NodeService,
    private preferenceService: PreferenceService) {}

  ngOnInit() {
      this.preferenceService.getPreference('ignoredAttributes')
          .subscribe( prefs => this.ignoreAttributesPrefs = this.ignoreAttributesPrefs.concat(prefs));
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['searchAttributes'] && this.environments && this.environments.length > 0) {
      this.nodes = this.environments.map(n => this.mapRootNode(n));
      // this.filterAttributes();
    }

    if (changes['environments'] && this.environments && this.environments.length > 0) {
      this.nodes = this.environments.map(n => this.mapRootNode(n));
    }
  }

  filterAttributes() {
    this.environments.forEach( env => {
      if (this.searchAttributes[env.sourceName]) {
      this.getFilters(env.sourceName).forEach( f => {
        let x = f.split('.', 2);
        let fType = x[0];
        let fName = x[1];
        this.searchAttributes[env.sourceName] = this.searchAttributes[env.sourceName].filter( sa =>
          !((fType === sa.boType || fType === '*') && (fName === sa.attrName || fName === '*'))
        );
      });
    }});
  }

  mapRootNode(node: Node) {
    let item = new MDMItem(node.sourceName, node.type, +node.id);

    return <TreeNode>{
      label: node.name,
      leaf: false,
      type: 'env',
      data: item
    };
  }

  loadNodes(event: any) {
    if (event.node) {
      event.node.children = this.getChildren(event.node);
    }
  }

  mapType(group: { boType: string, attributes: SearchAttribute[] }) {
    return <TreeNode>{
      label: group.boType,
      leaf: false,
      type: 'group',
      data: group.attributes
    };
  }

  mapAttribute(attribute: SearchAttribute) {
    return <TreeNode>{
      label: attribute.attrName,
      leaf: true,
      type: 'attribute',
      data: attribute
    };
  }

  getChildren(node: TreeNode): TreeNode[] {
    if (node.type === 'env') {
      return this.getSearchableGroups(node.data.source)
        .sort((a, b) => a.boType.localeCompare(b.boType))
        .map(g => this.mapType(g));
    } else if (node.type === 'group') {
      return (<SearchAttribute[]> node.data)
        .sort((a, b) => a.attrName.localeCompare(b.attrName))
        .map(a => this.mapAttribute(a));
    } else {
      return [];
    }
  }

  getSearchableGroups(env: string): { boType: string, attributes: SearchAttribute[] }[] {
    let distinctGroupArray: { boType: string, attributes: SearchAttribute[] }[] = [];
    if (this.searchAttributes.hasOwnProperty(env)) {
      this.searchAttributes[env].forEach(attribute => {
          let item = distinctGroupArray.find(p => p.boType === attribute.boType);
          if (item && item.attributes.every(a => a.attrName !== attribute.attrName)) {
            item.attributes.push(attribute);
          } else if (!item) {
            distinctGroupArray.push({ boType: attribute.boType, attributes: [attribute]});
          }
      });
    }

    return distinctGroupArray;
  }

  nodeSelect(event) {
    this.onNodeSelect$.emit(event.node);
    if (event.originalEvent.timeStamp - this.lastClickTime < 300) {
      if (!event.node.expanded && !event.node.children) {
        this.loadNodes(event);
      }
      event.node.expanded = !event.node.expanded;
    }
    this.lastClickTime = event.originalEvent.timeStamp;
  }

  getFilters(source: string): string[] {

    return this.ignoreAttributesPrefs
      .filter(p => p.scope !== 'Source' || p.source === source)
      .sort(this.sortByScope)
      .map(p => this.parsePreference(p))
      .reduce((acc, value) => acc.concat(value), []);
  }

  private parsePreference(pref: Preference) {
    try {
        return <string[]> JSON.parse(pref.value);
    } catch (e) {
        console.log('Preference for ignored attributes is corrupted.\n', pref, e);
        return [];
    }
  }
  private sortByScope(p1: Preference, p2: Preference) {
    let priority = { System: 1, Source: 2, User: 3 };
    let one = priority[p1.scope] || 4;
    let two = priority[p2.scope] || 4;
    return one - two;
  }

}

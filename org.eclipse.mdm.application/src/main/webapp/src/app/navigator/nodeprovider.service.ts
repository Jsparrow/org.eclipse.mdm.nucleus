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

import {PreferenceService, Preference} from '../core/preference.service';
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
              private preferenceService: PreferenceService) {
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
    this.preferenceService.getPreferenceForScope('system', 'nodeprovider.')
      .subscribe( preferences => preferences.forEach(p => {
          try {
              this.nodeproviders.push(JSON.parse(p.value));
          } catch (e) {
              console.error('Nodeprovider preferences are corrupted.', p, e);
          }
      }));
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
        console.warn('Type ' + type + ' not supported! Use type ' + type);
      }

      if (attr === 'Id') {
        return '' + item.id;
      } else {
        // TODO support for other filters
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

/*
  getParentNodes(node: Node) {
    return Observable.forkJoin(
            this.getParentNodesUrl(node).map(n => this.getNode(n.url).do(o => console.log(o)).map(nodes => nodes[0])));
  }

  private getUrl(node: Node) {
    let url = this._nodeUrl + '/' + node.sourceName;
    let current = this.activeNodeprovider;

    do {
      if (current.type === node.type) {
        return url + current.children.query.replace(/{(\w+)\.(\w+)}/g, function(match, type, attr) {

          if (type !== current.type) {
            console.warn('Type ' + type + ' not supported! Use type ' + current.type);
          }

          if (attr === 'Id') {
            return '' + node.id;
          } else if (attr === 'Name') {
            return node.name;
          } else {
            return node.attributes.filter(a => a.name === attr)[0].name;
          }
        });
      } else {
        current = current.children;
      }
    }
    while (current);

    return;
  }

  private getParentNodesUrl(mdmItem: Node): {type: string, url: string}[] {
    let current = this.activeNodeprovider;
    let url = this._nodeUrl + '/' + mdmItem.sourceName;
    let i = 0;
    let currentParents: any[] = [];
    let parentList: any[] = [];
    do {
      currentParents[i] = current;
      if (current.type === 'Environment') {
        parentList.push({type: current.type, url: this._nodeUrl});
        i = i + 1;
      } else if (current.type === 'Project') {
        let itemUrl = url + current.query + '?filter=' + mdmItem.sourceType + '.Id eq ' + mdmItem.id;
        parentList.push({type: current.type, url: itemUrl});
        i = i + 1;
      } else if (current.type === 'Test') {
        let itemUrl = url + '/tests?filter=' + mdmItem.sourceType + '.Id eq ' + mdmItem.id;
        parentList.push({type: current.type, url: itemUrl});
        i = i + 1;
      } else if (current.type === 'ChannelGroup') {
        let itemUrl = url + '/channelgroups?filter=' + mdmItem.sourceType + '.Id eq ' + mdmItem.id;
        parentList.push({type: current.type, url: itemUrl});
          i = i + 1;
      } else {
        let transientUrl = url + current.query.replace(/{(\w+)\.(\w+)}/g, function(match, type, attr) {
          if (type !== current.type) {
            console.warn('Type ' + type + ' not supported! Use type ' + current.type);
          }
          if (attr === 'Id') { return '' + mdmItem.id; }
        });
        let itemUrl = transientUrl.replace(currentParents[i - 1].type, mdmItem.sourceType);
        parentList.push({type: current.type, url: itemUrl});
        i = i + 1;
      }
      if (current.type === mdmItem.type) { break; }
      current = current.children;
    }
    while (current);
    return parentList;
  }*/
}

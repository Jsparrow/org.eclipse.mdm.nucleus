// *******************************************************************************
//   * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
//   * All rights reserved. This program and the accompanying materials
//   * are made available under the terms of the Eclipse Public License v1.0
//   * which accompanies this distribution, and is available at
//   * http://www.eclipse.org/legal/epl-v10.html
//   *
//   * Contributors:
//   * Dennis Schroeder - initial implementation
//   *******************************************************************************
import {Injectable, EventEmitter} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {MDMItem} from '../core/mdm-item';
import {Node} from './node';
import {PropertyService} from '../core/property.service';
import {PreferenceService, Preference} from '../core/preference.service';
import {QueryService, Query} from '../tableview/query.service';

@Injectable()
export class NodeService {


  private _nodeUrl: string;

  constructor(private http: Http,
              private _prop: PropertyService,
              private queryService: QueryService,
              private preferenceService: PreferenceService) {
      this._nodeUrl = _prop.getUrl() + '/mdm/environments';
  }

  searchNodes(query, env, type) {
    return this.http.get(this._nodeUrl + '/' + env + '/' + type + '?' + query)
              .map(res => <Node[]> res.json().data)
              .catch(this.handleError);
  }

  searchFT(query, env) {
    return this.http.get(this._nodeUrl + '/' + env + '/search?q=' + query)
              .map(res => <Node[]> res.json().data)
              .catch(this.handleError);
  }

  getNodes(node?: Node) {
    if (node === undefined) {
      return this.getRootNodes();
    }
    return this.getNode(this.getUrl(node));
  }

  addNode (name: string): Observable<Node> {
    let body = JSON.stringify({ name });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._nodeUrl, body, options)
                    .map(res =>  <Node> res.json().data)
                    .catch(this.handleError);
  }

  deleteNode(node: Node) {
    return this.http.delete(this.getUrl(node))
      .map(res => <Node[]> res.json().data)
      .catch(this.handleError);
  }

  compareNode(node1, node2) {
    if (node1 === undefined || node2 === undefined) { return; }
    let n1 = node1.name + node1.id + node1.type + node1.sourceName;
    let n2 = node2.name + node2.id + node2.type + node2.sourceName;
    if (n1 === n2) { return true; };
    return false;
  }

  getRootNodes() {
    return this.http.get(this._nodeUrl)
      .map(res => <Node[]> res.json().data);
  }

  getNodeFromItem(mdmItem: MDMItem) {
    if (mdmItem.type === 'Environment') {
      return this.getNode(this._nodeUrl + '/' + mdmItem.source)
        .map(nodes => (nodes && nodes.length > 0) ? nodes[0] : undefined);
    } else {
      return this.getNode(this._nodeUrl + '/' + mdmItem.source + '/' + this.typeToUrl(mdmItem.type) + '/' + mdmItem.id)
        .map(nodes => (nodes && nodes.length > 0) ? nodes[0] : undefined);
    }
  }

  getNodesFromItem(mdmItem: MDMItem) {
    return this.getNode(this._nodeUrl + '/' + mdmItem.source + '/' + this.typeToUrl(mdmItem.type) + '/' + mdmItem.id)
      .map(nodes => (nodes && nodes.length > 0) ? nodes[0] : undefined);
  }

  typeToUrl(type: string) {
    switch (type) {
      case 'Project':
        return 'projects';
      case 'Project':
        return 'projects';
      case 'Pool':
      case 'StructureLevel':
        return 'pools';
      case 'Test':
        return 'tests';
      case 'TestStep':
        return 'teststeps';
      case 'Measurement':
      case 'MeaResult':
        return 'measurements';
      case 'ChannelGroup':
      case 'SubMatrix':
        return 'channelgroups';
      case 'Channel':
      case 'MeaQuantity':
        return 'channels';
    }
  }

  getNode(url: string) {
    return this.http.get(url)
      .map(res => <Node[]> res.json().data);
  }

  getNodesByUrl(url: string) {
    return this.http.get(this._nodeUrl + url)
      .map(res => <Node[]> res.json().data);
  }

  private getUrl(node: Node) {
    return this._nodeUrl + '/' + node.sourceName + '/' + node.type + '/' + node.id;
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
}

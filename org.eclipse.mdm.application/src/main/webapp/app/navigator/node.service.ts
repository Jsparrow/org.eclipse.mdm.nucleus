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

import {Node} from './node';
import {PropertyService} from '../core/properties';

declare function require(path: string): any;
const defaultNodeProvider = require('./defaultnodeprovider.json');
const nodeprovider2  = require('./nodeprovider2.json');

@Injectable()
export class NodeService {

  public nodeProviderChanged: EventEmitter<any> = new EventEmitter<any>();

  private _nodeUrl: string;

  private nodeproviders = [defaultNodeProvider, nodeprovider2];

  private activeNodeprovider: any = this.nodeproviders[0];

  constructor(private http: Http,
              private _prop: PropertyService) {
    this._nodeUrl = _prop.getUrl() + '/mdm/environments';
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

  getNodes(node: Node) {
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

  private getRootNodes() {
    return this.http.get(this._nodeUrl)
    .map(res => <Node[]> res.json().data)
    .catch(this.handleError);
  }

  private getNode(url: string) {
    return this.http.get(url)
    .map(res => <Node[]> res.json().data)
    .catch(this.handleError);
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
}

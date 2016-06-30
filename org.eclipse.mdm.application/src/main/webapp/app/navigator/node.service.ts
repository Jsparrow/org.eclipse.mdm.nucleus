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
import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {Node} from './node';
import {PropertyService} from '../properties'

@Injectable()
export class NodeService {
  constructor(private http: Http,
              private _prop: PropertyService){}

  private _host = this._prop.api_host
  private _port = this._prop.api_port
  private _url = 'http://' + this._host + ':' + this._port + this._prop.api_prefix
  private _nodeUrl = this._url + '/mdm/environments'

  public locals

  private getRootNodes(){
    return this.http.get(this._nodeUrl)
    .map(res => <Node[]> res.json().data)
    .catch(this.handleError);
  }

  private getNode(url: string) {
    return this.http.get(url)
    .map(res => <Node[]> res.json().data)
    .catch(this.handleError);
  }

  searchNodes(query, env, type){
    return this.http.get(this._nodeUrl + "/" + env + "/" + type + "?" + query)
              .map(res => <Node[]> res.json().data)
              .catch(this.handleError);
  }

  searchFT(query, env){
    return this.http.get(this._nodeUrl + "/" + env + "/search?q=" + query)
              .map(res => <Node[]> res.json().data)
              .catch(this.handleError);
  }

  getNodes(node: Node) {
    if (node === undefined){
      return this.getRootNodes()
    }
    return this.getNode(this.getUrl(node))
  }

  addNode (name: string) : Observable<Node>  {
    let body = JSON.stringify({ name });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._nodeUrl, body, options)
                    .map(res =>  <Node> res.json().data)
                    .catch(this.handleError)
  }

  deleteNode(node : Node) {
    return this.http.delete(this.getUrl(node))
      .map(res => <Node[]> res.json().data)
      .catch(this.handleError);
  }

  private getUrl(node: Node) {
    let url = this._nodeUrl + "/" + node.sourceName
    switch(node.type)
    {
      case 'Environment':
        return url + "/tests"
      case 'Test':
        return url + "/teststeps?filter=Test.Id eq " + node.id
      case 'TestStep':
        return url + "/measurements?filter=TestStep.Id eq " + node.id
      case 'Measurement':
        return url + "/channelgroups?filter=Measurement.Id eq " + node.id
      case 'ChannelGroup':
        return url + "/channels?filter=ChannelGroup.Id eq " + node.id
      case 'Channel':
        return
    }
  }
  compareNode(node1, node2){
    if (node1 == undefined || node2 == undefined) { return }
    let n1 = node1.name + node1.id + node1.type + node1.sourceName
    let n2 = node2.name + node2.id + node2.type + node2.sourceName
    if (n1 == n2) { return true }
    return false
  }

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
}

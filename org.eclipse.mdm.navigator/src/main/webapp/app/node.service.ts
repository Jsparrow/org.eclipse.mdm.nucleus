// Copyright (c) 2016 Gigatronik Ingolstadt GmbH
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Observable} from 'rxjs/Observable';

import {LocalizationService} from './localization.service';
import {Localization} from './localization';
import {Node} from './node';

@Injectable()
export class NodeService {
  constructor(private http: Http,
              private _local: LocalizationService){}

  selectedNode: Node;
  locals: Localization[] = [];

  private _host = "127.0.0.1"
  private _port = "8080"
  private _url = 'http://' + this._host + ':' + this._port
  private _nodeUrl = this._url + '/org.eclipse.mdm.application-1.0.0/mdm/environments'

  private getRootNodes(){
    return this.http.get(this._nodeUrl)
    .map(res => <Node[]> res.json().data)
    .catch(this.handleError);
  }

  setSelectedNode(node: Node) {
    this.selectedNode = node;
    this.getLocalization(node);
  }

  getNodes(node: Node) {
    if (node === undefined){
      return this.getRootNodes()
    }
    return this.getNode(this.getUrl(node))
  }

  private getLocalization(node: Node) {
    console.log("debug")
    this._local.getLocalization(node).subscribe(
      locals => this.locals = locals
      error => this.errorMessage = <any>error);
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
        return url + "/teststeps?test.id=" + node.id
      case 'TestStep':
        return url + "/measurements?teststep.id=" + node.id
      case 'Measurement':
        return url + "/channelgroups?measurement.id=" + node.id
      case 'ChannelGroup':
        return url + "/channels?channelgroup.id=" + node.id
      case 'Channel':
        return
    }
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

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
import {Context, Sensor} from './context';
import {PropertyService} from '../properties';

import {Node} from '../navigator/node';

import {Components} from './context';

@Injectable()
export class ContextService {
  constructor(private http: Http,
              private _prop: PropertyService){}

  private _host = this._prop.api_host
  private _port = this._prop.api_port
  private _url = 'http://' + this._host + ':' + this._port + this._prop.api_prefix
  private _contextUrl = this._url + '/mdm/environments'

  private test : {}
  private errorMessage: string;

  getContext(node: Node){
    let url = this._contextUrl + "/" + node.sourceName;
    url = url + "/" + node.type.toLowerCase() + "s/" + node.id + '/contexts';
    return this.get(url)
  }
  getSensors(node: Node){
    let url = this._contextUrl + "/" + node.sourceName + "/" + node.type.toLowerCase() + "s/" + node.id + "/contexts/testequipment/sensors"
    return this.http.get(url)
        .map((res) => {return <{}> this.merge(res.json().data)})
        .catch(this.handleError);
  }

  private get(url: string){
    return this.http.get(url)
    .map((res) => {
      let data = res.json().data
      let context = this.specialMerger([data[0].contextOrdered, data[0].contextMeasured]);
      return <{}> context
    })
    .catch(this.handleError);
  }

  private merge(sensor: Sensor) {
    let sensorm = sensor[0].sensorContextMeasured
    let sensoro = sensor[0].sensorContextOrdered
    let merge = []
    sensoro.forEach((node) => {
      let pos = sensorm.map(function(e) { return e.name; }).indexOf(node.name);
      if (pos == -1) {
        merge.push(this.empty_m(node))
      } else {
        merge.push(this.mergeNode(node, sensorm[pos]))
        sensorm.splice(pos, 1);
      }
    });
    sensorm.forEach((node) => {
      merge.push(this.empty_o(node))
    })
    return merge
  }

  private mergeNode(oNode, mNode){
    oNode.attributes.forEach((attr, i) => {
      attr.dataType = [attr.dataType, mNode.attributes[i].dataType]
      attr.name = [attr.name, mNode.attributes[i].name]
      attr.unit = [attr.unit, mNode.attributes[i].unit]
      attr.value = [attr.value, mNode.attributes[i].value]
    })
    return oNode
  }

  private empty_o(node){
    node.attributes.forEach((attr) => {
      attr.dataType = ["", attr.dataType]
      attr.name = ["", attr.name]
      attr.unit = ["", attr.unit]
      attr.value = ["", attr.unit]
    })
    return node
  }

  private empty_m(node){
    node.attributes.forEach((attr) => {
      attr.dataType = [attr.dataType, ""]
      attr.name = [attr.name, ""]
      attr.unit = [attr.unit, ""]
      attr.value = [attr.unit, ""]
    })
    return node
  }

  private specialMerger(contexts) {
		var result = new Object();
		var resultattributegroup, resultattributes, resultattribute;

		for (var i = 0; i < contexts.length; ++i) {
			for (var testname in contexts[i]) {
				var test = contexts[i][testname];
				if (!Array.isArray(test)) continue;

				if (!result[testname]) result[testname] = new Array();
				var subresult = result[testname];

				for (var j = 0; j < test.length; ++j) {
					var attributegroup = test[j];
					if (!(attributegroup instanceof Object)) continue;

					var index = -1;
					subresult.forEach(function (resultattributegroup, idx) {
						if (resultattributegroup["name"] == attributegroup["name"]) { index = idx; return true; }
					});
					if (index < 0) {
						index = subresult.length;
						subresult.push(resultattributegroup = JSON.parse(JSON.stringify(attributegroup)));
						resultattributegroup["attributes"] = new Array();
					}
					else resultattributegroup = subresult[index];
					resultattributes = resultattributegroup["attributes"];

					var attributes = attributegroup["attributes"];
					if (!Array.isArray(attributes)) continue;

					for (var k = 0; k < attributes.length; ++k) {
						var attribute = attributes[k];
						if (!(attribute instanceof Object)) continue;

						var index = -1;
						resultattributes.forEach(function (resultattribute, idx) {
							if (resultattribute["name"] == attribute["name"]) { index = idx; return true; }
						});
						if (index < 0) {
							index = resultattributes.length;
							resultattributes.push(resultattribute = JSON.parse(JSON.stringify(attribute)));
							resultattribute["value"] = new Array(contexts.length);
						}
						else resultattribute = resultattributes[index];
						resultattribute["value"][i] = attribute["value"];
					}
				}
			}
		}
		return result;
	}

  private handleError(error: Response) {
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }
}

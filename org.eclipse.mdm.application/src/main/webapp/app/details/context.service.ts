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
import {Context} from './context';
import {PropertyService} from '../properties';

import {Node} from '../navigator/node';
import {Localization} from '../localization';

import {Components} from './context';

@Injectable()
export class ContextService {
  constructor(private http: Http,
              private _prop: PropertyService){}

  private _host = this._prop.api_host
  private _port = this._prop.api_port
  private _url = 'http://' + this._host + ':' + this._port + this._prop.api_prefix
  private _contextUrl = this._url + '/mdm/environments'

  private _cache : Localization[];
  private test : {}
  private errorMessage: string;

  getContext(node: Node){
    let url = this._contextUrl + "/" + node.sourceName;
    url = url + "/" + node.type.toLowerCase() + "s/" + node.id + '/contexts';
    return this.get(url)
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

  private specialMerger(contexts) {
		// if (arguments.length < 1) return null;

		var result = new Object();
		var resultattributegroup, resultattributes, resultattribute;

		// Iterate through UNITUNDERTESTs
		for (var i = 0; i < contexts.length; ++i) {
			for (var testname in contexts[i]) {
				var test = contexts[i][testname];
				if (!Array.isArray(test)) continue;

				if (!result[testname]) result[testname] = new Array();
				var subresult = result[testname];

				// Iterate through UNITUNDERTEST attribute groups
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

					// Iterate through attributes
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

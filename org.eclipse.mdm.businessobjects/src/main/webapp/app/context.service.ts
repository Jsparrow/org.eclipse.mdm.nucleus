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
import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {Context} from './context'
import {PropertyService} from './properties'

import {Components} from './context';

@Injectable()
export class ContextService {
  constructor(private http: Http
              private _prop: PropertyService){}

  private _host = this._prop.api_host
  private _port = this._prop.api_port
  private _url = 'http://' + this._host + ':' + this._port + this._prop.api_prefix
  private _contextUrl = this._url + '/mdm/environments'

  private _cache : Localization[];
  private test : {}
  private errorMessage: string;

  getContext(node: Node){
    let url = this._contextUrl + "/" + node.sourceName
    url = url + "/" + node.type.toLowerCase() + "s/" + node.id + "/contexts"
    return this.get(url)
  }

  private get(url: string){
    return this.http.get(url)
    .map((res) => {
      let data = res.json().data
      let context = this.specialMerger(data[0].contextOrdered, data[0].contextMeasured)
      // let context = {};
      // let comp = ['UNITUNDERTEST','TESTSEQUENCE','TESTEQUIPMENT']
      // if data[0].contextMeasured {
      //   console.log("debug")
      //   let workaround = true
      // } else {
      //   console.log(data[0].contextMeasured)
      //   console.log("debug²")
      // }
      // comp.forEach(function(entry){
      //   context[entry] = []
      //   for (let i = 0; i < data[0].contextOrdered[entry].length; i++){
      //     let order = data[0].contextOrdered[entry][i].attributes
      //     if workaround {
      //       console.log("debug")
      //       let exec = data[0].contextOrdered[entry][i].attributes
      //       let name = data[0].contextOrdered[entry][i].name
      //     } else {
      //       let exec = data[0].contextMeasured[entry][i].attributes
      //       let name = data[0].contextMeasured[entry][i].name
      //     }
      //     context[entry][i] = {"attributes": order, 'name': name}
      //     for (let a = 0; a < context[entry][i].attributes.length; a++){
      //       context[entry][i].attributes[a]["exec_value"] = exec[a].value
      //     }
      //   }
      // })
      return <{}> context
    })
    .catch(this.handleError);
  }

  private specialMerger() {
		if (arguments.length < 1) return null;

		var result = new Object();
		var resultattributegroup, resultattributes, resultattribute;

		// Iterate through UNITUNDERTESTs
		for (var i = 0; i < arguments.length; ++i) {
			for (var testname in arguments[i]) {
				var test = arguments[i][testname];
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
							resultattribute["value"] = new Array(arguments.length);
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

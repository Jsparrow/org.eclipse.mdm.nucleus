/*******************************************************************************
*  Original work: Copyright (c) 2016 Gigatronik Ingolstadt GmbH                *
*  Modified work: Copyright (c) 2017 Peak Solution GmbH                        *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Dennis Schroeder - initial implementation                                   *
*  Matthias Koller, Johannes Stamm - additional client functionality           *
*******************************************************************************/

import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {Context, Sensor} from './context';
import {PropertyService} from '../core/property.service';
import {HttpErrorHandler} from '../core/http-error-handler';
import {Node} from '../navigator/node';

import {Components} from './context';

@Injectable()
export class ContextService {

  private _contextUrl: string;

  private test: {};
  private errorMessage: string;

  constructor(private http: Http,
              private httpErrorHandler: HttpErrorHandler,
              private _prop: PropertyService) {
    this._contextUrl = _prop.getUrl('/mdm/environments');
  }

  getContext(node: Node) {
    let url = this._contextUrl + '/' + node.sourceName;
    url = url + '/' + node.type.toLowerCase() + 's/' + node.id + '/contexts';
    return this.get(url);
  }

  getSensors(node: Node) {
    let url = this._contextUrl + '/' + node.sourceName + '/' + node.type.toLowerCase() + 's/' + node.id + '/contexts/testequipment/sensors';
    return this.http.get(url)
        .map((res) => { return <{}> this.merge(res.json().data); })
        .catch(this.httpErrorHandler.handleError);
  }

  private get(url: string) {
    return this.http.get(url)
    .map((res) => {
      let data = res.json().data;
      let context = this.specialMerger([data[0].contextOrdered, data[0].contextMeasured]);
      return <{}> context;
    })
    .catch(this.httpErrorHandler.handleError);
  }

  private merge(sensor: Sensor) {
    let sensorm = sensor[0].sensorContextMeasured;
    let sensoro = sensor[0].sensorContextOrdered;
    let merge = [];
    sensoro.forEach((node) => {
      let pos = sensorm.map(function(e) { return e.name; }).indexOf(node.name);
      if (pos === -1) {
        merge.push(this.empty_m(node));
      } else {
        merge.push(this.mergeNode(node, sensorm[pos]));
        sensorm.splice(pos, 1);
      }
    });
    sensorm.forEach((node) => {
      merge.push(this.empty_o(node));
    });
    return merge;
  }

  private mergeNode(oNode, mNode) {
    oNode.attributes.forEach((attr, i) => {
      attr.dataType = [attr.dataType, mNode.attributes[i].dataType];
      attr.name = [attr.name, mNode.attributes[i].name];
      attr.unit = [attr.unit, mNode.attributes[i].unit];
      attr.value = [attr.value, mNode.attributes[i].value];
    });
    return oNode;
  }

  private empty_o(node) {
    node.attributes.forEach((attr) => {
      attr.dataType = ['', attr.dataType];
      attr.name = ['', attr.name];
      attr.unit = ['', attr.unit];
      attr.value = ['', attr.unit];
    });
    return node;
  }

  private empty_m(node) {
    node.attributes.forEach((attr) => {
      attr.dataType = [attr.dataType, ''];
      attr.name = [attr.name, ''];
      attr.unit = [attr.unit, ''];
      attr.value = [attr.unit, ''];
    });
    return node;
  }

  private specialMerger(contexts) {
    let result = new Object();
    let resultattributegroup, resultattributes, resultattribute;

    for (let i = 0; i < contexts.length; ++i) {
      for (let testname in contexts[i]) {
        if (contexts[i].hasOwnProperty(testname)) {
          let test = contexts[i][testname];
          if (!Array.isArray(test)) { continue; }

          if (!result[testname]) {
            result[testname] = new Array();
          }

          let subresult = result[testname];

          for (let j = 0; j < test.length; ++j) {
            let attributegroup = test[j];
            if (!(attributegroup instanceof Object)) { continue; }

            let index = -1;
            subresult.forEach(function (_resultattributegroup, idx) {
              if (_resultattributegroup['name'] === attributegroup['name']) { index = idx; return true; }
            });
            if (index < 0) {
              index = subresult.length;
              subresult.push(resultattributegroup = JSON.parse(JSON.stringify(attributegroup)));
              resultattributegroup['attributes'] = new Array();
            } else {
              resultattributegroup = subresult[index];
            }
            resultattributes = resultattributegroup['attributes'];

            let attributes = attributegroup['attributes'];
            if (!Array.isArray(attributes)) { continue; };

            for (let k = 0; k < attributes.length; ++k) {
              let attribute = attributes[k];
              if (!(attribute instanceof Object)) { continue; }

              let index2 = -1;
              resultattributes.forEach(function (_resultattribute, idx) {
                if (_resultattribute['name'] === attribute['name']) { index2 = idx; return true; }
              });
              if (index2 < 0) {
                index2 = resultattributes.length;
                resultattributes.push(resultattribute = JSON.parse(JSON.stringify(attribute)));
                resultattribute['value'] = new Array(contexts.length);
              } else {
                resultattribute = resultattributes[index2];
              }
              resultattribute['value'][i] = attribute['value'];
            }
          }
        }
      }
    }
    return result;
  }
}

/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


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
    return this.http.get(url)
      .map((res) => {
        let data = res.json().data;
        let context = this.mergeContextRoots([data[0].contextOrdered, data[0].contextMeasured]);
        return <{}> context;
      })
      .catch(this.httpErrorHandler.handleError);
  }

  getSensors(node: Node) {
    let url = this._contextUrl + '/' + node.sourceName + '/' + node.type.toLowerCase() + 's/' + node.id + '/contexts/testequipment/sensors';
    return this.http.get(url)
        .map((res) => { return <{}> this.merge(res.json().data); })
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

  /** Merges several ContextRoots by merging their ContextComponents */
  private mergeContextRoots(contexts) {
    let result : { [context: string]: MergedContextComponent[] } = {};

    for (let i = 0; i < contexts.length; ++i) {
      for (let contextType in contexts[i]) {
        if (contexts[i].hasOwnProperty(contextType)) {
          result[contextType] = this.mergeComponents(contexts[i][contextType], i, result[contextType]);
        }
      }
    }
    return result;
  }

  /** Merges several ContextComponents by merging their ContextAttributes */
  private mergeComponents(contextComponents: any, contextIndex: number, resultComponents: MergedContextComponent[]) {
    if (!Array.isArray(contextComponents)) {
      return resultComponents;
    }

    let result = resultComponents || [];

    for (let contextComponent of contextComponents) {
      if (!(contextComponent instanceof Object)) { continue; }

      let resultComponent = result.find(cc => cc.name === contextComponent['name']);

      if (!resultComponent) {
        resultComponent = JSON.parse(JSON.stringify(contextComponent))
        resultComponent['attributes'] = [];
        result.push(resultComponent);
      }

      resultComponent['attributes'] = this.mergeAttributes(contextComponent['attributes'], contextIndex, resultComponent['attributes']);
    }
    return result;
  }

  /** Merges a array of ContextAttributes to a MergedContextAttribute
   * and adds it to the given resultAttributes array. Merging the ContextAttributes
   * copies the values of all properties (which should be identical), except the
   * value property. The value property in MergedContextAttribute is an array
   * with all values of the value property from all ContextAttributes.
   */
  private mergeAttributes(attributes: ContextAttribute[], contextIndex: number, resultAttributes: MergedContextAttribute[]) {
    if (!Array.isArray(attributes)) {
      return resultAttributes;
    };

    let result = resultAttributes || [];

    for (let attribute of attributes) {
      if (!(attribute instanceof Object)) { continue; }

      let resultAttribute = result.find(a => a.name === attribute['name']);

      if (!resultAttribute) {
        resultAttribute = JSON.parse(JSON.stringify(attribute))
        resultAttribute['value'] = [];
        result.push(resultAttribute);
      }
      resultAttribute['value'][contextIndex] = attribute['value'];
    }

    return resultAttributes;
  }
}

/** Represents a ContextAttribute */
export class ContextAttribute {
  name: string;
  value: string;
  unit: string;
  dataType: string;
}

/** Represents multiple ContextAttributes with their value properties merged to an array */
export class MergedContextAttribute {
  name: string;
  value: string[];
  unit: string;
  dataType: string;
}

/** Represents merged ContextComponents */
export class MergedContextComponent {
  name: string;
  id: string;
  type: string;
  sourcType: string;
  sourceName: string;
  attributes: MergedContextAttribute[];
}

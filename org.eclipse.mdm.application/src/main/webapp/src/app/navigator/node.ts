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


import {Type, Exclude, plainToClass, serialize, deserializeArray} from 'class-transformer';

export class Node {
  name: string;
  id: string;
  type: string;
  sourceType: string;
  sourceName: string;
  @Type(() => Attribute)
  attributes: Attribute[];
  active: boolean;

  getClass() {
    switch (this.type) {
      case 'StructureLevel':
        return 'pool';
      case 'MeaResult':
        return 'measurement';
      case 'SubMatrix':
        return 'channelgroup';
      case 'MeaQuantity':
        return 'channel';
      default:
        return this.type.toLowerCase();
    }
  }
}

export class Attribute {
  name: string;
  value: string;
  unit: string;
  dataType: string;
}

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

import {Type, Exclude, plainToClass, serialize, deserializeArray} from 'class-transformer';

export class Node {
  name: string;
  id: number;
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

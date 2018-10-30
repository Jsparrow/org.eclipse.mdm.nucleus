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


import {Node} from '../navigator/node';

export class MDMItem {
  source: string;
  type: string;
  id: string;

  constructor(source: string, type: string, id: string) {
    this.source = source;
    this.type = type;
    this.id = id;
  }

  equalsNode(node: Node) {
    return this.source === node.sourceName && this.type === node.type && this.id === node.id;
  }

  equals(item: MDMItem) {
    return this.source === item.source && this.type === item.type && this.id === item.id;
  }
}

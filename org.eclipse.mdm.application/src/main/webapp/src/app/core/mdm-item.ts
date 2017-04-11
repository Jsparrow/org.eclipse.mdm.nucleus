/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

import {Node} from '../navigator/node';

export class MDMItem {
  source: string;
  type: string;
  id: number;

  constructor(source: string, type: string, id: number) {
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

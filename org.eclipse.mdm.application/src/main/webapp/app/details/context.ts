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
import {Node} from '../navigator/node';

export class Context {
  contextMeasured: Components;
  contextOrdered: Components;
}

export class Components {
  UNITUNDERTEST: Node[];
  TESTSEQUENCE: Node[];
  TESTEQUIPMENT: Node[];
}

export class Sensor {
  sensorContextMeasured: Node[];
  sensorContextOrdered: Node[];
}

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
export class Node {
  name: string;
  id: number;
  type: string;
  sourceType: string;
  sourceName: string;
  attributes: attribute[];
}

export class attribute {
  name: string;
  value: string;
  unit: string;
  dataType: string;
}

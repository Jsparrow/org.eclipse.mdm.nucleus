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


export class SearchBase<T>{
  value: T;
  t: string;
  a: string;
  key: string;
  label: string;
  required: boolean;
  order: number;
  controlType: string;
  active: boolean;
  constructor(options: {
      value?: T,
      t?: string,
      a?: string,
      key?: string,
      label?: string,
      required?: boolean,
      order?: number,
      controlType?: string,
      active?: boolean
    } = {}) {
    this.value = options.value;
    this.t = options.t;
    this.a = options.a;
    this.key = options.key || '';
    this.label = options.label || '';
    this.required = !!options.required;
    this.order = options.order === undefined ? 1 : options.order;
    this.controlType = options.controlType || '';
    this.active = options.active || false;
  }
}

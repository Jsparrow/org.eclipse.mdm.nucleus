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
export class SearchBase<T>{
  value:T;
  key:string;
  label:string;
  required:boolean;
  order:number;
  controlType:string;
  active:boolean;
  constructor(options:{
      value?:T,
      key?:string,
      label?:string,
      required?:boolean,
      order?:number,
      controlType?:string,
      active?:boolean
    } = {}){
    this.value = options.value;
    this.key = options.key || '';
    this.label = options.label || '';
    this.required = !!options.required;
    this.order = options.order === undefined ? 1 : options.order;
    this.controlType = options.controlType || '';
    this.active = options.active || false;
  }
}

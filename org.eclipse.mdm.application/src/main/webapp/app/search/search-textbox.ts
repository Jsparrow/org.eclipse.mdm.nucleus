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
import {SearchBase} from './search-base';

export class TextboxSearch extends SearchBase<string>{
  controlType = 'textbox';
  type:string;

  constructor(options:{} = {}){
    super(options);
    this.type = options['type'] || '';
  }
}

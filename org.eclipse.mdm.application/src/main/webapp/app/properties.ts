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
import {Injectable, Inject} from '@angular/core';

import {DOCUMENT} from '@angular/platform-browser';

@Injectable()
export class PropertyService {
  api_host: string;
  api_port: string;
  api_prefix: string;
  data_host: string;

  constructor(@Inject(DOCUMENT) private document) {
    this.api_host = 'localhost';
    this.api_port = '8080';
    this.api_prefix = '/org.eclipse.mdm.nucleus';
    this.data_host = 'http://'+ this.api_host + ':' + this.api_port + '/';
  }
}

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
import {Injectable} from '@angular/core';

@Injectable()
export class PropertyService {
  private api_host: string;
  private api_port: string;
  private api_prefix: string;

  constructor() {
    this.api_host = 'sa:sa@localhost';
    this.api_port = '4200';
    this.api_prefix = '/org.eclipse.mdm.nucleus';
  }

  getDataHost(): string {
      return 'http://' + this.api_host + ':' + this.api_port;
  }
  getUrl(): string {
    return this.getDataHost() + this.api_prefix;
  }
}

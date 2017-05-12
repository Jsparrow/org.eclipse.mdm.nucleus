// *******************************************************************************
//   * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
//   * All rights reserved. This program and the accompanying materials
//   * are made available under the terms of the Eclipse Public License v1.0
//   * which accompanies this distribution, and is available at
//   * http://www.eclipse.org/legal/epl-v10.html
//   *
//   * Contributors:
//   * Dennis Schroeder - initial implementation
//   * Matthias Koller, Johannes Stamm - additional client functionality
//   *******************************************************************************
import {Injectable} from '@angular/core';

@Injectable()
export class PropertyService {
  api_host = '/org.eclipse.mdm.nucleus';
  data_host = 'http://sa:sa@localhost:9090/';

  getDataHost(): string {
      return this.data_host;
  }

  getUrl(restUrl: string): string {
    if (this.api_host) {
      return this.api_host + restUrl;
    }
  }
}

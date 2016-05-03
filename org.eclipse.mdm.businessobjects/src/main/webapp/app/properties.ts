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
import {Injectable} from 'angular2/core';

@Injectable()
export class PropertyService {
  api_host: string = 'localhost';
  api_port: string = '8080';
  api_prefix: string = '/org.eclipse.mdm.nucleus';
}

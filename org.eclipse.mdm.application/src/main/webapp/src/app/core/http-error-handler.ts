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


import { Injectable } from '@angular/core';

import { Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';

import { isDevMode } from '@angular/core';

@Injectable()
export class HttpErrorHandler {

  handleError(error: Response | any) {
      let errMsg: string;
      if (error instanceof Response) {
        try {
          const body = error.json() || '';
          const err = body.error || JSON.stringify(body);
          errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
        }
        catch (e) {
          if (isDevMode()) {
            errMsg = `${error.status} - ${error.statusText || ''} ${error.text()}`;
          } else {
            errMsg = `Please contact the administrator. Status code: ${error.status} - ${error.statusText || ''}`;
          }
        }
      } else {
        errMsg = error.message ? error.message : error.toString();
      }
      return Observable.throw(errMsg);
    }
}

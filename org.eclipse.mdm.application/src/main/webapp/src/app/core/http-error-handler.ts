/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller - initial implementation                                    *
*******************************************************************************/

import { Injectable } from '@angular/core';

import { Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';

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
          errMsg = `${error.status} - ${error.statusText || ''} ${error.text()}`;
        }
      } else {
        errMsg = error.message ? error.message : error.toString();
      }
      return Observable.throw(errMsg);
    }
}

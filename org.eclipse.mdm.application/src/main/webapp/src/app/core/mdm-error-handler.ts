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

import { ErrorHandler, Injectable } from '@angular/core';
import { MDMNotificationService } from './mdm-notification.service';

@Injectable()
export class MDMErrorHandler extends ErrorHandler {

  constructor(private notificationService: MDMNotificationService) {
    super(true);
  }

  handleError(error) {
    this.notificationService.notifyError("Applikationsfehler",
      "Es ist ein Applikationsfehler aufgetreten. Für eine detailierte "
      + "Fehlermeldung öffnen Sie bitte die Entwicklerkonsole Ihres Browsers.");

    super.handleError(error);
  }
}

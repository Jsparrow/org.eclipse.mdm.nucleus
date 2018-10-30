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

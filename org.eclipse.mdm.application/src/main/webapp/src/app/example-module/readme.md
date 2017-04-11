/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

To embed this example module in MDM you have to register this module in the MDMModules Module.

This is done by registering a child route to **moduleRoutes** in **modules-routing.module.ts**:
***
 { path: 'example', loadChildren: '../example-module/mdm-example.module#MDMExampleModule'},
***

Furthermore you have to define a display name for the registered route in the array returned by **getLinks** in  **modules.component.ts**:
***
{ path: 'example', name: 'Example Module' }
***

For further information refer to the Angular 2 documentation for modules & router:
* https://angular.io/docs/ts/latest/guide/ngmodule.html
* https://angular.io/docs/ts/latest/guide/router.html

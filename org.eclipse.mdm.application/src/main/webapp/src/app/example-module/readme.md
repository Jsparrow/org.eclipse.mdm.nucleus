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

## Copyright and License ##
Copyright (c) 2015-2018 Contributors to the Eclipse Foundation

 See the NOTICE file(s) distributed with this work for additional
 information regarding copyright ownership.

 This program and the accompanying materials are made available under the
 terms of the Eclipse Public License v. 2.0 which is available at
 http://www.eclipse.org/legal/epl-2.0.

 SPDX-License-Identifier: EPL-2.0

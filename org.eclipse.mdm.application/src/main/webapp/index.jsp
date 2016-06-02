<!-- *******************************************************************************
  * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Dennis Schroeder - initial implementation
  ******************************************************************************* -->
<!DOCTYPE html>
<html>
  <head>
    <base href="${pageContext.request.contextPath}/">
    <title>openMDM5 Web</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <script src="node_modules/es6-shim/es6-shim.min.js"></script>

    <script src="node_modules/zone.js/dist/zone.js"></script>
    <script src="node_modules/reflect-metadata/Reflect.js"></script>
    <script src="node_modules/systemjs/dist/system.src.js"></script>

    <script src="node_modules/ng2-bootstrap/bundles/ng2-bootstrap.min.js"></script>

    <link rel="stylesheet" href="node_modules/bootstrap/dist/css/bootstrap.min.css">

    <script src="systemjs.config.js"></script>
    <script>
    	System.import('app').catch(function(err){ console.error(err); });
    </script>
  </head>
  <body>
    <mdm-web>Loading...</mdm-web>
  </body>
</html>

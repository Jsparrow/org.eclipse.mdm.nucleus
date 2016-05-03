<!--
Copyright (c) 2016 Gigatronik Ingolstadt GmbH
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
-->

## minimum requirements
* JDK 1.8.0_45
* Gradle 2.0

## build application
Before you can install and build the application, you have to checkout and install: (gradlew install)
* org.eclipse.mdm.api.base
* org.eclipse.mdm.api.odsadapter

After installing the API, the command **gradlew install** at **org.eclipse.mdm.nucleus** creates a ZIP archive named **mdm_web.zip** at
**/org.eclipse.mdm.nucleus/build/distributions**

The ZIP archive contains the backend **org.eclipse.mdm.nucleus.war**, the frontend **/frontend** and the configurations **/configuration**

## deploy and configure application
1. **deploy** the backend ( **org.eclipse.mdm.nuclues.war** file) at your application server
2. **copy the content** of the extracted **/configuration** folder to **GLASSFISH_ROOT/glassfish/domains/domain1/config**
3. **edit** the **org.eclipse.mdm.connector/service.xml** file to configure the data sources
4. **copy the content** of the extracted **/frontend** folder to **GLASSFISH_ROOPT/glassfish/domains/domain1/docroot**
5. **edit** the **/app/property.ts** and set the variables host, port and prefix for your deployed
(This properties are used to create the rest URLs to communicate with the backend)
6. **install** and **configure** the **LoginModule** (see org.eclipse.mdm.realms - README.md)
7. **restart** the application server


## available rest URLs
   
 **Business Object: Environment**
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/localizations

* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments_
      
**Business Object: Test**
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/tests 
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/tests/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/tests/TESTID

* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/MDMDATASOURCE1/tests/123_

**Business Object: TestStep**
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/TESTSTEPID
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/TESTSTEPID/contexts
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/TESTSTEPID/contexts/unitundertest
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/TESTSTEPID/contexts/testsequence
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/TESTSTEPID/contexts/testequipment

* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/MDMDATASOURCE1/teststeps/1234/contexts_

**Business Object: Measurement**
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/TESTSTEPID
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/TESTSTEPID/contexts
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/TESTSTEPID/contexts/unitundertest
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/TESTSTEPID/contexts/testsequence
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/TESTSTEPID/contexts/testequipment

* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/MDMDATASOURCE1/measurements/12345/contexts_
   
**Business Object: ChannelGroup**
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channelgroups 
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channelgroups/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channelgroups/CHANNELGROUPID

* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/MDMDATASOURCE1/channelgroups/12345_
   
   
**Business Object: Channel**
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channels 
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channels /localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channels/CHANNELID

* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/MDMDATASOURCE1/channels/123456_

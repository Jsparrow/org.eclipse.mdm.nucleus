<!--
Copyright (c) 2016 Gigatronik Ingolstadt GmbH
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
-->

## minimum requirements
* JDK 1.8.0_45
* Gradle 2.13

## build dependencies
Before you can install and build the application, you have to checkout and install: (gradlew install)
* org.eclipse.mdm.api.base
* org.eclipse.mdm.api.default
* org.eclipse.mdm.api.odsadapter

## build, deploy and configure the application

1. **edit** the **org.eclipse.mdm.nucleus/org.eclipse.mdm.application/src/main/webapp/app** and set the variables host, port and prefix for your deployment
(This properties are used to create the rest URLs to communicate with the backend)
2. **build** the application (gradlew install)
The command **gradlew install** at **org.eclipse.mdm.nucleus** creates a ZIP archive named **mdm_web.zip** at
**/org.eclipse.mdm.nucleus/build/distributions**
The ZIP archive contains the backend **org.eclipse.mdm.nucleus.war** and the configurations **/configuration**
3. **deploy** the backend ( **org.eclipse.mdm.nuclues.war** file) at your application server, check that database for preference service is running (**asadmin start-database**)
4. **copy the content** of the extracted **/configuration** folder to **GLASSFISH_ROOT/glassfish/domains/domain1/config**
5. **edit** the **org.eclipse.mdm.connector/service.xml** file to configure the data sources
6. **install** and **configure** the **LoginModule** (see org.eclipse.mdm.realms - README.md)
7. **restart** the application server
8. **visit** the main page of the client to make sure everything works fine. The main page of the client should be available under
http://SERVER:PORT/APPLICATIONROOT
_(eg: http://localhost:8080/org.eclipse.mdm.nucleus_)


## available rest URLs
   
**Business Object: Environment** 

* http://SERVER:PORT/APPLICATIONROOT/mdm/environments
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/localizations
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments_
      
**Business Object: Test**

* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/tests 
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/tests?filter=FILTERSTRING
* _example:  [http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/tests?filter=Test.Name eq t*](http://localhost:8080/org.eclipse.mdm.nucleus/mdm/MDMDATASOURCE1/tests?filter=Test.Name%20eq%20t*)_
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/tests/searchattributes
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/tests/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/tests/TESTID
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/tests/123_

**Business Object: TestStep**

* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps?filter=FILTERSTRING
* _example: [http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/teststeps?filter=TestStep.Name eq t*](http://localhost:8080/org.eclipse.mdm.nucleus/mdm/MDMDATASOURCE1/teststeps?filter=TestStep.Name%20eq%20t*)_
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/searchattributes
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/TESTSTEPID
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/TESTSTEPID/contexts
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/TESTSTEPID/contexts/unitundertest
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/TESTSTEPID/contexts/testsequence
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/teststeps/TESTSTEPID/contexts/testequipment
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/teststeps/1234/contexts_

**Business Object: Measurement**

* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements?filter=FILTERSTRING
* _example: [http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/measurements?filter=User.Name eq s*](http://localhost:8080/org.eclipse.mdm.nucleus/mdm/MDMDATASOURCE1/measurements?filter=User.Name%20eq%20s*)_
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/searchattributes
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/TESTSTEPID
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/TESTSTEPID/contexts
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/TESTSTEPID/contexts/unitundertest
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/TESTSTEPID/contexts/testsequence
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/measurements/TESTSTEPID/contexts/testequipment
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/measurements/12345/contexts_
   
**Business Object: ChannelGroup**

* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channelgroups 
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channelgroups/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channelgroups/CHANNELGROUPID
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/channelgroups/12345_ 
   
**Business Object: Channel**

* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channels 
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channels/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/channels/CHANNELID
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/channels/123456_

## Preference Service
Preference service stores its data to a relational database. The database connection is looked up be JNDI and the JNDI name is specified in src/main/resources/META-INF/persistence.xml. The default is set to jdbc/__default which is available in glassfish per default and uses a derby database. The derby database is not started automatically with glassfish, but has to be started with following command: GLASSFISH_HOME/bin/asadmin start-database

* http://SERVER:POART/APPLICATIONROOT/mdm/preferences
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/preferences

## FreeTextSearch
### Configuration
1. **start** ElasticSearch. ElasticSearch can be downloaded at https://www.elastic.co/products/elasticsearch. For testing purpose, it can be simply started by executing bin/run.bat
2. **edit** the configuration (global.properties) to fit your environment. You need an ODS Server which supports Notifications. All fields have to be there, but can be empty. However certain ODS Servers ignore some parameters (e.g. PeakODS ignores pollingIntervall since it pushes notifications).
3. **start up** the application. At the first run it will index the database. This might take a while. After that MDM registers itself as NotificationListener and adapts all changes one-by-one.

### Run on dedicated server
The Indexing is completely independent from the searching. So the Indexer can be freely deployed at any other machine. In the simplest case, the same steps as in Configuration have to be done. The application can then be deployed on any other machine. All components besides the FreeTextIndexer and its dependencies are not user. Those can be left out, if desired.


If you run into "java.lang.ClassNotFoundException: javax.xml.parsers.ParserConfigurationException not found by org.eclipse.persistence.moxy" this is a bug described in https://bugs.eclipse.org/bugs/show_bug.cgi?id=463169.
This solution is to replace GLASSFISH_HOME/glassfish/modules/org.eclipse.persistence.moxy.jar with this: http://central.maven.org/maven2/org/eclipse/persistence/org.eclipse.persistence.moxy/2.6.1/org.eclipse.persistence.moxy-2.6.1.jar

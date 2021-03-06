## minimum requirements
* JDK 1.8.0_45
* Gradle 2.13

## build dependencies
Before you can install and build the application, you have to checkout and install: (gradlew install)
* org.eclipse.mdm.api.base
* org.eclipse.mdm.api.default
* org.eclipse.mdm.api.odsadapter

## build, deploy and configure the application

1. **edit** the **org.eclipse.mdm.nucleus/org.eclipse.mdm.application/src/main/webapp/app/core/property.service.ts** and set the variables host, port and prefix for your deployment
(This properties are used to create the rest URLs to communicate with the backend)
Furthermore, specify the **contextPath** in **org.eclipse.mdm.nucleus/org.eclipse.mdm.application/src/main/webapp/webpack.config.js**
2. **build** the application (gradlew install)
The command **gradlew install** at **org.eclipse.mdm.nucleus** creates a ZIP archive named **mdm_web-${version}.zip** at
**/org.eclipse.mdm.nucleus/build/distributions**
The ZIP archive contains the backend **org.eclipse.mdm.nucleus-${version}.war** and the configurations **/configuration**
3. **check** that the database for the preference service is running or else start it with **asadmin start-database**.
4. **deploy** the backend ( **org.eclipse.mdm.nucleuss-${version}.war** file) at your application server. Make sure to deploy the war file with application name **org.eclipse.mdm.nucleus**, otherwise the LoginRealmModule is not able to lookup the ConnectorService EJB. Additionally in the following examples, we assume that the context root is also set to **org.eclipse.mdm.nucleus**.
When deploying on command line you can use: **asadmin deploy --name org.eclipse.mdm.nucleus "/path/to/org.eclipse.mdm.nucleus-${version}.war"**
5. **copy the content** of the extracted **/configuration** folder to **GLASSFISH_ROOT/glassfish/domains/domain1/config**
6. **edit** the **org.eclipse.mdm.connector/service.xml** file to configure the data sources
7. **configure** a **LoginModule** with name **MDMRealm** (See section **Configure LoginModule** for details)
8. **restart** the application server
9. **visit** the main page of the client to make sure everything works fine. The main page of the client should be available under
http://SERVER:PORT/{APPLICATIONROOT}
_(eg: http://localhost:8080/org.eclipse.mdm.nucleus_)

## configure LoginModule
MDM 5 backend implements the delegated approach to roles and permissions, wherein the data sources (ASAM ODS server, PAK cloud)
themselves can implement their own security scheme (which they already have) and then delegates the appropriate
user data to the backends.

In the case of ASAM ODS servers this is done using a technical user and the `for_user` attribute on the established
connection, so it is a form of 'login on behalf'.

In the case of PAK Cloud this is done by passing the user name along with a http header `X-Remote-User` which is then
used by PAK Cloud to establish the users roles (from an internal database or an external authentication provider).

Before the user is 'logged in on behalf' he is authenticated by a LoginModule within the Glassfish application server.
There are different implementations available (e.g. LDAP, Certificate, JDBC, ...). To keep this guide simple, we will setup
a FileRealm, which stores the user information in a flat file:

The following command will create a FileRealm with name `MDMRealm` that stores the users in a file called `mdm-keyfile`:

```
asadmin create-auth-realm --classname com.sun.enterprise.security.auth.realm.file.FileRealm --property file=${com.sun.aas.instanceRoot}/config/mdm-keyfile:jaas-context=MDMRealm:assign-groups=MDM MDMRealm
```

To be able to login you need to explicitly add users to the `MDMRealm`:

```
asadmin create-file-user --authrealmname MDMRealm
```

Next you need to add the following snippet to your `login.conf` in `${com.sun.aas.instanceRoot}/config/`

```
MDMRealm {
  com.sun.enterprise.security.auth.login.FileLoginModule required;
};
```
As a last step you have to provide the credentials of the technical user in adapter configuration in `service.xml`. 
For the ODS adapter you you have to specify the parameters `user` and `password` of the technical user. For example:

```
<service entityManagerFactoryClass="org.eclipse.mdm.api.odsadapter.ODSContextFactory">
    ...
	<param name="user">sa</param>
	<param name="password">sa</param>
	...
</service>
```

Make sure to restart Glassfish after this change.

### Remove MDMLoginRealm from previous versions:
In versions 5.0.0M1, 0.10 and older the configuration of a custom login realm was neccessary. If you configured your glassfish instance
for one of these version, you can remove the old configuration options and artifact, as they are no longer needed:

* **delete** the jar file **org.eclipse.mdm.realm.login.glassfish-VERSION.jar** from **GLASSFISH4_ROOT/glassfish/domains/domain1/lib**
   
* **open** the Glassfish login **configuration file** at **GLASSFISH4_ROOT/glassfish/domains/domain1/config/login.conf**
   
* **delete** custom MDM realm module entry to this config file
    
```   
MDMLoginRealm {
  org.eclipse.mdm.realm.login.glassfish.LoginRealmModule required;
};
```

* **remove** the MDMLoginRealm by executing the following command or by deleting it in the Glassfish web console:
 
```
asadmin delete-auth-realm MDMLoginRealm
```


## configure logging

MDM 5 uses SLF4J and logback for logging. The default configuration file can be found at **org.eclipse.mdm.nucleus/src/main/resources/logback.xml**. It logs INFO level messages to **mdm5.log** in the **logs** folder of the Glassfish domain. If you want to customize logging, you can either edit the file within the war file or preferably provide your own logging configuration via system parameter in the JVM settings in Glassfish: **-Dlogback.configurationFile=/path/to/config.xml**

## available rest URLs

**Business Object: Environment**

* http://{SERVER}:{PORT}/{APPLICATIONROOT}/mdm/environments
* http://{SERVER}:{PORT}/{APPLICATIONROOT}/mdm/environments/{SOURCENAME}
* http://{SERVER}:{PORT}/{APPLICATIONROOT}/mdm/environments/{SOURCENAME}/localizations
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments_

Since all other calls operate on a specific source all subsequent calls share the common prefix
`http://{SERVER}:{PORT}/{APPLICATIONROOT}/mdm/environments/{SOURCENAME}/`, which we will be our root for the description of the next calls.
Strings enclosed in curly brackets are meant to be replaced by appropriate values. The following parameters are recurring throughout the different URLs:

* APPLICATIONROOT is the context root under which MDM is deployed
* SOURCENAME is the source name the underlying data source
* CONTEXTTYPE is one of [unitundertest, testsequence, testequipment]
* DATATYPE is one of [STRING, STRING_SEQUENCE, DATE, DATE_SEQUENCE, BOOLEAN, BOOLEAN_SEQUENCE, BYTE, BYTE_SEQUENCE, SHORT, SHORT_SEQUENCE, INTEGER, INTEGER_SEQUENCE, LONG, LONG_SEQUENCE, FLOAT, FLOAT_SEQUENCE, DOUBLE, DOUBLE_SEQUENCE, BYTE_STREAM, BYTE_STREAM_SEQUENCE, FLOAT_COMPLEX, FLOAT_COMPLEX_SEQUENCE, DOUBLE_COMPLEX, DOUBLE_COMPLEX_SEQUENCE, FILE_LINK, FILE_LINK_SEQUENCE] 
* FILTERSTRING is a String defining a filter. For example `Test.Name eq 't*'` filters for all tests which names begin with `t`.


**Business Object: Test**

* GET:    /tests
* GET:    /tests?filter={FILTERSTRING}
* GET:    /tests/searchattributes
* GET:    /tests/localizations
* GET:    /tests/{TESTID}

**Business Object: TestStep**

* GET:    /teststeps
* GET:    /teststeps?filter=FILTERSTRING
* GET:    /teststeps/searchattributes
* GET:    /teststeps/localizations
* GET:    /teststeps/{TESTSTEPID}
* GET:    /teststeps/{TESTSTEPID}/contexts
* GET:    /teststeps/{TESTSTEPID}/contexts/{CONTEXTTYPE}

**Business Object: Measurement**

* GET:    /measurements
* GET:    /measurements?filter={FILTERSTRING}
* GET:    /measurements/searchattributes
* GET:    /measurements/localizations
* GET:    /measurements/{MEASUREMENTID}
* GET:    /measurements/{MEASUREMENTID}/contexts
* GET:    /measurements/{MEASUREMENTID}/contexts/{CONTEXTTYPE}

**Business Object: ChannelGroup**

* GET:    /channelgroups
* GET:    /channelgroups/localizations
* GET:    /channelgroups/{CHANNELGROUPID}

**Business Object: Channel**

* GET:    /channels
* GET:    /channels/localizations
* GET:    /channels/{CHANNELID}

**Business Object: Project**

* GET:    /projects
* GET:    /projects?filter={FILTERSTRING}
* GET:    /projects/searchattributes
* GET:    /projects/localizations
* GET:    /projects/{PROJECTID}

**Business Object: Pool**

* GET:    /pools
* GET:    /pools?filter={FILTERSTRING}
* GET:    /pools/searchattributes
* GET:    /pools/localizations
* GET:    /pools/{POOLID}

**Business Object: ValueList**

* GET:    /valuelists
* POST:   /valuelists (JSON: { "Name" : "testValueList" })
* GET:    /valuelists/{VALUELISTID}
* PUT:    /valuelists/{VALUELISTID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /valuelists/{VALUELISTID}
* GET:    /valuelists/searchattributes
* GET:    /valuelists/localizations

**Business Object: ValueListValue**

* GET:    /valuelists/{VALUELISTID}/values
* POST:   /valuelists/{VALUELISTID}/values (JSON: { "Name" : "testValueListValue" })
* GET:    /valuelists/{VALUELISTID}/values/{VALUELISTVALUEID}
* PUT:    /valuelists/{VALUELISTID}/values/{VALUELISTVALUEID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /valuelists/{VALUELISTID}/values/{VALUELISTVALUEID}
* GET:    /valuelists/{VALUELISTID}/values/searchattributes
* GET:    /valuelists/{VALUELISTID}/values/localizations

**Business Object: PhysicalDimension**

* GET:    /physicaldimensions
* POST:   /physicaldimensions (JSON: { "Name" : "testPhysicalDimension" })
* GET:    /physicaldimensions/{PHYSICALDIMENSIONID}
* PUT:    /physicaldimensions/{PHYSICALDIMENSIONID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /physicaldimensions/{PHYSICALDIMENSIONID}
* GET:    /physicaldimensions/searchattributes
* GET:    /physicaldimensions/localizations

**Business Object: Unit**

* GET:    /units
* POST:   /units (JSON: { "Name" : "testUnit", "PhysicalDimension" : "PHYSICALDIMENSIONID" })
* GET:    /units/{UNITID}
* PUT:    /units/{UNITID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /units/{UNITID}
* GET:    /units/searchattributes
* GET:    /units/localizations

**Business Object: Quantity**

* GET:    /quantities
* POST:   /quantities (JSON: { "Name" : "testQuantity", "Unit" : "UNITID" })
* GET:    /quantities/{QUANTITYID}
* PUT:    /quantities/{QUANTITYID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /quantities/{QUANTITYID}
* GET:    /quantities/searchattributes
* GET:    /quantities/localizations

**Business Object: CatalogComponent**

* GET:    /catcomps/{CONTEXTTYPE}
* POST:   /catcomps/{CONTEXTTYPE} (JSON: { "Name" : "testCatalogComponent" })
* GET:    /catcomps/{CONTEXTTYPE}/{CATALOGCOMPONENTID}
* PUT:    /catcomps/{CONTEXTTYPE}/{CATALOGCOMPONENTID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /catcomps/{CONTEXTTYPE}/{CATALOGCOMPONENTID}
* GET:    /catcomps/{CONTEXTTYPE}/searchattributes
* GET:    /catcomps/{CONTEXTTYPE}/localizations

**Business Object: CatalogAttribute**

* GET:    /catcomps/{CONTEXTTYPE}/{CATALOGCOMPONENTID}/catattrs
* POST:   /catcomps/{CONTEXTTYPE}/{CATALOGCOMPONENTID}/catattrs (JSON: { "Name" : "testCatalogAttribute", "DataType" : "DATATYPE" })
* GET:    /catcomps/{CONTEXTTYPE}/{CATALOGCOMPONENTID}/catattrs/{CATALOGATTRIBUTEID}
* PUT:    /catcomps/{CONTEXTTYPE}/{CATALOGCOMPONENTID}/catattrs/{CATALOGATTRIBUTEID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /catcomps/{CONTEXTTYPE}/{CATALOGCOMPONENTID}/catattrs/{CATALOGATTRIBUTEID}
* GET:    /catcomps/{CONTEXTTYPE}/{CATALOGCOMPONENTID}/catattrs/searchattributes
* GET:    /catcomps/{CONTEXTTYPE}/{CATALOGCOMPONENTID}/catattrs/localizations

**Business Object: CatalogSensor**

* GET:    /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors
* POST:   /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors (JSON: { "Name" : "testCatalogSensor" })
* GET:    /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/{CATALOGSENSORID}
* PUT:    /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/{CATALOGSENSORID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/{CATALOGSENSORID}
* GET:    /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/searchattributes
* GET:    /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/localizations

**Business Object: CatalogSensorAttribute**

* GET:    /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/{CATALOGSENSORID}/catsensorattrs
* POST:   /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/{CATALOGSENSORID}/catsensorattrs (JSON: { "Name" : "testCatalogAttribute", "DataType" : "DATATYPE" })
* GET:    /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/{CATALOGSENSORID}/catsensorattrs/{CATALOGATTRIBUTEID}
* PUT:    /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/{CATALOGSENSORID}/catsensorattrs/{CATALOGATTRIBUTEID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/{CATALOGSENSORID}/catsensorattrs/{CATALOGATTRIBUTEID}
* GET:    /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/{CATALOGSENSORID}/catsensorattrs/searchattributes
* GET:    /catcomps/testequipment/{CATALOGCOMPONENTID}/catsensors/{CATALOGSENSORID}/catsensorattrs/localizations

**Business Object: TemplateRoot**

* GET:    /tplroots/{CONTEXTTYPE}
* POST:   /tplroots/{CONTEXTTYPE} (JSON: { "Name" : "testTemplateRoot" })
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}
* PUT:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}
* GET:    /tplroots/{CONTEXTTYPE}/searchattributes
* GET:    /tplroots/{CONTEXTTYPE}/localizations

**Business Object: TemplateComponent**

* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps
* POST:   /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps (JSON: { "Name" : "testTemplateComponent", "CatalogComponent" : "CATALOGCOMPONENTID" })
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}
* PUT:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/searchattributes
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/localizations

**Business Object: TemplateAttribute**

* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplattrs
* POST:   /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplattrs (JSON: { "Name" : "testCatalogAttribute" } (name must be identical with corresponding CatalogAttribute))
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplattrs/{TEMPLATEATTRIBUTEID}
* PUT:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplattrs/{TEMPLATEATTRIBUTEID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplattrs/{TEMPLATEATTRIBUTEID}
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplattrs/searchattributes
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplattrs/localizations

**Business Object: TemplateSensor**

* GET:    /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors
* POST:   /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors (JSON: { "Name" : "testTemplateSensor", "CatalogSensor" : "CATALOGSENSORID", "quantity" : "QUANTITYID" })
* GET:    /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors/{TEMPLATESENSORID}
* PUT:    /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors/{TEMPLATESENSORID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors/{TEMPLATESENSORID}
* GET:    /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors/searchattributes
* GET:    /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors/localizations

**Business Object: TemplateSensorAttribute**

* GET:    /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors/{TEMPLATESENSORID}/tplsensorattrs
* GET:    /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors/{TEMPLATESENSORID}/tplsensorattrs/{TEMPLATEATTRIBUTEID}
* PUT:    /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors/{TEMPLATESENSORID}/tplsensorattrs/{TEMPLATEATTRIBUTEID} (JSON: { "MimeType" : "myMimeType" })
* GET:    /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors/{TEMPLATESENSORID}/tplsensorattrs/searchattributes
* GET:    /tplroots/testequipment/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplsensors/{TEMPLATESENSORID}/tplsensorattrs/localizations

**Business Object: NestedTemplateComponent**

* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps
* POST:   /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps (JSON: { "Name" : "testNestedTemplateComponent", "CatalogComponent" : "CATALOGCOMPONENTID" })
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/{NESTEDTEMPLATECOMPONENTID}
* PUT:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/{NESTEDTEMPLATECOMPONENTID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/{NESTEDTEMPLATECOMPONENTID}
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/searchattributes
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/localizations

**Business Object: NestedTemplateAttribute**

* CONTEXTTYPE is one of [unitundertest, testsequence, testequipment]

* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/{NESTEDTEMPLATECOMPONENTID}/tplattrs
* POST:   /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/{NESTEDTEMPLATECOMPONENTID}/tplattrs (JSON: { "Name" : "testCatalogAttribute" } (name must be identical with corresponding CatalogAttribute))
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/{NESTEDTEMPLATECOMPONENTID}/tplattrs/{NESTEDTEMPLATEATTRIBUTEID}
* PUT:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/{NESTEDTEMPLATECOMPONENTID}/tplattrs/{NESTEDTEMPLATEATTRIBUTEID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/{NESTEDTEMPLATECOMPONENTID}/tplattrs/{NESTEDTEMPLATEATTRIBUTEID}
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/{NESTEDTEMPLATECOMPONENTID}/tplattrs/searchattributes
* GET:    /tplroots/{CONTEXTTYPE}/{TEMPLATEROOTID}/tplcomps/{TEMPLATECOMPONENTID}/tplcomps/{NESTEDTEMPLATECOMPONENTID}/tplattrs/localizations

**Business Object: TemplateTest**

* GET:    /tpltests
* POST:   /tpltests (JSON: { "Name" : "testTemplateTest" })
* GET:    /tpltests/{TEMPLATETESTID}
* PUT:    /tpltests/{TEMPLATETESTID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /tpltests/{TEMPLATETESTID}
* GET:    /tpltests/searchattributes
* GET:    /tpltests/localizations

**Business Object: TemplateTestStep**

* GET:    /tplteststeps
* POST:   /tplteststeps (JSON: { "Name" : "testTemplateTestStep" })
* GET:    /tplteststeps/{TEMPLATETESTSTEPID}
* PUT:    /tplteststeps/{TEMPLATETESTSTEPID} (JSON: { "MimeType" : "myMimeType" })
* DELETE: /tplteststeps/{TEMPLATETESTSTEPID}
* GET:    /tplteststeps/searchattributes
* GET:    /tplteststeps/localizations

**Business Object: TemplateTestStepUsage**

* GET:    /tpltests/{TEMPLATETESTID}/tplteststepusages
* POST:   /tpltests/{TEMPLATETESTID}/tplteststepusages (JSON: { "Name" : "testTemplateTestStepUsage", "TemplateTestStep" : "TEMPLATETESTSTEPID" })
* GET:    /tpltests/{TEMPLATETESTID}/tplteststepusages/{TEMPLATETESTSTEPUSAGEID}
* DELETE: /tpltests/{TEMPLATETESTID}/tplteststepusages/{TEMPLATETESTSTEPUSAGEID}
* GET:    /tpltests/{TEMPLATETESTID}/tplteststepusages/searchattributes
* GET:    /tpltests/{TEMPLATETESTID}/tplteststepusages/localizations

**Query endpoint**

* http://{SERVER}:{PORT}/{APPLICATIONROOT}/mdm/query

  _example:  
`curl -POST -H "Content-Type: application/json" -d '{"resultType": "test", "columns": ["Test.Name", "TestStep.Name"], "filters": { "sourceName": "SOURCENAME", "filter": "Test.Id gt 1", "searchString": ""}}'http://sa:sa@localhost:8080/org.eclipse.mdm.nucleus/mdm/query`
* http://{SERVER}:{PORT}/{APPLICATIONROOT}/mdm/suggestions

  _example:  `curl -POST -H "Content-Type: application/json" -d '{"sourceNames": ["SOURCENAME"], "type": "Test", "attrName": "Name"}' http://sa:sa@localhost:8080/org.eclipse.mdm.nucleus/mdm/suggestions`



## Preference Service
Preference service stores its data to a relational database. The database connection is looked up by JNDI and the JNDI name and other database relevant parameters are specified in src/main/resources/META-INF/persistence.xml. The default JNDI name for the JDBC resource is set to jdbc/openMDM. This JDBC resource and its dependent JDBC Connection Pool has to be created and configured within the glassfish web administration console or through asadmin command line tool.

Furthermore the schema has to be created in the configured database. Therefore database DDL scripts are available for PostgreSQL and Apache Derby databases in the folder `schema/org.eclipse.mdm.preferences` of the distribution. Other databases supported by EclipseLink may also work, but is up to the user to adapt the DDL scripts.

### available rest URLs
* http://{SERVER}:POART/{APPLICATIONROOT}/mdm/preferences
* example: `curl -GET -H "Content-Type: application/json" http://localhost:8080/org.eclipse.mdm.nucleus/mdm/preferences?scope=SYSTEM&key=ignoredAttributes`
* example: `curl -PUT -H "Content-Type: application/json" -d '{"scope": "SYSTEM", "key": "ignoredAttributes", "value": "[\"*.MimeType\"]"}' http://localhost:8080/org.eclipse.mdm.nucleus/mdm/preferences`
* example: `curl -DELETE http://localhost:8080/org.eclipse.mdm.nucleus/mdm/preferences/ID`


## FreeTextSearch
### Configuration
1. **start** ElasticSearch. ElasticSearch can be downloaded at https://www.elastic.co/products/elasticsearch. For testing purpose, it can be simply started by executing bin/run.bat
2. **edit** the configuration (global.properties) to fit your environment. You need an ODS Server which supports Notifications. All fields have to be there, but can be empty. However certain ODS Servers ignore some parameters (e.g. PeakODS ignores pollingIntervall since it pushes notifications).
3. **start up** the application. At the first run it will index the database. This might take a while. After that MDM registers itself as NotificationListener and adapts all changes one-by-one.

### Run on dedicated server
The Indexing is completely independent from the searching. So the Indexer can be freely deployed at any other machine. In the simplest case, the same steps as in Configuration have to be done. The application can then be deployed on any other machine. All components besides the FreeTextIndexer and its dependencies are not user. Those can be left out, if desired.


##Known issues:
If you run into "java.lang.ClassNotFoundException: javax.xml.parsers.ParserConfigurationException not found by org.eclipse.persistence.moxy" this is a bug described in https://bugs.eclipse.org/bugs/show_bug.cgi?id=463169 and https://java.net/jira/browse/GLASSFISH-21440.
This solution is to replace GLASSFISH_HOME/glassfish/modules/org.eclipse.persistence.moxy.jar with this: http://central.maven.org/maven2/org/eclipse/persistence/org.eclipse.persistence.moxy/2.6.1/org.eclipse.persistence.moxy-2.6.1.jar


If you run into "java.lang.ClassNotFoundException: com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector not found by com.fasterxml.jackson.jaxrs.jackson-jaxrs-json-provider" you have to download http://central.maven.org/maven2/com/fasterxml/jackson/module/jackson-module-jaxb-annotations/2.5.1/jackson-module-jaxb-annotations-2.5.1.jar and put it under GLASSFISH_HOME/glassfish/domains/domain1/autodeploy/bundles

## Client preferences

The applications preferences are managed in the administration section. This section can be accessed via the `Administration` button in the main navigation bar or via
* `http://../administration`.
A preference is a pair of a unique key and a value. The key is composed of a prefix defining the purpose of the preference followed by an arbitrary but unique identifier string. It is recommended to choose the identifier the same as the preferences 'name' field, in case there is one. The value holds the preference's data in a Json string. The following preferences, sorted by their scope, can be set:

User:
  - Basket
  - View
  - Filter

System:
  - Node provider
  - Shopping basket file extensions

Source:
  - Ignored attributes

However, it might be necessary to reload the application before a newly defined preference is available or any changes on an existing preferences are applied.
WARNING: Corrupted preferences can result in malfunctions of the application.

### User scope
A user scoped preference's area of effect is limited to the logged in user. All user scoped preferences can also be set in dialogs in the main application.

1.) Basket
  Basket preferences keys must start with the prefix `basket.nodes.`. This preference has the fields 'items' and 'name' and holds all the information for saved baskets. The field 'items' holds an array of MDMItems, providing the relevant information of a related node, i.e. 'source', 'type' and 'id'. The field 'name' defines the name, which is provided in the main application to load this basket.

  ** Example:
  { "items": [{"source":"MDMNVH","type":"Test","id":38}],
    "name": "basketExample" }

2.) View
  View preferences keys must start with the prefix `tableview.view.` This preference has the fields 'columns' and 'name' and holds the layout information for the tables displaying the search results and the basket nodes.
  The field 'columns' holds an array of ViewColumn objects. A ViewColumn is an Object with the fields 'type', 'name', 'sortOrder' and an optional field 'style'. The ViewColumn's 'type' can be set to all available MDM data types, i.e. `Project`, `Pool`, `Test`, `TestStep`, `Measurement`, `ChannelGroup`, `Channel`. The ViewColumn's 'name' field specifies an attribute, which must be an searchable attribute for the given 'type'. The ViewColumn's sortOrder can be set by the number `1` (ascending), `-1` (descending), or null (unsorted). Only one column of the array can have a non-null value sortOrder at a time. The ViewColumn's style element can hold any CSS-style object. However, it is supposed to contain only the columns width. The column order in the array is identically with the appearance in the table.
  The view's field 'name' defines the name, which is provided in the main application to load this view.

  **Example:
  { "columns": [{"type":"Test","name":"Id","style":{"width":"75px"},"sortOrder":null}],
    "name": "viewExample" }

3.) Filter
  Filter preferences keys must start with the prefix `filter.nodes.`. This preference has the fields 'conditions', 'name', 'environments', 'resultType' and 'fulltextQuery'. It provides the information for the attribute based / advanced search.
  The field 'conditions' holds an array of Condition objects. A Condition specifies a search condition for attribute based search. It consists of the fields 'type', 'name', 'operator', 'value' and 'valueType'. The Condition's 'type' can be set to all available MDM data types, i.e. `Project`, `Pool`, `Test`, `TestStep`, `Measurement`, `ChannelGroup`, `Channel`. The Condition's 'name' field specifies an attribute, which must be an searchable attribute for the given 'type'. The Condition's 'operator' field, holds on of the following numbers: `0`(=), `1`(<), `2`(>), `3`(like). The Condition's 'value' field holds a string array containing input for the attribute based search. The Condition's 'resultType' field should match the type corresponding to the attribute specified in the 'name' filed, e.g. `string`, `date` or `long`.
  The Filter's field 'name' defines the name, which is provided in the main application to load this filter.
  The Filter's field 'environments' holds an string array with the names of the sources that should be included in the search.
  The Filter's field 'resultType' can be set to all available MDM data types (see above). Only nodes of this type will be included in the search.
  The Filter's field 'fulltextQuery' holds a string containing full text search input.

  **Example:
  { "conditions":[{"type":"Test","attribute":"Name","operator":0,"value":[],"valueType":"string"}],
    "name":"filterExample",
    "environments":["sourceName"],
    "resultType":"Test",
    "fulltextQuery":"" }


### System scope
System scoped preference are applied globally.

1.) Node provider

  The navigation tree structure can be defined via a node provider. The default node provider is set in
  * ..\src\main\webapp\src\app\navigator\defaultnodeprovider.json.
  It is recommended not to change the default node provider. Instead new node providers can be added as preferences. Their keys must start with the prefix ´nodeprovider.´. Once a custom node provider is supplied it can be selected in the dropdown menu in the navigation tree header.

  I.) Structure

    a) First layer/root nodes
    In the node provider each layer of nodes of the navigation tree is defined in a nested object. The first layer of nodes, is always the environment level. This is necessary due to the provided endpoints. The first layer consists of the fields 'name', 'type', and 'children'. The field 'name' sets the name, which is displayed in the application to select the corresponding node provider. The field 'type' defines the data type of the nodes, which is always `Environments` on the first layer. The next layer of nodes are defined via the field 'children'.

    b) Children
    A child object consists of the fields 'type', 'query', and 'children'. The field 'type' sets the data type of this layer of nodes. It can be set to all available MDM data types, i.e. `Project`, `Pool`, `Test`, `TestStep`, `Measurement`, `ChannelGroup`, `Channel`. The filed 'query' holds the URL to load this layer of nodes. The first part of the query for each child object should be `/` plus the type of the layer in small letters followed by an `s`. For a nested child objected a filter is attached to the query in the form: `?filter=<parentType>.Id eq {<parentType>.Id}`. The placeholder <parentType> has to be replaced by the actual parent type (set in the field 'type' in the parent child or root layer, see example below). At runtime the curly braces will be replaced by the Id of the parent node. Further child objects, and thereby more sublayers, can be nested via the field 'children'. The children field does not need to be set for the last layer of nodes.

  II.) Examples

    a) Minimal node provider
    { "name": "My name to display", "type": "Environment"}

    b) node provider with two child layers
    {
      "name": "My name to display",
      "type": "Environment",
      "children": {
        "type": "Project",
        "query": "/projects",
        "children": {
          "type": "Pool",
          "query": "/pools?filter=Project.Id eq {Project.Id}"
        }
      }
    }
    
2.) Shopping basket file extensions

When downloading the contents of a shopping basket, a file with extension `mdm` is generated. The file extension can be changed by adding a preference with key `shoppingbasket.fileextensions`. For example the used extension can be set to `mdm-xml` by setting the value to `{ "default": "mdm-xml" }`.

### Source scope
Source scoped preferences are applied at any user but limited to the specified source. The source can be specified in the `Add Preference` or `Edit Preference` dialog.

1.) Ignored Attributes
The ignore attributes preference must have the exact key `ignoredAttributes`. An identifier must not be added. The preference specifies all attributes, which are supposed to be ignored in the detail view. The preference is a simple Json string holding a list of attributes in the form {"<type>.<AttributeName>"}. The placeholders <type> and <AttributeName> have to be replaced by the actual type and name of the attribute which should be ignored, respectively.

**Example:
["*.MimeType", "TestStep.Sortindex"]

##Create a module
Any MDM module needs to be a valid angular2 module. An angular2 module consists of one angular2 component and one ng-module at least. In the angular2 component any content can be defined. The component must be declared in a ng-module to grant accessibility in the rest of the application. In a module additional components and services can be defined. All related files should be stored in a new subfolder in the app folder
* ..\org.eclipse.mdm.nucleus\org.eclipse.mdm.application\src\main\webapp\src\app.

###Angular2 components (see example 1)
A component is defined in a typescript file starting with the @Component() identifier. Any html content can be provided here in an inline template or via a link to an external html resource. Thereafter the component itself, which is supposed to hold any logic needed, is defined and exported.

###Ng-module (see example 2)
 On one hand the ng-module provides other MDM modules of the application, and thereby all the services and components declared within them, in this MDM module. The 'imports' array holds all modules from the application needed in this MDM module. It should always hold the MDMCoreModule, which provides basic functionalities. On the other hand a ng-module grants accessibility of components of this module in other directives (including the html template) within the module (in a 'declaration' array) or even in other parts of the application (in an 'export' array). For more details see *https://angular.io/docs/ts/latest/guide/ngmodule.html.

 ** Minimal example
 First create a new folder
 * ..\org.eclipse.mdm.nucleus\org.eclipse.mdm.application\src\main\webapp\src\app\new-module

 1) Minimal angular2 component
 Add a new typescript file named 'mdm-new.component.ts' with the following content:

 import {Component} from '@angular/core';
 @Component({template: '<h1>Example Module</h1>'})
 export class MDMNewComponent {}

 2) Minimal ng-module
 Add a new typescript file named 'mdm-new.module.ts' with the following content:

 import { NgModule } from '@angular/core';
 import { MDMCoreModule } from '../core/mdm-core.module';
 import { MDMNewComponent } from './mdm-new.component';
 @NgModule({imports: [MDMCoreModule], declarations: [MDMNewComponent]})
 export class MDMNewModule {}

###Embedding a module (no lazy loading)
To embed this new module in MDM you have to register this module in the MDMModules Module.
This is done by registering the component to **moduleRoutes** in **modules-routing.module.ts**:
***
 { path: 'new', component: MDMNewComponent}
***

Furthermore you have to define a display name for the registered route in the array returned by **getLinks** in  **modules.component.ts**:
***
{ path: 'new', name: 'New Module' }
***

For further information refer to the Angular 2 documentation for modules & router:
* https://angular.io/docs/ts/latest/guide/ngmodule.html
* https://angular.io/docs/ts/latest/guide/router.html


 ###Lazy loading and routing module
 For lazy-loading (recommended in case there is a high number of modules) embedding of the module is slightly different.
 ***
  { path: 'example', loadChildren: '../example-module/mdm-example.module#MDMExampleModule'}
 ***

 Additionally, a ng-module, the so called routing module, is needed to provide the routes to this modules components.
 ***
  const moduleRoutes: Routes = [{ path: '', component: MDMExampleComponent }];
  @NgModule({imports: [RouterModule.forChild(moduleRoutes)], exports: [RouterModule]})
  export class MDMExampleRoutingModule {}
 ***   

 The routing module needs to be declared in the ng-module of this module as well. A full example is provided in
  * ..\org.eclipse.mdm.nucleus\org.eclipse.mdm.application\src\main\webapp\src\app\example-module


 ###Filerelease module
 The filerelease module is stored in the following folder:
*..\org.eclipse.mdm.nucleus\org.eclipse.mdm.application\src\main\webapp\src\app\filerelease

It can be embedded as any other module described above. Register to **moduleRoutes** in **modules-routing.module.ts**:
***
 { path: 'filerelease', component: MDMFilereleaseComponent }
***

Add entry to **links** array in **MDMModulesComponent**: 
***
  { name: 'MDM Suche', path: 'search'}
***

To make the filerelease module available in the detail view it needs to be imported in the corresponding ng-module **mdm-detail.module.ts**.
Thereafter, the MDMFilereleaseCreateComponent can be imported to the **mdm-detail-view.component.ts**. Then the following has to be added to the **mdm-detail-view.component.html** file:
***
  <mdm-filerelease-create [node]=selectedNode [disabled]="isReleasable()"></mdm-filerelease-create>
***



It should be located right after the add to basket button:
***
<div class="btn-group pull-right" role="group">
  <button type="button" class="btn btn-default" (click)="add2Basket()" [disabled]="isShopable()">In den Warenkorb</button>
  <mdm-filerelease-create [node]=selectedNode [disabled]="isReleasable()"></mdm-filerelease-create>
</div>
***

## Copyright and License ##
Copyright (c) 2015-2018 Contributors to the Eclipse Foundation

 See the NOTICE file(s) distributed with this work for additional
 information regarding copyright ownership.

 This program and the accompanying materials are made available under the
 terms of the Eclipse Public License v. 2.0 which is available at
 http://www.eclipse.org/legal/epl-2.0.

 SPDX-License-Identifier: EPL-2.0
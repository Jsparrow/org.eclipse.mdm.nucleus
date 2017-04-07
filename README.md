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

1. **edit** the **org.eclipse.mdm.nucleus/org.eclipse.mdm.application/src/main/webapp/app/core/property.service.ts** and set the variables host, port and prefix for your deployment
(This properties are used to create the rest URLs to communicate with the backend)
Furthermore, specify the **contextPath** in **org.eclipse.mdm.nucleus/org.eclipse.mdm.application/src/main/webapp/webpack.config.js**
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

**Business Object: Project**

* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/projects
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/projects?filter=FILTERSTRING
* _example:  [http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/projects?filter=Project.Name eq p*](http://localhost:8080/org.eclipse.mdm.nucleus/mdm/MDMDATASOURCE1/projects?filter=Project.Name%20eq%20p*)_
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/projects/searchattributes
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/projects/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/projects/PROJECTID
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/projects/123_

**Business Object: Pool**

* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/pools
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/pools?filter=FILTERSTRING
* _example:  [http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/pools?filter=Pool.Name eq p*](http://localhost:8080/org.eclipse.mdm.nucleus/mdm/MDMDATASOURCE1/pools?filter=Pool.Name%20eq%20p*)_
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/pools/searchattributes
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/pools/localizations
* http://SERVER:PORT/APPLICATIONROOT/mdm/environments/SOURCENAME/pools/POOLID
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/environments/MDMDATASOURCE1/pools/123_


## Preference Service
Preference service stores its data to a relational database. The database connection is looked up by JNDI and the JNDI name and other database relevant parameters are specified in src/main/resources/META-INF/persistence.xml. Database DDL scripts are available for PostgreSQL and Apache Derby databases in the folder `schema/org.eclipse.mdm.preferences` of the distribution.

### Quickstart with Apache Derby

The default datasource is set to jdbc/__default, which is available in glassfish per default and uses a derby database. 
The derby database is not started automatically with glassfish, but has to be started with following command: 
`GLASSFISH_HOME/bin/asadmin start-database`
With the default parameter from persistence.xml the schema is created automatically during deployment.

### available rest URLs
* http://SERVER:POART/APPLICATIONROOT/mdm/preferences
* _example: http://localhost:8080/org.eclipse.mdm.nucleus/mdm/preferences

## FreeTextSearch
### Configuration
1. **start** ElasticSearch. ElasticSearch can be downloaded at https://www.elastic.co/products/elasticsearch. For testing purpose, it can be simply started by executing bin/run.bat
2. **edit** the configuration (global.properties) to fit your environment. You need an ODS Server which supports Notifications. All fields have to be there, but can be empty. However certain ODS Servers ignore some parameters (e.g. PeakODS ignores pollingIntervall since it pushes notifications).
3. **start up** the application. At the first run it will index the database. This might take a while. After that MDM registers itself as NotificationListener and adapts all changes one-by-one.

### Run on dedicated server
The Indexing is completely independent from the searching. So the Indexer can be freely deployed at any other machine. In the simplest case, the same steps as in Configuration have to be done. The application can then be deployed on any other machine. All components besides the FreeTextIndexer and its dependencies are not user. Those can be left out, if desired.


##Known issues:
If you run into "java.lang.ClassNotFoundException: javax.xml.parsers.ParserConfigurationException not found by org.eclipse.persistence.moxy" this is a bug described in https://bugs.eclipse.org/bugs/show_bug.cgi?id=463169.
This solution is to replace GLASSFISH_HOME/glassfish/modules/org.eclipse.persistence.moxy.jar with this: http://central.maven.org/maven2/org/eclipse/persistence/org.eclipse.persistence.moxy/2.6.1/org.eclipse.persistence.moxy-2.6.1.jar


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

### Source scope
Source scoped preferences are applied at any user but limited to the specified source. The source can be specified in the `Add Preference` or `Edit Preference` dialog.

1.) Ignored Attributes
The ignore attributes preference must have the exact key `ignoredAttributes`. An identifier must not be added. The preference specifies all attributes, which are supposed to be ignored in the detail view. The preference is a simple Json string holding a list of attributes in the form {"<type>.<AttributeName>"}. The placeholders <type> and <AttributeName> have to be replaced by the actual type and name of the attribute which should be ignored, respectively.

**Example:
["Test.Name"]

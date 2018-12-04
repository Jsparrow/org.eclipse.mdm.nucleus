# Release Notes - openMDM(R) Application #

* [mdmbl Eclipse Project Page](https://projects.eclipse.org/projects/technology.mdmbl)
* [mdmbl Eclipse Git Repositories](http://git.eclipse.org/c/?q=mdmbl)
* [mdmbl nightly builds - last stable version](http://download.eclipse.org/mdmbl/nightly_master/?d)

## Milestone 5.1.0M1, 2018/12/07 ##

### Changes ###

 * Support for Status was extended such that it can be attached to a Measurement / TestStep / Test class. Additional entities ProjectDomain Domain and Classification were added to the org.eclipse.mdm.api.default module
 * New system property "org.eclipse.mdm.api.odsadapter.filetransfer.interfaceName" to set a specific network interface name to be used


### Bugzilla Bugs fixed ###

 * [540889](https://bugs.eclipse.org/bugs/show_bug.cgi?id=540889)	- Contribution for 5.1.0M1 bug/feature collection
 * [529729](https://bugs.eclipse.org/bugs/show_bug.cgi?id=529729) -Status and MDMTag cannot be set on a test/testStep
 * [539983](https://bugs.eclipse.org/bugs/show_bug.cgi?id=539983) -	Corba File Server does not work on windows because of a path format problem
 * [539984](https://bugs.eclipse.org/bugs/show_bug.cgi?id=539984) -	Corba File Server always uses the first network interface
 * [541606](https://bugs.eclipse.org/bugs/show_bug.cgi?id=541606) - InstallationGuide improvements


## Release 5.0.0, 2018/11/07 ##

[Release Review and Graduation Review](https://projects.eclipse.org/projects/technology.mdmbl/releases/5.0.0) of the mdmbl Eclipse project succeeded.


## Version 5.0.0M5, 2018/10/30 ##

### Changes ###

 * License switch from Eclipse Public License 1.0 to Eclipse Public License 2.0.

### Bugzilla Bugs fixed ###

 * [540226](https://bugs.eclipse.org/bugs/show_bug.cgi?id=540226)   -  Failed EJB lookup if using catcomps endpoint
 * [539716](https://bugs.eclipse.org/bugs/show_bug.cgi?id=539716)   -  License switch EPL-1.0 to EPL-2.0


## Version 5.0.0M4, 2018/09/26 ##

This code brings along a change with the realm configuration. You can configure your realm now in a standardized way.  In the [readme.md](http://git.eclipse.org/c/mdmbl/org.eclipse.mdm.nucleus.git/tree/README.md) in the org.eclipse.mdm.nucleus project is described how to setup and configure a file realm for local installations. You can also configure your LDAP, AD or others.
See the [glassfish documentation](https://javaee.github.io/glassfish/doc/4.0/security-guide.pdf)

Note: The component "org.eclipse.mdm.realms" is not used any longer.

### Changes ###

* [526883](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526883) - [[REQU-109]](https://openmdm.atlassian.net/browse/REQU-101) - Integrate external distribution "Authentification and Authorization"


### Bugzilla Bugs fixed ###

 * [535381](https://bugs.eclipse.org/bugs/show_bug.cgi?id=535381)   -       Delegated roles and rights with backend connectors

## Version 5.0.0M3, 2018/09/10 ##

In this milestone new REST APIs were added for editing MDM business objects.
The list of all REST APIs see the [readme.md](http://git.eclipse.org/c/mdmbl/org.eclipse.mdm.nucleus.git/tree/README.md) in the org.eclipse.mdm.nucleus project.

### API changes ###

* no

### Changes ###

* [526883](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526883) - [[REQU-101]](https://openmdm.atlassian.net/browse/REQU-101) - The MDM business objects should be editable via openMDM5 RESTful API (CRUD operations)
* [REQU-102](https://openmdm.atlassian.net/browse/REQU-102) - Deploy data model via RESTful API
* [REQU-110](https://openmdm.atlassian.net/browse/REQU-110) - Integrate external distribtion "CRUD Operations for administrative objects"

### Bugzilla Bugs fixed ###

* no

## Version 5.0.0M2, 2018/07/23 ##

### API changes ###

 * no

### Changes ###

* [REQU-108](https://openmdm.atlassian.net/browse/REQU-108) - Bug Fixes and enhancements

### Bugzilla Bugs fixed ###

  * [534866](https://bugs.eclipse.org/bugs/show_bug.cgi?id=534866)	- Results incorrect if attribute search is combined with full text search yielding no results
  * [535606](https://bugs.eclipse.org/bugs/show_bug.cgi?id=535606)	- Attribute search is not case insensitive
  * [536840](https://bugs.eclipse.org/bugs/show_bug.cgi?id=536840)	- Avoid function calls from html template for translation
  * [530512](https://bugs.eclipse.org/bugs/show_bug.cgi?id=530512)	- Missing Information after change tabs
  * [526163](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526163)	- Resolve unmet dependency error cause by ng2-dropdown-multiselect
  * [532425](https://bugs.eclipse.org/bugs/show_bug.cgi?id=532425)	- Update and rename ng2-bootstrap to ngx-bootstrap
  * [536229](https://bugs.eclipse.org/bugs/show_bug.cgi?id=536229)	- Provide a readable error message, if preference service throws an error




## Version 5.0.0M1, 2018/05/18 ##

Note: the system of version numbers has changed!

### API changes ###

 * no

### Changes ###

* [REQU-106](https://openmdm.atlassian.net/browse/REQU-106) - Provide legal documentation, IP Checks, end user content
* [REQU-107](https://openmdm.atlassian.net/browse/REQU-107) - Notification Service Implementation vis OMG Specs

### Bugzilla Bugs fixed ###

* [528033](https://bugs.eclipse.org/bugs/show_bug.cgi?id=528033)	- Web client does not quote entity IDs in filter strings
* [532154](https://bugs.eclipse.org/bugs/show_bug.cgi?id=532154)	- Update libraries for IP check
* [532167](https://bugs.eclipse.org/bugs/show_bug.cgi?id=532167)	- logging
* [532343](https://bugs.eclipse.org/bugs/show_bug.cgi?id=532343)	- Configure Logging
* [532165](https://bugs.eclipse.org/bugs/show_bug.cgi?id=532165)	- Java lib
* [532170](https://bugs.eclipse.org/bugs/show_bug.cgi?id=532170)	- freetext.notificationType
* [534643](https://bugs.eclipse.org/bugs/show_bug.cgi?id=534643)	- search-datepicker does not recognize model changes via input textbox

## Version V0.10, 2018/02/23 ##

### API changes ###

 * [529569](https://bugs.eclipse.org/bugs/show_bug.cgi?id=529569) - [[REQU-103]](https://openmdm.atlassian.net/browse/REQU-103) - Using Shopping Basket (Search results)

#####  An overview of the API changes made :

* Shopping basket:
  - Added methods:
    - String org.eclipse.mdm.api.base.BaseApplicationContext.getAdapterType()
    - Map<Entity, String> org.eclipse.mdm.api.base.BaseEntityManager.getLinks(Collection<Entity>)
  - file extensions:
When downloading the contents of a shopping basket, a file with extension `mdm` is generated. The file extension can be changed by adding a preference with key `shoppingbasket.fileextensions`. For example the used extension can be set to `mdm-xml` by setting the value to `{ "default": "mdm-xml" }`.

### Changes ###

### Bugzilla Bugs fixed ###

* [521880](https://bugs.eclipse.org/bugs/show_bug.cgi?id=521880)	- Component with empty FileLink can not be updated
* [526124](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526124)	- OpenMDM web (nucleus) broken as of 10-16-2017 because of version ranges/tracking versions
* [528261](https://bugs.eclipse.org/bugs/show_bug.cgi?id=528261)	- ODS EntityConfigRepository.find(EntityType) should throw IllegalArgumentException if requested entity type is not found	2018-01-11
* [525848](https://bugs.eclipse.org/bugs/show_bug.cgi?id=525848)	- ODSConverter cannot parse 17 character dates
* [529568](https://bugs.eclipse.org/bugs/show_bug.cgi?id=529568)	- Junit Tests have to run stand alone
* [525980](https://bugs.eclipse.org/bugs/show_bug.cgi?id=525980)	- Remove version range in org.eclipse.mdm.api.base/build.gradle
* [529629](https://bugs.eclipse.org/bugs/show_bug.cgi?id=529629)	- Query group and aggregate function are not working as expected
* [526141](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526141)	- Remove version range in org.eclipse.mdm.api.odsadapter/build.gradle
* [529867](https://bugs.eclipse.org/bugs/show_bug.cgi?id=529867)	- Jenkins builds fail since 12.01.17
* [526147](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526147)	- wrong logic in org.eclipse.mdm.api.default/src/main/java/org/eclipse/mdm/api/dflt/model/EntityFactory
* [529887](https://bugs.eclipse.org/bugs/show_bug.cgi?id=529887)	- FileUpload throws java.net.UnknownHostException
* [526260](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526260)	- Writing enumeration values
* [530511](https://bugs.eclipse.org/bugs/show_bug.cgi?id=530511)	- Missing progress bar
* [526763](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526763)	- Issues in dynamic enumeration handling
* [530775](https://bugs.eclipse.org/bugs/show_bug.cgi?id=530775)	- ODS adapter: getParameters exposes sensitive information
* [527673](https://bugs.eclipse.org/bugs/show_bug.cgi?id=527673)	- ValueType.UNKNOWN.createValue() throws NPE due to ValueType.type not being set
* [530791](https://bugs.eclipse.org/bugs/show_bug.cgi?id=530791)	- UpdateStatement is broken
* [528149](https://bugs.eclipse.org/bugs/show_bug.cgi?id=528149)	- Search tab in web UI should accept attribute values even if no suggestions are present
* [528193](https://bugs.eclipse.org/bugs/show_bug.cgi?id=528193)	- Autocomplete text box in search UI should accept custom values automatically
* [528260](https://bugs.eclipse.org/bugs/show_bug.cgi?id=528260)	- ValueType.UNKNOWN should have Object rather than Void as representation type



## Version V0.9, 2017/11/24 ##

### API changes ###

* [525536](https://bugs.eclipse.org/bugs/show_bug.cgi?id=525536) - [[REQU-50]](https://openmdm.atlassian.net/browse/REQU-50) - Modelling of relations
* [526880](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526880) -  [[REQU-65]](https://openmdm.atlassian.net/browse/REQU-65) Separation of interfaces
* [526882](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526882) -  [[REQU-78]](https://openmdm.atlassian.net/browse/REQU-78) Packaging
* [522277](https://bugs.eclipse.org/bugs/show_bug.cgi?id=522277) -  [[REQU-80]](https://openmdm.atlassian.net/browse/REQU-80) Referencing

#####  An overview of the API changes made :

* service.xml:
 - Property `entityManagerFactoryClass` has to be changed to `org.eclipse.mdm.api.odsadapter.ODSContextFactory` for ODS Datasources.
 - Datasource specific parameters for `NotificationService` and the freetext search parameters are now supplied by service.xml not by global.properties.


* org.eclipse.mdm.api.base:
 - New entry class `BaseApplicationContextFactory` instead of `BaseEntityManagerFactory`.
 - Services can now be retrieved from `BaseApplicationContext` instead of `BaseEntityManager`.
 - `NotificationManagerFactory` was removed; `NotficationManager` was renamed to `NotificationService`.
 - Query package was split into query and search.
 - Creation of Query (`ModelManager#createQuery()`) was moved from `ModelManager` to new `QueryService` class.
 - Moved `ModelManager`, `EntityType`, `Core` and similar to subpackage `adapter`
 - `EntityStore` and `ChildrenStore` are now top-level classes in the subpackage `adapter`
 - Moved `FileService` to subpackage file
 - Added methods:
   - `ContextComponent#getContextRoot()`
   - `ContextSensor#getContextComponent()`
  - Introduced new (protected) method `BaseEntityFactory.createBaseEntity(Class, Core)` for creating instances of classes derived from `BaseEntity` using an already existing `Core` instance. Must be overridden in derived classes to ensure the instances can be created via the (usually package-private) Constructor(Core) of the BaseEntity-derived class. If the constructor of the class passed to `createBaseEntity`  is not accessible, the super class implementation is called to try if the constructor is accessible from there.
  - Introduced new (protected) method `BaseEntityFactory.getCore(BaseEntity)` to extract the Core object from a BaseEntity instance.
  - Modified the interface of BaseEntityManager: the get*Store methods have been defined as non final.
  With that, these methods can be overriden in the OdsAdpter and be used there to access the stores without resorting directly to the core.
  There also comments added in several places to improve the understandibility of the current implementation.


* org.eclipse.mdm.api.default:
 - Introduced `ApplicationContextFactory` and `ApplicationContext` which extend their base counterparts. Should be unnecessary when merging api.base and api.default repositories.


* org.eclipse.mdm.api.odsadapter:
 - Adapted to the changes from `api.base` and `api.default`


* org.eclipse.mdm.nucleus:
 - Adapted to the changes from `api.base` and `api.default`
 - `ConnectorService` manages `ApplicationContexts` instead of `EntityManagers`
 - Datasource specific parameters for `NotificationService` are now supplied by service.xml not by global.properties.


### Changes ###

* [522278](https://bugs.eclipse.org/bugs/show_bug.cgi?id=522278) -  [[REQU-98]](https://openmdm.atlassian.net/browse/REQU-98) Search Parameter Parser
* [526881](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526881) -  [[REQU-75]](https://openmdm.atlassian.net/browse/REQU-75) Specifications

### Bugzilla Bugs fixed ###

* [521880](https://bugs.eclipse.org/bugs/show_bug.cgi?id=521880) -  Component with empty FileLink can not be updatet
* [526124](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526124) -  OpenMDM web (nucleus) broken as of 10-16-2017 because of version ranges/t
* [526763](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526763) -  Issues in dynamic enumeration handling
* [525980](https://bugs.eclipse.org/bugs/show_bug.cgi?id=525980) -  Remove version range in org.eclipse.mdm.api.base/build.gradle
* [526260](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526260) -  Writing enumeration values
* [526141](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526141) -  Remove version range in org.eclipse.mdm.api.odsadapter/build.gradle
* [525848](https://bugs.eclipse.org/bugs/show_bug.cgi?id=525848) -  ODSConverter cannot parse 17 character dates
* [526147](https://bugs.eclipse.org/bugs/show_bug.cgi?id=526147) -  wrong logic in org.eclipse.mdm.api.default/.../EntityFactory

## Version V0.8, 2017/09/08 ##

### API changes ###

  * [REQU-62](https://openmdm.atlassian.net/browse/REQU-62) - Polyvalant variants
  * [REQU-49](https://openmdm.atlassian.net/browse/REQU-49) - Extensibility of Entity Classes
  * [REQU-79](https://openmdm.atlassian.net/browse/REQU-79) - Consistent relationships
  * [REQU-97](https://openmdm.atlassian.net/browse/REQU-97) - Change handling of enumerations

### Changes ###

* [REQU-73](https://openmdm.atlassian.net/browse/REQU-73) - Representation class
* [REQU-74](https://openmdm.atlassian.net/browse/REQU-74) - Empty Interface
* [REQU-77](https://openmdm.atlassian.net/browse/REQU-77) - Name convention
* [REQU-82](https://openmdm.atlassian.net/browse/REQU-82) - Return-Type
* [REQU-91](https://openmdm.atlassian.net/browse/REQU-91) - Context Information

### Bugzilla Bugs fixed ###

* [520291](https://bugs.eclipse.org/bugs/show_bug.cgi?id=520291) - 	Elastic Search :: ElasticSearch answered 400, error while performing some search
* [521011](https://bugs.eclipse.org/bugs/show_bug.cgi?id=521011) - 	EntityFactory.createContextSensor always throws an exception
* [520330](https://bugs.eclipse.org/bugs/show_bug.cgi?id=520330) - 	Improve development setup in Eclipse
* [518063](https://bugs.eclipse.org/bugs/show_bug.cgi?id=518063) - 	Nucleus: config-dir is missing in the build artefact mdm-web.zip
* [518124](https://bugs.eclipse.org/bugs/show_bug.cgi?id=518124) - 	Configure JIPP and Sonar for mdmbl projects
* [518444](https://bugs.eclipse.org/bugs/show_bug.cgi?id=518444) - 	Unify used gradle versions and update to latest stable
* [518825](https://bugs.eclipse.org/bugs/show_bug.cgi?id=518825) - 	Nucleus build: create a separate gradle task for npm build
* [519212](https://bugs.eclipse.org/bugs/show_bug.cgi?id=519212) - 	Enable production mode for client build
* [519993](https://bugs.eclipse.org/bugs/show_bug.cgi?id=519993) - 	Create Gradle composite build
* [519453](https://bugs.eclipse.org/bugs/show_bug.cgi?id=519453) - 	org.eclipse.mdm.openatfx build can't download dependency
* [519995](https://bugs.eclipse.org/bugs/show_bug.cgi?id=519995) - 	Setup Guide and avalon
* [520248](https://bugs.eclipse.org/bugs/show_bug.cgi?id=520248) - 	Build of org.eclipse.mdm.api.odsadapter only works with "gradle clean install"
* [517057](https://bugs.eclipse.org/bugs/show_bug.cgi?id=517057) - 	Add Repository Descriptions



## Version V0.7, 2017/07/21 ##

### API changes ###
  * [REQU-48](https://openmdm.atlassian.net/browse/REQU-48) - Type of Entity-IDs


### Changes ###
* [REQU-67](https://openmdm.atlassian.net/browse/REQU-67) - Final
* [REQU-92](https://openmdm.atlassian.net/browse/REQU-92) - Error Handling

### Bugzilla Bugs fixed ###
* [519448](https://bugs.eclipse.org/bugs/show_bug.cgi?id=519448) - Build of of freetextindexer in org.eclipse.mdm.nucleus fails
* [518062](https://bugs.eclipse.org/bugs/show_bug.cgi?id=518062) - ODSAdapter: Encoding issue when switching to UTF-8
* [518060](https://bugs.eclipse.org/bugs/show_bug.cgi?id=518060) - ODSAdapter - junit tests fail
* [515748](https://bugs.eclipse.org/bugs/show_bug.cgi?id=515748) - Unable to build org.eclipse.mdm.nucleus
* [518335](https://bugs.eclipse.org/bugs/show_bug.cgi?id=518335) - Set executable flag for gradlew in git repo


## Version V0.6, 2017/06/07
### Changes ###

  * [REQU-2](https://openmdm.atlassian.net/browse/REQU-2) - Display a tree view for navigation
  * [REQU-3](https://openmdm.atlassian.net/browse/REQU-3) - Display icons in the tree view
  * [REQU-4](https://openmdm.atlassian.net/browse/REQU-4) - Display different ODS data sources in the tree view
  * [REQU-5](https://openmdm.atlassian.net/browse/REQU-5) - Expand serveral nodes of the tree view simultaneously    
  * [REQU-6](https://openmdm.atlassian.net/browse/REQU-6) - Display a scroll bar in the tree vie
  * [REQU-7](https://openmdm.atlassian.net/browse/REQU-7) - Web Client GUI Adjustment
  * [REQU-9](https://openmdm.atlassian.net/browse/REQU-9) - Display tabs on Detail view
  * [REQU-10](https://openmdm.atlassian.net/browse/REQU-10) - Update Detail View
  * [REQU-12](https://openmdm.atlassian.net/browse/REQU-12) - Select data source for attribute-based search
  * [REQU-13](https://openmdm.atlassian.net/browse/REQU-13) - Definition or selection of a search query
  * [REQU-14](https://openmdm.atlassian.net/browse/REQU-14) - Limit search to a certain result type
  * [REQU-15](https://openmdm.atlassian.net/browse/REQU-15) - Display attributes of the selected data source(s)
  * [REQU-16](https://openmdm.atlassian.net/browse/REQU-16) - Set search attribute values
  * [REQU-18](https://openmdm.atlassian.net/browse/REQU-18) - Select data source for fulltext search
  * [REQU-22](https://openmdm.atlassian.net/browse/REQU-22) - Create and store a view for search results
  * [REQU-23](https://openmdm.atlassian.net/browse/REQU-23) - Select a view to display search results
  * [REQU-24](https://openmdm.atlassian.net/browse/REQU-24) - Filter fulltext search results
  * [REQU-25](https://openmdm.atlassian.net/browse/REQU-25) - Display actions for search results
  * [REQU-27](https://openmdm.atlassian.net/browse/REQU-27) - Select data objects for shoppping basket
  * [REQU-28](https://openmdm.atlassian.net/browse/REQU-28) - Store a shopping basket
  * [REQU-29](https://openmdm.atlassian.net/browse/REQU-29) - Select a shopping basket
  * [REQU-30](https://openmdm.atlassian.net/browse/REQU-30) - Export shopping basket
  * [REQU-31](https://openmdm.atlassian.net/browse/REQU-31) - Load an exported shopping basket
  * [REQU-32](https://openmdm.atlassian.net/browse/REQU-32) - Display actions for shopping basket
  * [REQU-85](https://openmdm.atlassian.net/browse/REQU-85) - Seach type date
  * [REQU-86](https://openmdm.atlassian.net/browse/REQU-86) - Search across multiple data sources
  * [REQU-95](https://openmdm.atlassian.net/browse/REQU-95) - Backend configuration

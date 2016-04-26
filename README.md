<!--
Copyright (c) 2016 Gigatronik Ingolstadt GmbH
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
-->

<h3>build</h3>

- <b>minimum requirements</b>
    - JDK 1.8.0_45
    - Gradle 2.0
  <br><br>
- <b>build application (war)</b>
    <br>
    The command '<b>gradlew installBackend</b>' at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application</i> installs all projects 
    inclusive org.eclipse.mdm.api.base and org.eclipse.mdm.api.odsadapter if this
    projects are checked out at the same root folder as org.eclipse.mdm.nucleus
    for example:
    - root
     - org eclipse.mdm.api.base
     - org.eclispe.mdm.api.odsadapter
     - org.eclipse.mdm.nuclues

    The war file will be generated at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application/build/libs/org.eclipse.mdm.application-1.0.0.war</i>
    <br><br>
    Use the command '<b>gradlew cleanBackend</b>' to delete the backend war at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application/build/libs</i>
    and to cleanup all build folders at the dependend projects</i>
    <br><br><br>
    
- <b>build angular.js frontend (zip)</b>
    <br>
    The command '<b>gradlew installFrontend</b>' at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application</i> collects all frontend 
    parts for each project at <i>src/main/webapp</i> and generates a ZIP file with the complete
    frontend.
    <br>
    The frontend zip file will be generated at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application/build/distributions/org.eclipse.mdm.application-1.0.0_frontend.zip</i><br>
    Unzip this archive at the doc root folder at your web server and edit the file ./app/properties.ts to set the variables host, port and prefix for your deployed 
    backend application.
    <br>
    (doc root path example: <i>GLASSFISH_ROOPT/glassfish/domains/domain1/docroot</i>)
    <br><br>
    Use the command '<b>gradlew cleanFrontend</b>' to delete the fontend zip at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application/build/distributions</i>
    and to cleanup the tmp directory 'frontend' at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application/build/tmp</i>
    <br><br><br>
    
- <b>build configurations (zip)</b>
    <br>
    The command '<b>gradlew installConfiguration</b>' at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application</i> collects all component configurations 
    parts for each project at <i>src/main/configuration</i> and generates a ZIP file with the complete component configurations
    <br>
    The configuration zip file will be generated at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application/build/distributions/org.eclipse.mdm.application-1.0.0_configuration.zip</i><br>
    Unzip the archive at the configuration directory and edit the configurations files (for example: service.xml at org.eclipse.mdm.connector)
    <br>
    (configuration path example: <i>GLASSFISH_ROOPT/glassfish/domains/domain1/config</i>)
    <br><br>
    Use the command '<b>gradlew cleanConfiguration</b>' to delete the fontend zip at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application/build/distributions</i>
    and to cleanup the tmp directory 'configration' at <i>./org.eclipse.mdm.nucleus/org.eclipse.mdm.application/build/tmp</i>
    <br><br><br>
    
<h3>available projects</h3>

- <b>org.eclipse.mdm.application</b>
    <br>
    includes application server configurations and gradle build scripts for the complete
    backend (war), complete frontend (zip) and configurations (zip) (see: 1.build)
    frontend at: src/main/webapp
    <br><br>
    available rest urls: none
    <br><br>
    
- <b>org.eclipse.mdm.connector</b>
    <br>
    The mdm connector singelton bean manages the connections for logged on users (Principal) at 
    the the application server. A application server specific login realm module for the MDM application has to
    be installed and configured at the used application server. This module has to call the 'connect' (login phase 1) and 
    'registerConnections' (login phase 2) methods of this service to create and register MDM connections for a user (Principal).
    <br><br>
    available rest urls: none
    <br><br> 
    <b>hint:</b> 
    - configure available MDM data sources at configuration file <i>./org.eclipse.mdm.connector/src/main/configuration/service.xml</i>
    <br><br>
    
- <b>org.eclipse.mdm.businesstyperegistry</b>
    <br>
    The business type registry singleton bean provides actions for registered MDM business object typs.
    Each action has to be a bean which implements the provided interface ActionBeanLI.
    (see: org.eclipse.mdm.action.delete)
    <br><br>
    available rest urls:
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/tests/actions</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/tests/actions</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of registered test actions</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/teststeps/actions</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/teststeps/actions</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of registered teststep actions</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/measurements/actions</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/measurements/actions</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of registered measurement actions</td></tr>
        </table>
    <br><br>
    
- <b>org.eclipse.mdm.i18n</b>
    <br>
    This i18n stateless bean provides localizations for MDM business object type and their attributes.
    
    available rest urls:
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/tests/localizations</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/tests/localizations</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of localized test attributes</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/teststeps/localizations</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/teststeps/localizations</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of localized teststep attributes</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/measurements/localizations</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/measurements/localizations</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of localized measurement attributes</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/channelgroups/localizations</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/channelgroups/localizations</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of localized channel group attributes</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/channels/localizations</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/channels/localizations</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of localized channel attributes</td></tr>
        </table>
        <br><br>
- <b>org.eclipse.mdm.navigator</b>
    <br>
    This navigator stateless bean provides environments and business object children. 
    
    available rest urls:
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of Environments and their attributes (Environment name = SOURCE_NAME)</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/tests</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/tests</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of Tests and their attributes</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/teststeps?test.id=PARENT_TEST_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/teststeps?test.id=123</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of TestSteps and their attributes</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/measurements?teststep.id=PARENT_TESTSTEP_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/measurements?teststep.id=123</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of Measurements and their attributes</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/channelgroups?measurement.id=PARENT_MEASUREMENT_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/channelgroups?measurement.id=123</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of ChannelGroups and their attributes</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/channels?channelgroup.id=PARENT_CHANNELGROUP_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/channels?channelgroup.id=123</td></tr>
        <tr><td>type:</td><td>GET</td></tr>
        <tr><td>return:</td><td>a JSON string with a list of Channels and their attributes</td></tr>
        </table>
        <br><br>
- <b>org.eclipse.mdm.action.delete</b>
    <br>
    The delete action stateless bean is a template action bean implementation yet. This action bean implements the
    ActionBeanLI interface of org.eclipse.mdm.businesstyperegistry. This delete action is registered for the MDM business
    object types Test, TestStep and Measurement (see implemented method: getSupportedEntityTypes() at DeleteActionBean)
    
    hint: the delete method of DeleteActionBean is not implemented yet and does nothing
    
    available rest urls:
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/tests/delete?test.id=TEST_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/tests/delete?test.id=123</td></tr>
        <tr><td>type:</td><td>DELETE</td></tr>
        <tr><td>return:</td><td>an empty JSON string if the delete operation was successful, or the test.id if the operation fails</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/teststeps/delete?teststep.id=TESTSTEP_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/teststeps/delete?teststep.id=123</td></tr>
        <tr><td>type:</td><td>DELETE</td></tr>
        <tr><td>return</td><td>an empty JSON string if the delete operation was successful, or the teststep.id if the operation fails</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/measurements/delete?measurement.id=MEASUREMENT_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/measurements/delete?measurement.id=123</td></tr>
        <tr><td>type:</td><td>DELETE</td></tr>
        <tr><td>return:</td><td>an empty JSON string if the delete operation was successful, or the measurement.id if the operation fails</td></tr>
        </table>
                <br><br>
- <b>org.eclipse.mdm.contextprovider</b>
    <br>
    The contextprovider bean provides the ordered and measured context for the MDM business objects TestStep and Measurement. 
    The provided context contains always the orderd and measured context parts if available. 
    
    available rest urls:
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/teststeps/contexts?teststep.id=TESTSTEP_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/teststeps/context?teststep.id=123</td></tr>
        <tr><td>type:</td><td>GET/td></tr>
        <tr><td>return:</td><td>a JSON string with a the context data of UnitUnderTest, TestSequence and TestEquipment (ordered and measured)</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/teststeps/contexts/unitundertest?teststep.id=TESTSTEP_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/teststeps/context/unitundertest?teststep.id=123</td></tr>
        <tr><td>type:</td><td>GET/td></tr>
        <tr><td>return:</td><td>a JSON string with a the context data of UnitUnderTest (ordered and measured)</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/teststeps/contexts/testsequence?teststep.id=TESTSTEP_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/teststeps/context/testsequence?teststep.id=123</td></tr>
        <tr><td>type:</td><td>GET/td></tr>
        <tr><td>return:</td><td>a JSON string with a the context data of TestSequence (ordered and measured)</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/teststeps/contexts/testequipment?teststep.id=TESTSTEP_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/teststeps/context/testequipment?teststep.id=123</td></tr>
        <tr><td>type:</td><td>GET/td></tr>
        <tr><td>return:</td><td>a JSON string with a the context data of TestEquipment (ordered and measured)</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/measurements/contexts?measurement.id=MEASUREMENT_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/measurements/context?measurement.id=123</td></tr>
        <tr><td>type:</td><td>GET/td></tr>
        <tr><td>return:</td><td>a JSON string with a the context data of UnitUnderTest, TestSequence and TestEquipment (ordered and measured)</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/measurements/contexts/unitundertest?measurement.id=MEASUREMENT_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/measurements/context/unitundertest?measurement.id=123</td></tr>
        <tr><td>type:</td><td>GET/td></tr>
        <tr><td>return:</td><td>a JSON string with a the context data of UnitUnderTest (ordered and measured)</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/measurements/contexts/testsequence?measurement.id=MEASUREMENT_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/measurements/context/testsequence?measurement.id=123</td></tr>
        <tr><td>type:</td><td>GET/td></tr>
        <tr><td>return:</td><td>a JSON string with a the context data of TestSequence (ordered and measured)</td></tr>
        </table>
        <br>
        <table>
        <tr><td>url:</td><td>http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/measurements/contexts/testequipment?measurement.id=MEASUREMENT_ID</td></tr>
        <tr><td>example:</td><td>http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/measurements/context/testequipment?measurement.id=123</td></tr>
        <tr><td>type:</td><td>GET/td></tr>
        <tr><td>return:</td><td>a JSON string with a the context data of TestEquipment (ordered and measured)</td></tr>
        </table>
        <br>
        
        
Copyright (c) 2016 Gigatronik Ingolstadt GmbH
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html



1. build

- minimum requirements
    - JDK 1.8.0_45
    - Gradle 2.0

- build application (war)

    The command 'gradlew install' at org.eclipse.mdm.application installs all projects 
    inclusive org.eclipse.mdm.api.base and org.eclipse.mdm.api.odsadapter if this
    projects are checked out at the same root folder as org.eclipse.mdm.nucleus
    for example:
        -   root
            - org eclipse.mdm.api.base
            - org.eclispe.mdm.api.odsadapter
            - org.eclipse.mdm.nuclues
    The war file will be generated at ./org.eclipse.mdm.application/build/libs/org.eclipse.mdm.application-1.0.0.war

- build angular.js frontend (zip)

    The command 'gradlew installFrontend' at org.eclipse.mdm.appliation collects all frontend 
    parts for each project at 'src/main/webapp' and generates a ZIP file with the complete
    frontend.
    
    The frontend zip file will be generated at ./org.eclipse.mdm.application/build/distributions/org.eclipse.mdm.application-1.0.0.zip
    Please note the readme file at the generated zip archive
    
    Use the command 'gradlew cleanFrontend' to delete the fontend zip at ./org.eclipse.mdm.application/build/distributions 
    and to cleanup the tmp directory 'frontend' at ./org.eclipse.mdm.application/build/tmp


2. available projects 

-org.eclipse.mdm.application

    includes application server configurations and gradle build scripts for the complete
    backend (war) and the complete frontend (zip) (see: 1.build)
    frontend at: src/main/webapp
    
    provided rest urls: none
    
    
-org.eclipse.mdm.connector

    The mdm connector singelton bean manages the connections for logged on users (Principal) at 
    the the application server. A application server specific login realm module for the MDM application has to
    be installed and configured at the used application server. This module has to call the 'connect' method of 
    this service to create and register MDM connections for a user (Principal).
    
    provided rest urls: none
    
    
-org.eclipse.mdm.businesstyperegistry

    The business type registry singleton bean provides actions for registered MDM business object typs.
    Each action has to be a bean which implements the provided interface ActionBeanLI.
    (see: org.eclipse.mdm.action.delete)

    provided rest urls:
    
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/MDM_BO_TYPE/actions
        example:    http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/teststeps/actions
        type:       GET
        return:     a JSON string with a list of actions
    
    
-org.eclipse.mdm.i18n

    This i18n stateless bean provides localizations for MDM business object type and their attributes.
    
    provided rest urls:
    
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/MDM_BO_TYPE/localizations
        example:    http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/teststeps/localizations
        type:       GET
        return:     a JSON string with a list of localizes attributes
       
-org.eclipse.mdm.navigator

    This navigator stateless bean provides environments and business object children. 
    
    provided rest urls:
    
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/
        example:    http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments
        type:       GET
        return:     a JSON string with a list of Environments and their attributes (Environment name = SOURCE_NAME)
              
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/tests
        example:    http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/tests
        type:       GET
        return:     a JSON string with a list of Tests and their attributes
           
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/teststeps?test.id=PARENT_TEST_ID
        example:    http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/teststeps?test.id=123
        type:       GET
        return      a JSON string with a list of TestSteps and their attributes
          
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/measurements?teststep.id=PARENT_TESTSTEP_ID
        example     http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/measurements?teststep.id=123
        type:       GET
        returns     a JSON string with a list of Measurements and their attributes
            
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/channelgroups?measurement.id=PARENT_MEASUREMENT_ID
        example:    http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/channelgroups?measurement.id=123
        type:       GET
        return:     a JSON string with a list of ChannelGroups and their attributes
           
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/channels?channelgroup.id=PARENT_CHANNELGROUP_ID
        example:    http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/channels?channelgroup.id=123
        type:       GET
        return:     a JSON string with a list of Channels and their attributes
          
-org.eclipse.mdm.action.delete

    The delete action stateless bean is a template action bean implementation yet. This action bean implements the
    ActionBeanLI interface of org.eclipse.mdm.businesstyperegistry. This delete action is registered for the MDM business
    object types Test, TestStep and Measurement (see implemented method: getSupportedEntityTypes() at DeleteActionBean)
    
    hint: the delete method of DeleteActionBean is not implemented yet and does nothing
    
    provided rest urls:
    
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/tests/delete?test.id=TEST_ID
        example:    http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/tests/delete?test.id=123
        type:       DELETE
        return:     an empty JSON string if the delete operation was successful, or the test.id if the operation fails
          
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/teststeps/delete?teststep.id=TESTSTEP_ID
        example:    http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/teststeps/delete?teststep.id=123
        type:       DELETE
        return      an empty JSON string if the delete operation was successful, or the teststep.id if the operation fails
    
        url:        http://SERVER:PORT/APPLICATION_ROOT/mdm/environments/SOURCE_NAME/measurements/delete?measurement.id=MEASUREMENT_ID
        example:    http://localhost:8080/org.eclipse.mdm.application-1.0.0/mdm/environments/MDMSource1/measurements/delete?measurement.id=123
        type:       DELETE
        return:     an empty JSON string if the delete operation was successful, or the measurement.id if the operation fails


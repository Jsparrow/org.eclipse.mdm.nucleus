/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


export const MockEnvNodes = {
  'type': 'Environment',
  'data': [
    {
      'name': 'Test Environment',
      'id': 'id1',
      'type': 'Environment',
      'sourceType': 'Environment',
      'sourceName': 'Test Environment',
      'attributes': [
        {
          'name': 'Timezone',
          'value': 'GMT',
          'unit': '',
          'dataType': 'STRING'
        }
      ]
    }
  ]
};

export const MockTestNodes = {
    'type': 'Test',
    'data': [
      {
        'name': 'Test 1',
        'id': 'id10',
        'type': 'Test',
        'sourceType': 'Test',
        'sourceName': 'Test Environment',
        'attributes': [
          {
            'name': 'DateClosed',
            'value': '',
            'unit': '',
            'dataType': 'DATE'
          }
        ]
      },
      {
        'name': 'Test 2',
        'id': 'id20',
        'type': 'Test',
        'sourceType': 'Test',
        'sourceName': 'Test Environment',
        'attributes': [
          {
            'name': 'DateClosed',
            'value': '',
            'unit': '',
            'dataType': 'DATE'
          }
        ]
      },
      {
        'name': 'Test 3',
        'id': 'id30',
        'type': 'Test',
        'sourceType': 'Test',
        'sourceName': 'Test Environment',
        'attributes': [
          {
            'name': 'DateClosed',
            'value': '',
            'unit': '',
            'dataType': 'DATE'
          }
        ]
      }
    ]
};

export const MockNodeProvider = {
  'name' : 'Channels',
  'type' : 'Environment',
  'children' : {
    'type' : 'Test',
    'attribute' : 'Name',
    'query' : '/tests',
    'children' : {
      'type' : 'Channel',
      'attribute' : 'Name',
      'query' : '/channels?filter=Test.Id eq \'{Test.Id}\''
    }
  }
};

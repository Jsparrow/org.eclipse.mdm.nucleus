/*******************************************************************************
*  Copyright (c) 2017 Peak Solution GmbH                                       *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Matthias Koller, Johannes Stamm - initial implementation                    *
*******************************************************************************/

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
      'query' : '/channels?filter=Test.Id eq {Test.Id}'
    }
  }
};

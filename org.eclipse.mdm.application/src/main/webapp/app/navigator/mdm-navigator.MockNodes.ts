export const MockEnvNodes = {
  'type': 'Environment',
  'data': [
    {
      'name': 'Test Environment',
      'id': 1,
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
        'id': 10,
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
        'id': 20,
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
        'id': 30,
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

// *******************************************************************************
//   * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
//   * All rights reserved. This program and the accompanying materials
//   * are made available under the terms of the Eclipse Public License v1.0
//   * which accompanies this distribution, and is available at
//   * http://www.eclipse.org/legal/epl-v10.html
//   *
//   * Contributors:
//   * Dennis Schroeder - initial implementation
//   *******************************************************************************
import {Component, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';

import {Node} from './node';
import {NodeService} from './node.service';

@Component({
  selector: 'mdm-search',
  templateUrl : 'templates/mdm-search.component.html'
  providers: []
})
export class MDMSearchComponent implements OnInit {
}

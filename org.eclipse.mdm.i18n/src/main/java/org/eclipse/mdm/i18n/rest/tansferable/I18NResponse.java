/*******************************************************************************
  * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Sebastian Dirsch - initial implementation
  *******************************************************************************/

package org.eclipse.mdm.i18n.rest.tansferable;

import java.util.ArrayList;
import java.util.List;

/**
 * Transferable I18NResponse (Container for {@link LocalizedAttribute}s)
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class I18NResponse {

	
	
	/** transferable data content */
	public final List<LocalizedAttribute> data;
	
	
	
	/**
	 * Constructor
	 * @param localizedAttributes list of {@link LocalizedAttribute}s to transfer
	 */
	public I18NResponse(List<LocalizedAttribute> localizedAttributes) {
		this.data = localizedAttributes;
	}
	
	
	
	/**
	 * Constructor
	 */
	public I18NResponse() {
		this.data = new ArrayList<>();
	}
}

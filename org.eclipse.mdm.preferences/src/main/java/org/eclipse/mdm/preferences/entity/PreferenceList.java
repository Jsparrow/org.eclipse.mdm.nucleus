/*******************************************************************************
  * Copyright (c) 2017 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Johannes Stamm - initial implementation
  *******************************************************************************/
package org.eclipse.mdm.preferences.entity;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.MoreObjects;

/**
 * 
 * @author Johannes Stamm, Peak Solution GmbH
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PreferenceList {

	private List<PreferenceMessage> preferences;

	public PreferenceList(List<PreferenceMessage> preferenceList) {
		preferences = preferenceList;
	}

	public List<PreferenceMessage> getPreferences() {
		return preferences;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final PreferenceList other = (PreferenceList) obj;

		return Objects.equals(this.preferences, other.preferences);
	}

	@Override
	public int hashCode() {
		return Objects.hash(preferences);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(PreferenceList.class).add("preferences", preferences).toString();
	}
}

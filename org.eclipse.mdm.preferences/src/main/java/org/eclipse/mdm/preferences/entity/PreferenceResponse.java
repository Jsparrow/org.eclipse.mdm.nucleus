package org.eclipse.mdm.preferences.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PreferenceResponse {

	private List<Preference> preferences;
	
	public PreferenceResponse(List<Preference> preferenceList) {
		preferences = preferenceList;
	}

	public List<Preference> getPreferences() {
		return preferences;
	}
}

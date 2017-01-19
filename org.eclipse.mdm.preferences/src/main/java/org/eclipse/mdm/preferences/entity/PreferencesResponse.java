package org.eclipse.mdm.preferences.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PreferencesResponse {

	private List<PreferenceResponse> preferences;
	
	public PreferencesResponse(List<PreferenceResponse> preferenceList) {
		preferences = preferenceList;
	}

	public List<PreferenceResponse> getPreferences() {
		return preferences;
	}
}

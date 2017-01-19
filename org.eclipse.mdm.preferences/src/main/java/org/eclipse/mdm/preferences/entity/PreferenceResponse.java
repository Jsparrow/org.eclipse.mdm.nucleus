package org.eclipse.mdm.preferences.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="preference")
public class PreferenceResponse {

	public static enum Scope {
		System,
		Source,
		User
	}
	
	private Scope scope;
	private String source;
	private String user;
	private String key;
	private String value;
	private Long id;
	
	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
}

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

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.common.base.MoreObjects;

/**
 * 
 * @author Johannes Stamm, Peak Solution GmbH
 *
 */
@XmlRootElement(name="preference")
public class PreferenceMessage {

	public enum Scope {
		SYSTEM,
		SOURCE,
		USER
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
	
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final PreferenceMessage other = (PreferenceMessage) obj;

		return new EqualsBuilder()
				.append(this.id, other.id)
				.append(this.scope, other.scope)
				.append(this.source, other.source)
				.append(this.user, other.user)
				.append(this.key, other.key)
				.append(this.value, other.value)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, scope, source, user, key, value);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(PreferenceMessage.class)
				.add("id", id)
				.add("scope", scope)
				.add("source", source)
				.add("user", user)
				.add("key", key)
				.add("value", value)
				.toString();
	}
}

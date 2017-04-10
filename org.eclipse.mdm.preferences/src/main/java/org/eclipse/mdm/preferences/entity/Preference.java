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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.common.base.MoreObjects;

/**
 * 
 * @author Johannes Stamm, Peak Solution GmbH
 *
 */
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"source", "username", "keyCol"})})
public class Preference {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String source;
	
	@Column(name="username")
	private String user;
	
	@Column(name="keyCol")
	private String key;
	
	@Column(name="valueCol", columnDefinition="CLOB NOT NULL")
	@Lob
	private String value;

	public Preference()
	{
		super();
	}

	public Preference(String source, String user, String key, String value)
	{
		super();
		this.source = source;
		this.user = user;
		this.key = key;
		this.value = value;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final Preference other = (Preference) obj;
		return new EqualsBuilder()
				.append(this.id, other.id)
				.append(this.source, other.source)
				.append(this.user, other.user)
				.append(this.key, other.key)
				.append(this.value, other.value)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, source, user, key, value);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Preference.class)
				.add("id", id)
				.add("source", source)
				.add("user", user)
				.add("key", key)
				.add("value", value)
				.toString();
	}
	
}

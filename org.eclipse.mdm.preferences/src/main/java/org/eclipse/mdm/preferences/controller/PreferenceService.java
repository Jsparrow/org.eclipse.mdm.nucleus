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
package org.eclipse.mdm.preferences.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.eclipse.mdm.preferences.entity.Preference;
import org.eclipse.mdm.preferences.entity.PreferenceMessage;
import org.eclipse.mdm.preferences.entity.PreferenceMessage.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;


/**
 * 
 * @author Johannes Stamm, Peak Solution GmbH
 *
 */
@Stateless
public class PreferenceService
{
	private static final Logger LOG = LoggerFactory.getLogger(PreferenceService.class); 
	
	@PersistenceContext(unitName="openMDM")
	private EntityManager em;

	@Resource
	private SessionContext sessionContext;
	
	public PreferenceService() {
		// public no-arg constructor
	}
	
	/**
	 * Constructor for unit tests
	 * @param em EntityManager to use
	 * @param sessionContext sessionManager to use
	 */
	PreferenceService(EntityManager em, SessionContext sessionContext) {
		this.em = em;
		this.sessionContext = sessionContext;
	}
	
	public List<PreferenceMessage> getPreferences(String scope, String key) {

		TypedQuery<Preference> query;
		if (scope == null || scope.trim().isEmpty()) {
			query = em.createQuery("select p from Preference p where (p.user is null or p.user = :user) and LOWER(p.key) like :key", Preference.class)
				.setParameter("user", sessionContext.getCallerPrincipal().getName())
				.setParameter("key", key.toLowerCase() + "%");
		} else {
			query = em.createQuery(buildQuery(scope, key), Preference.class);
			
			if (key != null && !key.trim().isEmpty()) {
				query.setParameter("key", key.toLowerCase() + "%");
			}
		}
		
		return query.getResultList()
				.stream()
				.map(this::convert)
				.collect(Collectors.toList());
	}
	
	public List<PreferenceMessage> getPreferencesBySource(String source, String key) {
		TypedQuery<Preference> query;
		
		if (key == null || key.trim().isEmpty()) {
			query = em.createQuery("select p from Preference p where p.source = :source", Preference.class)
				.setParameter("source", Strings.emptyToNull(source));
				
		} else {
			query = em.createQuery("select p from Preference p where p.source = :source and p.key = :key", Preference.class)
				.setParameter("source", Strings.emptyToNull(source))
				.setParameter("key", Strings.emptyToNull(key));
		}
		return query.getResultList()
				.stream()
				.map(this::convert)
				.collect(Collectors.toList());
	}
	
	public PreferenceMessage save(PreferenceMessage preference) {
		Principal principal = sessionContext.getCallerPrincipal();
		List<Preference> existingPrefs = getExistingPreference(preference, principal);
		
		Preference pe;
		
		if (existingPrefs.isEmpty()) {
			pe = convert(preference);
		} else {
			if (existingPrefs.size() > 1) {
				LOG.warn("Found multiple entries for preference with scope={}, source={}, user={}, key={} where one entry was expected!", 
						preference.getScope(), preference.getSource(), preference.getUser(), preference.getKey() );
			}
			pe = existingPrefs.get(0);
			pe.setValue(preference.getValue());
		}
		em.persist(pe);
		em.flush();
		return convert(pe);
	}

	public PreferenceMessage deletePreference(Long id){
		Preference preference = em.find(Preference.class, id);
		em.remove(preference);
		em.flush();
		return convert(preference);
	}

	
	private PreferenceMessage convert(Preference pe)
	{
		PreferenceMessage p = new PreferenceMessage();
		p.setKey(pe.getKey());
		p.setValue(pe.getValue());
		p.setId(pe.getId());
		
		if (pe.getUser() == null && pe.getSource() == null) {
			p.setSource(pe.getSource());
			p.setScope(Scope.SYSTEM);
		} else if (pe.getUser() != null) {
			p.setUser(pe.getUser());
			p.setScope(Scope.USER);
		} else if (pe.getSource() != null) {
			p.setSource(pe.getSource());
			p.setScope(Scope.SOURCE);
		} 
		
		return p;
	}
	
	private Preference convert(PreferenceMessage p)
	{
		Principal principal = sessionContext.getCallerPrincipal();

		Preference pe = new Preference();
		pe.setKey(p.getKey());
		pe.setValue(p.getValue());
		pe.setId(p.getId());
		
		switch (p.getScope()) {
			case SOURCE:
				pe.setSource(p.getSource());
				break;
			case USER:
				pe.setUser(principal.getName());
				break;
			case SYSTEM:
			default:
				break;
		}
		return pe;
	}
	
	private String buildQuery(String scope, String key){
		String query = "select p from Preference p";
		String whereOrAnd = " where";
		if(scope != null && scope.trim().length() > 0) {
			switch (scope.toLowerCase()) {
				case "system":
					query = query.concat(whereOrAnd).concat(" p.source is null and p.user is null");
					break;
				case "source":
					query = query.concat(whereOrAnd).concat(" p.source is not null and p.user is null");
					break;
				case "user":
					query = query.concat(whereOrAnd).concat(" p.source is null and p.user is not null");
					break;
				default:
			}
			whereOrAnd = " and";
		}
		if(key != null && key.trim().length() > 0) {
			query = query.concat(whereOrAnd).concat(" LOWER(p.key) LIKE :key");
		}
		return query;
	}

	private List<Preference> getExistingPreference(PreferenceMessage preference, Principal principal) {
		Preconditions.checkNotNull(preference.getScope(), "Scope cannot be null!");
		
		if (preference.getId() == null) {
			switch (preference.getScope())
			{
			case USER:
				return em.createQuery(
						"select p from Preference p where p.source is null and p.user = :user and p.key = :key", Preference.class)
						.setParameter("user", Strings.emptyToNull(principal.getName()))
						.setParameter("key", preference.getKey())
						.getResultList();			
			case SOURCE:
				return em.createQuery(
						"select p from Preference p where p.source = :source and p.user is null and p.key = :key", Preference.class)
						.setParameter("source", Strings.emptyToNull(preference.getSource()))
						.setParameter("key", preference.getKey())
						.getResultList();			
			case SYSTEM:
				return em.createQuery(
						"select p from Preference p where p.source is null and p.user is null and p.key = :key", Preference.class)
						.setParameter("key", preference.getKey())
						.getResultList();	
			default:
				throw new IllegalArgumentException("Unknown Scope!");
			}
		} else {
			return Arrays.asList(em.find(Preference.class, preference.getId()));
		}
	}
}

package org.eclipse.mdm.preferences.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eclipse.mdm.preferences.entity.PreferenceResponse;
import org.eclipse.mdm.preferences.entity.PreferenceResponse.Scope;
import org.eclipse.mdm.preferences.entity.Preference;

import com.google.common.base.Strings;

@Stateless
public class PreferenceService
{
	@PersistenceContext(unitName="preferences")
	private EntityManager em;

	@Resource
	private SessionContext sessionContext;
	
	public List<PreferenceResponse> getPreferences(String scope, String key) {
		String query = queryBuilder(scope, key);
		return loadQuery(query, key);	
	}
	
	public String queryBuilder(String scope, String key){
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
			query = query.concat(whereOrAnd).concat(" UPPER(p.key) LIKE :key");
		}
		return query;
	}

	public List<PreferenceResponse> loadQuery(String query, String key) {
		if (key != null && key.trim().length() > 0) {
			return em.createQuery(query, Preference.class)
				.setParameter("key", key.toUpperCase() + "%")
				.getResultList()
				.stream()
				.map(p -> convert(p))
				.collect(Collectors.toList());
		}
		return em.createQuery(query, Preference.class)
				.getResultList()
				.stream()
				.map(p -> convert(p))
				.collect(Collectors.toList());
	}

	public PreferenceResponse deletePreference(Long id){
		Preference preference = em.createQuery(
				"select p from Preference p where p.id = :id", Preference.class)
				.setParameter("id", id)
				.getSingleResult();
		em.remove(preference);
		em.flush();
		return convert(preference);
	}

	
	public PreferenceResponse convert(Preference pe)
	{
		PreferenceResponse p = new PreferenceResponse();
		p.setKey(pe.getKey());
		p.setValue(pe.getValue());
		p.setId(pe.getId());
		
		if (pe.getUser() == null && pe.getSource() == null) {
			p.setSource(pe.getSource());
			p.setScope(Scope.Source);
		} else if (pe.getUser() != null) {
			p.setUser(pe.getUser());
			p.setScope(Scope.User);
		} else if (pe.getSource() != null) {
			p.setSource(pe.getSource());
			p.setScope(Scope.Source);
		} 
		
		return p;
	}
	
	public Preference convert(PreferenceResponse p)
	{
		Principal principal = sessionContext.getCallerPrincipal();

		Preference pe = new Preference();
		pe.setKey(p.getKey());
		pe.setValue(p.getValue());
		pe.setId(p.getId());
		
		switch (p.getScope()) {
			case Source:
				pe.setSource(p.getSource());
				break;
			case User:
				pe.setUser(principal.getName());
				break;
			case System:
			default:
				break;
		}
		return pe;
	}
	
	public PreferenceResponse save(PreferenceResponse preference) {
		Principal principal = sessionContext.getCallerPrincipal();
		List<Preference> existingPrefs = getExistingPreference(preference, principal);
		
		if (existingPrefs.isEmpty()) {
			Preference pe = convert(preference);
			em.persist(pe);
			em.flush();
			return convert(pe);
		} else {
			Preference existingPref = existingPrefs.get(0);
			existingPref.setValue(preference.getValue());
			em.persist(existingPref);
			return convert(existingPref);
		}
	}

	private List<Preference> getExistingPreference(PreferenceResponse preference, Principal principal) {
		
		if ( preference.getId() == null) {
			if (preference.getScope() == Scope.User) {
				return em.createQuery(
						"select p from Preference p where p.source is null and p.user = :user and p.key = :key", Preference.class)
						.setParameter("user", Strings.emptyToNull(principal.getName()))
						.setParameter("key", preference.getKey())
						.getResultList();			
			} else if (preference.getScope() == Scope.Source) {
				return em.createQuery(
						"select p from Preference p where p.source = :source and p.user is null and p.key = :key", Preference.class)
						.setParameter("source", Strings.emptyToNull(preference.getSource()))
						.setParameter("key", preference.getKey())
						.getResultList();			
			} else if (preference.getScope() == Scope.System) {
				return em.createQuery(
						"select p from Preference p where p.source is null and p.user is null and p.key = :key", Preference.class)
						.setParameter("key", preference.getKey())
						.getResultList();			
			} else {
				throw new RuntimeException("Unknown Scope!");
			}
		} else {
			return em.createQuery(
					"select p from Preference p where p.id = :id", Preference.class)
					.setParameter("id", preference.getId())
					.getResultList();
		}
	}
}

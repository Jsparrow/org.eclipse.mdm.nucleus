package org.eclipse.mdm.preferences.controller;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eclipse.mdm.preferences.entity.Preference;

@Stateless
public class PreferenceService
{
  @PersistenceContext(unitName="preferences")
  private EntityManager em;

  public Preference load(String key) {
	  return em.createQuery("select p from Preference p where key = :key", Preference.class)
		        .setParameter("key", key)
		        .getSingleResult();
  }
  
  public List<Preference> loadAll() {
	  return em.createQuery("select p from Preference p", Preference.class)
		        .getResultList();
  }

  public void save(Preference preference) {
	  em.persist(preference);
	  em.flush();
  }
}

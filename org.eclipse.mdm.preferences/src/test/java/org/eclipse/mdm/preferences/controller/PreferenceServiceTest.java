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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.List;

import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.assertj.core.groups.Tuple;
import org.eclipse.mdm.preferences.entity.Preference;
import org.eclipse.mdm.preferences.entity.PreferenceMessage;
import org.eclipse.mdm.preferences.entity.PreferenceMessage.Scope;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class PreferenceServiceTest
{
	private EntityManagerFactory factory;
	private EntityManager em;
	private SessionContext sessionContext = mock(SessionContext.class);
	private PreferenceService service;
	
	@Before
    public void init() {
		factory = Persistence.createEntityManagerFactory("preferenceTest", 
				ImmutableMap.of(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence-test.xml"));
		
		em = factory.createEntityManager();
		
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn("testUser");
		when(sessionContext.getCallerPrincipal()).thenReturn(principal);
		
		service  = new PreferenceService(em, sessionContext);
    }

    @After
    public void destroy() {
    	factory.close();
    }
    
	private void initData(Preference... preferences) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("delete from Preference").executeUpdate();
		for (Preference p : preferences) {
			em.persist(p);
		}
	
		em.getTransaction().commit();
		em.close();
	}

	@Test
	public void testGetPreferences() {
		initData(new Preference(null, null, "testGetPreferences", "myValue1"),
				new Preference("MDMTEST", null, "testGetPreferences", "myValue2"),
				new Preference(null, "testUser", "testGetPreferences", "myValue3"),
				new Preference(null, "otherUser", "testGetPreferences", "myValue4"));
		
		
		assertThat(service.getPreferences(null, "testGetPreferences"))
			.extracting("scope", "source", "user", "key", "value")
			.containsExactlyInAnyOrder(
					new Tuple(Scope.SYSTEM, null, null, "testGetPreferences", "myValue1"),
					new Tuple(Scope.SOURCE, "MDMTEST", null, "testGetPreferences", "myValue2"),
					new Tuple(Scope.USER, null, "testUser", "testGetPreferences", "myValue3"));
	}
	
	
	@Test
	public void testGetPreferencesForSystem() {
		initData(new Preference(null, null, "testGetPreferencesSystem", "myValue"),
				new Preference("MDMTEST", null, "testGetPreferencesSystem", "myValue"));
		
		
		assertThat(service.getPreferences("system", "testGetPreferencesSystem"))
			.extracting("scope", "source", "user", "key", "value")
			.containsExactly(new Tuple(Scope.SYSTEM, null, null, "testGetPreferencesSystem", "myValue"));
	}
	
	@Test
	public void testGetPreferencesForSource() {
		initData(new Preference(null, "testUser", "testGetPreferencesForSource", "myValue"),
				new Preference("MDM_OTHER", null, "testGetPreferencesForSource", "myValue"),
				new Preference("MDMTEST", null, "testGetPreferencesForSource", "myValue"));
		
		
		assertThat(service.getPreferences("source", "testGetPreferencesForSource"))
			.extracting("scope", "source", "user", "key", "value")
			.containsExactlyInAnyOrder(
					new Tuple(Scope.SOURCE, "MDM_OTHER", null, "testGetPreferencesForSource", "myValue"),
					new Tuple(Scope.SOURCE, "MDMTEST", null, "testGetPreferencesForSource", "myValue"));
	}
	
	@Test
	public void testGetPreferencesForUser() {
		initData(new Preference(null, "other", "testGetPreferencesForUser", "myValue"),
				new Preference(null, "testUser", "testGetPreferencesForUser", "myValue"),
				new Preference("MDMTEST", null, "testGetPreferencesForUser", "myValue"));
		
		
		assertThat(service.getPreferences("user", "testGetPreferencesForUser"))
			.extracting("scope", "source", "user", "key", "value")
			.containsExactly(
					new Tuple(Scope.USER, null, "other", "testGetPreferencesForUser", "myValue"),
					new Tuple(Scope.USER, null, "testUser", "testGetPreferencesForUser", "myValue"));
	}
	
	@Test
	public void testGetPreferencesBySource() {
		initData(new Preference("MDMTEST", null, "testGetPreferencesSource", "myValue"),
				new Preference("MDM_OTHER", null, "testGetPreferencesSource", "myValue"));
		
		assertThat(service.getPreferencesBySource("MDMTEST", "testGetPreferencesSource"))
			.hasSize(1)
			.extracting("scope", "source", "user", "key", "value")
			.containsExactly(new Tuple(Scope.SOURCE, "MDMTEST", null, "testGetPreferencesSource", "myValue"));
	}
	
	@Test
	public void testGetPreferencesBySourceKeyEmpty() {
		initData(new Preference("MDMTEST", null, "testGetPreferencesSourceKeyEmpty", "myValue"),
				new Preference("MDM_OTHER", null, "testGetPreferencesSourceKeyEmpty", "myValue"));
		
		assertThat(service.getPreferencesBySource("MDMTEST", ""))
			.hasSize(1)
			.extracting("scope", "source", "user", "key", "value")
			.containsExactly(new Tuple(Scope.SOURCE, "MDMTEST", null, "testGetPreferencesSourceKeyEmpty", "myValue"));
	}
	
	@Test
	public void testDeletePreference() {
		initData(new Preference(null, null, "testDeletePreference", "myValue"));
		
		List<PreferenceMessage> listBeforeDelete = service.getPreferences("system", "testDeletePreference");
		
		assertThat(listBeforeDelete).hasSize(1);
		
		em.getTransaction().begin();
		service.deletePreference(listBeforeDelete.get(0).getId());
		em.getTransaction().commit();
		
		
		List<PreferenceMessage> listAfterDelete = service.getPreferences("system", "testDeletePreference");
		assertThat(listAfterDelete).hasSize(0);
	}
	
	@Test
	public void testSaveSystemScope() {
		PreferenceMessage pref = new PreferenceMessage();
		pref.setScope(Scope.SYSTEM);
		pref.setKey("testSaveSystemScope");
		pref.setValue("myValue");
		
		em.getTransaction().begin();
		PreferenceMessage saved = service.save(pref);
		em.getTransaction().commit();
		
		assertThat(saved)
			.hasNoNullFieldsOrPropertiesExcept("source", "user")
			.hasFieldOrPropertyWithValue("scope", Scope.SYSTEM)
			.hasFieldOrPropertyWithValue("key", "testSaveSystemScope")
			.hasFieldOrPropertyWithValue("value", "myValue");
	}
	
	@Test
	public void testSaveSourceScope() {
		PreferenceMessage pref = new PreferenceMessage();
		pref.setScope(Scope.SOURCE);
		pref.setSource("MDMTEST");
		pref.setKey("testSaveSourceScope");
		pref.setValue("myValue");
		
		em.getTransaction().begin();
		PreferenceMessage saved = service.save(pref);
		em.getTransaction().commit();
		
		assertThat(saved)
			.hasNoNullFieldsOrPropertiesExcept("user")
			.hasFieldOrPropertyWithValue("scope", Scope.SOURCE)
			.hasFieldOrPropertyWithValue("source", "MDMTEST")
			.hasFieldOrPropertyWithValue("key", "testSaveSourceScope")
			.hasFieldOrPropertyWithValue("value", "myValue");
	}

	@Test
	public void testSaveUserScope() {
		PreferenceMessage pref = new PreferenceMessage();
		pref.setScope(Scope.USER);
		pref.setKey("testSaveUserScope");
		pref.setValue("myValue");
		
		em.getTransaction().begin();
		PreferenceMessage saved = service.save(pref);
		em.getTransaction().commit();
		
		assertThat(saved)
			.hasNoNullFieldsOrPropertiesExcept("source")
			.hasFieldOrPropertyWithValue("scope", Scope.USER)
			.hasFieldOrPropertyWithValue("user", "testUser")
			.hasFieldOrPropertyWithValue("key", "testSaveUserScope")
			.hasFieldOrPropertyWithValue("value", "myValue");
	}
	
	@Test
	public void testSaveOverrideExisting() {
		initData(new Preference(null, null, "testSaveOverrideExisting", "myValue"));
		
		PreferenceMessage pref = new PreferenceMessage();
		pref.setScope(Scope.SYSTEM);
		pref.setKey("testSaveOverrideExisting");
		pref.setValue("myValue");
		
		em.getTransaction().begin();
		PreferenceMessage saved = service.save(pref);
		em.getTransaction().commit();
		
		assertThat(saved)
			.hasNoNullFieldsOrPropertiesExcept("source", "user")
			.hasFieldOrPropertyWithValue("scope", Scope.SYSTEM)
			.hasFieldOrPropertyWithValue("key", "testSaveOverrideExisting")
			.hasFieldOrPropertyWithValue("value", "myValue");
		
		assertThat(service.getPreferences("System", "testSaveOverrideExisting")).hasSize(1);
	}
}

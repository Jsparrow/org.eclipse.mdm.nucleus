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

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class PreferenceTest
{
	private static final long CLOB_SIZE = 1_000_000;

	private EntityManagerFactory factory ;

	@Before
    public void init() {
		factory = Persistence.createEntityManagerFactory("preferenceTest", 
				ImmutableMap.of(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence-test.xml"));
    }

    @After
    public void destroy() {
    	factory.close();
    }
    
	@Test
	public void testPersist() {
		
		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();
		Preference p = new Preference("MDMXYZ", "*", "key1", "value1");
		em.persist(p);
		em.getTransaction().commit();
	}

	@Test
	public void testLoad() {
		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();
		Preference p = new Preference("MDMXYZ", "*", "key2", "value1");
		em.persist(p);
		em.getTransaction().commit();

		assertThat(em.find(Preference.class, p.getId()))
		.isEqualToIgnoringGivenFields(new Preference("MDMXYZ", "*", "key2", "value1"), "id");

		em.close();
	}

	@Test
	public void testQuery() {
		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();
		Preference p = new Preference("MDMXYZ", "*", "key3", "value1");
		em.persist(p);
		em.getTransaction().commit();

		TypedQuery<Preference> q = em.createQuery("select p from Preference p", Preference.class);
		assertThat(q.getResultList())
		.hasSize(1)
		.usingElementComparatorIgnoringFields("id")
		.contains(new Preference("MDMXYZ", "*", "key3", "value1"));

		em.close();
	}

	@Test
	public void testPersistLob() {
		String longString = generateString(CLOB_SIZE);

		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();
		Preference p = new Preference("MDMXYZ", "*", "clob", longString);
		em.persist(p);
		em.getTransaction().commit();
		em.close();

		em = factory.createEntityManager();
		Preference loaded = em.find(Preference.class, p.getId());

		assertThat(loaded).isEqualToIgnoringGivenFields(new Preference("MDMXYZ", "*", "clob", longString), "id");
		em.close();
	}

	private String generateString(long length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++)
		{
			builder.append("a");
		}
		return builder.toString();
	}
}

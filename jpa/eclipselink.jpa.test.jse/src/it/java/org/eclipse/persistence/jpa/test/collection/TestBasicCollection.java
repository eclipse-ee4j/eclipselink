/*
 * Copyright (c) 2023 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
package org.eclipse.persistence.jpa.test.collection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Set;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.collection.model.CityEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestBasicCollection {

    @Emf(createTables = DDLGen.DROP_CREATE, 
            classes = { CityEntity.class }, 
            properties = { @Property(name = "eclipselink.logging.level", value = "ALL"),
                    @Property(name = "eclipselink.logging.level.sql", value = "FINE"),
                    @Property(name = "eclipselink.logging.parameters", value = "true")})
    private EntityManagerFactory emf;

    @Test
    public void testUpdateBasicCollectionWithQuery() {
        Set<Integer> origSet = Set.of(608);

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(new CityEntity("Minneapolis", origSet));
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }

        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CityEntity find1 = em.find(CityEntity.class, "Minneapolis");

            Assert.assertNotNull(find1);
            Assert.assertNotNull(find1.getAreaCodes());
            Assert.assertEquals(origSet.size(), find1.getAreaCodes().size());
            Assert.assertTrue(origSet.containsAll(find1.getAreaCodes()));
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }

        Set<Integer> updatedSet = Set.of(563, 456);

        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Query q = em.createQuery("UPDATE CityEntity o SET o.areaCodes=?1 WHERE o.name=?2");
            q.setParameter(1, updatedSet);
            q.setParameter(2, "Minneapolis");
            q.executeUpdate();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }

        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CityEntity find2 = em.find(CityEntity.class, "Minneapolis");

            Assert.assertNotNull(find2);
            Assert.assertNotNull(find2.getAreaCodes());
            Assert.assertEquals(updatedSet.size(), find2.getAreaCodes().size());
            Assert.assertTrue(updatedSet.containsAll(find2.getAreaCodes()));
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }
    }

    @Test
    public void testINBasicCollectionWithQuery() {
        Set<Integer> origSet = Set.of(234, 789);

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(new CityEntity("Los Angeles", origSet));
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }

        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CityEntity find1 = em.find(CityEntity.class, "Los Angeles");

            Assert.assertNotNull(find1);
            Assert.assertNotNull(find1.getAreaCodes());
            Assert.assertEquals(origSet.size(), find1.getAreaCodes().size());
            Assert.assertTrue("Expected " + origSet + " / actual " + find1.getAreaCodes(), origSet.containsAll(find1.getAreaCodes()));
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }

        Set<Integer> set1 = Set.of(123, 234);
        Set<Integer> set2 = Set.of(345);
        Set<Integer> set3 = Set.of(234, 789);
        Set<Integer> set4 = Set.of(678);
        Set<Set<Integer>> searchSet = Set.of(set1, set2, set3, set4);

        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<CityEntity> q = em.createQuery("SELECT e FROM CityEntity e WHERE e.areaCodes IN ?1", CityEntity.class);
            q.setParameter(1, searchSet);
            List<CityEntity> res = q.getResultList();

            Assert.assertEquals(1, res.size());
            CityEntity find2 = res.get(1);
            Assert.assertTrue(origSet.containsAll(find2.getAreaCodes()));
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }
    }
}

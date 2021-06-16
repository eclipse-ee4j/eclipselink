/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     01/21/2020: Will Dazey
//       - #559346: ClassCastException: DataReadQuery incompatible with ObjectLevelReadQuery
package org.eclipse.persistence.jpa.test.criteria;

import java.util.HashMap;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.jpa.test.criteria.model.CriteriaCar;
import org.eclipse.persistence.jpa.test.criteria.model.CriteriaCarId;
import org.eclipse.persistence.jpa.test.criteria.model.CriteriaCar_;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestCollectionTable {
    @Emf(name = "CollectionTableJoinEMF", createTables = DDLGen.DROP_CREATE, classes = { CriteriaCar.class },
            properties = { @Property(name = "eclipselink.cache.shared.default", value = "false") })
    private EntityManagerFactory emf;

    @Before
    public void setup() {
        //Populate the table
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaCar[] data = new CriteriaCar[] {
                    new CriteriaCar("One", 1, new HashMap<String, String>() {{
                        put("American", "Mustang");
                        put("German", "Volkswagen");
                    }}),
                    new CriteriaCar("Two", 1, new HashMap<String, String>() {{
                        put("American", "Mustang");
                        put("German", "Volkswagen");
                    }}),
                    new CriteriaCar("Three", 1, new HashMap<String, String>() {{
                        put("American", "Mustang");
                        put("German", "Volkswagen");
                    }}),
                    new CriteriaCar("Four", 1, new HashMap<String, String>() {{
                        put("American", "Mustang");
                        put("German", "Volkswagen");
                    }})
            };

            em.getTransaction().begin();
            for(CriteriaCar d : data) {
                if(em.find(CriteriaCar.class, new CriteriaCarId(d.getId(), d.getVersion())) == null) {
                    em.persist(d);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testJoinOnCollectionTable() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            final CriteriaBuilder builder = em.getCriteriaBuilder();
            final CriteriaQuery<CriteriaCar> criteriaQuery = builder.createQuery(CriteriaCar.class);
            final Root<CriteriaCar> from = criteriaQuery.from(CriteriaCar.class);

            from.fetch(CriteriaCar_.origin);

            final TypedQuery<CriteriaCar> query = em.createQuery(criteriaQuery);

            int resultMax = 2;
            query.setMaxResults(resultMax);

            List<CriteriaCar> res = query.getResultList();
            Assert.assertEquals(resultMax, res.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = emf.createEntityManager();
        try {
            final CriteriaBuilder builder = em.getCriteriaBuilder();
            final CriteriaQuery<CriteriaCar> criteriaQuery = builder.createQuery(CriteriaCar.class);
            final Root<CriteriaCar> from = criteriaQuery.from(CriteriaCar.class);

            from.fetch(CriteriaCar_.origin);

            final TypedQuery<CriteriaCar> query = em.createQuery(criteriaQuery);

            int resultMax = 2;
            query.setMaxResults(resultMax);

            List<CriteriaCar> res = query.getResultList();
            Assert.assertEquals(resultMax, res.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }
}

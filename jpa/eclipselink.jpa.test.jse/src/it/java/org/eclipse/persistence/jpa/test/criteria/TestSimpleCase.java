/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     02/01/2022: Tomas Kraus
//       - Issue 1442: Implement New JPA API 3.1.0 Features
package org.eclipse.persistence.jpa.test.criteria;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.jpa.test.criteria.model.NumberEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test new API 3.1.0 when methods of SimpleCase subclass of CriteriaBuilder.
 * Added to JPA-API as PR #362
 */
@RunWith(EmfRunner.class)
public class TestSimpleCase {

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {
                    NumberEntity.class
            },
            properties = {
                    @Property(name = "eclipselink.cache.shared.default", value = "false"),
                    // This property remaps String from VARCHAR->NVARCHAR(or db equivalent)
                    @Property(name = "eclipselink.target-database-properties",
                            value = "UseNationalCharacterVaryingTypeForString=true"),
            })
    private EntityManagerFactory emf;

    private final NumberEntity[] NUMBER = {
            new NumberEntity(1, 0L, 0D),
            new NumberEntity(2, 3L, 10D),
            new NumberEntity(3, 3L, 100D),
            new NumberEntity(4, 5L, 10D),
            new NumberEntity(5, 5L, 100D),
            new NumberEntity(6, 5L, 1000D)
    };

    @Before
    public void setup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (NumberEntity e : NUMBER) {
                em.persist(e);
            }
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @After
    public void cleanup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM NumberEntity e").executeUpdate();
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test SimpleCase<C,R> when(Expression<? extends C>, R) prototype
    // Strategy: Execute the query and check whether 3 rows where returned
    @Test
    public void testWhenExpressionValue() {
        try (final EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<NumberEntity> cq = cb.createQuery(NumberEntity.class);
            Root<NumberEntity> entity = cq.from(NumberEntity.class);
            cq.select(entity);
            Expression<Object> selectCase = cb.selectCase(entity.get("longValue"))
                    .when(cb.literal(3), 10)
                    .when(cb.literal(5), 100)
                    .otherwise(0);
            cq.where(cb.equal(entity.get("doubleValue"), selectCase));
            List<NumberEntity> result = em.createQuery(cq).getResultList();
            MatcherAssert.assertThat(result.size(), Matchers.equalTo(3));
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    // Test SimpleCase<C,R> when(Expression<? extends C>, Expression<? extends R>) prototype
    // Strategy: Execute the query and check whether 3 rows where returned
    @Test
    public void testWhenExpressionExpression() {
        try (final EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<NumberEntity> cq = cb.createQuery(NumberEntity.class);
            Root<NumberEntity> entity = cq.from(NumberEntity.class);
            cq.select(entity);
            Expression<Object> selectCase = cb.selectCase(entity.get("longValue"))
                    .when(cb.literal(3), cb.literal(10))
                    .when(cb.literal(5), cb.literal(100))
                    .otherwise(0);
            cq.where(cb.equal(entity.get("doubleValue"), selectCase));
            List<NumberEntity> result = em.createQuery(cq).getResultList();
            MatcherAssert.assertThat(result.size(), Matchers.equalTo(3));
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

}

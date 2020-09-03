/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.criteria;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.eclipse.persistence.jpa.test.criteria.model.CoalesceEntity;
import org.eclipse.persistence.jpa.test.criteria.model.CoalesceEntity_;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestCoalesceFunction {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { CoalesceEntity.class })
    private EntityManagerFactory emf;

    /**
     * Test for javax.persistence.criteria.CriteriaBuilder.coalesce(Expression<? extends Y> x, Y y)
     */
    @Test
    public void testCoalesceFunction3Long() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            final CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);
            Root<CoalesceEntity> root = criteriaQuery.from(CoalesceEntity.class);

            Subquery<Long> countQuery = criteriaQuery.subquery(Long.class);
            Root<CoalesceEntity> countRoot = countQuery.from(CoalesceEntity.class);
            countQuery.select(builder.count(countRoot));

            // Pass the literal value directly
            Expression<Long> coalesceExp = builder.coalesce(countQuery, 0L);

            criteriaQuery.select(
                    builder.array(
                            root.get("description"),
                            coalesceExp));

            em.createQuery(criteriaQuery).getResultList();
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

            CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);
            Root<CoalesceEntity> root = criteriaQuery.from(CoalesceEntity.class);

            Subquery<Long> countQuery = criteriaQuery.subquery(Long.class);
            Root<CoalesceEntity> countRoot = countQuery.from(CoalesceEntity.class);
            countQuery.select(builder.count(countRoot));

            // create a ConstantExpression from the literal value
            Expression<Long> coalesceExp = builder.coalesce(countQuery, builder.literal(0L));
            criteriaQuery.select(
                    builder.array(
                            root.get("description"),
                            coalesceExp));

            em.createQuery(criteriaQuery).getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Test for javax.persistence.criteria.CriteriaBuilder.coalesce(Expression<? extends Y> x, Y y)
     */
    @Test
    public void testCoalesceFunction3BigDecimal() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            final CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);
            Root<CoalesceEntity> root = criteriaQuery.from(CoalesceEntity.class);

            Subquery<BigDecimal> countQuery = criteriaQuery.subquery(BigDecimal.class);
            Root<CoalesceEntity> countRoot = countQuery.from(CoalesceEntity.class);
            countQuery.select(countRoot.get(CoalesceEntity_.bigDecimal));

            // Pass the literal value directly
            Expression<BigDecimal> coalesceExp = builder.coalesce(countQuery, new BigDecimal(50));

            criteriaQuery.select(
                    builder.array(
                            root.get("description"),
                            coalesceExp));

            em.createQuery(criteriaQuery).getResultList();
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

            CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);
            Root<CoalesceEntity> root = criteriaQuery.from(CoalesceEntity.class);

            Subquery<BigDecimal> countQuery = criteriaQuery.subquery(BigDecimal.class);
            Root<CoalesceEntity> countRoot = countQuery.from(CoalesceEntity.class);
            countQuery.select(countRoot.get(CoalesceEntity_.bigDecimal));

            // create a ConstantExpression from the literal value
            Expression<BigDecimal> coalesceExp = builder.coalesce(countQuery, builder.literal(new BigDecimal(50)));
            criteriaQuery.select(
                    builder.array(
                            root.get("description"),
                            coalesceExp));

            em.createQuery(criteriaQuery).getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Test for javax.persistence.criteria.CriteriaBuilder.coalesce(Expression<? extends Y> x, Y y)
     */
    @Test
    public void testCoalesceFunction3Date() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            final CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);
            Root<CoalesceEntity> root = criteriaQuery.from(CoalesceEntity.class);

            Subquery<Date> countQuery = criteriaQuery.subquery(Date.class);
            Root<CoalesceEntity> countRoot = countQuery.from(CoalesceEntity.class);
            countQuery.select(countRoot.get(CoalesceEntity_.date));

            // Pass the literal value directly
            Expression<Date> coalesceExp = builder.coalesce(countQuery, new Date());

            criteriaQuery.select(
                    builder.array(
                            root.get("description"),
                            coalesceExp));

            em.createQuery(criteriaQuery).getResultList();
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

            CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);
            Root<CoalesceEntity> root = criteriaQuery.from(CoalesceEntity.class);

            Subquery<Date> countQuery = criteriaQuery.subquery(Date.class);
            Root<CoalesceEntity> countRoot = countQuery.from(CoalesceEntity.class);
            countQuery.select(countRoot.get(CoalesceEntity_.date));

            // create a ConstantExpression from the literal value
            Expression<Date> coalesceExp = builder.coalesce(countQuery, builder.literal(new Date()));
            criteriaQuery.select(
                    builder.array(
                            root.get("description"),
                            coalesceExp));

            em.createQuery(criteriaQuery).getResultList();
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

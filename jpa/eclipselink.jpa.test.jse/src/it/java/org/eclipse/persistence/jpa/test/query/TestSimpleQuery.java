/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//     IBM - Bug 521402: Add support for Criteria queries with only literals
package org.eclipse.persistence.jpa.test.query;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLCallListener;
import org.eclipse.persistence.jpa.test.query.model.SimpleQueryEntity;
import org.eclipse.persistence.jpa.test.query.model.SimpleQueryEntity_;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestSimpleQuery {
    @Emf(name = "SimpleQueryPersistenceUnit", 
            createTables = DDLGen.DROP_CREATE, 
            classes = { SimpleQueryEntity.class }, 
            properties = { 
                    @Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emf;

    private static boolean POPULATED = false;

    @SQLCallListener(name = "SimpleQueryPersistenceUnit")
    List<String> _sql;

    @Test
    public void testSimpleQuery() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            _sql.clear();

            Query jpqlQuery = em.createQuery("SELECT 'HELLO' FROM SimpleQueryEntity s");
            // Set parameter binding off so that we can validate the arguments
            jpqlQuery.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            List<?> res = jpqlQuery.getResultList();
            Assert.assertEquals(2, res.size());
            Assert.assertEquals(1, _sql.size());

            Assert.assertEquals("SELECT 'HELLO' FROM SIMPLEQUERYENTITY", _sql.remove(0));

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(SimpleQueryEntity.class);
            cquery.multiselect(cb.literal("HELLO"));

            Query query = em.createQuery(cquery);
            // Set parameter binding off so that we can validate the arguments
            query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            List<?> res2 = query.getResultList();
            Assert.assertEquals(2, res2.size());
            Assert.assertEquals(1, _sql.size());

            Assert.assertEquals("SELECT 'HELLO' FROM SIMPLEQUERYENTITY", _sql.remove(0));
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testSimpleQuery2() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            _sql.clear();

            Query jpqlQuery = em.createQuery("SELECT 'HELLO', s.intVal1 FROM SimpleQueryEntity s");
            // Set parameter binding off so that we can validate the arguments
            jpqlQuery.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            List<?> res = jpqlQuery.getResultList();
            Assert.assertEquals(2, res.size());
            Assert.assertEquals(1, _sql.size());

            Assert.assertEquals("SELECT 'HELLO', INTVAL1 FROM SIMPLEQUERYENTITY", _sql.remove(0));

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<SimpleQueryEntity> root = cquery.from(SimpleQueryEntity.class);
            cquery.multiselect(cb.literal("HELLO"), root.get(SimpleQueryEntity_.intVal1));

            Query query = em.createQuery(cquery);
            // Set parameter binding off so that we can validate the arguments
            query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            List<?> res2 = query.getResultList();
            Assert.assertEquals(2, res2.size());
            Assert.assertEquals(1, _sql.size());

            Assert.assertEquals("SELECT 'HELLO', INTVAL1 FROM SIMPLEQUERYENTITY", _sql.remove(0));
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testSimpleQuery3() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            _sql.clear();

            Query jpqlQuery = em.createQuery("SELECT 'HELLO' FROM SimpleQueryEntity s WHERE ?1 = ?2");
            jpqlQuery.setParameter(1, 1);
            jpqlQuery.setParameter(2, 1);
            // Set parameter binding off so that we can validate the arguments
            jpqlQuery.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            List<?> res = jpqlQuery.getResultList();
            Assert.assertEquals(2, res.size());
            Assert.assertEquals(1, _sql.size());

            Assert.assertEquals("SELECT 'HELLO' FROM SIMPLEQUERYENTITY WHERE (1 = 1)", _sql.remove(0));

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            cquery.from(SimpleQueryEntity.class);
            cquery.multiselect(cb.literal("HELLO"));
            ParameterExpression<Integer> intParam1 = cb.parameter(Integer.class);
            ParameterExpression<Integer> intParam2 = cb.parameter(Integer.class);
            cquery.where(cb.equal(intParam1, intParam2));

            Query query = em.createQuery(cquery);
            query.setParameter(intParam1, 1);
            query.setParameter(intParam2, 1);
            // Set parameter binding off so that we can validate the arguments
            query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            List<?> res2 = query.getResultList();
            Assert.assertEquals(2, res2.size());
            Assert.assertEquals(1, _sql.size());

            Assert.assertEquals("SELECT 'HELLO' FROM SIMPLEQUERYENTITY WHERE (1 = 1)", _sql.remove(0));
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private void populate() {
        EntityManager em = emf.createEntityManager();
        try {
            SimpleQueryEntity s1 = new SimpleQueryEntity(21L, 51, "simple1");
            SimpleQueryEntity s2 = new SimpleQueryEntity(22L, 52, "simple2");

            em.getTransaction().begin();
            em.persist(s1);
            em.persist(s2);
            em.getTransaction().commit();

            POPULATED = true;
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

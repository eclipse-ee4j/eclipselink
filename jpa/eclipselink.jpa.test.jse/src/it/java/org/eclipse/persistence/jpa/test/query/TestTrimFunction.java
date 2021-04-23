/*
 * Copyright (c) 2021 IBM Corporation, Oracle, and/or affiliates. All rights reserved.
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
//     IBM - Bug 573094: TRIM function generates incorrect SQL for CriteriaBuilder
package org.eclipse.persistence.jpa.test.query;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.Trimspec;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLCallListener;
import org.eclipse.persistence.jpa.test.query.model.TrimEntity;
import org.eclipse.persistence.jpa.test.query.model.TrimEntity_;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.FirebirdPlatform;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestTrimFunction {
    @Emf(name = "TrimPersistenceUnit", 
            createTables = DDLGen.DROP_CREATE, 
            classes = { TrimEntity.class }, 
            properties = { 
                    @Property(name = "eclipselink.logging.level", value = "ALL"),
                    @Property(name = "eclipselink.logging.level.sql", value = "FINE"),
                    @Property(name = "eclipselink.logging.parameters", value = "true"),
                    @Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emf;

    @SQLCallListener(name = "TrimPersistenceUnit")
    List<String> _sql;

    @Test
    public void testWhereTrim() {
        EntityManager em = emf.createEntityManager();
        try {
            Query jpqlQuery = em.createQuery("SELECT e.strVal1 FROM TrimEntity e WHERE (e.strVal1 = TRIM('A' FROM 'AAHELLO WORDAAAAA'))");
            // Set parameter binding off so that we can validate the arguments
            jpqlQuery.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            jpqlQuery.getResultList();
            Assert.assertEquals(1, _sql.size());

            Platform platform = getPlatform(emf);
            // Add more platform specific diction to support more platforms
            if(platform.isSybase()) {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = STR_REPLACE('A', 'AAHELLO WORDAAAAA', NULL)))", _sql.remove(0));
            } else if(platform.isSQLServer()) {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = RTRIM('A' FROM LTRIM('A' FROM 'AAHELLO WORDAAAAA')))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAHELLO WORDAAAAA'))", _sql.remove(0));
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<TrimEntity> root = cquery.from(TrimEntity.class);
            cquery.multiselect(root.get(TrimEntity_.strVal1));
            cquery.where(cb.equal(root.get(TrimEntity_.strVal1), cb.trim(cb.literal('A'), cb.literal("AAHELLO WORDAAAAA"))));

            Query query = em.createQuery(cquery);
            // Set parameter binding off so that we can validate the arguments
            query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());

            // Add more platform specific diction to support more platforms
            if(platform.isSybase()) {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = STR_REPLACE('A', 'AAHELLO WORDAAAAA', NULL)))", _sql.remove(0));
            } else if(platform.isSQLServer()) {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = RTRIM('A' FROM LTRIM('A' FROM 'AAHELLO WORDAAAAA')))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = TRIM('A' FROM 'AAHELLO WORDAAAAA'))", _sql.remove(0));
            }
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testWhereLeftTrim() {
        EntityManager em = emf.createEntityManager();
        try {
            Query jpqlQuery = em.createQuery("SELECT e.strVal1 FROM TrimEntity e WHERE (e.strVal1 = TRIM(LEADING 'A' FROM 'AAHELLO WORDAAAAA'))");
            // Set parameter binding off so that we can validate the arguments
            jpqlQuery.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            jpqlQuery.getResultList();
            Assert.assertEquals(1, _sql.size());

            Platform platform = getPlatform(emf);
            // Add more platform specific diction to support more platforms
            if(platform.isMySQL() || platform.isDB2() || platform.isDerby() || platform.isSymfoware()) {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAHELLO WORDAAAAA'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = LTRIM('A', 'AAHELLO WORDAAAAA'))", _sql.remove(0));
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<TrimEntity> root = cquery.from(TrimEntity.class);
            cquery.multiselect(root.get(TrimEntity_.strVal1));
            cquery.where(cb.equal(root.get(TrimEntity_.strVal1), cb.trim(Trimspec.LEADING, cb.literal('A'), cb.literal("AAHELLO WORDAAAAA"))));

            Query query = em.createQuery(cquery);
            // Set parameter binding off so that we can validate the arguments
            query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());

            // Add more platform specific diction to support more platforms
            if(platform.isMySQL() || platform.isDB2() || platform.isDerby() || platform.isSymfoware()) {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = TRIM(LEADING 'A' FROM 'AAHELLO WORDAAAAA'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = LTRIM('A', 'AAHELLO WORDAAAAA'))", _sql.remove(0));
            }
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testWhereRightTrim() {
        EntityManager em = emf.createEntityManager();
        try {
            Query jpqlQuery = em.createQuery("SELECT e.strVal1 FROM TrimEntity e WHERE (e.strVal1 = TRIM(TRAILING 'A' FROM 'AAHELLO WORDAAAAA'))");
            // Set parameter binding off so that we can validate the arguments
            jpqlQuery.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            jpqlQuery.getResultList();
            Assert.assertEquals(1, _sql.size());

            Platform platform = getPlatform(emf);
            // Add more platform specific diction to support more platforms
            if(platform.isMySQL() || platform.isDB2() || platform.isDerby() || platform.isSymfoware() || platform.isHSQL() || platform instanceof FirebirdPlatform) {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAHELLO WORDAAAAA'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = RTRIM('A', 'AAHELLO WORDAAAAA'))", _sql.remove(0));
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<TrimEntity> root = cquery.from(TrimEntity.class);
            cquery.multiselect(root.get(TrimEntity_.strVal1));
            cquery.where(cb.equal(root.get(TrimEntity_.strVal1), cb.trim(Trimspec.TRAILING, cb.literal('A'), cb.literal("AAHELLO WORDAAAAA"))));

            Query query = em.createQuery(cquery);
            // Set parameter binding off so that we can validate the arguments
            query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());

            // Add more platform specific diction to support more platforms
            if(platform.isMySQL() || platform.isDB2() || platform.isDerby() || platform.isSymfoware() || platform.isHSQL() || platform instanceof FirebirdPlatform) {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = TRIM(TRAILING 'A' FROM 'AAHELLO WORDAAAAA'))", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT STRVAL1 FROM TRIMENTITY WHERE (STRVAL1 = RTRIM('A', 'AAHELLO WORDAAAAA'))", _sql.remove(0));
            }
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testSelectTrim() {
        EntityManager em = emf.createEntityManager();
        try {
            Query jpqlQuery = em.createQuery("SELECT TRIM('A' FROM 'AAHELLO WORDAAAAA') FROM TrimEntity e WHERE (e.strVal1 = 'HELLO')");
            // Set parameter binding off so that we can validate the arguments
            jpqlQuery.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            jpqlQuery.getResultList();
            Assert.assertEquals(1, _sql.size());

            Platform platform = getPlatform(emf);
            // Add more platform specific diction to support more platforms
            if(platform.isSybase()) {
                Assert.assertEquals("SELECT STR_REPLACE('A', 'AAHELLO WORDAAAAA', NULL) FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO'))", _sql.remove(0));
            } else if(platform.isSQLServer()) {
                Assert.assertEquals("SELECT RTRIM('A' FROM LTRIM('A' FROM 'AAHELLO WORDAAAAA')) FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM('A' FROM 'AAHELLO WORDAAAAA') FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<TrimEntity> root = cquery.from(TrimEntity.class);
            cquery.multiselect(cb.trim(cb.literal('A'), cb.literal("AAHELLO WORDAAAAA")));
            cquery.where(cb.equal(root.get(TrimEntity_.strVal1), cb.literal("HELLO")));

            Query query = em.createQuery(cquery);
            // Set parameter binding off so that we can validate the arguments
            query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());

            // Add more platform specific diction to support more platforms
            if(platform.isSybase()) {
                Assert.assertEquals("SELECT STR_REPLACE('A', 'AAHELLO WORDAAAAA', NULL) FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO'))", _sql.remove(0));
            } else if(platform.isSQLServer()) {
                Assert.assertEquals("SELECT RTRIM('A' FROM LTRIM('A' FROM 'AAHELLO WORDAAAAA')) FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT TRIM('A' FROM 'AAHELLO WORDAAAAA') FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            }
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testSelectLeftTrim() {
        EntityManager em = emf.createEntityManager();
        try {
            Query jpqlQuery = em.createQuery("SELECT TRIM(LEADING 'A' FROM 'AAHELLO WORDAAAAA') FROM TrimEntity e WHERE (e.strVal1 = 'HELLO')");
            // Set parameter binding off so that we can validate the arguments
            jpqlQuery.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            jpqlQuery.getResultList();
            Assert.assertEquals(1, _sql.size());

            Platform platform = getPlatform(emf);
            // Add more platform specific diction to support more platforms
            if(platform.isMySQL() || platform.isDB2() || platform.isDerby() || platform.isSymfoware()) {
                Assert.assertEquals("SELECT TRIM(LEADING 'A' FROM 'AAHELLO WORDAAAAA') FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT LTRIM('A', 'AAHELLO WORDAAAAA') FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<TrimEntity> root = cquery.from(TrimEntity.class);
            cquery.multiselect(cb.trim(Trimspec.LEADING, cb.literal('A'), cb.literal("AAHELLO WORDAAAAA")));
            cquery.where(cb.equal(root.get(TrimEntity_.strVal1), cb.literal("HELLO")));

            Query query = em.createQuery(cquery);
            // Set parameter binding off so that we can validate the arguments
            query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());

            // Add more platform specific diction to support more platforms
            if(platform.isMySQL() || platform.isDB2() || platform.isDerby() || platform.isSymfoware()) {
                Assert.assertEquals("SELECT TRIM(LEADING 'A' FROM 'AAHELLO WORDAAAAA') FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT LTRIM('A', 'AAHELLO WORDAAAAA') FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            }
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testSelectRightTrim() {
        EntityManager em = emf.createEntityManager();
        try {
            Query jpqlQuery = em.createQuery("SELECT TRIM(TRAILING 'A' FROM 'AAHELLO WORDAAAAA') FROM TrimEntity e WHERE (e.strVal1 = 'HELLO')");
            // Set parameter binding off so that we can validate the arguments
            jpqlQuery.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            jpqlQuery.getResultList();
            Assert.assertEquals(1, _sql.size());

            Platform platform = getPlatform(emf);
            // Add more platform specific diction to support more platforms
            if(platform.isMySQL() || platform.isDB2() || platform.isDerby() || platform.isSymfoware() || platform.isHSQL() || platform instanceof FirebirdPlatform) {
                Assert.assertEquals("SELECT TRIM(TRAILING 'A' FROM 'AAHELLO WORDAAAAA') FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT RTRIM('A', 'AAHELLO WORDAAAAA') FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cquery = cb.createQuery(Object[].class);
            Root<TrimEntity> root = cquery.from(TrimEntity.class);
            cquery.multiselect(cb.trim(Trimspec.TRAILING, cb.literal('A'), cb.literal("AAHELLO WORDAAAAA")));
            cquery.where(cb.equal(root.get(TrimEntity_.strVal1), cb.literal("HELLO")));

            Query query = em.createQuery(cquery);
            // Set parameter binding off so that we can validate the arguments
            query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            query.getResultList();
            Assert.assertEquals(1, _sql.size());

            // Add more platform specific diction to support more platforms
            if(platform.isMySQL() || platform.isDB2() || platform.isDerby() || platform.isSymfoware() || platform.isHSQL() || platform instanceof FirebirdPlatform) {
                Assert.assertEquals("SELECT TRIM(TRAILING 'A' FROM 'AAHELLO WORDAAAAA') FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            } else {
                Assert.assertEquals("SELECT RTRIM('A', 'AAHELLO WORDAAAAA') FROM TRIMENTITY WHERE (STRVAL1 = 'HELLO')", _sql.remove(0));
            }
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}
/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     12/06/2018 - Will Dazey
//       - 542491: Add new 'eclipselink.jdbc.force-bind-parameters' property to force enable binding
package org.eclipse.persistence.jpa.test.property;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.property.model.GenericEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
/**
 * Tests the 'eclipselink.jdbc.force-bind-parameters' persistence property. 
 * For DB2 platforms, parameter binding is disabled by default. This new property
 * allows parameter binding to be enabled. 
 */
public class TestParameterBinding {

    @Emf(name = "forceBindEMF", createTables = DDLGen.DROP_CREATE, 
            classes = { GenericEntity.class }, 
            properties = {
                    @Property(name = "eclipselink.jdbc.force-bind-parameters", value = "true"),
                    //Setting query timeout just to speed up tests and eliminate retries if they fail
                    @Property(name = "javax.persistence.query.timeout", value = "1000"),
                    @Property(name = "eclipselink.logging.level", value = "ALL"),
                    @Property(name = "eclipselink.logging.level.sql", value = "FINE"),
                    @Property(name = "eclipselink.logging.parameters", value = "true")})
    private EntityManagerFactory forceBindEMF;

    @Test
    public void testCOALESCE_ForceBindJPQLParameters() {
        EntityManager em = forceBindEMF.createEntityManager();
        try {
            //1: Test COALESCE function with positional parameter and typed parameter
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 1 FROM GenericEntity s "
                    + "WHERE ABS(COALESCE(s.itemInteger1, ?1)) >= ?2", GenericEntity.class);
            query.setParameter(1, 0);
            query.setParameter(2, 99);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number in the query" , 3, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //2: Test COALESCE function with literal and typed parameter
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 1 FROM GenericEntity s "
                    + "WHERE ABS(COALESCE(s.itemInteger1, 0)) >= 99", GenericEntity.class);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 3, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 0, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //3: Test COALESCE function with all arguments as parameters
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 1 FROM GenericEntity s "
                    + "WHERE ABS(COALESCE(?1, ?2)) >= ?3", GenericEntity.class);
            query.setParameter(1, new Integer(1));
            query.setParameter(2, new Integer(20));
            query.setParameter(3, new Integer(300));
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.isDB2Z() || pl.isDerby()) {
                Assert.fail("Expected a failure from " + pl);
            }

            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 4, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 3, call.getParameters().size());
            }
        } catch(PersistenceException e) {
            Platform pl = forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.isDB2Z()) {
                //If all Arguments of COALESCE are untyped parameters, this is expected to fail for DB2/z
                Assert.assertEquals(DatabaseException.class, e.getCause().getClass());
                Assert.assertEquals("com.ibm.db2.jcc.am.SqlSyntaxErrorException", e.getCause().getCause().getClass().getName());
            } else if(pl.isDB2()) {
                //If all Arguments of COALESCE are untyped parameters, this is expected to fail for DB2 LUW
                Assert.assertEquals(DatabaseException.class, e.getCause().getClass());
            } else if(pl.isDerby()) {
                //All the arguments to the COALESCE/VALUE function cannot be parameters. 
                //The function needs at least one argument that is not a parameter. Error 42610.
                Assert.assertEquals(DatabaseException.class, e.getCause().getClass());
                Assert.assertEquals("java.sql.SQLSyntaxErrorException", e.getCause().getCause().getClass().getName());
            } else {
                Assert.fail("Unexpected failure: " + e);
            }
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
    public void testABS_ForceBindJPQLParameters() {
        EntityManager em = forceBindEMF.createEntityManager();
        try {
            //1: Test ABS function with parameter
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 2, COUNT(ABS(?1)) FROM GenericEntity s "
                    + "WHERE s.itemInteger1 = ABS(?1)", GenericEntity.class);
            query.setParameter(1, -3);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.isDB2Z()) {
                Assert.fail("Expected a failure from " + pl);
            }

            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 3, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            }
        } catch(PersistenceException e) {
            Platform pl = forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            //ABS is a built-in function and built-in functions do not support untyped parameters on DB2/z
            if(pl.isDB2Z()) {
                Assert.assertEquals(DatabaseException.class, e.getCause().getClass());
                Assert.assertEquals("com.ibm.db2.jcc.am.SqlSyntaxErrorException", e.getCause().getCause().getClass().getName());
            } else {
                Assert.fail("Unexpected failure: " + e);
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //2: Test ABS function with literal
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 2, COUNT(ABS(-3)) FROM GenericEntity s "
                    + "WHERE s.itemInteger1 = ABS(-3)", GenericEntity.class);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 3, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 0, call.getParameters().size());
            }
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
    public void testCONCAT_ForceBindJPQLParameters() {
        EntityManager em = forceBindEMF.createEntityManager();
        try {
            //1: Test string CONCAT with untyped parameter and literal
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 2 FROM GenericEntity s "
                    + "WHERE s.itemString1 = TRIM(CONCAT(?1 , '-'))"
                    + "AND s.itemString1 = TRIM(CONCAT(?2 , '-'))", GenericEntity.class);
            query.setParameter(1, "1");
            query.setParameter(2, "99");
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 5, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //1: Test string CONCAT with untyped parameter and untyped parameter
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 2 FROM GenericEntity s "
                    + "WHERE s.itemString1 = TRIM(CONCAT(?1 , ?1))"
                    + "AND s.itemString1 = TRIM(CONCAT(?2 , ?2))", GenericEntity.class);
            query.setParameter(1, "1");
            query.setParameter(2, "99");
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.isDB2Z() || pl.isDerby()) {
                Assert.fail("Expected a failure from " + pl);
            }

            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 5, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 4, call.getParameters().size());
            }
        } catch(PersistenceException e) {
            Platform pl = forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            //When both operands of a CONCAT operator are untyped parameters, error on DB2/z
            if(pl.isDB2Z()) {
                Assert.assertEquals(DatabaseException.class, e.getCause().getClass());
                Assert.assertEquals("com.ibm.db2.jcc.am.SqlSyntaxErrorException", e.getCause().getCause().getClass().getName());
            } else if(pl.isDerby()) {
                //When all the operands of '||' expression are untyped parameters, error 42X35 on Derby
                Assert.assertEquals(DatabaseException.class, e.getCause().getClass());
                Assert.assertEquals("java.sql.SQLSyntaxErrorException", e.getCause().getCause().getClass().getName());
            } else {
                Assert.fail("Unexpected failure: " + e);
            }
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
    public void testEXISTS_ForceBindJPQLParameters() {
        EntityManager em = forceBindEMF.createEntityManager();
        try {

            //9: Test string parameter in sub query
            TypedQuery<GenericEntity> query = em.createQuery("SELECT s FROM GenericEntity s "
                    + "WHERE s.itemString1 = ?1  AND EXISTS (SELECT 1 FROM GenericEntity e WHERE s.itemInteger1 = ?2 )", GenericEntity.class);
            query.setParameter(1, "Test");
            query.setParameter(2, 33);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 3, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //10: Test string literal in sub query
            TypedQuery<GenericEntity> query = em.createQuery("SELECT s FROM GenericEntity s "
                    + "WHERE s.itemString1 = 'Test' AND EXISTS (SELECT 1 FROM GenericEntity e WHERE s.itemInteger1 = 33 )", GenericEntity.class);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 3, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 0, call.getParameters().size());
            }
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
    public void testNUMERICALEXPRESSION_ForceBindJPQLParameters() {
        EntityManager em = forceBindEMF.createEntityManager();
        try {
            //Test numerical expression with untyped parameters + typed parameters
            TypedQuery<GenericEntity> query = em.createQuery("SELECT (s.itemInteger1 + ?4) FROM GenericEntity s "
                    + "WHERE (s.itemInteger1 + ?4) > 1", GenericEntity.class);
            query.setParameter(4, 2);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 3, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //Test numerical expression with parameters
            TypedQuery<GenericEntity> query = em.createQuery("SELECT (?3 + ?4) FROM GenericEntity s "
                    + "WHERE (?3 + ?4) > 1", GenericEntity.class);
            query.setParameter(3, 2);
            query.setParameter(4, 2);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.isDerby()) {
                Assert.fail("Expected a failure from " + pl);
            }

            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 5, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 4, call.getParameters().size());
            }
        } catch(PersistenceException e) {
            Platform pl = forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            //When all the operands of a numeric expression are untyped parameters, error 42X35 on Derby
            if(pl.isDerby()) {
                Assert.assertEquals(DatabaseException.class, e.getCause().getClass());
                Assert.assertEquals("java.sql.SQLSyntaxErrorException", e.getCause().getCause().getClass().getName());
            } else {
                Assert.fail("Unexpected failure: " + e);
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //Test numerical expression with literals
            TypedQuery<GenericEntity> query = em.createQuery("SELECT (s.itemInteger1 + 4) FROM GenericEntity s "
                    + "WHERE ABS(s.itemInteger1 + 4) > 1", GenericEntity.class);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 3, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 0, call.getParameters().size());
            }
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
    public void testIN_ForceBindJPQLParameters() {
        EntityManager em = forceBindEMF.createEntityManager();
        try {
            //Test all the operands of an IN predicate
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 2 FROM GenericEntity s "
                    + "WHERE ?1 IN (?2, ?3, ?4)", GenericEntity.class);
            query.setParameter(1, 4);
            query.setParameter(2, 4);
            query.setParameter(3, 5);
            query.setParameter(4, 6);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.isDB2Z() || pl.isDerby()) {
                Assert.fail("Expected a failure from " + pl);
            }

            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 5, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 4, call.getParameters().size());
            }
        } catch(PersistenceException e) {
            Platform pl = forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.isDB2Z()) {
                //When all the operands of an IN predicate are untyped parameters, error on DB2/z
                Assert.assertEquals(DatabaseException.class, e.getCause().getClass());
                Assert.assertEquals("com.ibm.db2.jcc.am.SqlSyntaxErrorException", e.getCause().getCause().getClass().getName());
            } else if(pl.isDerby()) {
                //Use as the left operand of an IN list is not allowed when all operands are untyped parameters, error 42X35 on Derby
                Assert.assertEquals(DatabaseException.class, e.getCause().getClass());
                Assert.assertEquals("java.sql.SQLSyntaxErrorException", e.getCause().getCause().getClass().getName());
            } else {
                Assert.fail("Unexpected failure: " + e);
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //Test the first and second operands of an IN predicate
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 2 FROM GenericEntity s "
                    + "WHERE ?1 IN (?2, 'b', 'c')", GenericEntity.class);
            query.setParameter(1, "a");
            query.setParameter(2, "a");
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 5, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //Test the first operand of an IN predicate and zero or more operands of the IN list except for the first operand of the IN list
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 2 FROM GenericEntity s "
                    + "WHERE ?1 IN (5, ?2, 6)", GenericEntity.class);
            query.setParameter(1, 4);
            query.setParameter(2, 4);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 5, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //Test any or all operands of the IN list of the IN predicate and the first operand of the IN predicate is not an untyped parameter marker
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 2 FROM GenericEntity s "
                    + "WHERE s.itemString1 IN (?1, 'b', ?2)", GenericEntity.class);
            query.setParameter(1, "a");
            query.setParameter(2, "c");
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 4, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            }
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
    public void testLIKE_ForceBindJPQLParameters() {
        EntityManager em = forceBindEMF.createEntityManager();
        try {
            //Test the first operand of a LIKE predicate (the match-expression) is untyped parameter
            // when at least one other operand (the pattern-expression or escape-expression) 
            // is not an untyped parameter marker
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 1 FROM GenericEntity s "
                    + "WHERE ?1 LIKE ?2 ESCAPE '_'", GenericEntity.class);
            query.setParameter(1, "HELLO_WORLD");
            query.setParameter(2, "HELLO%");
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 4, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //Test the first operand of a LIKE predicate (the match-expression) is untyped parameter
            // when at least one other operand (the pattern-expression or escape-expression) 
            // is not an untyped parameter marker
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 1 FROM GenericEntity s "
                    + "WHERE s.itemString1 LIKE ?2", GenericEntity.class);
            query.setParameter(2, "HELLO%");
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 1, call.getParameters().size());
            }
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
    public void testSUBSTR_ForceBindJPQLParameters() {
        EntityManager em = forceBindEMF.createEntityManager();
        try {
            //Test untyped parameter is first argument of SUBSTRING
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 1 FROM GenericEntity s "
                    + "WHERE TRIM(s.itemString1) = TRIM(SUBSTRING(?1, 1, 5))", GenericEntity.class);
            query.setParameter(1, "HELLO WORLD");
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 4, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 1, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //Test untyped parameter is first & second argument of SUBSTRING
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 1 FROM GenericEntity s "
                    + "WHERE TRIM(s.itemString1) = TRIM(SUBSTRING(?1, ?2, 5))", GenericEntity.class);
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 1);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 4, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 2, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //Test untyped parameter is all arguments of SUBSTRING
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 1 FROM GenericEntity s "
                    + "WHERE s.itemString1 = SUBSTRING(?1, ?2, ?3)", GenericEntity.class);
            query.setParameter(1, "HELLO WORLD");
            query.setParameter(2, 1);
            query.setParameter(3, 5);
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 4, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 3, call.getParameters().size());
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        em = forceBindEMF.createEntityManager();
        try {
            //Test SUBSTRING function with IN expression
            TypedQuery<GenericEntity> query = em.createQuery("SELECT 1 FROM GenericEntity s "
                    + "WHERE SUBSTRING(s.itemString1, 1, ?1) NOT IN (?2, ?3, ?4, ?5)", GenericEntity.class);
            query.setParameter(1, 5);
            query.setParameter(2, "TEST1");
            query.setParameter(3, "TEST2");
            query.setParameter(4, "HELLO");
            query.setParameter(5, "TEST3");
            query.getResultList();

            DatabaseCall call = ((JpaQuery<GenericEntity>)query).getDatabaseQuery().getCall();
            Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", call.isUsesBindingSet());

            DatabasePlatform pl = (DatabasePlatform)forceBindEMF.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(pl.shouldBindLiterals()) {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 7, call.getParameters().size());
            } else {
                Assert.assertEquals("The number of parameters found does not match the number supplied" , 5, call.getParameters().size());
            }
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
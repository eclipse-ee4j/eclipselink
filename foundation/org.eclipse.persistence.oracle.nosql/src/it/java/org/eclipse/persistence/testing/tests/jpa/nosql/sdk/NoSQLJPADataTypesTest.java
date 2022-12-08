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
//     Oracle - initial API and implementation.
package org.eclipse.persistence.testing.tests.jpa.nosql.sdk;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.resource.cci.Connection;
import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.ops.TableLimits;
import oracle.nosql.driver.ops.TableRequest;
import org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLConnection;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.testing.models.jpa.nosql.DataTypesEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Oracle NoSQL JPA database tests. Focused on various datatypes.
 */
public class NoSQLJPADataTypesTest {

    /** The persistence unit name. */
    private static final String PU_UNIT_NAME = "nosql-datatypes-sdk";

    /* Name of your table */
    private static final String TABLE_NAME = "DATA_TYPES_TAB";

    private static final long ID = 1L;
    private static final byte[] FIELD_BINARY = {1, 2, 3, 4};
    private static final boolean FIELD_BOOLEAN = true;
    private static final String FIELD_JSON = "{\"a\": 1.006, \"b\": null," +
            "\"bool\" : true, \"map\": {\"m1\": 5}," +
            "\"ar\" : [1,2.7,3]}";
    private static final String FIELD_STRING = "abcd";
    private static final Timestamp FIELD_TIMESTAMP = new Timestamp(123456L);

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    @Test
    public void testJPAFind() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PU_UNIT_NAME);
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            DataTypesEntity dataTypesEntity = em.find(DataTypesEntity.class, ID);
            LOG.log(SessionLog.INFO, String.format("Entity by em.find(...):\t%s", dataTypesEntity));

            assertEquals(ID, dataTypesEntity.getId());
            assertArrayEquals(FIELD_BINARY, dataTypesEntity.getFieldBinary());
            assertEquals(FIELD_BOOLEAN, dataTypesEntity.isFieldBoolean());
            assertNull(dataTypesEntity.getFieldNull());
            assertEquals(FIELD_STRING, dataTypesEntity.getFieldString());
            assertEquals(FIELD_TIMESTAMP, dataTypesEntity.getFieldTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Test
    public void testJPAQueryFindAll() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PU_UNIT_NAME);
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            Query query = em.createNamedQuery("DataTypesEntity.findAll", DataTypesEntity.class);
            List<DataTypesEntity> testEntities = query.getResultList();
            assertTrue(testEntities.size() > 0);
            for (DataTypesEntity dataTypesEntity : testEntities) {
                LOG.log(SessionLog.INFO, String.format("Entity by DataTypesEntity.findAll query:\t%s", dataTypesEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Test
    public void testJPAQueryFindById() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PU_UNIT_NAME);
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            Query query = em.createNamedQuery("DataTypesEntity.findById", DataTypesEntity.class);
            query.setParameter("id", ID);
            List<DataTypesEntity> testEntities = query.getResultList();
            assertTrue(testEntities.size() > 0);
            for (DataTypesEntity dataTypesEntity : testEntities) {
                LOG.log(SessionLog.INFO, String.format("Entity by DataTypesEntity.findById query:\t%s", dataTypesEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void testJPAPersist() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PU_UNIT_NAME);
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            DataTypesEntity entity = prepareEntity(ID);
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em != null) {
                em.close();
            }
        }
    }

    public static void testJPAMerge() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PU_UNIT_NAME);
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            DataTypesEntity entity = prepareEntity(ID);
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em != null) {
                em.close();
            }
        }
    }

    @AfterClass
    public static void testJPADelete() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PU_UNIT_NAME);
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            DataTypesEntity entity = em.find(DataTypesEntity.class, ID);
            assertEquals(ID, entity.getId());
            assertEquals(FIELD_STRING, entity.getFieldString());
            em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em != null) {
                em.close();
            }
        }
    }

    private static DataTypesEntity prepareEntity(long id) {
        DataTypesEntity dataTypesEntity = new DataTypesEntity(id);
        dataTypesEntity.setFieldBinary(FIELD_BINARY);
        dataTypesEntity.setFieldBoolean(FIELD_BOOLEAN);
        dataTypesEntity.setFieldJson(FIELD_JSON);
        dataTypesEntity.setFieldNull(null);
        dataTypesEntity.setFieldString(FIELD_STRING);
        dataTypesEntity.setFieldTimestamp(FIELD_TIMESTAMP);
        return dataTypesEntity;
    }

    @BeforeClass
    public static void testJPASetup() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PU_UNIT_NAME);
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Connection connection = em.unwrap(jakarta.resource.cci.Connection.class);
        NoSQLHandle noSQLHandle = ((OracleNoSQLConnection)connection).getNoSQLHandle();
        dropTable(noSQLHandle);
        createTable(noSQLHandle);
        testJPAPersist();
        testJPAMerge();
        em.getTransaction().commit();
        em.close();
    }


    private static void createTable(NoSQLHandle handle) {
        /*
         * Create a simple table with an integer key and various other columns to test data types
         */
        String createTableDDL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " " +
                "(id INTEGER, " +
                "col_binary BINARY, " +
                "col_boolean BOOLEAN, " +
                "col_json JSON, " +
                "col_null STRING, " +
                "col_string STRING, " +
                "col_timestamp TIMESTAMP(3), " +
                "PRIMARY KEY(id))";
        TableLimits limits = new TableLimits(1, 2, 1);
        TableRequest tableRequest = new TableRequest().setStatement(createTableDDL).setTableLimits(limits);
        LOG.log(SessionLog.INFO, String.format("Creating table:\t%s", TABLE_NAME));
        handle.doTableRequest(tableRequest, 60000, 1000);
        LOG.log(SessionLog.INFO, String.format("Table \t%s is active", TABLE_NAME));
    }

    private static void dropTable(NoSQLHandle handle) {
        /* Drop the table and wait for the table to move to dropped state */
        LOG.log(SessionLog.INFO, String.format("Dropping table:\t%s", TABLE_NAME));
        TableRequest tableRequest = new TableRequest().setStatement("DROP TABLE IF EXISTS " + TABLE_NAME);
        handle.doTableRequest(tableRequest, 60000, 1000);
        LOG.log(SessionLog.INFO, String.format("Table \t%s has been dropped", TABLE_NAME));
    }
}

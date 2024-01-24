/*
 * Copyright (c) 2022, 2024 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.resource.cci.Connection;

import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.ops.GetRequest;
import oracle.nosql.driver.ops.GetResult;
import oracle.nosql.driver.ops.PutRequest;
import oracle.nosql.driver.ops.PutResult;
import oracle.nosql.driver.ops.TableLimits;
import oracle.nosql.driver.ops.TableRequest;
import oracle.nosql.driver.values.ArrayValue;
import oracle.nosql.driver.values.JsonNullValue;
import oracle.nosql.driver.values.MapValue;

import org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLConnection;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

import org.eclipse.persistence.testing.models.jpa.nosql.DataTypesEntity;
import org.eclipse.persistence.testing.models.jpa.nosql.NativeQueryEntity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;
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
    private static final String FIELD_JSON_STRING = "{\"a\": 1.006, \"b\": null," +
            "\"bool\" : true, \"map\": {\"m1\": 5}," +
            "\"ar\" : [1,2.7,3]}";
    private static final MapValue FIELD_JSON_OBJECT = new MapValue()
            .put("a", 1.006)
            .put("b", JsonNullValue.getInstance())
            .put("bool", true)
            .put("map", new MapValue()
                    .put("m1", 5))
            .put("ar", new ArrayValue()
                    .add(1)
                    .add(2)
                    .add(7)
                    .add(3));
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
            assertNotNull(dataTypesEntity);
            assertDataTypesEntity(dataTypesEntity, ID);
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
            assertTrue(!testEntities.isEmpty());
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
            DataTypesEntity dataTypesEntity = (DataTypesEntity)query.getSingleResult();
            assertNotNull(dataTypesEntity);
            assertDataTypesEntity(dataTypesEntity, ID);
            LOG.log(SessionLog.INFO, String.format("Entity by DataTypesEntity.findById query:\t%s", dataTypesEntity));
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
    public void testJPAQueryFindByIdAndName() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PU_UNIT_NAME);
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            Query query = em.createNamedQuery("DataTypesEntity.findByIdAndName", DataTypesEntity.class);
            query.setParameter("id", ID);
            query.setParameter("name", FIELD_STRING);
            DataTypesEntity dataTypesEntity = (DataTypesEntity)query.getSingleResult();
            assertNotNull(dataTypesEntity);
            assertDataTypesEntity(dataTypesEntity, ID);
            LOG.log(SessionLog.INFO, String.format("Entity by DataTypesEntity.findByIdAndName query:\t%s", dataTypesEntity));
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
    public void testJPANativeQueryFindById() {
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PU_UNIT_NAME);
            EntityManager em = entityManagerFactory.createEntityManager();
        try {
            Query query = em.createNamedQuery("NativeQueryEntity.findByIdSQLNativeQueryEntity", NativeQueryEntity.class);
            query.setParameter("id", ID);
            NativeQueryEntity nativeQueryEntity = (NativeQueryEntity)query.getSingleResult();
            assertNativeQueryEntity(nativeQueryEntity, ID);
            LOG.log(SessionLog.INFO, String.format("Entity by NativeQueryEntity.findByIdSQLNativeQueryEntity query:\t%s", nativeQueryEntity));
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
        dataTypesEntity.setFieldJsonString(FIELD_JSON_STRING);
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
        //Update col_json_object with MapValue
        updateRow(noSQLHandle);
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
                "col_json_string JSON, " +
                "col_json_object JSON, " +
                "col_null STRING, " +
                "col_string STRING, " +
                "col_timestamp TIMESTAMP(3), " +
                "PRIMARY KEY(id))";
        TableLimits limits = new TableLimits(1, 2, 1);
        TableRequest tableRequest = new TableRequest().setStatement(createTableDDL).setTableLimits(limits);
        LOG.log(SessionLog.INFO, String.format("Creating table:\t%s", TABLE_NAME));
        handle.doTableRequest(tableRequest, 60000, 2024);
        LOG.log(SessionLog.INFO, String.format("Table \t%s is active", TABLE_NAME));
    }

    private static void dropTable(NoSQLHandle handle) {
        /* Drop the table and wait for the table to move to dropped state */
        LOG.log(SessionLog.INFO, String.format("Dropping table:\t%s", TABLE_NAME));
        TableRequest tableRequest = new TableRequest().setStatement("DROP TABLE IF EXISTS " + TABLE_NAME);
        handle.doTableRequest(tableRequest, 60000, 2024);
        LOG.log(SessionLog.INFO, String.format("Table \t%s has been dropped", TABLE_NAME));
    }

    private static void updateRow(NoSQLHandle handle) {
        // Fetch a row modify and update it back to DB
        MapValue key = new MapValue().put("id", ID);
        GetRequest getRequest = new GetRequest().setKey(key).setTableName(TABLE_NAME);
        GetResult getResult = handle.get(getRequest);
        MapValue value = getResult.getValue();
        value.put("col_json_object", FIELD_JSON_OBJECT);
        PutRequest putRequest = new PutRequest().setValue(value).setTableName(TABLE_NAME);
        putRequest.setOption(PutRequest.Option.IfPresent);
        PutResult putResult = handle.put(putRequest);
        if (putResult.getVersion() != null) {
            LOG.log(SessionLog.INFO, String.format("Updated row with ID: \t%s", value));
        } else {
            LOG.log(SessionLog.INFO,"Update failed");
        }
    }

    private void assertDataTypesEntity(DataTypesEntity dataTypesEntity, long id) {
        if (dataTypesEntity.getId() == id) {
            assertEquals(ID, dataTypesEntity.getId());
            assertArrayEquals(FIELD_BINARY, dataTypesEntity.getFieldBinary());
            assertEquals(FIELD_BOOLEAN, dataTypesEntity.isFieldBoolean());
            assertEquals(FIELD_JSON_STRING, dataTypesEntity.getFieldJsonString());
            JsonObject jsonObjectFromStringField = Json.createReader(new StringReader(dataTypesEntity.getFieldJsonString())).readObject();
            assertNotNull(jsonObjectFromStringField);
            assertEquals(FIELD_JSON_OBJECT.toJson(), dataTypesEntity.getFieldJsonObject());
            JsonObject jsonObjectFromObjectField = Json.createReader(new StringReader(dataTypesEntity.getFieldJsonObject())).readObject();
            assertNotNull(jsonObjectFromObjectField);
            assertNull(dataTypesEntity.getFieldNull());
            assertEquals(FIELD_STRING, dataTypesEntity.getFieldString());
            assertEquals(FIELD_TIMESTAMP, dataTypesEntity.getFieldTimestamp());
        }
    }

    private void assertNativeQueryEntity(NativeQueryEntity nativeQueryEntity, long id) {
        if (nativeQueryEntity.getId() == id) {
            assertEquals(ID, nativeQueryEntity.getId());
            assertEquals("{\"m1\":5}", nativeQueryEntity.getComponent());
        }
    }
}

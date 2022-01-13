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
//     13/01/2022-4.0.0 Tomas Kraus - 1391: JSON support in JPA
package org.eclipse.persistence.jpa.json;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.json.model.JsonArrayEntity;
import org.eclipse.persistence.jpa.json.model.JsonEntity;
import org.eclipse.persistence.jpa.json.model.JsonObjectEntity;
import org.eclipse.persistence.jpa.json.model.JsonTestConverter;
import org.eclipse.persistence.jpa.json.model.JsonValueWithConverter;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestJson implements JsonTestConverter.ConverterStatus {

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {
                    JsonEntity.class,
                    JsonObjectEntity.class,
                    JsonArrayEntity.class,
                    JsonValueWithConverter.class,
                    JsonTestConverter.class
            },
            properties = {
                    @Property(name = "eclipselink.cache.shared.default", value = "false"),
                    // This property remaps String from VARCHAR->NVARCHAR(or db equivalent)
                    @Property(name = "eclipselink.target-database-properties",
                            value = "UseNationalCharacterVaryingTypeForString=true"),
            })

    private EntityManagerFactory emf;

    @Test
    public void testCreateJsonObjectInValueField() {
        EntityManager em = emf.createEntityManager();

        JsonValue value = Json.createObjectBuilder()
                .add("id", "1001")
                .add("name", "Joe Wright")
                .add("age", 49)
                .build();

        try {
            em.getTransaction().begin();
            JsonEntity e = new JsonEntity(1001, value);
            em.persist(e);
            em.flush();
            em.getTransaction().commit();
            em.clear();

            JsonEntity dbValue = em.createQuery("SELECT v FROM JsonEntity v WHERE v.id=:id", JsonEntity.class)
                    .setParameter("id", e.getId())
                    .getSingleResult();
            Assert.assertEquals(value, dbValue.getValue());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testCreateJsonArrayInValueField() {
        EntityManager em = emf.createEntityManager();

        JsonValue value = Json.createArrayBuilder()
                .add("JsonString")
                .add(42)
                .add(JsonValue.FALSE)
                .build();

        try {
            em.getTransaction().begin();
            JsonEntity e = new JsonEntity(1002, value);
            em.persist(e);
            em.flush();
            em.getTransaction().commit();
            em.clear();

            JsonEntity dbValue = em.createQuery("SELECT v FROM JsonEntity v WHERE v.id=:id", JsonEntity.class)
                    .setParameter("id", e.getId())
                    .getSingleResult();
            Assert.assertEquals(value, dbValue.getValue());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testCreateJsonObjectField() {
        EntityManager em = emf.createEntityManager();

        JsonObject value = Json.createObjectBuilder()
                .add("id", "1003")
                .add("name", "Joe Wright")
                .add("age", 49)
                .build();

        try {
            em.getTransaction().begin();
            JsonObjectEntity e = new JsonObjectEntity(1003, value);
            em.persist(e);
            em.flush();
            em.getTransaction().commit();
            em.clear();

            JsonObjectEntity dbValue = em.createQuery(
                    "SELECT v FROM JsonObjectEntity v WHERE v.id=:id", JsonObjectEntity.class)
                    .setParameter("id", e.getId())
                    .getSingleResult();
            Assert.assertEquals(value, dbValue.getValue());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testCreateJsonArrayField() {
        EntityManager em = emf.createEntityManager();

        JsonArray value = Json.createArrayBuilder()
                .add("JsonString")
                .add(42)
                .add(JsonValue.FALSE)
                .build();

        try {
            em.getTransaction().begin();
            JsonArrayEntity e = new JsonArrayEntity(1004, value);
            em.persist(e);
            em.flush();
            em.getTransaction().commit();
            em.clear();

            JsonArrayEntity dbValue = em.createQuery(
                            "SELECT v FROM JsonArrayEntity v WHERE v.id=:id", JsonArrayEntity.class)
                    .setParameter("id", e.getId())
                    .getSingleResult();
            Assert.assertEquals(value, dbValue.getValue());
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    private boolean usedToDatabaseColumn = false;
    private boolean usedToEntityAttribute = false;

    @Override
    public void usedToDatabaseColumn() {
        usedToDatabaseColumn = true;
    }

    @Override
    public void usedToEntityAttribute() {
        usedToEntityAttribute = true;
    }

    @Test
    public void testAnnotationconverterUsage() {
        EntityManager em = emf.createEntityManager();

        JsonValue value = Json.createObjectBuilder()
                .add("id", "1005")
                .add("name", "Joe Wright")
                .add("age", 49)
                .build();

        JsonTestConverter.setConverterStatus(this);

        try {
            em.getTransaction().begin();
            JsonValueWithConverter e = new JsonValueWithConverter(1005, value);
            em.persist(e);
            em.flush();
            em.getTransaction().commit();
            em.clear();

            JsonValueWithConverter dbValue = em.createQuery(
                    "SELECT v FROM JsonValueWithConverter v WHERE v.id=:id", JsonValueWithConverter.class)
                    .setParameter("id", e.getId())
                    .getSingleResult();
            Assert.assertEquals(value, dbValue.getValue());
            Assert.assertTrue(usedToDatabaseColumn);
            Assert.assertTrue(usedToEntityAttribute);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testSelectJsonInWhereCondition() {
        EntityManager em = emf.createEntityManager();

        JsonValue value = Json.createObjectBuilder()
                .add("id", "1006")
                .build();
        try {
            em.getTransaction().begin();
            JsonEntity e = new JsonEntity(1006, value);
            em.persist(e);
            em.flush();
            em.getTransaction().commit();
            em.clear();
            JsonEntity dbValue = em.createQuery(
                    "SELECT v FROM JsonEntity v WHERE v.value = :value", JsonEntity.class)
                    .setParameter("value", value)
                    .getSingleResult();
            Assert.assertEquals(value, dbValue.getValue());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

}

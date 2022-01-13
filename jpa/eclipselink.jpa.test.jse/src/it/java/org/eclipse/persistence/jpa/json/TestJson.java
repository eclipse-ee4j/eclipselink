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
import jakarta.json.JsonValue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.json.model.JsonEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestJson {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { JsonEntity.class }, properties = {
            @Property(name = "eclipselink.cache.shared.default", value = "false"),
            // This property remaps String from VARCHAR->NVARCHAR(or db equivalent)
            @Property(name = "eclipselink.target-database-properties",
                    value = "UseNationalCharacterVaryingTypeForString=true"), })

    private EntityManagerFactory emf;

    @Test
    public void testCreateJsonField() {
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
            em.getTransaction().commit();

            JsonEntity dbValue = em.createQuery("SELECT v FROM JsonEntity v WHERE v.id=:id", JsonEntity.class)
                    .setParameter("id", e.getId()).getSingleResult();
            Assert.assertEquals(value, dbValue.getValue());

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

}
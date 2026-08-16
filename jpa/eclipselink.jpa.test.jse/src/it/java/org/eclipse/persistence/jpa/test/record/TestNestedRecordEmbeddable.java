/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.test.record;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.record.model.LocationEntity;
import org.eclipse.persistence.jpa.test.record.model.NestedActivityRecord;
import org.eclipse.persistence.jpa.test.record.model.NestedCargoEntity;
import org.eclipse.persistence.jpa.test.record.model.NestedDeliveryRecord;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for a nested {@code @Embedded} record used inside another {@code @Embeddable} record.
 */
@RunWith(EmfRunner.class)
public class TestNestedRecordEmbeddable {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = {
            NestedCargoEntity.class, NestedDeliveryRecord.class, NestedActivityRecord.class, LocationEntity.class })
    private EntityManagerFactory emf;

    @Test
    public void testPersistNestedEmbeddedRecord() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            LocationEntity location = new LocationEntity(100L, "CNHKG");
            em.persist(location);

            NestedActivityRecord activity = new NestedActivityRecord("LOAD", location);
            NestedDeliveryRecord delivery = new NestedDeliveryRecord("IN_TRANSIT", activity);
            NestedCargoEntity cargo = new NestedCargoEntity(1L, delivery, "ABC123");

            em.persist(cargo);
            em.getTransaction().commit();
            em.clear();

            NestedCargoEntity found = em.find(NestedCargoEntity.class, 1L);
            assertNotNull("Cargo should be found after persist", found);
            assertEquals("ABC123", found.getTrackingId());
            assertNotNull("Nested delivery should not be null", found.getDelivery());
            assertEquals("LOAD", found.getDelivery().nextExpectedActivity().type());
            assertEquals("CNHKG", found.getDelivery().nextExpectedActivity().location().getName());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}

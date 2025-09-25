/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.jpa.persistence32;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import junit.framework.Test;
import org.eclipse.persistence.testing.models.jpa.persistence32.SequenceGeneratorEntity;
import org.eclipse.persistence.testing.models.jpa.persistence32.TableGeneratorEntity;


public class TableSequenceGeneratorTest extends AbstractSuite {

    private final String NAME = "abcde";

    public static Test suite() {
        return suite(
                "StaticInnerEntitiesTest",
                new TableSequenceGeneratorTest("testTableGeneratorWithoutName"),
                new TableSequenceGeneratorTest("testSequenceGeneratorWithoutName")
        );
    }

    public TableSequenceGeneratorTest() {
    }

    public TableSequenceGeneratorTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "persistence32";
    }

    @Override
    protected void suiteSetUp() {
        super.suiteSetUp();
    }

    public void testTableGeneratorWithoutName() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                TableGeneratorEntity entity = new TableGeneratorEntity();
                entity.setName(NAME);
                em.persist(entity);
                et.commit();
                em.clear();
                TableGeneratorEntity fetchedEntity = em.find(TableGeneratorEntity.class, entity.getId());
                assertEquals(entity, fetchedEntity);
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    public void testSequenceGeneratorWithoutName() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                SequenceGeneratorEntity entity = new SequenceGeneratorEntity();
                entity.setName(NAME);
                em.persist(entity);
                et.commit();
                em.clear();
                SequenceGeneratorEntity fetchedEntity = em.find(SequenceGeneratorEntity.class, entity.getId());
                assertEquals(entity, fetchedEntity);
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }
}

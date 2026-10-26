/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.entityagent;

import java.util.List;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.persistence.jpa.entityagent.model.AgentValidatedRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestEntityAgentValidation {
    @Emf(name = "entityagent-validation-default", createTables = DDLGen.DROP_CREATE,
            classes = AgentValidatedRecord.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "CALLBACK"))
    private EntityManagerFactory emf;

    @Emf(name = "entityagent-validation-groups", createTables = DDLGen.CREATE_UPDATE,
            classes = AgentValidatedRecord.class, properties = {
            @Property(name = "jakarta.persistence.validation.mode", value = "CALLBACK"),
            @Property(name = "jakarta.persistence.validation.group.pre-insert", value =
                    " org.eclipse.persistence.jpa.entityagent.model.AgentValidatedRecord$InsertChecks , "
                    + "org.eclipse.persistence.jpa.entityagent.model.AgentValidatedRecord$ExtraChecks "),
            @Property(name = "jakarta.persistence.validation.group.pre-update", value = ""),
            @Property(name = "jakarta.persistence.validation.group.pre-upsert", value = ""),
            @Property(name = "jakarta.persistence.validation.group.pre-delete", value =
                    "org.eclipse.persistence.jpa.entityagent.model.AgentValidatedRecord$DeleteChecks")})
    private EntityManagerFactory groupsEmf;

    @Emf(name = "entityagent-validation-none", createTables = DDLGen.CREATE_UPDATE,
            classes = AgentValidatedRecord.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory disabledEmf;

    @Before
    public void clearEvents() {
        AgentValidatedRecord.EVENTS.clear();
    }

    @Test
    public void insertValidationFollowsUserCallbackAndSuppressesSuccessfulPost() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentValidatedRecord invalid = new AgentValidatedRecord(401, "x");
            assertThrows(ConstraintViolationException.class, () -> agent.insert(invalid));
            assertEquals(List.of("pre-insert"), AgentValidatedRecord.EVENTS);
            assertNull(agent.find(AgentValidatedRecord.class, 401));
            invalid.repairInCallback = true;
            AgentValidatedRecord.EVENTS.clear();
            agent.insert(invalid);
            assertEquals(List.of("pre-insert", "post-insert"), AgentValidatedRecord.EVENTS);
            assertEquals("repaired", agent.get(AgentValidatedRecord.class, 401).name);
            agent.delete(invalid);
        }
    }

    @Test
    public void defaultUpdateAndUpsertValidateAndDeleteDoesNot() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentValidatedRecord record = new AgentValidatedRecord(402, "valid");
            agent.insert(record);
            record.name = "x";
            assertThrows(ConstraintViolationException.class, () -> agent.update(record));
            assertEquals("valid", agent.get(AgentValidatedRecord.class, 402).name);
            assertThrows(ConstraintViolationException.class, () -> agent.upsert(record));
            assertEquals("valid", agent.get(AgentValidatedRecord.class, 402).name);
            agent.delete(record);
            assertNull(agent.find(AgentValidatedRecord.class, 402));
        }
    }

    @Test
    public void explicitGroupsWhitespaceAndEmptyLists() {
        try (EntityAgent agent = groupsEmf.createEntityAgent()) {
            AgentValidatedRecord record = new AgentValidatedRecord(403, "x");
            record.extraCheck = "x";
            assertThrows(ConstraintViolationException.class, () -> agent.insert(record));
            record.extraCheck = "valid";
            agent.insert(record);
            record.insertCheck = "x";
            agent.update(record);
            agent.upsert(record);
            record.deleteCheck = "x";
            assertThrows(ConstraintViolationException.class, () -> agent.delete(record));
            record.deleteCheck = "valid";
            agent.delete(record);
        }
    }

    @Test
    public void noneModeKeepsCallbacksAndSkipsValidation() {
        try (EntityAgent agent = disabledEmf.createEntityAgent()) {
            AgentValidatedRecord record = new AgentValidatedRecord(404, "x");
            agent.insert(record);
            assertEquals(List.of("pre-insert", "post-insert"), AgentValidatedRecord.EVENTS);
            agent.delete(record);
        }
    }

    @Test
    public void bulkStatementDoesNotValidateOrCallEntityListeners() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentValidatedRecord record = new AgentValidatedRecord(405, "valid");
            agent.insert(record);
            AgentValidatedRecord.EVENTS.clear();
            agent.getTransaction().begin();
            assertEquals(1, agent.createStatement(
                    "UPDATE AgentValidatedRecord r SET r.name = 'x' WHERE r.id = 405").execute());
            agent.getTransaction().commit();
            assertTrue(AgentValidatedRecord.EVENTS.isEmpty());
            assertEquals("x", agent.get(AgentValidatedRecord.class, 405).name);
            agent.delete(record);
        }
    }

    @Test
    public void entityManagerDefaultValidationOccursAtDatabaseInsert() {
        try (EntityManager manager = emf.createEntityManager()) {
            manager.getTransaction().begin();
            AgentValidatedRecord record = new AgentValidatedRecord(406, "x");
            manager.persist(record);
            assertTrue(AgentValidatedRecord.EVENTS.isEmpty());
            assertThrows(ConstraintViolationException.class, manager::flush);
            assertTrue(AgentValidatedRecord.EVENTS.contains("pre-insert"));
            assertFalse(AgentValidatedRecord.EVENTS.contains("post-insert"));
            manager.getTransaction().rollback();
        }
    }
}

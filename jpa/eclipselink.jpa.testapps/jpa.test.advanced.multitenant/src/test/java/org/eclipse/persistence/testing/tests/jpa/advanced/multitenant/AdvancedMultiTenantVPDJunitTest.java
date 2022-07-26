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

package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SubTask;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Task;

public class AdvancedMultiTenantVPDJunitTest extends AdvancedMultiTenantJunitBase {

    private static boolean skip = false;

    public AdvancedMultiTenantVPDJunitTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() { return "multi-tenant-vpd"; }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenantVPDJunitTest");

        suite.addTest(new AdvancedMultiTenantVPDJunitTest("testSetup"));
        suite.addTest(new AdvancedMultiTenantVPDJunitTest("testVPDEMPerTenant"));

        return suite;
    }

    public EntityManager createVPDEntityManager(){
        return createEntityManager(getPersistenceUnitName());
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    @Override
    public void testSetup() {
        String db = (String) getEntityManagerFactory(getPersistenceUnitName()).getProperties().get(PersistenceUnitProperties.TARGET_DATABASE);
        if (!(db != null && db.toLowerCase().contains("oracle"))) {
            warning("VPD tests currently run only on an Oracle platform");
            skip = true;
            return;
        }
        super.testSetup();
    }

    /**
     * This test will try to DDL generate on deploy. Meaning once we try to
     * access the VPD PU we'll get an exception if we are not an oracle platform
     * so check before throwing an exception.
     *
     * VPD is currently supported only on Oracle.
     */
    public void testVPDEMPerTenant() {
        if (skip) {
            return;
        }
        EntityManager em1 = null;
        EntityManager em2 = null;

        try {
            em1 = createVPDEntityManager();
            em1.setProperty("tenant.id", "bsmith@here.com");

            em2 = createVPDEntityManager();
            em2.setProperty("tenant.id", "gdune@there.ca");

            testInsertTask(em1, "blah", false);
            testInsertTask(em2, "halb", false);

            assertEquals("Found more than one result", 1, em1.createQuery("Select t from Task t").getResultList().size());
            assertEquals("Found more than one result", 1, em2.createQuery("Select t from Task t").getResultList().size());

            Task task1 = testInsertTaskWithOneSubtask(em1, "Rock that Propsal", false, "Write Proposal", false);
            assertNotNull(em1.find(Task.class, task1.getId()));
            assertNull(em2.find(Task.class, task1.getId())); // negative test

            Task task3 = testInsertTask(em2, "mow lawn", true);
            assertNull(em1.find(Task.class, task3.getId())); // negative test
            assertNotNull(em2.find(Task.class, task3.getId()));

            SubTask task4 = testInsertSubTaskObject(em1, "SubTask Object Creation", true);
            assertNotNull(em1.find(SubTask.class, task4.getId()));
            assertNull(em2.find(SubTask.class, task4.getId())); // negative test

        } catch (RuntimeException e) {
            if (em1 != null && isTransactionActive(em1)){
                rollbackTransaction(em1);
            }

            if (em2 != null && isTransactionActive(em2)){
                rollbackTransaction(em2);
            }

            throw e;
        } finally {
            if (em1 != null) {
                closeEntityManager(em1);
            }

            if (em2 != null) {
                closeEntityManager(em2);
            }
        }
    }

    private Task testInsertTask(EntityManager em, String description, boolean isCompleted) {
        beginTransaction(em);
        Task task = new Task();
        task.setDescription(description);
        task.setCompleted(isCompleted);
        em.persist(task);
        commitTransaction(em);
        return task;
    }

    private Task testInsertTaskWithOneSubtask(EntityManager em, String description, boolean isCompleted, String subtaskDesc, boolean isSubtaskCompleted) {
        beginTransaction(em);
        Task task = new Task();
        Task subtask = new Task();
        task.setDescription(description);
        task.setCompleted(isCompleted);
        subtask.setDescription(subtaskDesc);
        subtask.setCompleted(isSubtaskCompleted);
        task.addSubtask(subtask);
        em.persist(subtask);
        em.persist(task);
        commitTransaction(em);
        return task;
    }

    private SubTask testInsertSubTaskObject(EntityManager em, String description, boolean isCompleted) {
        beginTransaction(em);
        SubTask subTask = new SubTask();
        subTask.setDescription(description);
        subTask.setCompleted(isCompleted);
        em.persist(subTask);
        commitTransaction(em);
        return subTask;
    }
}

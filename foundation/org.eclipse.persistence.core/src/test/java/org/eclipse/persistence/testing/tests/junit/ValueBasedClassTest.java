/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.sessions.CommitManager;
import org.eclipse.persistence.internal.jpa.jpql.HermesParser;
import org.eclipse.persistence.queries.JPAQueryBuilder;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValueBasedClassTest {

    @Test
    void independentlyConstructedProjectsHaveDistinctMonitors() throws Exception {
        assertNotSame(monitor(new Project(), Project.class, "descriptorsLock"),
                monitor(new Project(), Project.class, "descriptorsLock"));
    }

    @Test
    void independentlyConstructedPlatformsHaveDistinctMonitors() throws Exception {
        assertNotSame(monitor(new DatasourcePlatform(), DatasourcePlatform.class, "sequencesLock"),
                monitor(new DatasourcePlatform(), DatasourcePlatform.class, "sequencesLock"));
    }

    @Test
    void unrelatedProjectDoesNotBlockDescriptorAddition() throws Exception {
        Project first = new Project(new DatabaseLogin());
        Project second = new Project(new DatabaseLogin());
        RelationalDescriptor descriptor = descriptor("independent");
        whileHolding(monitor(first, Project.class, "descriptorsLock"),
                () -> second.addDescriptor(descriptor, new DatabaseSessionImpl(second)));
        assertSame(descriptor, second.getAliasDescriptors().get("independent"));
    }

    @Test
    void unrelatedPlatformDoesNotBlockSequenceAddition() throws Exception {
        DatasourcePlatform first = new DatasourcePlatform();
        DatasourcePlatform second = new DatasourcePlatform();
        TableSequence sequence = new TableSequence("independent", 1);
        whileHolding(monitor(first, DatasourcePlatform.class, "sequencesLock"),
                () -> second.addSequence(sequence));
        assertSame(sequence, second.getSequence("independent"));
    }

    @Test
    void projectClonesPreserveSharedMonitorAndDescriptorStateAfterSerialization() throws Exception {
        Project original = new Project(new DatabaseLogin());
        // Configure the supported supplier with a serializable implementation.
        // The default query-builder supplier is independently nonserializable.
        original.setQueryBuilderSupplier((Supplier<JPAQueryBuilder> & Serializable) HermesParser::new);
        original.addDescriptor(descriptor("original"));
        Project clone = original.clone();
        assertSame(monitor(original, Project.class, "descriptorsLock"),
                monitor(clone, Project.class, "descriptorsLock"));
        assertSame(original.getOrderedDescriptors(), clone.getOrderedDescriptors());
        Object[] restored = roundTrip(new Object[] {original, clone});
        Project first = (Project) restored[0];
        Project second = (Project) restored[1];
        assertSame(monitor(first, Project.class, "descriptorsLock"),
                monitor(second, Project.class, "descriptorsLock"));
        assertNotSame(monitor(original, Project.class, "descriptorsLock"),
                monitor(first, Project.class, "descriptorsLock"));
        RelationalDescriptor added = descriptor("restored");
        second.addDescriptor(added, new DatabaseSessionImpl(second));
        assertSame(added, first.getAliasDescriptors().get("restored"));
    }

    @Test
    void platformClonesPreserveMonitorAndIndependentSequencesAfterSerialization() throws Exception {
        DatasourcePlatform original = new DatasourcePlatform();
        original.addSequence(new TableSequence("original", 1));
        DatasourcePlatform clone = (DatasourcePlatform) original.clone();
        assertSame(monitor(original, DatasourcePlatform.class, "sequencesLock"),
                monitor(clone, DatasourcePlatform.class, "sequencesLock"));
        assertNotSame(original.getSequence("original"), clone.getSequence("original"));
        Object[] restored = roundTrip(new Object[] {original, clone});
        DatasourcePlatform first = (DatasourcePlatform) restored[0];
        DatasourcePlatform second = (DatasourcePlatform) restored[1];
        assertSame(monitor(first, DatasourcePlatform.class, "sequencesLock"),
                monitor(second, DatasourcePlatform.class, "sequencesLock"));
        assertNotSame(monitor(original, DatasourcePlatform.class, "sequencesLock"),
                monitor(first, DatasourcePlatform.class, "sequencesLock"));
        TableSequence added = new TableSequence("restored", 1);
        second.addSequence(added);
        assertSame(added, second.removeSequence("restored"));
        assertFalse(first.getSequences().containsKey("restored"));
        assertTrue(first.getSequences().containsKey("original"));
        assertTrue(second.getSequences().containsKey("original"));
    }

    @Test
    void commitPredicatesAcceptEqualButDistinctStates() {
        TestCommitManager manager = new TestCommitManager();
        Object object = new Object();
        for (int state = 1; state <= 4; state++) {
            Integer value = distinctInteger(state);
            assertNotSame(Integer.valueOf(state), value);
            manager.setState(object, value);
            assertEquals(state == 1, manager.isCommitInPreModify(object));
            assertEquals(state == 2, manager.isCommitInPostModify(object));
            assertEquals(state == 3, manager.isCommitCompleted(object));
            assertEquals(state >= 2, manager.isCommitCompletedInPostOrIgnore(object));
            assertTrue(manager.isProcessedCommit(object));
        }
    }

    @Test
    void commitPredicatesRejectMissingAndUnknownStates() {
        TestCommitManager manager = new TestCommitManager();
        Object object = new Object();
        for (Integer state : new Integer[] {null, 5}) {
            manager.setState(object, state);
            assertFalse(manager.isCommitInPreModify(object));
            assertFalse(manager.isCommitInPostModify(object));
            assertFalse(manager.isCommitCompleted(object));
            assertFalse(manager.isCommitCompletedInPostOrIgnore(object));
        }
    }

    @Test
    @SuppressWarnings("removal")
    void procedureArgumentsRenderIdenticallyWithDistinctDirections() {
        StoredProcedureDefinition procedure = new StoredProcedureDefinition();
        procedure.setName("VALUE_BASED_PROCEDURE");
        procedure.addArgument("INPUT_VALUE", Integer.class);
        procedure.addOutputArgument("OUTPUT_VALUE", Integer.class);
        procedure.addInOutputArgument("BOTH_VALUES", Integer.class);
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
        String expected = procedure.buildCreationWriter(session, new StringWriter()).toString();
        assertTrue(expected.contains("INPUT_VALUE"));
        assertTrue(expected.contains("OUTPUT_VALUE"));
        assertTrue(expected.contains("BOTH_VALUES"));
        for (int index = 0; index < procedure.getArgumentTypes().size(); index++) {
            Integer original = procedure.getArgumentTypes().get(index);
            Integer distinct = distinctInteger(original);
            assertNotSame(original, distinct);
            procedure.getArgumentTypes().set(index, distinct);
        }
        assertEquals(expected, procedure.buildCreationWriter(session, new StringWriter()).toString());
        procedure.getArgumentTypes().set(0, null);
        assertFalse(procedure.buildCreationWriter(session, new StringWriter()).toString().contains("INPUT_VALUE"));
    }

    private static Object monitor(Object owner, Class<?> type, String name) throws Exception {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(owner);
    }

    private static RelationalDescriptor descriptor(String alias) {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(String.class);
        descriptor.setAlias(alias);
        return descriptor;
    }

    private static Object[] roundTrip(Object[] value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (Object[]) input.readObject();
        }
    }

    private static void whileHolding(Object monitor, Runnable operation) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch started = new CountDownLatch(1);
        try {
            synchronized (monitor) {
                Future<?> result = executor.submit(() -> {
                    started.countDown();
                    operation.run();
                });
                assertTrue(started.await(5, TimeUnit.SECONDS));
                result.get(5, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    @SuppressWarnings("removal")
    private static Integer distinctInteger(int value) {
        // Deliberately bypass Integer caches to exercise value rather than identity equality.
        return new Integer(value);
    }

    private static class TestCommitManager extends CommitManager {
        TestCommitManager() {
            super(null);
        }

        void setState(Object object, Integer state) {
            getCommitState().put(object, state);
        }
    }
}

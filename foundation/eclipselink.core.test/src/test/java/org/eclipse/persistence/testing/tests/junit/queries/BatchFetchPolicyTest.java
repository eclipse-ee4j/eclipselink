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
package org.eclipse.persistence.testing.tests.junit.queries;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.internal.indirection.BatchValueHolder;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.queries.BatchFetchPolicy;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class BatchFetchPolicyTest {

    private final DatabaseMapping first = new DirectToFieldMapping();
    private final DatabaseMapping second = new DirectToFieldMapping();

    @Test
    public void mappingRowsHaveIndependentContainers() {
        BatchFetchPolicy policy = new BatchFetchPolicy(BatchFetchType.IN);
        AbstractRecord firstRow = row("FIRST_ID", 1);
        AbstractRecord secondRow = row("SECOND_ID", 2);
        List<AbstractRecord> supplied = new ArrayList<>(List.of(firstRow));
        policy.setAllDataResults(first, supplied, false);
        policy.setAllDataResults(second, List.of(secondRow), false);

        supplied.clear();
        policy.getDataResults(first).clear();
        assertEquals(List.of(firstRow), policy.getAllDataResults(first));
        assertEquals(List.of(), policy.getDataResults(first));
        assertEquals(List.of(secondRow), policy.getAllDataResults(second));
        assertEquals(List.of(secondRow), policy.getDataResults(second));
        assertSame(firstRow, policy.getAllDataResults(first).get(0));
        assertNotSame(policy.getAllDataResults(second), policy.getDataResults(second));
    }

    @Test
    public void cursorAppendPreservesLayoutsAndConsumedRows() {
        BatchFetchPolicy policy = new BatchFetchPolicy(BatchFetchType.IN);
        AbstractRecord raw = row("SCALAR", 100);
        AbstractRecord nextRaw = row("SCALAR", 101);
        AbstractRecord firstRow = row("FIRST_ID", 1);
        AbstractRecord nextFirst = row("FIRST_ID", 2);
        AbstractRecord secondRow = row("SECOND_ID", 3);
        policy.setDataResults(new ArrayList<>(List.of(raw)));
        policy.setAllDataResults(first, List.of(firstRow), false);
        policy.setAllDataResults(second, List.of(secondRow), false);
        policy.setDataResults(first, new ArrayList<>());

        policy.addDataResults(nextRaw);
        assertEquals(List.of(raw, nextRaw), policy.getAllDataResults());
        assertEquals(List.of(firstRow), policy.getAllDataResults(first));
        assertEquals(List.of(), policy.getDataResults(first));
        assertEquals(List.of(secondRow), policy.getDataResults(second));

        policy.setAllDataResults(first, List.of(nextFirst), true);
        assertEquals(List.of(firstRow, nextFirst), policy.getAllDataResults(first));
        assertEquals(List.of(nextFirst), policy.getDataResults(first));
        assertEquals(List.of(secondRow), policy.getAllDataResults(second));
        assertEquals(List.of(secondRow), policy.getDataResults(second));
    }

    @Test
    public void materializedRowsReplacePreviousMappingHistory() {
        BatchFetchPolicy policy = new BatchFetchPolicy(BatchFetchType.IN);
        AbstractRecord previous = row("ID", 1);
        AbstractRecord replacement = row("ID", 2);
        policy.setAllDataResults(first, List.of(previous), false);
        policy.setDataResults(first, new ArrayList<>());
        policy.setAllDataResults(first, List.of(replacement), false);

        assertEquals(List.of(replacement), policy.getAllDataResults(first));
        assertEquals(List.of(replacement), policy.getDataResults(first));
        assertNotSame(policy.getAllDataResults(first), policy.getDataResults(first));
    }

    @Test
    public void listResetDiscardsMappingHistory() {
        BatchFetchPolicy policy = new BatchFetchPolicy(BatchFetchType.IN);
        policy.setAllDataResults(first, List.of(row("OLD_ID", 1)), false);
        AbstractRecord replacement = row("NEW_ID", 2);
        policy.setDataResults(new ArrayList<>(List.of(replacement)));

        assertEquals(List.of(replacement), policy.getAllDataResults(first));
        assertEquals(List.of(replacement), policy.getDataResults(first));
    }

    @Test
    public void mapResetDiscardsMappingHistory() {
        BatchFetchPolicy policy = new BatchFetchPolicy(BatchFetchType.IN);
        policy.setAllDataResults(first, List.of(row("OLD_ID", 1)), false);
        AbstractRecord replacement = row("NEW_ID", 2);
        Map<Object, List<AbstractRecord>> rows = new HashMap<>();
        rows.put(policy, new ArrayList<>(List.of(replacement)));
        policy.setDataResults(rows);

        assertEquals(List.of(replacement), policy.getAllDataResults(first));
        assertEquals(List.of(replacement), policy.getDataResults(first));
    }

    @Test
    public void cloneDoesNotRetainOrMutateMappingExecutionHistory() {
        BatchFetchPolicy policy = new BatchFetchPolicy(BatchFetchType.IN);
        AbstractRecord generic = row("ID", 1);
        AbstractRecord trimmed = row("TRIMMED_ID", 2);
        policy.setDataResults(new ArrayList<>(List.of(generic)));
        policy.setAllDataResults(first, List.of(trimmed), false);

        BatchFetchPolicy clone = policy.clone();
        assertEquals(List.of(generic), clone.getAllDataResults(first));
        assertNotSame(policy.getAllDataResults(), clone.getAllDataResults());
        clone.getDataResults(first).clear();
        clone.setAllDataResults(second, List.of(row("OTHER_ID", 3)), false);
        assertEquals(List.of(generic), policy.getAllDataResults());
        assertEquals(List.of(trimmed), policy.getAllDataResults(first));
        assertEquals(List.of(trimmed), policy.getDataResults(first));
        assertEquals(List.of(generic), policy.getAllDataResults(second));
    }

    @Test
    public void serializationPreservesConfigurationAndBaseIdentity() throws Exception {
        assertEquals(3163373303825174025L, ObjectStreamClass.lookup(BatchFetchPolicy.class).getSerialVersionUID());
        assertEquals(-4955294616212586177L, ObjectStreamClass.lookup(BatchValueHolder.class).getSerialVersionUID());
        BatchFetchPolicy policy = new BatchFetchPolicy(BatchFetchType.IN);
        policy.setSize(2);
        policy.setAttributes(new ArrayList<>(List.of("children", "owner")));
        // Execution rows are transient, including values that cannot be serialized.
        policy.setAllDataResults(first, List.of(row("ID", new Object())), false);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(policy);
        }
        BatchFetchPolicy restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (BatchFetchPolicy) input.readObject();
        }
        assertEquals(BatchFetchType.IN, restored.getType());
        assertEquals(2, restored.getSize());
        assertEquals(policy.getAttributes(), restored.getAttributes());
        assertNotSame(policy.getAttributes(), restored.getAttributes());
        assertNull(restored.getDataResults());
    }

    private static AbstractRecord row(String field, Object value) {
        DatabaseRecord row = new DatabaseRecord();
        row.put(field, value);
        return row;
    }
}

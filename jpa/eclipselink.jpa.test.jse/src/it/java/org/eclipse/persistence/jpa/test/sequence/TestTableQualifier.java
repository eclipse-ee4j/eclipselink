/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2017, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky,lukas - initial implementation
package org.eclipse.persistence.jpa.test.sequence;

import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.sequence.model.EntityWithSchema;
import org.eclipse.persistence.sequencing.TableSequence;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestTableQualifier {

    @Emf(name="tableQualifiers", createTables = DDLGen.NONE, classes = { EntityWithSchema.class })
    private EntityManagerFactory emf;

    /*
     * Test for Bug 445466
     * When a table qualifier is configured on an Entity/TableSequence combination,
     * the SQL is not printed correctly. Programmatically test that an Entity's TableSequence
     * has the expected table name & qualifier information configured.
     */
    @Test
    public void testTableQualifierOnSequencingTableAndEntity() {
        // Access serverSession directly, do not create an EM as the Entity 'EntityWithSchema'
        // has a hard-coded schema for programmatic testing only
        ClassDescriptor descriptor = ((org.eclipse.persistence.jpa.JpaEntityManager)emf.createEntityManager()).getServerSession().getDescriptor(EntityWithSchema.class);
        TableSequence tableSequence = (TableSequence) descriptor.getSequence();
        DatabaseTable table = tableSequence.getTable();

        String tableName = table.getName();
        String tableQualifier = table.getTableQualifier();

        String tsQualifier = tableSequence.getQualifier();
        String tsTableName = tableSequence.getTableName();
        String tsQualifiedTableName = tableSequence.getQualifiedTableName();

        Assert.assertNotNull("Table name should be non-null", tableName);
        Assert.assertTrue("Table name should not be empty", !tableName.isEmpty());
        Assert.assertNotNull("Table qualifier should be non-null", tableQualifier);
        Assert.assertTrue("Table qualifier should not be empty", !tableQualifier.isEmpty());

        Assert.assertEquals("Table Sequence : table name should be equal", tableName, tsTableName);
        Assert.assertEquals("Table Sequence : table qualifier should be equal", tableQualifier, tsQualifier);
        Assert.assertEquals("Table Sequence : qualified table name should be equal", (tableQualifier + "." + tableName), tsQualifiedTableName);
    }
}

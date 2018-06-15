/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.optimisticlocking;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.*;

/**
 * <b>Purpose</b>: <p>
 * <b>Description</b>: <p>
 * <b>Responsibilities</b>:<ul>
 * <li>
 * </ul>
 * @since TopLink 2.0
 * @author Peter Krogh
 */
public class ChangedRow extends LockObject {

    /**
     * ChangedRow constructor comment.
     */
    public ChangedRow() {
        super();
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(ChangedRow.class);
        descriptor.setTableName("CHNG_ROW");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("value", "A_VALUE");
    descriptor.useAllFieldsLocking();

        return descriptor;
    }

    public static ChangedRow example1() {
        ChangedRow cr = new ChangedRow();
        cr.value = "1st";
        return cr;
    }

    public static ChangedRow example2() {
        ChangedRow cr = new ChangedRow();
        cr.value = "2nd";
        return cr;
    }

    public static ChangedRow example3() {
        ChangedRow cr = new ChangedRow();
        cr.value = "3rd";
        return cr;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("CHNG_ROW");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("A_VALUE", String.class, 30);
        definition.addField("VERSION", java.math.BigDecimal.class, 15);

        return definition;
    }

    public void verify(org.eclipse.persistence.testing.framework.TestCase testCase) {
        //not sure what to do here
    }
}

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

public class TimestampInAggregateObject extends LockObject {
    public TimestampVersion version;

    /**
     * LockInObject constructor comment.
     */
    public TimestampInAggregateObject() {
        super();
        version = new TimestampVersion();
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(TimestampInAggregateObject.class);
        descriptor.setTableName("TSAO_LCK");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.useTimestampLocking("AGG_VERSION", false);

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("value", "A_VALUE");

        // SECTION: AGGREGATEOBJECTMAPPING
        org.eclipse.persistence.mappings.AggregateObjectMapping aggregateobjectmapping = new org.eclipse.persistence.mappings.AggregateObjectMapping();
        aggregateobjectmapping.setAttributeName("version");
        aggregateobjectmapping.setIsReadOnly(false);
        aggregateobjectmapping.setReferenceClass(TimestampVersion.class);
        aggregateobjectmapping.setIsNullAllowed(true);
        descriptor.addMapping(aggregateobjectmapping);

        return descriptor;
    }

    public static TimestampInAggregateObject example1() {
        TimestampInAggregateObject tio = new TimestampInAggregateObject();
        tio.value = "1st";
        tio.version = new TimestampVersion();
        return tio;
    }

    public static TimestampInAggregateObject example2() {
        TimestampInAggregateObject tio = new TimestampInAggregateObject();
        tio.value = "2nd";
        tio.version = new TimestampVersion();
        tio.version.setVersionInfo("info2");
        return tio;
    }

    public static TimestampInAggregateObject example3() {
        TimestampInAggregateObject tio = new TimestampInAggregateObject();
        tio.value = "3rd";
        tio.version = new TimestampVersion();
        tio.version.setVersionInfo("info3");
        return tio;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("TSAO_LCK");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("A_VALUE", String.class, 30);
        definition.addField("AGG_VERSION", java.sql.Timestamp.class);
        definition.addField("AGG_INFO", String.class, 30);

        return definition;
    }

    public void verify(org.eclipse.persistence.testing.framework.TestCase testCase) {
        if (this.version.t_id == null) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Value did not get set in object");
        }
    }
}

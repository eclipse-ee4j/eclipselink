/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.optimisticlocking;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.*;

//
public class LockInAggregateObject extends LockObject {
    public ObjectVersion version;

    /**
     * LockInObject constructor comment.
     */
    public LockInAggregateObject() {
        super();
        version = new ObjectVersion();
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(LockInAggregateObject.class);
        descriptor.setTableName("AO_LCK");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.useVersionLocking("AGG_VERSION", false);

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("value", "A_VALUE");

        // SECTION: AGGREGATEOBJECTMAPPING
        org.eclipse.persistence.mappings.AggregateObjectMapping aggregateobjectmapping = new org.eclipse.persistence.mappings.AggregateObjectMapping();
        aggregateobjectmapping.setAttributeName("version");
        aggregateobjectmapping.setIsReadOnly(false);
        aggregateobjectmapping.setReferenceClass(ObjectVersion.class);
        aggregateobjectmapping.setIsNullAllowed(false);
        descriptor.addMapping(aggregateobjectmapping);

        return descriptor;
    }

    public static LockInAggregateObject example1() {
        LockInAggregateObject tio = new LockInAggregateObject();
        tio.value = "1st";
        tio.version = new ObjectVersion();
        return tio;
    }

    public static LockInAggregateObject example2() {
        LockInAggregateObject tio = new LockInAggregateObject();
        tio.value = "2nd";
        tio.version = new ObjectVersion();
        return tio;
    }

    public static LockInAggregateObject example3() {
        LockInAggregateObject tio = new LockInAggregateObject();
        tio.value = "3rd";
        tio.version = new ObjectVersion();
        return tio;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("AO_LCK");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("A_VALUE", String.class, 30);
        definition.addField("AGG_VERSION", java.math.BigDecimal.class);
        definition.addField("AGG_INFO", String.class, 30);

        return definition;
    }

    public void verify(org.eclipse.persistence.testing.framework.TestCase testCase) {
        if (this.version.id == null) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Value did not get set in object");
        }
    }
}

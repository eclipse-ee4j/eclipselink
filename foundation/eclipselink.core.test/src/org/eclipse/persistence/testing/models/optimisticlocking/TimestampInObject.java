/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

public class TimestampInObject extends LockObject {
    public java.sql.Timestamp version;

    public TimestampInObject() {
        super();
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(TimestampInObject.class);
        descriptor.setTableName("TSO_LCK");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.useTimestampLocking("VERSION", false);

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("value", "A_VALUE");
        descriptor.addDirectMapping("version", "VERSION");

        return descriptor;
    }

    public static TimestampInObject example1() {
        TimestampInObject tio = new TimestampInObject();
        tio.value = "1st";
        return tio;
    }

    public static TimestampInObject example2() {
        TimestampInObject tio = new TimestampInObject();
        tio.value = "2nd";
        return tio;
    }

    public static TimestampInObject example3() {
        TimestampInObject tio = new TimestampInObject();
        tio.value = "3rd";
        return tio;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("TSO_LCK");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("A_VALUE", String.class, 30);
        definition.addField("VERSION", java.sql.Timestamp.class);

        return definition;
    }

    public void verify(org.eclipse.persistence.testing.framework.TestCase testCase) {
        if (this.version == null) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Value did not get set in object");
        }
    }
}

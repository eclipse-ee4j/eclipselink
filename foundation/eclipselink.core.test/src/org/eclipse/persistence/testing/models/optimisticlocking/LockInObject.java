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

public class LockInObject extends LockObject {
    public java.lang.Integer version;

    /**
     * LockInObject constructor comment.
     */
    public LockInObject() {
        super();
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(LockInObject.class);
        descriptor.setTableName("OBJ_LCK");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.useVersionLocking("VERSION", false);

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("value", "A_VALUE");
        descriptor.addDirectMapping("version", "VERSION");

        return descriptor;
    }

    public static LockInObject example1() {
        LockInObject lic = new LockInObject();
        lic.value = "1st";
        return lic;
    }

    public static LockInObject example2() {
        LockInObject lic = new LockInObject();
        lic.value = "2nd";
        return lic;
    }

    public static LockInObject example3() {
        LockInObject lic = new LockInObject();
        lic.value = "3rd";
        return lic;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("OBJ_LCK");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("A_VALUE", String.class, 30);
        definition.addField("VERSION", java.math.BigDecimal.class, 15);

        return definition;
    }

    public void verify(org.eclipse.persistence.testing.framework.TestCase testCase) {
        if (this.version == null) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Value did not get set in object");
        }
    }
}

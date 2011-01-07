/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.events;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Address {
    public static int preUpdateCount = 0;
    public String address;
    public Number id;

    public Address() {
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(Address.class);
        descriptor.setTableName("EADDRESS");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("address", "DESCR");
        descriptor.getQueryManager().setUpdateQuery(new org.eclipse.persistence.queries.UpdateObjectQuery("UPDATE EADDRESS SET DESCR = #DESCR WHERE ID = #ID"));

        return descriptor;
    }

    public static Address example1() {
        Address address = new Address();

        address.address = new String("ABC Av, Someplace");
        return address;
    }

    public static Address example2() {
        Address address = new Address();

        address.address = new String("XYZ Av, Whichplace");
        return address;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("EADDRESS");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("DESCR", String.class, 40);

        return definition;
    }

    public String toString() {
        return "Address(" + this.address + ")";
    }
}

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
package org.eclipse.persistence.testing.models.events;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Order {
    public Number id;
    public String sku;
    public int quantity;
    public Customer customer;

    public Order() {
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.events.Order.class);
        descriptor.setTableName("EVENTORDER");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("sku", "SKU");
        descriptor.addDirectMapping("quantity", "QUANTITY");

        org.eclipse.persistence.mappings.OneToOneMapping customerMapping = new org.eclipse.persistence.mappings.OneToOneMapping();
        customerMapping.setAttributeName("customer");
        customerMapping.setReferenceClass(Customer.class);
        customerMapping.dontUseIndirection();
        customerMapping.addForeignKeyFieldName("EVENTORDER.CUSTOMER_ID", "EVENTCUSTOMER.ID");
        descriptor.addMapping(customerMapping);

        return descriptor;
    }

    public static Order example1() {
        Order order = new Order();
        order.sku = "556995655856";
        order.quantity = 5;
        return order;
    }

    public static Order example2() {
        Order order = new Order();
        order.sku = "986899568556";
        order.quantity = 1;
        return order;
    }

    public static Order example3() {
        Order order = new Order();
        order.sku = "887521132264";
        order.quantity = 5000;
        return order;
    }

    public static Order example4() {
        Order order = new Order();
        order.sku = "8858787875854";
        order.quantity = 2;
        return order;
    }

    /**
      * Return a platform independant definition of the database table.
      */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("EVENTORDER");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("QUANTITY", Integer.class, 15);
        definition.addField("SKU", String.class, 40);
        definition.addField("CUSTOMER_ID", java.math.BigDecimal.class, 15);

        return definition;
    }
}

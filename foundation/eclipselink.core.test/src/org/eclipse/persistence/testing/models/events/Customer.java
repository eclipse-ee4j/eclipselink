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
package org.eclipse.persistence.testing.models.events;

import java.util.Vector;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Customer {
    public Address address;
    public Phone phoneNumber;
    public EmailAccount email;
    public CreditCard creditCard;
    public String name;
    public ValueHolderInterface orders;
    public Vector associations;
    public Number id;
    public boolean preWrite;
    public boolean postWrite;
    public boolean preUpdate;

    public Customer() {
        this.associations = new Vector(3);
        this.orders = new ValueHolder(new Vector());
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(Customer.class);
        descriptor.setTableName("EVENTCUSTOMER");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        OneToOneMapping addressMapping = new OneToOneMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(Address.class);
        addressMapping.dontUseIndirection();
        addressMapping.privateOwnedRelationship();
        addressMapping.addForeignKeyFieldName("EVENTCUSTOMER.ADDRESS_ID", "EADDRESS.ID");
        descriptor.addMapping(addressMapping);

        OneToOneMapping phoneMapping = new OneToOneMapping();
        phoneMapping.setAttributeName("phoneNumber");
        phoneMapping.setReferenceClass(Phone.class);
        phoneMapping.dontUseIndirection();
        phoneMapping.privateOwnedRelationship();
        phoneMapping.addForeignKeyFieldName("EVENTCUSTOMER.PHONE_ID", "EPHONE.ID");
        descriptor.addMapping(phoneMapping);

        OneToOneMapping emailMapping = new OneToOneMapping();
        emailMapping.setAttributeName("email");
        emailMapping.setReferenceClass(EmailAccount.class);
        emailMapping.dontUseIndirection();
        emailMapping.privateOwnedRelationship();
        emailMapping.addForeignKeyFieldName("EVENTCUSTOMER.EMAIL_ID", "EMAILACC.ID");
        descriptor.addMapping(emailMapping);

        DirectCollectionMapping associationsMapping = new DirectCollectionMapping();
        associationsMapping.setAttributeName("associations");
        associationsMapping.dontUseIndirection();
        associationsMapping.setReferenceTableName("EASSOCIATIONS");
        associationsMapping.setDirectFieldName("EASSOCIATIONS.DESCRIP");
        associationsMapping.addReferenceKeyFieldName("EASSOCIATIONS.CUSTOMER_ID", "EVENTCUSTOMER.ID");
        descriptor.addMapping(associationsMapping);

        OneToManyMapping ordersMapping = new OneToManyMapping();
        ordersMapping.setAttributeName("orders");
        ordersMapping.setReferenceClass(Order.class);
        ordersMapping.useBasicIndirection();
        ordersMapping.addTargetForeignKeyFieldName("EVENTORDER.CUSTOMER_ID", "EVENTCUSTOMER.ID");
        descriptor.addMapping(ordersMapping);

        AggregateObjectMapping creditMapping = new AggregateObjectMapping();
        creditMapping.setAttributeName("creditCard");
        creditMapping.setReferenceClass(org.eclipse.persistence.testing.models.events.CreditCard.class);
        creditMapping.setIsNullAllowed(true);
        descriptor.addMapping(creditMapping);

        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("name", "NAME");

        return descriptor;
    }

    public static Customer example1() {
        Customer customer = new Customer();
        customer.address = Address.example1();
        customer.email = org.eclipse.persistence.testing.models.events.EmailAccount.example1();
        customer.phoneNumber = Phone.example1();
        ((Vector)customer.orders.getValue()).add(Order.example2());
        ((Vector)customer.orders.getValue()).add(Order.example1());
        customer.associations.add("Mickey Mouse Club");
        customer.associations.add("Canadian Penitentiary System");
        customer.name = "John Lancy";
        customer.creditCard = new CreditCard();
        customer.creditCard.number = "346556544565";
        customer.creditCard.expiry = "04/03";
        return customer;
    }

    public static Customer example2() {
        Customer customer = new Customer();
        customer.address = Address.example2();
        customer.email = org.eclipse.persistence.testing.models.events.EmailAccount.example2();
        customer.phoneNumber = Phone.example2();
        ((Vector)customer.orders.getValue()).add(Order.example3());
        ((Vector)customer.orders.getValue()).add(Order.example4());
        customer.associations.add("Masons");
        customer.associations.add("Illuminaty");
        customer.name = "Bobby Ore";
        customer.creditCard = new CreditCard();
        customer.creditCard.number = "456556544565";
        customer.creditCard.expiry = "03/03";
        return customer;
    }

    /**
      * Return a platform independant definition of the database table.
      */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("EVENTCUSTOMER");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("ADDRESS_ID", java.math.BigDecimal.class, 15);
        definition.addField("PHONE_ID", java.math.BigDecimal.class, 15);
        definition.addField("EMAIL_ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 40);
        definition.addField("CARD_NUMBER", String.class, 12);
        definition.addField("CARD_EXPIRY", String.class, 5);

        return definition;
    }

    /**
      * Return a platform independant definition of the database table.
      */
    public static TableDefinition directCollectionTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("EASSOCIATIONS");

        definition.addField("CUSTOMER_ID", java.math.BigDecimal.class, 15);
        definition.addField("DESCRIP", String.class, 40);

        return definition;
    }
}

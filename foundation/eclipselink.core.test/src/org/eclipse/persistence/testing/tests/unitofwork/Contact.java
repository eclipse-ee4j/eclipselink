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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.math.BigDecimal;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;


public class Contact implements java.io.Serializable {
    public BigDecimal id;
    public String type;
    public MailAddress mailAddress;
    public Person person;

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        OneToOneMapping addressMapping = new OneToOneMapping();
        OneToOneMapping employeeMapping = new OneToOneMapping();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(Contact.class);
        descriptor.setTableName("U_CON");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("type", "TYPE");

        addressMapping.setAttributeName("mailAddress");
        addressMapping.setReferenceClass(MailAddress.class);
        addressMapping.addForeignKeyFieldName("ADDRESS", "ID");
        addressMapping.dontUseIndirection();
        descriptor.addMapping(addressMapping);

        employeeMapping.setAttributeName("person");
        employeeMapping.setReferenceClass(Person.class);
        employeeMapping.addForeignKeyFieldName("EMP_ID", "ID");
        employeeMapping.dontUseIndirection();
        descriptor.addMapping(employeeMapping);

        return descriptor;
    }

    public static Contact example1(Person person) {
        Contact example = new Contact();

        example.type = "Home";
        example.mailAddress = MailAddress.example1();
        example.person = person;

        return example;
    }

    public static Contact example2(Person person) {
        Contact example = new Contact();

        example.type = "Office";
        example.mailAddress = MailAddress.example2();
        example.person = person;

        return example;
    }

    public static Contact example3(Person person) {
        Contact example = new Contact();

        example.type = "Email";
        example.mailAddress = MailAddress.example3();
        example.person = person;

        return example;
    }

    public static Contact example4(Person person) {
        Contact example = new Contact();

        example.type = "Home";
        example.mailAddress = MailAddress.example4();
        example.person = person;

        return example;
    }

    public MailAddress getMailAddress() {
        return mailAddress;
    }

    public void setAddress(MailAddress mailAddress) {
        this.mailAddress = mailAddress;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("U_CON");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("TYPE", String.class, 15);
        definition.addField("ADDRESS", java.math.BigDecimal.class, 15);
        definition.addField("EMP_ID", java.math.BigDecimal.class, 15);

        return definition;
    }
}

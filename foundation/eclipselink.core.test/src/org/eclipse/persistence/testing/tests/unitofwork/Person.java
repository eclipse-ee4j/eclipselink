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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.math.BigDecimal;

import java.util.Vector;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;


public class Person implements java.io.Serializable {
    public String name;
    public Vector contacts;
    public BigDecimal id;
    public BigDecimal luckyNumber;

    public BigDecimal calculateLuckyNumber(Record row, Session session) {
        Number code = (Number)row.get("ID");
        return new BigDecimal(code.doubleValue() * 2.435);
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        OneToManyMapping oneToMany = new OneToManyMapping();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(Person.class);
        descriptor.setTableName("U_EMP");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("name", "NAME");

        oneToMany.setAttributeName("contacts");
        oneToMany.setReferenceClass(Contact.class);
        oneToMany.addTargetForeignKeyFieldName("EMP_ID", "ID");
        oneToMany.dontUseIndirection();
        descriptor.addMapping(oneToMany);

        TransformationMapping trans = new TransformationMapping();
        trans.setIsReadOnly(true);
        trans.dontUseIndirection();
        trans.setAttributeName("luckyNumber");
        trans.setAttributeTransformation("calculateLuckyNumber");
        descriptor.addMapping(trans);
        return descriptor;
    }

    public static Person example1() {
        Person example = new Person();
        Vector contacts = new Vector();

        contacts.addElement(Contact.example1(example));
        contacts.addElement(Contact.example2(example));
        example.name = "Dave";
        example.contacts = contacts;

        return example;
    }

    public static Person example2() {
        Person example = new Person();
        Vector contacts = new Vector();

        contacts.addElement(Contact.example3(example));
        contacts.addElement(Contact.example4(example));
        example.name = "Bob";
        example.contacts = contacts;

        return example;
    }

    public Vector getContacts() {
        return contacts;
    }

    public void addContact(Contact newContact) {
        contacts.addElement(newContact);
        newContact.person = this;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("U_EMP");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 20);
        definition.addField("CONTACT", java.math.BigDecimal.class, 15);

        return definition;
    }
}

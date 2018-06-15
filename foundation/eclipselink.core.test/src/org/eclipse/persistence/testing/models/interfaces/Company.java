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
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.tools.schemaframework.*;

public class Company implements ContactHolder, java.io.Serializable, Cloneable {
    public Number id;
    public String name;
    public Contact contact;
    public Email email;

    public Object clone() {
        Company clone = new Company();
        clone.id = this.id;
        clone.name = this.name;
        if (this.contact != null) {
            clone.contact = (Contact)this.contact.clone();
            clone.contact.setHolder(clone);
        }
        return clone;
    }

    public static Company example1() {
        Company example = new Company();
        example.setName("The Object People");
        example.setContact(Email.example1());
        return example;
    }

    public static Company example2() {
        Company example = new Company();
        example.setName("IBM");
        example.setContact(Phone.example1());
        return example;
    }

    public static Company example3() {
        Company example = new Company();
        example.setName("Microsoft");
        example.setContact(Email.example2());
        return example;
    }

    public Contact getContact() {
        return contact;
    }

    public Number getId() {
        return this.id;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition table = new TableDefinition();

        table.setName("INT_CMP");
        table.addField("ID", java.math.BigDecimal.class, 15);
        table.addField("NAME", String.class, 30);
        table.addField("TYPE", String.class, 10);
        table.addField("CON_ID", java.math.BigDecimal.class, 15);
        table.addField("EMAIL_ID", java.math.BigDecimal.class, 15);

        table.addForeignKeyConstraint("INT_CMP_INT_EML", "EMAIL_ID", "E_ID", "INT_EML");

        return table;
    }
}

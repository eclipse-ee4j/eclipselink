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

public class Email implements Contact, java.io.Serializable {
    public String address;
    public Number id;
    public ContactHolder employee;

    public Object clone() {
        Email object = new Email();
        object.address = this.address;
        object.id = this.id;
        return object;
    }

    public static Email example1() {
        Email example = new Email();
        example.setAddress("example1@objectpeople.com");
        return example;
    }

    public static Email example2() {
        Email example = new Email();
        example.setAddress("example2@microsoft.com");
        return example;
    }

    public static Email example3() {
        Email example = new Email();
        example.setAddress("example3@objectpeople.com");
        return example;
    }

    public ContactHolder getEmp() {
        return this.employee;
    }

    public ContactHolder getHolder() {
        return this.employee;
    }

    public Number getId() {
        return id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmp(Employee emp) {
        this.employee = emp;
    }

    public void setHolder(ContactHolder emp) {
        this.employee = emp;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition table = new TableDefinition();

        table.setName("INT_EML");
        table.addPrimaryKeyField("E_ID", java.math.BigDecimal.class, 15);
        table.addField("ADDR", String.class, 30);
        table.addField("EMP", java.math.BigDecimal.class, 15);
        table.addField("TYPE", String.class, 5);

        return table;
    }
}

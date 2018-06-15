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

public class Phone implements Contact, java.io.Serializable {
    public String number;
    public Number id;
    public ContactHolder employee;

    public Object clone() {
        Phone object = new Phone();
        object.number = this.number;
        object.id = this.id;
        return object;
    }

    public static Phone example1() {
        Phone example = new Phone();
        example.setNumber("416 123-4353");
        return example;
    }

    public static Phone example2() {
        Phone example = new Phone();
        example.setNumber("612 123-4353");
        return example;
    }

    public static Phone example3() {
        Phone example = new Phone();
        example.setNumber("613 123-4353");
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

    public void setEmp(Employee emp) {
        this.employee = emp;
    }

    public void setHolder(ContactHolder emp) {
        this.employee = emp;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition table = new TableDefinition();
        table.setName("INT_PHN");
        table.addField("P_ID", java.math.BigDecimal.class, 15);
        table.addField("P_NUM", String.class, 30);
        table.addField("EMP", java.math.BigDecimal.class, 15);
        table.addField("TYPE", String.class, 5);

        return table;
    }
}

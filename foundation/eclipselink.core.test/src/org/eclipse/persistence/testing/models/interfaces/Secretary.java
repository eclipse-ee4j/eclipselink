/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.tools.schemaframework.*;

public class Secretary extends AdministrativeJob {
    public String department;

    public static Secretary example1() {
        Secretary example = new Secretary();

        example.setDepartment("Finance");
        example.setSalary(33500.00f);
        example.setMinimumSalary(31000.00f);

        return example;
    }

    public static Secretary example2() {
        Secretary example = new Secretary();

        example.setDepartment("Employment");
        example.setSalary(33500.00f);
        example.setMinimumSalary(31000.00f);

        return example;
    }

    public static Secretary example3() {
        Secretary example = new Secretary();

        example.setDepartment("Public Relations");
        example.setSalary(35000.00f);
        example.setMinimumSalary(31000.00f);

        return example;
    }

    public String getDepartment() {
        return department;
    }

    public static TableDefinition secretaryTable() {
        TableDefinition table = new TableDefinition();

        table.setName("SECRTRY");
        table.addField("CODE", java.math.BigDecimal.class, 15);
        table.addField("SALARY", Float.class);
        table.addField("DEPT", String.class, 30);

        return table;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String toString() {
        return new String("Secretary: " + getJobCode());
    }
}

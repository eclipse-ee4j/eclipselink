/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
        example.setSalary(new Float(33500.00));
        example.setMinimumSalary(new Float(31000.00));

        return example;
    }

    public static Secretary example2() {
        Secretary example = new Secretary();

        example.setDepartment("Employment");
        example.setSalary(new Float(33500.00));
        example.setMinimumSalary(new Float(31000.00));

        return example;
    }

    public static Secretary example3() {
        Secretary example = new Secretary();

        example.setDepartment("Public Relations");
        example.setSalary(new Float(35000.00));
        example.setMinimumSalary(new Float(31000.00));

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

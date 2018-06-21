/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.distributedservers;

import java.math.BigDecimal;

import java.util.Vector;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;


public class Dist_Employee {
    public BigDecimal id;
    public String name;
    public Vector heldItems;

    public static Dist_Employee example1() {
        Dist_Employee employee = new Dist_Employee();
        employee.name = "Anthony Smith";
        return employee;
    }

    public static Dist_Employee example2() {
        Dist_Employee employee = new Dist_Employee();
        employee.name = "Johnathan Smith";
        return employee;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static

    TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("DIST_EMPLOYEE");

        definition.addIdentityField("ID", BigDecimal.class);
        definition.addField("NAME", String.class, 255);
        return definition;
    }

}

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
package org.eclipse.persistence.testing.models.legacy;

import java.math.*;
import java.util.*;
import java.io.*;

public class Shipment implements Serializable {
    public Number shipmentNumber;
    public String employeeFirstName;
    public String employeeLastName;
    public String quantityShipped;
    public String shipMode;
    public Vector orders;
    public Employee employee;

    public Shipment() {
        orders = new Vector();
    }

    public static Shipment example1(Employee employee) {
        Shipment example = new Shipment();

        example.employee = employee;
        example.employeeFirstName = employee.firstName;
        example.employeeLastName = employee.lastName;
        example.shipmentNumber = new BigDecimal(1);
        example.quantityShipped = "1 ton";
        example.shipMode = "Air";

        example.orders.addElement(Order.example1(example, employee));
        example.orders.addElement(Order.example2(example, employee));

        return example;
    }

    public static Shipment example2(Employee employee) {
        Shipment example = new Shipment();

        example.employee = employee;
        example.employeeFirstName = employee.firstName;
        example.employeeLastName = employee.lastName;
        example.shipmentNumber = new BigDecimal(2);
        example.quantityShipped = "2 ton";
        example.shipMode = "Air";

        example.orders.addElement(Order.example3(example, employee));
        example.orders.addElement(Order.example4(example, employee));

        return example;
    }

    public static Shipment example3(Employee employee) {
        Shipment example = new Shipment();

        example.employee = employee;
        example.employeeFirstName = employee.firstName;
        example.employeeLastName = employee.lastName;
        example.shipmentNumber = new BigDecimal(3);
        example.quantityShipped = "3 ton";
        example.shipMode = "Ship";

        example.orders.addElement(Order.example5(example, employee));
        example.orders.addElement(Order.example6(example, employee));

        return example;
    }

    public static Shipment example4(Employee employee) {
        Shipment example = new Shipment();

        example.employee = employee;
        example.employeeFirstName = employee.firstName;
        example.employeeLastName = employee.lastName;
        example.shipmentNumber = new BigDecimal(4);
        example.quantityShipped = "4 ton";
        example.shipMode = "Ship";

        example.orders.addElement(Order.example7(example, employee));
        example.orders.addElement(Order.example8(example, employee));

        return example;
    }
}

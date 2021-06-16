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

public class GaurenteedShipment extends InsuredShipment {
    public GaurenteedShipment() {
        super();
    }

    public static Shipment example1(Employee employee) {
        InsuredShipment example = new GaurenteedShipment();

        example.employee = employee;
        example.employeeFirstName = employee.firstName;
        example.employeeLastName = employee.lastName;
        example.shipmentNumber = new BigDecimal(2345);
        example.quantityShipped = "1 kg";
        example.shipMode = "Goat";
        example.insuranceAmount = 500.00;

        return example;
    }

    public static Shipment example2(Employee employee) {
        InsuredShipment example = new GaurenteedShipment();

        example.employee = employee;
        example.employeeFirstName = employee.firstName;
        example.employeeLastName = employee.lastName;
        example.shipmentNumber = new BigDecimal(666);
        example.quantityShipped = "22 tons";
        example.shipMode = "Traincoptor";
        example.insuranceAmount = 50.00;
        example.orders.addElement(Order.example15(example, employee));

        return example;
    }
}

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

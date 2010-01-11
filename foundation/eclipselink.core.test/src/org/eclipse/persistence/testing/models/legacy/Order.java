/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.legacy;

import java.math.*;
import java.io.*;

public class Order implements Serializable {
    public String employeeFirstName;
    public String employeeLastName;
    public Number shipmentNumber;
    public BigDecimal orderNumber;
    public String description;
    public Shipment shipment;
    public Employee employee;

    public static Order example1(Shipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(1);
        example.description = "Towels";
        example.employee = employee;
        return example;
    }

    public static Order example10(InsuredShipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(19);
        example.description = "Sony Walkman";
        example.employee = employee;
        return example;
    }

    public static Order example11(InsuredShipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(20);
        example.description = "M-16 Assault Rifle";
        example.employee = employee;
        return example;
    }

    public static Order example12(InsuredShipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(21);
        example.description = "Priceless Ming Vase";
        example.employee = employee;
        return example;
    }

    public static Order example13(InsuredShipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(22);
        example.description = "Isaih Thomas Rookie Card";
        example.employee = employee;
        return example;
    }

    public static Order example14(InsuredShipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(23);
        example.description = "Mr. Bungle CD";
        example.employee = employee;
        return example;
    }

    public static Order example15(InsuredShipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(11123);
        example.description = "Mr. Bungle 8track";
        example.employee = employee;
        return example;
    }

    public static Order example2(Shipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(2);
        example.description = "Bicycles";
        example.employee = employee;
        return example;
    }

    public static Order example3(Shipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(3);
        example.description = "Mobikes";
        example.employee = employee;
        return example;
    }

    public static Order example4(Shipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(4);
        example.description = "Cars";
        example.employee = employee;
        return example;
    }

    public static Order example5(Shipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(5);
        example.description = "Buses";
        example.employee = employee;
        return example;
    }

    public static Order example6(Shipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(6);
        example.description = "Planes";
        example.employee = employee;
        return example;
    }

    public static Order example7(Shipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(7);
        example.description = "Petroleum";
        example.employee = employee;
        return example;
    }

    public static Order example8(Shipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(8);
        example.description = "Bread";
        example.employee = employee;
        return example;
    }

    public static Order example9(InsuredShipment shipment, Employee employee) {
        Order example = new Order();

        example.shipment = shipment;
        example.employeeFirstName = shipment.employeeFirstName;
        example.employeeLastName = shipment.employeeLastName;
        example.shipmentNumber = shipment.shipmentNumber;
        example.orderNumber = new BigDecimal(18);
        example.description = "Bread";
        example.employee = employee;
        return example;
    }
}

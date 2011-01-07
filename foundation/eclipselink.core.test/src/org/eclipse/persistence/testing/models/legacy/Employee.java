/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.*;
import java.io.*;

public class Employee implements Serializable {
    public String firstName;
    public String lastName;
    public String address;
    public Computer computer;
    public Vector shipments;

    public Employee() {
        this.shipments = new Vector();
    }

    public static Employee example1() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Dave";
        example.lastName = "Vadis";
        example.address = "885 Meadowlands Dr.";
        example.computer = Computer.example1(example);

        example.shipments.addElement(Shipment.example1(example));
        example.shipments.addElement(Shipment.example2(example));

        return example;
    }

    public static Employee example2() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Tracy";
        example.lastName = "Chapman";
        example.address = "885 Meadowlands Dr.";
        example.computer = Computer.example2(example);

        example.shipments.addElement(Shipment.example3(example));
        example.shipments.addElement(Shipment.example4(example));

        return example;
    }

    public static Employee example3() {
        Employee example = new Employee();

        example.firstName = "Edward";
        example.lastName = "White";
        example.address = "885 Meadowlands Dr.";
        example.computer = Computer.example3(example);

        example.shipments.addElement(InsuredShipment.example1(example));
        example.shipments.addElement(InsuredShipment.example2(example));
        example.shipments.addElement(GaurenteedShipment.example2(example));

        return example;
    }

    public static Employee example4() {
        Employee example = new Employee();

        example.firstName = "Graham";
        example.lastName = "Gooch";
        example.address = "885 Meadowlands Dr.";
        example.computer = Computer.example4(example);

        return example;
    }

    public static Employee example5() {
        Employee example = new Employee();

        example.firstName = "Tracy";
        example.lastName = "Rue";
        example.address = "885 Meadowlands Dr.";
        example.computer = Computer.example5(example);

        return example;
    }

    public static Employee example6() {
        Employee example = new Employee();

        example.firstName = "Norman";
        example.lastName = "Louis";
        example.address = "885 Meadowlands Dr.";
        example.computer = Computer.example6(example);
        example.shipments.addElement(GaurenteedShipment.example1(example));

        return example;
    }
}

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
package org.eclipse.persistence.tools.workbench.test.models.complexinheritance;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Car extends FueledVehicle {
/**
 * This method was created in VisualAge.
 */
public Car() {
    super();
}
public static Car example1()
{
    Car example = new Car();

    example.setPassengerCapacity(new Integer(2));
    example.setFuelCapacity(new Integer(30));
    example.setDescription("PONTIAC");
    example.setFuelType("Petrol");
    example.addPartNumber("021776RM-b");
    example.addPartNumber("122500JC-s");
    example.addPartNumber("101101BI-n");
    return example;
}
public static Car example2()
{
    Car example = new Car();

    example.setPassengerCapacity(new Integer(4));
    example.setFuelCapacity(new Integer(50));
    example.setDescription("TOYOTA");
    example.setFuelType("Petrol");
    example.addPartNumber("021776TT-a");
    example.addPartNumber("122500RF-g");
    example.addPartNumber("101101ML-m");
    return example;
}
public static Car example3()
{
    Car example = new Car();

    example.setPassengerCapacity(new Integer(5));
    example.setFuelCapacity(new Integer(60));
    example.setDescription("BMW");
    example.setFuelType("Disel");
    example.addPartNumber("021776KM-k");
    example.addPartNumber("122500MP-k");
    example.addPartNumber("101101MP-d");
    return example;
}
public static Car example4()
{
    Car example = new Car();

    example.setPassengerCapacity(new Integer(8));
    example.setFuelCapacity(new Integer(100));
    example.setDescription("Mazda");
    example.setFuelType("Coca-Cola");
    example.addPartNumber("021776KM-k");
    example.addPartNumber("122500MP-k");
    example.addPartNumber("101101MP-d");
    return example;
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition()
{
    TableDefinition definition = new TableDefinition();

    definition.setName("CAR");

    definition.addField("ID", java.math.BigDecimal.class, 15);
    definition.addField("CDESCRIP", String.class, 30);

    return definition;
}
}

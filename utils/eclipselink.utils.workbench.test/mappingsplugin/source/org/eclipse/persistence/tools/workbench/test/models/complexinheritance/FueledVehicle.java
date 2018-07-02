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

public class FueledVehicle extends Vehicle {
    public Integer fuelCapacity;
    public String description;
    public String fuelType;

@Override
public void change()
{
    this.setPassengerCapacity(new Integer(100));
    this.addPartNumber("NEWPART 1");
    this.setFuelType("HOT AIR");

}
public static FueledVehicle example1(Company company)
{
    FueledVehicle example = new FueledVehicle();

    example.setPassengerCapacity(new Integer(1));
    example.setFuelCapacity(new Integer(10));
    example.setDescription("Motercycle");
    example.getOwner().setValue(company);
    return example;
}
public void setDescription(String aDescription)
{
    description = aDescription;
}
public void setFuelCapacity(Integer capacity)
{
    fuelCapacity = capacity;
}
public void setFuelType(String type)
{
    fuelType = type;
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition()
{
    TableDefinition definition = new TableDefinition();

    definition.setName("FUEL_VEH");

    definition.addField("ID", java.math.BigDecimal.class, 15);
    definition.addField("FUEL_CAP", Integer.class);
    definition.addField("FUEL_TYP", String.class, 30);

    return definition;
}
}

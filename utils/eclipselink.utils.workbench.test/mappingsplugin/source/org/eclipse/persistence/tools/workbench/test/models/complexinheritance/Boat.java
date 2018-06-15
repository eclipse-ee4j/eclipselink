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
package org.eclipse.persistence.tools.workbench.test.models.complexinheritance;


public class Boat extends NonFueledVehicle {



public static Boat example1(Company company)
{
    Boat example = new Boat();

    example.setPassengerCapacity(new Integer(10));
    example.getOwner().setValue(company);
    return example;
}
public static Boat example2(Company company)
{
    Boat example = new Boat();

    example.setPassengerCapacity(new Integer(20));
    example.getOwner().setValue(company);
    return example;
}
public static Boat example3(Company company)
{
    Boat example = new Boat();

    example.setPassengerCapacity(new Integer(30));
    example.getOwner().setValue(company);
    return example;
}
}

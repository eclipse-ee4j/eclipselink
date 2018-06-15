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


public class NonFueledVehicle extends Vehicle {


public static NonFueledVehicle example4(Company company)
{
    NonFueledVehicle example = new NonFueledVehicle();

    example.setPassengerCapacity(new Integer(1));
    example.getOwner().setValue(company);
    return example;
}
}

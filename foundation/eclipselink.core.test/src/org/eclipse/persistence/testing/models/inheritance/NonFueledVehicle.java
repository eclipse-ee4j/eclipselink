/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.inheritance;

public class NonFueledVehicle extends Vehicle {
    public static NonFueledVehicle example4(Company company) {
        NonFueledVehicle example = new NonFueledVehicle();

        example.setPassengerCapacity(new Integer(1));
        example.getOwner().setValue(company);
        return example;
    }
}
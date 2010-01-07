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
package org.eclipse.persistence.testing.models.inheritance;

public class Boat extends NonFueledVehicle {
    public static Boat example1(Company company) {
        Boat example = new Boat();

        example.setPassengerCapacity(new Integer(10));
        example.getOwner().setValue(company);
        return example;
    }

    public static Boat example2(Company company) {
        Boat example = new Boat();

        example.setPassengerCapacity(new Integer(20));
        example.getOwner().setValue(company);
        return example;
    }

    public static Boat example3(Company company) {
        Boat example = new Boat();

        example.setPassengerCapacity(new Integer(30));
        example.getOwner().setValue(company);
        return example;
    }
}

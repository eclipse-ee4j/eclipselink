/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.inheritance.Car;

public class OverrideInheritedMappingTest extends TestCase {
    private Car theCar;
    private Number carID;

    /**
     * This method was created in VisualAge.
     */
    public OverrideInheritedMappingTest() {
        setDescription("Subclass is able to override inherited mapping");
    }

    /**
     * This method was created in VisualAge.
     */
    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * This method was created in VisualAge.
     */
    @Override
    public void setup() {
        this.theCar = Car.example4();
    }

    /**
     * This method was created in VisualAge.
     */
    @Override
    public void test() {
        //Insert a car
        org.eclipse.persistence.sessions.UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(this.theCar);
        uow.commit();

        //Get the car's ID
        Car carRead = (Car)getSession().readObject(this.theCar);
        this.carID = carRead.id;

        //Initialize idmaps
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * This method was created in VisualAge.
     */
    @Override
    public void verify() {
        //Update fuel capacity of the previously inserted car
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("UPDATE CAR SET FUEL_CAP = 200 WHERE (ID = " + this.carID + ")"));

        //this.theCar.fuelCapacity = Integer.valueOf(200);
        //getAbstractSession().updateObject(this.theCar);
        //Read the car and check that the field was set (override of inherited mapping worked)
        Car carRead = (Car)getSession().readObject(Car.class, new ExpressionBuilder().get("id").equal(this.carID));
        if (carRead.fuelCapacity != 200) {
            throw new TestErrorException("The inherited mapping was not overridden!");
        }
    }
}

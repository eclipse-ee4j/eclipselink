/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.readonly;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.inheritance.Bicycle;
import org.eclipse.persistence.testing.models.inheritance.FueledVehicle;
import org.eclipse.persistence.testing.models.inheritance.Car;
import org.eclipse.persistence.testing.models.inheritance.Boat;
import org.eclipse.persistence.testing.models.inheritance.SportsCar;
import org.eclipse.persistence.testing.models.inheritance.Vehicle;
import org.eclipse.persistence.testing.models.inheritance.Bus;
import org.eclipse.persistence.testing.models.inheritance.Company;
import org.eclipse.persistence.testing.models.inheritance.NonFueledVehicle;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * <p>
 * <b>Purpose</b>: Test updating a non-read-only object which has a reference to a read-only
 * object which refers back to the non-read-only object.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Verify that modifications to the read-only objects do not get written to the database.
 * </ul>
 */
public class ReadOnlyClassOneToManyTestCase extends AutoVerifyTestCase {
    public Vehicle originalVehicle;
    UnitOfWork uow;
    Company originalCompany;
    Integer origCapacity;

    public ReadOnlyClassOneToManyTestCase() {
        super();
    }

    public void reset() {
        originalVehicle.setPassengerCapacity(origCapacity);
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        beginTransaction();
        originalCompany = (Company)getSession().readObject(Company.class);

        originalVehicle = (Vehicle)((Vector)originalCompany.getVehicles().getValue()).firstElement();

        origCapacity = originalVehicle.passengerCapacity;
        uow = getSession().acquireUnitOfWork();
        uow.addReadOnlyClass(Bus.class);
        uow.addReadOnlyClass(SportsCar.class);
        uow.addReadOnlyClass(Bicycle.class);
        uow.addReadOnlyClass(Boat.class);
        uow.addReadOnlyClass(Car.class);
        uow.addReadOnlyClass(NonFueledVehicle.class);
        uow.addReadOnlyClass(FueledVehicle.class);
        uow.addReadOnlyClass(Vehicle.class);
        Company cloneCompany = (Company)uow.registerObject(originalCompany);

        // Change the one of the Company's Vehicles  
        ((Vehicle)((Vector)cloneCompany.getVehicles().getValue()).firstElement()).setPassengerCapacity(new Integer(origCapacity.intValue() + 1));
    }

    protected void test() {
        uow.commit();
    }

    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        // Get the version from the database and compare to the original.
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("id").equal(originalVehicle.id);
        Vehicle dbVehicle = (Vehicle)getSession().readObject(Vehicle.class, exp);
        if (!origCapacity.equals(dbVehicle.passengerCapacity)) {
            throw new TestErrorException("We succeeded in changing a read-only objects in a 1:M mapping. This is very bad!");
        }
    }
}

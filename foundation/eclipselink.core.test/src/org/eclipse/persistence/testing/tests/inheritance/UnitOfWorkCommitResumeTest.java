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
package org.eclipse.persistence.testing.tests.inheritance;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.models.inheritance.Bicycle;
import org.eclipse.persistence.testing.models.inheritance.Car;
import org.eclipse.persistence.testing.models.inheritance.Boat;
import org.eclipse.persistence.testing.models.inheritance.Vehicle;
import org.eclipse.persistence.testing.models.inheritance.Bus;
import org.eclipse.persistence.testing.models.inheritance.Company;

/**
 * <p>
 * <b>Purpose</b>: This test checks to see if the Unit of Work commitAndResume functionality works  with the Inheritance mappings
 *
 * <p>
 * <b>Motivation </b>: This test was written to test a new feature: the UOW.
 * <p>
 * <b>Design</b>: The Complex Inheritance model is used. A Company is registered into the UOW, and then
 *                             its different levels of inheritance and relationship mappings are changed and commited to
 *                             to the database (using the commitAndResume method), changed again, committed again, and then read back and compared.
 *
 *     <p>
 * <b>Responsibilities</b>: Check if the unit of work commitAndResume functionality works properly with complex inheritance mappings
 *
 * <p>
 *     <b>Features Used</b>: Inheritance Mappings, Unit Of Work (commitAndResume)
 *
 * <p>
 * <b>Paths Covered</b>: Within the unit of work, different parts listed below were modified:
 *                                <ul>
 *                                <li>    <i>A vehicle removed from the company</i>,
 *                                <li>    <i>A vehicle changed seating capacity changed</i>,
 *                                <li>    <i>Bicycle added to vehicles</i>, 2L inheritance from vehicle, on same table
 *                                <li>    <i>Car added to vehicles</i>, 2L inheritance from vehicle, has own table
 *                                <li>    <i>Bus added to vehicles</i>, 2L inheritance from vehicle, has own table
 *                                <li>    <i>Vehicle deleted</i>, deletion with inheritance
 *                                <li>    <i>Company name adjusted</i>,
 *
 * */
public class UnitOfWorkCommitResumeTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;
    public Vector persons;

    /**
     * UnitOfWorkCommitResumeTest constructor comment.
     */
    public UnitOfWorkCommitResumeTest() {
        super();
    }

    /**
     * UnitOfWorkCommitResumeTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public UnitOfWorkCommitResumeTest(Object originalObject) {
        super(originalObject);
    }

    /** Change both the unitOfWorkWorkingCopy and anotherUoWWorkingCopyVector. The first
    is for a company, the second is for a vector of Person objects.
    */
    protected void changeUnitOfWorkWorkingCopy() {
        Company company = (Company)this.unitOfWorkWorkingCopy;
        Vector vehicles = (Vector)company.getVehicles().getValue();

        // Delete a vehicle
        vehicles.removeElement(vehicles.firstElement());

        // Change a vehicle
        Vehicle aVehicle = (Vehicle)vehicles.lastElement();
        aVehicle.setPassengerCapacity(new Integer(15));

        // Add some vehicles
        Car car = Car.example2();
        car.setOwner(company);
        vehicles.addElement(car);
        vehicles.addElement(Bicycle.example3(company));
        vehicles.addElement(Bicycle.example2(company));
        vehicles.addElement(Bus.example2(company));
    }

    /** Change both the unitOfWorkWorkingCopy and anotherUoWWorkingCopyVector. The first
    is for a company, the second is for a vector of Person objects.
    */
    protected void changeUnitOfWorkWorkingCopyAgain() {
        Company company = (Company)this.unitOfWorkWorkingCopy;
        Vector vehicles = (Vector)company.getVehicles().getValue();

        // Adjust Company name
        company.setName(company.name + "0");

        // Delete a vehicle
        vehicles.removeElement(vehicles.firstElement());

        // Change a vehicle
        Vehicle aVehicle = (Vehicle)vehicles.firstElement();
        aVehicle.setPassengerCapacity(new Integer(15));

        // Add some vehicles
        Car car = Car.example2();
        car.setOwner(company);
        vehicles.addElement(car);
        vehicles.addElement(Bus.example4(company));
        vehicles.addElement(Bicycle.example1(company));
        vehicles.addElement(Boat.example3(company));
        vehicles.addElement(Bus.example2(company));

    }

    protected void setup() {
        super.setup();

        // Acquire the unit of work, register the object, and change the clone of the registered object.
        this.unitOfWork = getSession().acquireUnitOfWork();
        this.unitOfWorkWorkingCopy = this.unitOfWork.registerObject(this.objectToBeWritten);
        changeUnitOfWorkWorkingCopy();

        // Use the original session for comparision
        if (!((AbstractSession)getSession()).compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
    }

    protected void test() {
        // CommitAndResume, changeAgain, CommitAndResume again.
        this.unitOfWork.commitAndResume();
        changeUnitOfWorkWorkingCopyAgain();
        this.unitOfWork.commitAndResume();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        try {
            if (!(((AbstractSession)getSession()).compareObjects(this.unitOfWorkWorkingCopy, this.objectToBeWritten))) {
                throw new TestErrorException("The object in the unit of work has not been commited properly to its parent");
            }
        } catch (DatabaseException exception) {
            if (getSession().getPlatform().isDBase()) {
                throw new TestWarningException("This fails because of some strange bug in the DBase driver. " + exception.getMessage());
            } else {
                throw exception;
            }
        }
        super.verify();
    }
}

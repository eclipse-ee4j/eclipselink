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
package org.eclipse.persistence.testing.tests.readonly;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.readonly.Country;
import org.eclipse.persistence.testing.models.readonly.Address;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * <p>
 * <b>Purpose</b>: Test inserting a non-read-only object which refers to a read-only object.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Register an non-read-only object with a UnitOfWork.
 * <li> Assign a read-only object to the non-read-only object.
 * <li> Insert the non-read-only object in the database.
 * <li> Verify that the non-read-only object was correctly inserted.
 * </ul>
 */
public class ReadOnlyClassInsertTestCase extends AutoVerifyTestCase {
    public Address address;

    public ReadOnlyClassInsertTestCase() {
        super();
    }

    public void reset() {
        getSession().getProject().setDefaultReadOnlyClasses(new Vector());

        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    /**
     * Return a random element of aVector. Return null if the Vector is empty.
     */
    protected Object selectRandom(Vector aVector) {
        if (aVector.isEmpty()) {
            return null;
        }

        int size = aVector.size();
        Random aRandom = new Random();
        int index = Math.abs(aRandom.nextInt()) % size;
        return aVector.elementAt(index);
    }

    protected void setup() {
        beginTransaction();
    }

    protected void test() {
        // Have the countries standing by, outside the unit of work.
        Vector countries = getSession().readAllObjects(Country.class);

        // Acquire a unit of work with a class read-only.
        Vector readOnlyClasses = new Vector();
        readOnlyClasses.addElement(Country.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.removeAllReadOnlyClasses();
        uow.addReadOnlyClasses(readOnlyClasses);

        // Create and register a new object.	
        address = new Address();
        Address addressClone = (Address)uow.registerObject(address);

        // Make changes to address. (Simulate editing the address in a GUI).
        addressClone.setStreetAddress("1203 Southport Drive");
        addressClone.setCity("Ottawa");
        addressClone.setZipCode("K1R 5N2");

        // Add the Country object which is from the parent session, 
        // and not registered in this uow.	
        addressClone.setCountry((Country)selectRandom(countries));
        uow.commit();
    }

    protected void verify() {
        getSession().getIdentityMapAccessor().removeFromIdentityMap(address);

        // Get the version from the database.
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("streetAddress").equal(address.getStreet());
        Address dbAddress = (Address)getSession().readObject(Address.class, exp);
        if (!address.equals(dbAddress)) {
            throw new TestErrorException("The insert of a non-read-only object referring to a read-only object failed. We should be able to do this!");
        }
    }
}

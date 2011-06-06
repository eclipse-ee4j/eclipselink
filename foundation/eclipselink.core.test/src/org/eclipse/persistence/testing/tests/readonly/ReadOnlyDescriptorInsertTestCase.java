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
 * <b>Purpose</b>: Test inserting a non-read-only object which refers to a read-only object by setting the flag on the descriptor.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Register a non-read-only object with a UnitOfWork e.g Address.
 * <li> Assign a read-only object to the non-read-only object e.g Country.
 * <li> Insert the non-read-only object in the database.
 * <li> Verify that the non-read-only object was correctly inserted.
 * </ul>
 */
public class ReadOnlyDescriptorInsertTestCase extends AutoVerifyTestCase {
    Address address;

    public ReadOnlyDescriptorInsertTestCase() {
        super();
    }

    protected void setup() {
        beginTransaction();
    }

    public void reset() {
        getSession().getProject().setDefaultReadOnlyClasses(new Vector());
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

    }

    protected void test() {
        // Read the country object in outside the unit of work
        Country count = (Country)getSession().readObject(Country.class);
        getSession().getDescriptor(count).setReadOnly();

        // Acquire a unit of work with a class read-only.
        UnitOfWork uow = getSession().acquireUnitOfWork();

        //uow.getDescriptor(Country.class).setReadOnly();
        // Create and register a new object.	
        address = new Address();
        Address addressClone = (Address)uow.registerObject(address);

        // Make changes to address. (Simulate editing the address in a GUI).
        addressClone.setStreetAddress("21D Water Street");
        addressClone.setCity("Perth");
        addressClone.setZipCode("K2C 5B6");

        // Add the Country object which is from the parent session, 
        // and not registered in this uow.	
        addressClone.setCountry(count);
        uow.commit();

    }

    /**
     * Check if the read only flag in the descriptor is recognized
     */
    protected void verify() {
        getSession().getIdentityMapAccessor().removeFromIdentityMap(address);

        // Get the version from the database.
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("streetAddress").equal(address.getStreet());
        Address dbAddress = (Address)getSession().readObject(Address.class, exp);
        if (!address.equals(dbAddress)) {
            throw new TestErrorException("The read-only flag set on the descriptor does not work!");
        }
    }
}

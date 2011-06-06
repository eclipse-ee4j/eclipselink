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
 * <b>Purpose</b>: Test that deleting a non-read-only object does not delete read-only reference.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Write a non-read-only object to the database which refers to a read-only object,
 * <li> Delete the non-read-only object.
 * <li> Verify that the read-only object is not deleted.
 * </ul>
 */
public class ReadOnlyClassDeleteTestCase extends AutoVerifyTestCase {
    public Address address;

    public ReadOnlyClassDeleteTestCase() {
        super();
    }

    public void reset() {
        getSession().getProject().setDefaultReadOnlyClasses(new Vector());
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        beginTransaction();

        // Create a new address and insert it.
        address = new Address();

        // Make changes to address. (Simulate editing the address in a GUI).
        address.setStreetAddress("1212 Madison Street");
        address.setCity("Victoria");
        address.setZipCode("V3Z 3G1");

        // Read a Country object to use in the Address object.
        Country aCountry = (Country)getSession().readObject(Country.class);
        address.setCountry(aCountry);

        // Write it to the db.
        beginTransaction();
        getDatabaseSession().writeObject(address);
        commitTransaction();
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

        // Read-in the address.
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("streetAddress").equal(address.getStreet());
        Address readAddress = (Address)uow.readObject(Address.class, exp);

        // Delete the address
        uow.deleteObject(readAddress);
        uow.commit();
    }

    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        // The address should be deleted by the Country (which is read-only) should not have been.
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("streetAddress").equal(address.getStreet());
        Address dbAddress = (Address)getSession().readObject(Address.class, exp);
        if (dbAddress != null) {
            throw new TestErrorException("The delete of a non-read-only object referring to a read-only object failed. We should be able to do this!");
        }

        // Read in the Country that was added to the Address that was deleted.
        ExpressionBuilder expBuilder2 = new ExpressionBuilder();
        Expression exp2 = expBuilder2.get("name").equal(address.getCountry().getName());
        Country aCountry = (Country)getSession().readObject(Country.class, exp2);
        if (aCountry == null) {
            throw new TestErrorException("during the  delete of a non-read-only object referring to a read-only object, the read-only object was also deleted. This should not have happened!");
        }
    }
}

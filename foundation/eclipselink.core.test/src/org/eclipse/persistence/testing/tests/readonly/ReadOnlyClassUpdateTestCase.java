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
import org.eclipse.persistence.testing.models.readonly.Country;
import org.eclipse.persistence.testing.models.readonly.Address;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * <p>
 * <b>Purpose</b>: Test updating a non-read-only object which has a reference to a read-only object.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Verify that modifications to the non-read-only object get written to the database.
 * </ul>
 */
public class ReadOnlyClassUpdateTestCase extends AutoVerifyTestCase {
    public Address address;
    Country originalCountry;

    public ReadOnlyClassUpdateTestCase() {
        super();
    }

     public void reset() {
        getSession().getProject().setDefaultReadOnlyClasses(new Vector());

        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        /*    // Reset the address that was changed.
Vector readOnlyClasses = new Vector();
readOnlyClasses.addElement(Country.class);
UnitOfWork uow = getSession().acquireUnitOfWork();
uow.setReadOnlyClasses(readOnlyClasses);

Address cloneAddress = (Address) uow.registerObject(address);
cloneAddress.setCountry(originalCountry);
uow.commit();
*/
        /*    getAbstractSession().beginTransaction();
address.setCountry(originalCountry);
getAbstractSession().updateObject(address);
getAbstractSession().commitTransaction();
*/
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

        // Read in an existing Address.
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("country").get("name").equal("United Kingdom");
        address = (Address)uow.readObject(Address.class, exp);
        Address addressClone = (Address)uow.registerObject(address);
        originalCountry = addressClone.getCountry();

        // Change the Address' Country.
        Country aCountry;
        do {
            aCountry = (Country)selectRandom(countries);
        } while (address.getCountry().equals(aCountry));
        addressClone.setCountry(aCountry);

        // Commit the unit of work
        uow.commit();
    }

    protected void verify() {
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("streetAddress").equal(address.getStreet());
        Address dbAddress = (Address)getSession().readObject(Address.class, exp);
        if (!address.equals(dbAddress)) {
            throw new TestErrorException("The update of a non-read-only object referring to a read-only object failed. We should be able to do this!");
        }
    }
}

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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.readonly.Country;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * <p>
 * <b>Purpose</b>: Define a for attempting to insert a read-only object in a UnitOfWork.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Register a newly created object in a UnitOfWork,
 * <li> Commit the UOW,
 * <li> Verify that the object was not written to the database. 
 * </ul>
 */
public class InsertReadOnlyClassTestCase extends AutoVerifyTestCase {
    public UnitOfWork uow;
    Country aCountry;

    public InsertReadOnlyClassTestCase() {
        super();
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        beginTransaction();

        // Acquire a unit of work with a class read-only.
        uow = getSession().acquireUnitOfWork();
        uow.addReadOnlyClass(Country.class);
    }

    protected void test() {
        // Create a new Country.
        aCountry = new Country();
        aCountry.setName("Utopia");

        // Register the new Country.
        uow.registerObject(aCountry);
        uow.commit();
    }

    protected void verify() {
        // Check to see that aCountry was not deleted from the database.
        ExpressionBuilder xBuilder = new ExpressionBuilder();
        Expression exp = xBuilder.get("name").equal(aCountry.name);
        Country dbCountry = (Country)getSession().readObject(Country.class, exp);
        if (dbCountry != null) {
            throw new TestErrorException("The Country object was inserted! It should not have been since it is read-only.");
        }
    }
}

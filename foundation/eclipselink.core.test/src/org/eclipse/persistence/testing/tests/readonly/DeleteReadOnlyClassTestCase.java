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
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * <p>
 * <b>Purpose</b>: Define a for attempting to delete a read-only object in a UnitOfWork.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Catches the exception which should be thrown when the user attempts to delete a read-only object 
 * </ul>
 */
public class DeleteReadOnlyClassTestCase extends AutoVerifyTestCase {
    public UnitOfWork uow;
    Country aCountry;

    public DeleteReadOnlyClassTestCase() {
        super();
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        beginTransaction();

        // Acquire a unit of work with a class read-only.
        Vector readOnlyClasses = new Vector();
        readOnlyClasses.addElement(Country.class);
        uow = getSession().acquireUnitOfWork();
        uow.removeAllReadOnlyClasses();
        uow.addReadOnlyClasses(readOnlyClasses);
    }

    protected void test() {
        // Read a Country in.
        aCountry = (Country)uow.readObject(Country.class);

        // Try to delete the contry.
        boolean caught = false;
        try {
            uow.deleteObject(aCountry);
            uow.commit();
        } catch (org.eclipse.persistence.exceptions.QueryException ex) {
            getSession().logMessage("Delete of read-only object threw an exception. OK.");
            caught = true;
        }
        if (!caught) {
            throw new TestErrorException("No exception was thrown when we illegally tried to delete a read-only object");
        }
    }

    protected void verify() {
        // Check to see that aCountry was not deleted from the database.
        ExpressionBuilder xBuilder = new ExpressionBuilder();
        Expression exp = xBuilder.get("name").equal(aCountry.name);
        Country dbCountry = (Country)getSession().readObject(Country.class, exp);
        if (dbCountry == null) {
            throw new TestErrorException("The Country object was deleted! It should not have been");
        }
    }
}

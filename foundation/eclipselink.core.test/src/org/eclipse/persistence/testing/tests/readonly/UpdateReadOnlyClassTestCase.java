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
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * <p>
 * <b>Purpose</b>: Test that updates to a read-only class do not get written to the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Verify that modifications to the read-only objects do not get written to the database.
 * </ul>
 */
public class UpdateReadOnlyClassTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public UnitOfWork uow;
    Country aCountry;
    String oldName;

    public UpdateReadOnlyClassTestCase() {
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
        // Read in a Country.
        aCountry = (Country)uow.readObject(Country.class);
        oldName = aCountry.getName();

        // Update the country and commit. Nothing should be written to the db.
        uow.registerObject(aCountry);
        aCountry.setName(aCountry.getName() + " 22");
        uow.commit();
    }

    protected void verify() {
        // Check to see that aCountry was not deleted from the database.
        ExpressionBuilder xBuilder = new ExpressionBuilder();
        Expression exp = xBuilder.get("name").equal(aCountry.name);
        Country dbCountry = (Country)getSession().readObject(Country.class, exp);
        if (dbCountry != null) {
            throw new TestErrorException("The Country object was illegally updated! It should not have been!");
        }
    }
}

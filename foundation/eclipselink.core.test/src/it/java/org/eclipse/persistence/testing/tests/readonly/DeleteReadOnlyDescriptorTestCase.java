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
package org.eclipse.persistence.testing.tests.readonly;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.readonly.Country;

/**
 * <p>
 * <b>Purpose</b>: Defined for attempting to delete a read-only object in a UnitOfWork.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Catches the exception which should be thrown when the user attempts to delete a read-only object
 * </ul>
 */
public class DeleteReadOnlyDescriptorTestCase extends AutoVerifyTestCase {
    public UnitOfWork uow;
    Country aCountry;

    public DeleteReadOnlyDescriptorTestCase() {
        super();
    }

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    protected void setup() {
        beginTransaction();

        // Acquire a unit of work with a read-only descriptor.
        uow = getSession().acquireUnitOfWork();
        uow.getDescriptor(Country.class).setReadOnly();
    }

    @Override
    protected void test() {
        // Read a Country in.
        aCountry = (Country)uow.readObject(Country.class);

        // Try to delete the country.
        boolean caught = false;
        try {
            uow.deleteObject(aCountry);
            uow.commit();
        } catch (org.eclipse.persistence.exceptions.QueryException ex) {
            getSession().logMessage("Delete of a read-only object threw an exception. OK.");
            caught = true;
        }
        if (!caught) {
            throw new TestErrorException("No exception was thrown when we illegally tried to delete a read-only object");
        }
    }

    @Override
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

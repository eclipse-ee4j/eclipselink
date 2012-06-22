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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 *  Test to ensure no ClassCastException is thrown when an object is deleted from a UnitOfWork with validation turned off
 *  CR#3216
 *  @author Tom Ware
 */
public class UnitOfWorkDeleteNoValidationTest extends AutoVerifyTestCase {
    public boolean caughtClassCastException = false;

    public UnitOfWorkDeleteNoValidationTest() {
        setDescription("This test tests to ensure there is no ClassCastException thrown when an object is deleted in a " + 
                       "UnitOfWork with validation turned off.");
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
    }

    public void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            Person personClone = (Person)uow.readObject(Person.class);
            uow.dontPerformValidation();
            uow.deleteObject(personClone);
            uow.commit();
        } catch (ClassCastException exception) {
            caughtClassCastException = true;
        }
    }

    public void verify() {
        if (caughtClassCastException) {
            throw new TestErrorException("A ClassCastException was thrown when an object was deleted in a UnitOfWork with validation turned off.");
        }
    }
}

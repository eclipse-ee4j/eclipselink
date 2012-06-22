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
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Previously when calling mergeCloneWithReferences on an object that contains a collection that contains a null.
 * A Null Pointer exception would be thrown.
 */
public class MergeCloneWithReferencesWithNullTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public MergeCloneWithReferencesWithNullTest() {
        setDescription("This test verifies that mergeCloneWithReferences will not throw an exception when encountering a null within a collection.");
    }

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    public void setup() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("This test cannot be run through the remote.");
        }
        getAbstractSession().beginTransaction();

    }

    /**
     * This testcase will attempt to call mergeCloneWithReferences on an object that contains
     * a collection that contains a null
     */
    public void test() {
        try {
            Employee empObject = (Employee)getSession().readObject(Employee.class);

            //being lazy just re-initialize the caches
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            empObject.getPhoneNumbers().add(null);
            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.readObject(empObject);
            uow.mergeCloneWithReferences(empObject);
            uow.release();
        } catch (NullPointerException exception) {
            throw new TestErrorException("Test Failed.  NullPointer was thrown");
        }
    }
}

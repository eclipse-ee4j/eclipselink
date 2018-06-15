/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.tests.validation.ExceptionTest;


//Currently nested unit of work is not supported for attribute change tracking.
//This test case tests if the correct exception is thrown when a nested uow is registered.
public class NestedUOWWithAttributeChangeTrackingTest extends ExceptionTest {

    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;
    public Object nestedUnitOfWorkWorkingCopy;
    public UnitOfWork nestedUnitOfWork;
    public Object object;

    public NestedUOWWithAttributeChangeTrackingTest(Object obj) {
        setDescription("Tests if nestedUOWNotSupportedForAttributeTracking exception would be thrown");
        object = obj;
    }

    protected void setup() {
        expectedException =
                org.eclipse.persistence.exceptions.ValidationException.nestedUOWNotSupportedForAttributeTracking();

        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();
        this.unitOfWorkWorkingCopy = this.unitOfWork.registerObject(object);

    }

    protected void test() {
        try {
            // Acquire nested unit of work
            this.nestedUnitOfWork = this.unitOfWork.acquireUnitOfWork();
            this.nestedUnitOfWorkWorkingCopy = this.nestedUnitOfWork.registerObject(this.unitOfWorkWorkingCopy);
            this.nestedUnitOfWork.commit();
        } catch (EclipseLinkException ex) {
            caughtException = ex;
        }
    }
}



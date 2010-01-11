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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.indirection.DatabaseValueHolder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;


/**
 * Test the the correct exception is thrown when encountering a dead indirection object in a merge.
 */
public class MergeDeadIndirectionTest extends AutoVerifyTestCase {
    public MergeDeadIndirectionTest() {
        setDescription("Test the the correct exception is thrown when encountering a dead indirection object in a merge.");
    }

    /**
     * Simulate detching and object and attempting to merge it.
     */
    public void test() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        PhoneNumber detachedPhone = (PhoneNumber)uow.readObject(PhoneNumber.class);
        ((DatabaseValueHolder)detachedPhone.owner).setSession(null);
        uow.release();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        uow = getSession().acquireUnitOfWork();
        DescriptorException caughtException = null;
        try {
            uow.deepMergeClone(detachedPhone);
        } catch (DescriptorException exception) {
            caughtException = exception;
        }
        if ((caughtException == null) || 
            (caughtException.getErrorCode() != DescriptorException.ATTEMPT_TO_REGISTER_DEAD_INDIRECTION)) {
            throwError("Incorrect exception thrown. " + caughtException);
        }
        uow.release();
    }
}

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
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 * @author  smcritch
 */
public class AcquireNestedUnitOfWork_WriteChanges_TestCase extends AutoVerifyTestCase {
    protected Exception exception;

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        UnitOfWork nestedUow = uow.acquireUnitOfWork();
        try {
            nestedUow.writeChanges();
        } catch (Exception e) {
            exception = e;
        } finally {
            nestedUow.release();
            uow.release();
        }
    }

    public void verify() {
        if (exception == null) {
            throw new TestErrorException("Exception not thrown attempting to writeChanges in a nested UnitOfWork.");
        } else if (!(exception instanceof ValidationException)) {
            throw new TestErrorException("Wrong exception type thrown.", exception);
        } else {
            ValidationException ve = (ValidationException)exception;
            if (ve.getErrorCode() != ValidationException.CANNOT_WRITE_CHANGES_ON_NESTED_UNIT_OF_WORK) {
                throw new TestErrorException("Wrong exception thrown.", ve);
            }
        }
    }

    public void reset() {
        exception = null;
    }
}

/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

    @Override
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

    @Override
    public void verify() {
        if (exception == null) {
            throw new TestErrorException("Exception not thrown attempting to writeChanges in a nested UnitOfWork.");
        } else if (!(exception instanceof ValidationException ve)) {
            throw new TestErrorException("Wrong exception type thrown.", exception);
        } else {
            if (ve.getErrorCode() != ValidationException.CANNOT_WRITE_CHANGES_ON_NESTED_UNIT_OF_WORK) {
                throw new TestErrorException("Wrong exception thrown.", ve);
            }
        }
    }

    @Override
    public void reset() {
        exception = null;
    }
}

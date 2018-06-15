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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.framework.*;

public class ReadAllBatchTest extends ReadAllTest {
    public Exception storedException;
    public int expectedExceptionCode;

    public ReadAllBatchTest(Class aClass, int numberOfObjects) {
        super(aClass, numberOfObjects);
        setDescription("Test that the correct exception is thrown when a batch query across a variable 1:1 mapping is attempted. Maybe this will be supported someday.");
        storedException = null;
        expectedExceptionCode = org.eclipse.persistence.exceptions.QueryException.BATCH_READING_NOT_SUPPORTED;
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        storedException = null;
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        storedException = null;
    }

    public void test() {
        try {
            super.test();
        } catch (Exception e) {
            storedException = e;
        }
    }

    public void verify() {
        if (storedException == null) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("NO EXCEPTION THROWN!!!  EXPECTING QueryException");
        }
        if (EclipseLinkException.class.isInstance(storedException)) {
            if (((EclipseLinkException)storedException).getErrorCode() == expectedExceptionCode) {
                return;
            }
        }
        throw new org.eclipse.persistence.testing.framework.TestErrorException("WRONG EXCEPTION THROWN!!!  EXPECTING QueryException");
    }
}

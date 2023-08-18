/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


public abstract class ExceptionTest extends AutoVerifyTestCase {
    public EclipseLinkException caughtException;
    public EclipseLinkException expectedException;

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    protected void verify() {
        if (caughtException == null) {
            throw new TestErrorException("The proper exception was not thrown:" + System.lineSeparator() + "caught exception was null! \n\n[EXPECTING] " + expectedException);
        }

        if (caughtException.getErrorCode() != expectedException.getErrorCode()) {
            throw new TestErrorException("The proper exception was not thrown:" + System.lineSeparator() + "[CAUGHT] " + caughtException + "\n\n[EXPECTING] " + expectedException);
        }
    }
}

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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


public abstract class ExceptionTest extends AutoVerifyTestCase {
    public EclipseLinkException caughtException;
    public EclipseLinkException expectedException;

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void verify() {
        if (caughtException == null) {
            throw new TestErrorException("The proper exception was not thrown:" + org.eclipse.persistence.internal.helper.Helper.cr() + "caught exception was null! \n\n[EXPECTING] " + expectedException);
        }

        if (caughtException.getErrorCode() != expectedException.getErrorCode()) {
            throw new TestErrorException("The proper exception was not thrown:" + org.eclipse.persistence.internal.helper.Helper.cr() + "[CAUGHT] " + caughtException + "\n\n[EXPECTING] " + expectedException);
        }
    }
}

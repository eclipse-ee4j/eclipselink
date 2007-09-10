/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;


public class KeyFromObjecWithoutDescriptor extends ExceptionTest {
    protected void setup() {
        expectedException = ValidationException.missingDescriptor(null);
    }

    protected void test() {
        try {
            getSession().keyFromObject(this);
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}

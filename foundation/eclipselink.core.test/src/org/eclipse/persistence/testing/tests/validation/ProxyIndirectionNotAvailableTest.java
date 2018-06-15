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

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.testing.framework.TestErrorException;


//Created by Ian Reid
//Date: Mar 5, 2k3
//None standard Test

public class ProxyIndirectionNotAvailableTest extends ExceptionTest {
    public ProxyIndirectionNotAvailableTest() {
        super();
        setDescription("This tests Proxy Indirection Not Available (TL-ERROR 159)");
    }

    protected void setup() {
        expectedException = DescriptorException.proxyIndirectionNotAvailable(null);
    }

    public void test() {
        ProxyIndirectionPolicy policy = new ProxyIndirectionPolicy();
        try {
            policy.initialize();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    protected void verify() {
        if (caughtException != null) {
            throw new TestErrorException("The proper exception was not thrown:"
                    + org.eclipse.persistence.internal.helper.Helper.cr() + "[CAUGHT] "
                    + caughtException + "\n\n[EXPECTING] no Exception");
        }
    }

}

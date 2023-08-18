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

    @Override
    protected void setup() {
        expectedException = DescriptorException.proxyIndirectionNotAvailable(null);
    }

    @Override
    public void test() {
        ProxyIndirectionPolicy policy = new ProxyIndirectionPolicy();
        try {
            policy.initialize();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    @Override
    protected void verify() {
        if (caughtException != null) {
            throw new TestErrorException("The proper exception was not thrown:"
                    + System.lineSeparator() + "[CAUGHT] "
                    + caughtException + "\n\n[EXPECTING] no Exception");
        }
    }

}

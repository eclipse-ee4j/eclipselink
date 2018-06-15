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
package org.eclipse.persistence.testing.tests.security;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;

public class SecurityWhileInitializingInstantiationPolicyTest extends ExceptionTestSaveSecurityManager {

    private InstantiationPolicy instantiationPolicy;

    public SecurityWhileInitializingInstantiationPolicyTest() {
        super("This tests security while initializing instantiation policy (TL-ERROR 90)", InstantiationPolicyTestClass.class);
    }

    protected void setup() {
        super.setup();
        expectedException = DescriptorException.securityWhileInitializingInstantiationPolicy(null, null, null);

        instantiationPolicy = new InstantiationPolicy();
        instantiationPolicy.useFactoryInstantiationPolicy((Object)null, "getCity");
        instantiationPolicy.setDescriptor(getTestDescriptor());
    }

    public void test() {
        try {
            instantiationPolicy.initialize((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    static class InstantiationPolicyTestClass {
        public String getCity() {
            //do nothing security manager will cause error to occur
            return "";
        }
    }
}

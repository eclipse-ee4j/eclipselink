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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;

/**
 * Code coverage test for clone copy policy exception.
 */
public class SecurityWhileInitializingCopyPolicyTest extends ExceptionTestSaveSecurityManager {

    private CloneCopyPolicy policy;

    public SecurityWhileInitializingCopyPolicyTest(Class c) {
        super("This tests Security While Initializing Copy Policy (TL-ERROR 89)", c);
    }

    protected void setup() {
        super.setup();
        expectedException = DescriptorException.securityWhileInitializingCopyPolicy("dummy_Method", new RelationalDescriptor(), new Exception());

        //need superclass == null
        getTestDescriptor().setCopyPolicy(new CloneCopyPolicy());
        policy = (CloneCopyPolicy) getTestDescriptor().getCopyPolicy();
        if (Policy.class.getName().equals(getTestClass().getName())) {
            //need not getMethodName() == null
            policy.setMethodName("dummy_Method"); //this method does exist in above class
            //need NoSuchMethod thrown from             this.setMethod(Helper.getDeclaredMethod(this.getDescriptor().getJavaClass(), this.getMethodName(), new Class[0]));
        } else {
            policy.setWorkingCopyMethodName("working_dummy_Method");
        }
    }

    public void test() {
        try {
            policy.initialize(getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    static class Policy {
        public void dummy_Method() {
            //do nothing security manager will cause error to occur
        }
    }

    static class WorkingPolicy {
        public void working_dummy_Method() {
            //do nothing security manager will cause error to occur
        }
    }
}

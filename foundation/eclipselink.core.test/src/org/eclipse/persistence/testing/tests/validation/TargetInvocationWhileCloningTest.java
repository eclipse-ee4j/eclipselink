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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;


/**
 * Code coverage test for clone copy policy exceptions.
 */
public class TargetInvocationWhileCloningTest extends ExceptionTest {
    public TargetInvocationWhileCloningTest() {
        setDescription("This tests Target Invocation While Cloning (TL-ERROR 97)");
    }

    org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems person;
    IntegrityChecker orgIntegrityChecker;
    CloneCopyPolicy policy;
    ClassDescriptor descriptor;

    protected void setup() {
        expectedException = DescriptorException.targetInvocationWhileCloning(null, null, null, null);
        person = new org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems();
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        policy = new CloneCopyPolicy();
        policy.setMethodName("getIllegalAccess");
        policy.setDescriptor(descriptor);

        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker()); //moved into setup
        getSession().getIntegrityChecker().dontCatchExceptions(); //moved into setup
    }

    public void reset() {
        getSession().setIntegrityChecker(orgIntegrityChecker);

    }

    public void test() {
        try {
            policy.initialize(getSession());
            policy.buildClone(person, getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}

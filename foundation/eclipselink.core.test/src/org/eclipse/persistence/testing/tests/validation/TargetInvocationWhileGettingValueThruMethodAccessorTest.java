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
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;


//Created by Ian
//Date: Mar 19, 2k3

public class TargetInvocationWhileGettingValueThruMethodAccessorTest extends ExceptionTest {

    public TargetInvocationWhileGettingValueThruMethodAccessorTest() {
        setDescription("This tests Target Invocation While Getting Value Thru Method Accessor (TL-ERROR 99)");
    }
    org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems person;
    DirectToFieldMapping mapping;
    IntegrityChecker orgIntegrityChecker;

    protected void setup() {
        expectedException = DescriptorException.targetInvocationWhileGettingValueThruMethodAccessor(null, null, null);
        person = new org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems();
        mapping = new DirectToFieldMapping();
        mapping.setAttributeName("illegalAccess");
        mapping.setFieldName("EMPLOYEE.F_NAME");
        mapping.setSetMethodName("setFirstName");
        mapping.setGetMethodName("getIllegalAccess");
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker()); //moved into setup
        getSession().getIntegrityChecker().dontCatchExceptions(); //moved into setup
    }

    public void reset() {

        getSession().setIntegrityChecker(orgIntegrityChecker);

    }

    public void test() {
        try {
            ((MethodAttributeAccessor)mapping.getAttributeAccessor()).initializeAttributes(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
            mapping.getAttributeValueFromObject(person);
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}

/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.mappings.DirectToFieldMapping;


//Created by Ian
//Date: Mar 19, 2k3

public class TargetInvocationWhileSettingValueThruMethodAccessorTest extends ExceptionTest {
    public TargetInvocationWhileSettingValueThruMethodAccessorTest() {
        setDescription("This tests Target Invocation While Setting Value Thru Method Accessor (TL-ERROR 106)");
    }

    org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems person;
    DirectToFieldMapping mapping;
    IntegrityChecker orgIntegrityChecker;

    @Override
    protected void setup() {
        expectedException = DescriptorException.targetInvocationWhileSettingValueThruMethodAccessor(null, null, null);
        person = new org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems();
        mapping = new DirectToFieldMapping();
        mapping.setAttributeName("illegalAccess");
        mapping.setFieldName("EMPLOYEE.F_NAME");
        mapping.setSetMethodName("setIllegalAccess");
        mapping.setGetMethodName("getFirstName");
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker()); //moved into setup
        getSession().getIntegrityChecker().dontCatchExceptions(); //moved into setup
    }

    @Override
    public void reset() {
        getSession().setIntegrityChecker(orgIntegrityChecker);

    }

    @Override
    public void test() {
        try {
            mapping.getAttributeAccessor().initializeAttributes(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
            mapping.setAttributeValueInObject(person, "invalid_data");
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}

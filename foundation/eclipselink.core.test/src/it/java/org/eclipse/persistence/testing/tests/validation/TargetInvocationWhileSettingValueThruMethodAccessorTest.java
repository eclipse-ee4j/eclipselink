/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

public class TargetInvocationWhileSettingValueThruMethodAccessorTest extends ExceptionTest {
    public TargetInvocationWhileSettingValueThruMethodAccessorTest() {
        setDescription("This tests Target Invocation While Setting Value Thru Method Accessor (TL-ERROR 106)");
    }

    org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems person;
    DirectToFieldMapping mapping;
    IntegrityChecker orgIntegrityChecker;

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

    public void reset() {
        getSession().setIntegrityChecker(orgIntegrityChecker);

    }

    public void test() {
        try {
            ((MethodAttributeAccessor)mapping.getAttributeAccessor()).initializeAttributes(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
            mapping.setAttributeValueInObject(person, "invalid_data");
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}

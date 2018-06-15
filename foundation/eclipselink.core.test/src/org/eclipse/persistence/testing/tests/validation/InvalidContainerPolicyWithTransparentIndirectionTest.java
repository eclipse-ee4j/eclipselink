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
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.mappings.OneToManyMapping;


//Created by Ian Reid
//Date: Mar 5, 2k3

public class InvalidContainerPolicyWithTransparentIndirectionTest extends ExceptionTest {

    IntegrityChecker orgIntegrityChecker;

    public InvalidContainerPolicyWithTransparentIndirectionTest() {
        super();
        setDescription("This tests Invalid Container Policy With Transparent Indirection (TL-ERROR 148)");
    }

    protected void setup() {
        expectedException = DescriptorException.invalidContainerPolicyWithTransparentIndirection(null, null);
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        TransparentIndirectionPolicy policy = new TransparentIndirectionPolicy();
        OneToManyMapping mapping = new OneToManyMapping();
        mapping.setAttributeName("manager");
        mapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        mapping.addTargetForeignKeyFieldName("EMPLOYEE.MANAGER_ID", "EMPLOYEE.EMP_ID");
        policy.setMapping(mapping);
        try {
            policy.validateContainerPolicy(getSession().getIntegrityChecker());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}

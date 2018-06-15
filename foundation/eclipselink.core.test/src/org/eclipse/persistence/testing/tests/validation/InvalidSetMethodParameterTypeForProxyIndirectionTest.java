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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.mappings.OneToManyMapping;


//Created by Ian Reid
//Date: Mar 5, 2k3

public class InvalidSetMethodParameterTypeForProxyIndirectionTest extends ExceptionTest {
    public InvalidSetMethodParameterTypeForProxyIndirectionTest() {
        setDescription("This tests Invalid Set Method Parameter Type For Proxy Indirection (TL-ERROR 162)");
    }

    IntegrityChecker orgIntegrityChecker;
    Class attributeType = org.eclipse.persistence.testing.models.employee.domain.Employee.class;
    Class[] targetInterfaces = { InvalidAttributeTypeForProxyIndirectionTest.class };
    OneToManyMapping mapping = new OneToManyMapping();

    protected void setup() {
        //setup need to remove setup null pointer thrown error
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(attributeType);
        mapping.setDescriptor(descriptor);
        mapping.setAttributeName("manager");

        expectedException = DescriptorException.invalidSetMethodParameterTypeForProxyIndirection(attributeType, targetInterfaces, mapping);
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        ProxyIndirectionPolicy policy = new ProxyIndirectionPolicy();
        mapping.setReferenceClass(attributeType);
        mapping.addTargetForeignKeyFieldName("EMPLOYEE.MANAGER_ID", "EMPLOYEE.EMP_ID");
        policy.setMapping(mapping);
        try {
            policy.validateSetMethodParameterType(attributeType, getSession().getIntegrityChecker());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}

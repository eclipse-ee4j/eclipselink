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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


public class GetMethodReturnsValueHolderTest extends ExceptionTest {
    public GetMethodReturnsValueHolderTest() {
        super();
        setDescription("This test contains a descriptor with a mapping that uses indirection, and that " + "mapping's get method returns a ValueHolder instead of a ValueHolderInterface.");
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor employeeDescriptor = new RelationalDescriptor();
        employeeDescriptor.setJavaClass(Employee.class);
        employeeDescriptor.setTableName("VAL_EMP");
        employeeDescriptor.setPrimaryKeyFieldName("ID");

        OneToOneMapping addressMapping = new OneToOneMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(Address.class);
        addressMapping.setSetMethodName("setAddress");
        addressMapping.setGetMethodName("getAddress");

        employeeDescriptor.addMapping(addressMapping);
        return employeeDescriptor;
    }

    protected void setup() {
        expectedException = DescriptorException.returnAndMappingWithIndirectionMismatch(new DirectToFieldMapping());
    }

    public void test() {
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();
            ((DatabaseSession)getSession()).addDescriptor(descriptor());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}

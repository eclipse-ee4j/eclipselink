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
import org.eclipse.persistence.sessions.DatabaseSession;


public class SequenceFieldNameNotSetTest extends ExceptionTest {
    public SequenceFieldNameNotSetTest() {
        super();
        setDescription("This test attempts to register a descriptor that uses sequencing but doesn't set the sequence field name.");
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor employeeDescriptor = new RelationalDescriptor();
        employeeDescriptor.setJavaClass(Employee.class);
        employeeDescriptor.setTableName("VAL_EMP");
        employeeDescriptor.setPrimaryKeyFieldName("ID");
        employeeDescriptor.setSequenceNumberName("SEQ");
        return employeeDescriptor;
    }

    protected void setup() {
        expectedException = DescriptorException.sequenceNumberPropertyNotSpecified(null);
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

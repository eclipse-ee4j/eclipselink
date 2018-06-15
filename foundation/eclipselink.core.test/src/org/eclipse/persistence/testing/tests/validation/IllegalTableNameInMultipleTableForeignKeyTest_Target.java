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
import org.eclipse.persistence.internal.sessions.AbstractSession;


//Created by Ian Reid
//Date: Feb 26, 2k3

public class IllegalTableNameInMultipleTableForeignKeyTest_Target extends ExceptionTest {

    IntegrityChecker orgIntegrityChecker;

    public IllegalTableNameInMultipleTableForeignKeyTest_Target() {
        setDescription("This tests Illegal Table Name In Multiply Table Foreign Key (Target) (TL-ERROR 135)");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        expectedException = DescriptorException.illegalTableNameInMultipleTableForeignKeyField(null, null);

        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            descriptor().preInitialize((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addTableName("SALARY");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        descriptor.addForeignKeyFieldNameForMultipleTable("SALARY_BAD.EMP_ID", "EMPLOYEE.EMP_ID");

        return descriptor;
    }

}

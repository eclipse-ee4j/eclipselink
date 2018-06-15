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
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Jan 31, 2k3

public class NoMappingForPrimaryKeyTest extends ExceptionTest {

    public NoMappingForPrimaryKeyTest() {
        super();
        setDescription("This tests No Mapping For Primary Key (TL-ERROR 46) " + "");
    }

    protected void setup() {
        expectedException = DescriptorException.noMappingForPrimaryKey(null, null);
        orgDescriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        orgIntegrityChecker = getSession().getIntegrityChecker();
    }
    ClassDescriptor orgDescriptor;
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        ((DatabaseSession)getSession()).getDescriptors().remove(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        if (orgDescriptor != null)
            ((DatabaseSession)getSession()).addDescriptor(orgDescriptor);
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
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

    public RelationalDescriptor descriptor() {
        RelationalDescriptor employeeDescriptor = new RelationalDescriptor();
        employeeDescriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        employeeDescriptor.setTableName("EMPLOYEE");
        employeeDescriptor.setPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        //if the following is missing then the correct error will occure.
        /*
  DirectToFieldMapping idMapping = new DirectToFieldMapping();
      idMapping.setAttributeName("id");
      idMapping.setFieldName("EMPLOYEE.EMP_ID");
      employeeDescriptor.addMapping(idMapping);
    */

        return employeeDescriptor;
    }


}

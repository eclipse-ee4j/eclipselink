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
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Feb 17, 2k3
//uses class org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems

public class NoSuchMethodWhileConvertingToMethodTest extends ExceptionTest {
    public NoSuchMethodWhileConvertingToMethodTest() {
        super();
        setDescription("This tests No Such Method While Converting To Method (TL-ERROR 58) " + " uses EmployeeWithProblems class");
    }

    protected void setup() {
        expectedException = DescriptorException.noSuchMethodWhileConvertingToMethod(null, null, null);
        orgDescriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    ClassDescriptor orgDescriptor;
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        ((DatabaseSession)getSession()).getDescriptors().remove(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        if (orgDescriptor != null) {
            ((DatabaseSession)getSession()).addDescriptor(orgDescriptor);
        }
        if (orgIntegrityChecker != null) {
            getSession().setIntegrityChecker(orgIntegrityChecker);
        }
    }

    public void test() {
        try {
            ((DatabaseSession)getSession()).addDescriptor(descriptor());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        //the following helps cause the correct error to occure.
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);

        descriptor.addTableName("EMPLOYEE");
        descriptor.addTableName("ADDRESS");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteFullIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("EMP_ID");
        descriptor.setSequenceNumberName("EMP_SEQ");


        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);


        TransformationMapping normalHoursMapping = new TransformationMapping();
        normalHoursMapping.setAttributeName("normalHours");
        normalHoursMapping.setAttributeTransformation("buildNormalHours"); //setFemale

        //the following causes the correct error to occure.
        normalHoursMapping.addFieldTransformation("EMPLOYEE.START_TIME", "getStartTime_BAD");
        //the following causes the correct error to occure.
        normalHoursMapping.addFieldTransformation("EMPLOYEE.END_TIME", "getEndTime_BAD");
        descriptor.addMapping(normalHoursMapping);

        return descriptor;
    }
}

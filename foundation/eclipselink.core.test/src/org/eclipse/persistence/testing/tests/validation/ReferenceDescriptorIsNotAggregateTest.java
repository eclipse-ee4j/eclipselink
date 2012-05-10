/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Feb 6, 2k3

public class ReferenceDescriptorIsNotAggregateTest extends ExceptionTestSaveDescriptor {
    public ReferenceDescriptorIsNotAggregateTest() {
        super();
        setDescription("This tests Reference Descriptor Is Not Aggregate (TL-ERROR 77) " + "");
    }

    protected void setup() {
        expectedException = DescriptorException.referenceDescriptorIsNotAggregate(null, null);
        super.setup();
    }

    public void test() {
        try {
            ((DatabaseSession)getSession()).addDescriptor(buildEmployeeDescriptor());
            ((DatabaseSession)getSession()).addDescriptor(buildEmploymentPeriodDescriptor());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor buildEmployeeDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addTableName("SALARY");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Interface properties.
        //	descriptor.getInterfacePolicy().addParentInterface(org.eclipse.persistence.testing.models.employee.interfaces.Employee.class);

        // Descriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteFullIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("EMP_ID");
        descriptor.setSequenceNumberName("EMP_SEQ");

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Event manager.

        // Mappings.

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);


        AggregateObjectMapping periodMapping = new AggregateObjectMapping();
        periodMapping.setAttributeName("period");
        //	periodMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod.class);
        periodMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        //works	periodMapping.setReferenceClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        periodMapping.setIsNullAllowed(true);
        descriptor.addMapping(periodMapping);

        return descriptor;
    }

    public RelationalDescriptor buildEmploymentPeriodDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        //	descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod.class);
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        //works	descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);


        //if the following is missing then the correct error will occure.  
        //	descriptor.descriptorIsAggregate();

        descriptor.addTableName("EMPLOYEE"); //added to prevent error tl-94
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID"); //added to prevent ERRROR 74 and 59
        //added to prevent error - 46 
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        // Descriptor properties.

        // Query manager.

        // Event manager.

        // Mappings.
        /*DirectToFieldMapping endDateMapping = new DirectToFieldMapping();
	endDateMapping.setAttributeName("endDate");
	endDateMapping.setFieldName("END_DATE");
	descriptor.addMapping(endDateMapping);
	
	DirectToFieldMapping startDateMapping = new DirectToFieldMapping();
	startDateMapping.setAttributeName("startDate");
	startDateMapping.setFieldName("START_DATE");
	descriptor.addMapping(startDateMapping);
*/


        return descriptor;
    }
}

/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Feb 24, 2k3

public class ReferenceDescriptorIsNotAggreagteCollectionTest extends ExceptionTestSaveDescriptor {
    public ReferenceDescriptorIsNotAggreagteCollectionTest() {
        super();
        setDescription("This tests Reference Descriptor Is Not Aggregate Collection (TL-ERROR 153) " + "");
    }

    protected void setup() {
        expectedException = DescriptorException.referenceDescriptorIsNotAggregateCollection(null, null);
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


        org.eclipse.persistence.mappings.AggregateCollectionMapping periodMapping = new org.eclipse.persistence.mappings.AggregateCollectionMapping();
        periodMapping.setAttributeName("periodWithProblems");
        periodMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod.class);
        //periodMapping.setIsNullAllowed(true);
        periodMapping.setUsesIndirection(false);
        periodMapping.setIsPrivateOwned(true);
        periodMapping.setIsReadOnly(false);
        descriptor.addMapping(periodMapping);
        /*
	// SECTION: AGGREGATECOLLECTIONMAPPING
	org.eclipse.persistence.mappings.AggregateCollectionMapping aggregatecollectionmapping = new org.eclipse.persistence.mappings.AggregateCollectionMapping();
	aggregatecollectionmapping.setAttributeName("phones");
	aggregatecollectionmapping.setIsReadOnly(false );
	aggregatecollectionmapping.setUsesIndirection(false);
	aggregatecollectionmapping.setGetMethodName("getPhones");
	aggregatecollectionmapping.setSetMethodName("setPhones");
	aggregatecollectionmapping.setIsPrivateOwned(true);
	aggregatecollectionmapping.setReferenceClass(org.eclipse.persistence.testing.models.insurance.Phone.class);
	aggregatecollectionmapping.addTargetForeignKeyFieldName("INS_PHONE.HOLDER_SSN","HOLDER.SSN");
	descriptor.addMapping(aggregatecollectionmapping);	
 */

        return descriptor;
    }

    public RelationalDescriptor buildEmploymentPeriodDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod.class);

        //if the following is missing then the correct error will occure.  
        //  descriptor.descriptorIsAggregateCollection();

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

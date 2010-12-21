/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;


//Created by Ian
//Date: Mar 18, 2k3

public class IllegalAccessWhileGettingValueThruInstanceVariableAccessorTest extends ExceptionTestSaveDescriptor {
    public IllegalAccessWhileGettingValueThruInstanceVariableAccessorTest() {
        setDescription("This tests Access While Getting Value Thru Instance Variable Accessor (TL-ERROR 13)");
    }

    protected void setup() {
        expectedException = DescriptorException.illegalAccesstWhileGettingValueThruInstanceVaraibleAccessor(null, null, null);
        //  	getAbstractSession().beginTransaction();
        //    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();    
        super.setup();
    }

    public void reset() {
        //   getAbstractSession().rollbackTransaction();
        //   getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        super.reset();
    }

    public void test() {
        org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems person = new org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems();
        //  person.setName("Person");
        String address = "Test_data";
        try {
            //    ((DatabaseSession) getSession()).addDescriptor(descriptor());

            //      UnitOfWork uow = ((DatabaseSession) getSession()).acquireUnitOfWork();
            //     uow.registerObject(person);
            //      uow.commit();

            DatabaseMapping dMapping = descriptor().getMappingForAttributeName("illegalAccess");
            //   DatabaseMapping idMapping = descriptor().getMappingForAttributeName("id");
            //InstanceVariableAttributeAccessor    MethodAttributeAccessor
            ((MethodAttributeAccessor)dMapping.getAttributeAccessor()).initializeAttributes(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
            //    ((InstanceVariableAttributeAccessor)dMapping.getAttributeAccessor()).initializeAttributes(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class); 
            address = (String)dMapping.getAttributeValueFromObject(person);

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteSoftCacheWeakIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("EMP_ID");
        descriptor.setSequenceNumberName("EMP_SEQ");
        descriptor.setAlias("EMPLOYEE");

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        //Named Queries

        // Event manager.

        // Mappings.
        DirectToFieldMapping p_idMapping = new DirectToFieldMapping();
        p_idMapping.setAttributeName("id");
        p_idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(p_idMapping);

        DirectToFieldMapping p_nameMapping = new DirectToFieldMapping();
        p_nameMapping.setAttributeName("illegalAccess");
        p_nameMapping.setFieldName("EMPLOYEE.F_NAME");
        p_nameMapping.setSetMethodName("setIllegalAccess");
        p_nameMapping.setGetMethodName("getIllegalAccess");
        descriptor.addMapping(p_nameMapping);

        return descriptor;
    }

}

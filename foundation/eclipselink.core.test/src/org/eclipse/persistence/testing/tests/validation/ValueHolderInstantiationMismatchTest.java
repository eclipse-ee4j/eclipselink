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
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;


//Created by Ian Reid
//Date: Feb 17, 2k3

public class ValueHolderInstantiationMismatchTest extends ExceptionTest {
    public ValueHolderInstantiationMismatchTest() {
        super();
        setDescription("This tests Value Holder Instantiation Mismatch Test (TL-ERROR 125) " + "");
    }

    protected void setup() {
        expectedException = DescriptorException.valueHolderInstantiationMismatch(new java.util.Vector(), new OneToOneMapping());
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getAbstractSession().beginTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();

            //the following causes the correct error to occure. 
            RelationalDescriptor descriptor = descriptor();
            OneToOneMapping dMapping = (OneToOneMapping)descriptor.getMappingForAttributeName("addressWithProblems");
            org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems person = new org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems();
            BasicIndirectionPolicy indirectionPolicy = (BasicIndirectionPolicy)dMapping.getIndirectionPolicy();
            indirectionPolicy.validateAttributeOfInstantiatedObject(new java.util.Vector());

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

        OneToOneMapping addressMapping = new OneToOneMapping();
        //the following causes the correct error to occure.   
        addressMapping.setAttributeName("addressWithProblems");
        addressMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Address.class);
        addressMapping.useBasicIndirection();
        addressMapping.privateOwnedRelationship();
        addressMapping.addForeignKeyFieldName("EMPLOYEE.ADDR_ID", "ADDRESS.ADDRESS_ID");
        descriptor.addMapping(addressMapping);

        return descriptor;
    }
}

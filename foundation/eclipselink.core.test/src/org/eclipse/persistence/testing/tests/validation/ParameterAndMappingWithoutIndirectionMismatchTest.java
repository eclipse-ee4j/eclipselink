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
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;


//Created by Vesna
//Feb 2k3
//uses class org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder


public class ParameterAndMappingWithoutIndirectionMismatchTest extends ExceptionTest {

    ClassDescriptor orgDescriptor;
    IntegrityChecker orgIntegrityChecker;

    public ParameterAndMappingWithoutIndirectionMismatchTest() {
        super();
        setDescription("This tests Parameter And Mapping Without Indirection Mismatch (TL-ERROR 130)");
    }

    protected void setup() {
        expectedException = DescriptorException.parameterAndMappingWithoutIndirectionMismatch(new OneToOneMapping());
        orgIntegrityChecker = getSession().getIntegrityChecker();
    }

    public void reset() {
        if (orgIntegrityChecker != null)
            getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            getSession().setIntegrityChecker(new IntegrityChecker());
            getSession().getIntegrityChecker().dontCatchExceptions();

            ForeignReferenceMapping addressMapping = (ForeignReferenceMapping)descriptor().getMappingForAttributeName("address");
            addressMapping.getIndirectionPolicy().validateSetMethodParameterType(ClassConstants.ValueHolderInterface_Class, getSession().getIntegrityChecker());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }


    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("p_id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        idMapping.setGetMethodName("getId");
        idMapping.setSetMethodName("setId");
        descriptor.addMapping(idMapping);

        OneToOneMapping addressMapping = new OneToOneMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Address.class);
        addressMapping.dontUseIndirection();
        addressMapping.setGetMethodName("getAddressHolder");
        addressMapping.setSetMethodName("setAddressHolder");
        addressMapping.addForeignKeyFieldName("EMPLOYEE.ADDR_ID", "ADDRESS.ADDRESS_ID");
        descriptor.addMapping(addressMapping);

        return descriptor;
    }
}


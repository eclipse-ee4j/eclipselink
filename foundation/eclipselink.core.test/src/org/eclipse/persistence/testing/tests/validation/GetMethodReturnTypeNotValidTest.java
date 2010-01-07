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
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;


//Created by Vesna
//Feb 2k3
//uses class org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder


public class GetMethodReturnTypeNotValidTest extends ExceptionTest {
    IntegrityChecker orgIntegrityChecker;

    public GetMethodReturnTypeNotValidTest() {
        super();
        setDescription("This tests Get Method Return Type Not Valid (TL-ERROR 131)");
    }

    protected void setup() {
        expectedException = DescriptorException.getMethodReturnTypeNotValid(new OneToManyMapping());
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        ForeignReferenceMapping phoneNumbersMapping = (ForeignReferenceMapping)descriptor().getMappingForAttributeName("phoneNumbers");
        try {
            phoneNumbersMapping.getIndirectionPolicy().validateGetMethodReturnTypeForCollection(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class, getSession().getIntegrityChecker());
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
        ((MethodAttributeAccessor)idMapping.getAttributeAccessor()).initializeAttributes(PersonWithValueHolder.class);
        descriptor.addMapping(idMapping);

        OneToManyMapping phoneNumbersMapping = new OneToManyMapping();
        phoneNumbersMapping.setAttributeName("phoneNumbers");
        phoneNumbersMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class);
        phoneNumbersMapping.dontUseIndirection();
        //phoneNumbersMapping.useTransparentCollection();
        phoneNumbersMapping.setSetMethodName("setPhones");
        phoneNumbersMapping.setGetMethodName("getPhones");
        phoneNumbersMapping.addTargetForeignKeyFieldName("PHONE.EMP_ID", "EMPLOYEE.EMP_ID");
        ((MethodAttributeAccessor)phoneNumbersMapping.getAttributeAccessor()).initializeAttributes(PersonWithValueHolder.class);
        descriptor.addMapping(phoneNumbersMapping);
        return descriptor;
    }
}

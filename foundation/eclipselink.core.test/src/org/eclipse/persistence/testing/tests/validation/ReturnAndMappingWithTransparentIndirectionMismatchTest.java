/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Vesna
//Feb 2k3
//uses class org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder


public class ReturnAndMappingWithTransparentIndirectionMismatchTest extends ExceptionTestSaveDescriptor {

    public ReturnAndMappingWithTransparentIndirectionMismatchTest() {
        super();
        setDescription("This tests Return And Mapping With Transparent Indirection MismatchTest (TL-ERROR 139)");
    }

    protected void setup() {
        expectedException = DescriptorException.returnAndMappingWithTransparentIndirectionMismatch(new OneToManyMapping(), null, null);
        super.setup();
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
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("p_id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        OneToManyMapping phoneNumbersMapping = new OneToManyMapping();
        phoneNumbersMapping.setAttributeName("phoneNumbers");
        phoneNumbersMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class);
        phoneNumbersMapping.dontUseIndirection();
        phoneNumbersMapping.useTransparentCollection();
        phoneNumbersMapping.setSetMethodName("setPhones");
        phoneNumbersMapping.setGetMethodName("getPhones");
        //phoneNumbersMapping.privateOwnedRelationship();
        phoneNumbersMapping.addTargetForeignKeyFieldName("PHONE.EMP_ID", "EMPLOYEE.EMP_ID");
        descriptor.addMapping(phoneNumbersMapping);


        return descriptor;
    }
}

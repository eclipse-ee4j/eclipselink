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
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Vesna
//Feb 2k3
//uses class org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder

public class ReturnAndMappingWithoutIndirectionMismatchTest extends ExceptionTestSaveDescriptor {
    public ReturnAndMappingWithoutIndirectionMismatchTest() {
        super();
        setDescription("This tests Return And Mapping Without Indirection Mismatch (TL-ERROR 128)");
    }

    protected void setup() {
        expectedException = DescriptorException.returnAndMappingWithoutIndirectionMismatch(new OneToOneMapping());
        super.setup();
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
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("p_id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        OneToOneMapping addressMapping = new OneToOneMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Address.class);
        //addressMapping.useBasicIndirection();
        addressMapping.dontUseIndirection();
        addressMapping.setGetMethodName("getAddressHolder");
        addressMapping.setSetMethodName("setAddressHolder");
        addressMapping.addForeignKeyFieldName("EMPLOYEE.ADDR_ID", "ADDRESS.ADDRESS_ID");
        descriptor.addMapping(addressMapping);

        return descriptor;
    }


}

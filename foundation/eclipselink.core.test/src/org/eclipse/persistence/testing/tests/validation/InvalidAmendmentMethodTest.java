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
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Vesna
//Feb 2k3
//uses class org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class, org.eclipse.persistence.testing.tests.validation.AmendmentClass


public class InvalidAmendmentMethodTest extends ExceptionTestSaveDescriptor {
    public InvalidAmendmentMethodTest() {
        setDescription("This tests Invalid Amendment Method Test (TL-ERROR 164)");
    }

    protected void setup() {
        super.setup();
        expectedException = DescriptorException.invalidAmendmentMethod(AmendmentClass.class, "modifyPhoneDescriptor", null, null);
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
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class);
        descriptor.addTableName("PHONE");
        descriptor.addPrimaryKeyFieldName("PHONE.TYPE");

        // Descriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteFullIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setAmendmentClass(AmendmentClass.class);
        descriptor.setAmendmentMethodName("modifyPhoneDescriptor");

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Event manager.

        // Query keys.
        descriptor.addDirectQueryKey("id", "EMP_ID");

        // Mappings.

        DirectToFieldMapping typeMapping = new DirectToFieldMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setFieldName("PHONE.TYPE");
        descriptor.addMapping(typeMapping);

        descriptor.applyAmendmentMethod();
        return descriptor;
    }
}

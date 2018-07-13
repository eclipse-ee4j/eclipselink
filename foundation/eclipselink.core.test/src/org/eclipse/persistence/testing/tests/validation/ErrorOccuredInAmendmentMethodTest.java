/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Vesna
//Feb 2k3
//uses class org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class, org.eclipse.persistence.testing.tests.validation.AmendmentClass


public class ErrorOccuredInAmendmentMethodTest extends ExceptionTestSaveDescriptor {

    public ErrorOccuredInAmendmentMethodTest() {
        super();
        setDescription("This tests Error Occured In Amendment Method Test (TL-ERROR 165)");
    }

    protected void setup() {
        expectedException = DescriptorException.errorOccuredInAmendmentMethod(AmendmentClass.class, "modifyPhoneDescriptorAgain", null, null);
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
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class);
        descriptor.addTableName("PHONE");
        descriptor.addPrimaryKeyFieldName("PHONE.TYPE");

        // Descriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteFullIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setAmendmentClass(AmendmentClass.class);
        descriptor.setAmendmentMethodName("modifyPhoneDescriptorAgain");

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

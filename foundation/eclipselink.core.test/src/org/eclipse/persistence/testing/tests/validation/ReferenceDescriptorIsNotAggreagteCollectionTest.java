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
        //    descriptor.getInterfacePolicy().addParentInterface(org.eclipse.persistence.testing.models.employee.interfaces.Employee.class);

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


        org.eclipse.persistence.mappings.AggregateCollectionMapping addressesMapping = new org.eclipse.persistence.mappings.AggregateCollectionMapping();
        addressesMapping.setAttributeName("addressesWithProblems");
        addressesMapping.setReferenceClass(org.eclipse.persistence.testing.models.employee.domain.Address.class);
        addressesMapping.setUsesIndirection(false);
        addressesMapping.setIsPrivateOwned(true);
        addressesMapping.setIsReadOnly(false);
        descriptor.addMapping(addressesMapping);

        return descriptor;
    }
}

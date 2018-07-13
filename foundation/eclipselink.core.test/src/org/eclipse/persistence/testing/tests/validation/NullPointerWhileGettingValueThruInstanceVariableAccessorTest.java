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
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

public class NullPointerWhileGettingValueThruInstanceVariableAccessorTest extends ExceptionTest {
    public NullPointerWhileGettingValueThruInstanceVariableAccessorTest() {
        super();
        setDescription("This tests Null Pointer While Getting Value Thru Instance Variable Accessor (TL-ERROR 69)");
    }

    protected void setup() {
        expectedException = DescriptorException.nullPointerWhileGettingValueThruInstanceVariableAccessor("p_name", "Person", null);

    }

    public void test() {
        try {
            DatabaseMapping dMapping = descriptor().getMappingForAttributeName("p_name");
            String attributeName = dMapping.getAttributeName();
            dMapping.getAttributeAccessor().getAttributeValueFromObject(attributeName);
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.PersonInstanceAccess.class);
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
        p_idMapping.setAttributeName("p_id");
        p_idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(p_idMapping);

        DirectToFieldMapping p_nameMapping = new DirectToFieldMapping();
        p_nameMapping.setAttributeName("p_name");
        p_nameMapping.setFieldName("EMPLOYEE.F_NAME");
        descriptor.addMapping(p_nameMapping);

        return descriptor;
    }
}


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
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

public class NullPointerWhileGettingValueThruMethodAccessorTest extends ExceptionTest {
    public NullPointerWhileGettingValueThruMethodAccessorTest() {
        super();
        setDescription("This tests Null Pointer While Getting Value Thru Method Accessor (TL-ERROR 70)");
    }

    protected void setup() {
        expectedException = DescriptorException.nullPointerWhileGettingValueThruMethodAccessor("getName", "Person", null);
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
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.PersonMethodAccess.class);
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
        p_nameMapping.setGetMethodName("getName");
        p_nameMapping.setSetMethodName("setName");
        descriptor.addMapping(p_nameMapping);

        return descriptor;
    }

}


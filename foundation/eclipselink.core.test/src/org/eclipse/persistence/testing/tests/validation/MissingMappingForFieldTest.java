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
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;


//Created by Ian Reid
//Date: Feb 25, 2k3

public class MissingMappingForFieldTest extends ExceptionTest {
    public MissingMappingForFieldTest() {
        super();
        setDescription("This tests Missing Mapping For Field (TL-ERROR 45)");
    }

    protected void setup() {
        expectedException = DescriptorException.missingMappingForField(null, null);
    }

    public void test() {
        RelationalDescriptor descriptor = descriptor();
        org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems person = new org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems();

        OneToOneMapping dMapping = (OneToOneMapping)descriptor.getMappingForAttributeName("manager");
        DatabaseField dField = dMapping.getForeignKeyFields().firstElement();
        //the following causes the correct error to occure. 
        descriptor.getObjectBuilder().getMappingsByField().remove(dField);

        try {
            descriptor.getObjectBuilder().extractValueFromObjectForField(person, dField, (AbstractSession)getSession());
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
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteSoftCacheWeakIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("EMP_ID");
        descriptor.setSequenceNumberName("EMP_SEQ");
        descriptor.setAlias("EMPLOYEE");

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        OneToOneMapping managerMapping = new OneToOneMapping();
        managerMapping.setAttributeName("manager");
        managerMapping.setReferenceClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        managerMapping.useBasicIndirection();
        managerMapping.addForeignKeyFieldName("EMPLOYEE.MANAGER_ID", "EMPLOYEE.EMP_ID");
        descriptor.addMapping(managerMapping);

        return descriptor;
    }

}

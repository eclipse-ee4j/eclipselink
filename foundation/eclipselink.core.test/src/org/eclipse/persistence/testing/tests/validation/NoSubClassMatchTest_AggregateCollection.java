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
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Mar 3, 2k3

public class NoSubClassMatchTest_AggregateCollection extends ExceptionTestSaveDescriptor {
    public NoSubClassMatchTest_AggregateCollection() {
        setDescription("This tests No Sub Class Match (AggregateCollectionMapping) (TL-ERROR 126) ");
    }

    protected void setup() {
        expectedException = DescriptorException.noSubClassMatch(NoSubClassMatchTest_AggregateCollection.class, new AggregateCollectionMapping());
        super.setup();
    }

    public void test() {

        ((DatabaseSession)getSession()).addDescriptor(descriptor());

        AggregateCollectionMapping mapping = new AggregateCollectionMapping();
        mapping.setReferenceClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        mapping.setSelectionSQLString("SELECT * FROM TABLE");
        mapping.initialize((AbstractSession)getSession());

        try {
            mapping.getReferenceDescriptor(NoSubClassMatchTest_AggregateCollection.class, (AbstractSession)getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        descriptor.addTableName("EMPLOYEE");
        //	descriptor.addTableName("ADDRESS");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteFullIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("EMP_ID");
        descriptor.setSequenceNumberName("EMP_SEQ");


        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        descriptor.descriptorIsAggregateCollection();
        return descriptor;
    }

}

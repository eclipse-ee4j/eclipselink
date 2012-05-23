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

import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.sessions.DatabaseSession;

//import org.eclipse.persistence.mappings.*;
//import org.eclipse.persistence.internal.queries.*;

//Created by Ian Reid
//Date: Mar 3, 2k3

public class NoSubClassMatchTest_AggregateObject extends ExceptionTest {

    IntegrityChecker orgIntegrityChecker;
    org.eclipse.persistence.mappings.AggregateObjectMapping mapping;
    org.eclipse.persistence.internal.sessions.AggregateChangeRecord changeRecord;
    org.eclipse.persistence.descriptors.InheritancePolicy orgInheritancePolicy;

    public NoSubClassMatchTest_AggregateObject() {
        setDescription("This tests No Sub Class Match (AggregateObjectMapping) (TL-ERROR 126) ");
    }

    protected void setup() {
        expectedException = DescriptorException.noSubClassMatch(null, new AggregateObjectMapping());

        Employee employee = new Employee();

        ClassDescriptor descriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        ClassDescriptor projDescriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class);
        mapping = (org.eclipse.persistence.mappings.AggregateObjectMapping)descriptor.getMappingForAttributeName("period");
        orgInheritancePolicy = mapping.getReferenceDescriptor().getInheritancePolicyOrNull();
        org.eclipse.persistence.internal.sessions.ObjectChangeSet changeSet = new ObjectChangeSet(new Vector(), projDescriptor, employee, new org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet(), true);
        changeRecord = new org.eclipse.persistence.internal.sessions.AggregateChangeRecord(changeSet);
        changeRecord.setChangedObject(changeSet);

        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        getSession().setIntegrityChecker(orgIntegrityChecker);
        //This is needed because hidden in the test, getInheritancePolicy() is triggered that does lazy initialization.
        mapping.getReferenceDescriptor().setInheritancePolicy(orgInheritancePolicy);
    }

    public void test() {
        try {
            mapping.writeFromObjectIntoRowWithChangeRecord(changeRecord, new org.eclipse.persistence.sessions.DatabaseRecord(), (AbstractSession)getSession(), WriteType.UNDEFINED);
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}

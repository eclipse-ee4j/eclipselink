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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;

public class EmployeeJoinFetchTestModel extends TestModel {
    
    public EmployeeJoinFetchTestModel() {
        super();
    }

    public void addForcedRequiredSystems() {
        if (getSession().getPlatform().isHSQL()) {
            throw new TestWarningException("HSQL does not support complex outer joins correctly.");
        }
        
        EmployeeSystem system = new EmployeeSystem();
        ClassDescriptor descriptor = system.project.getClassDescriptor(Employee.class);
        ((ForeignReferenceMapping)descriptor.getMappingForAttributeName("address")).useInnerJoinFetch();
        ((ForeignReferenceMapping)descriptor.getMappingForAttributeName("manager")).useOuterJoinFetch();
        ((ForeignReferenceMapping)descriptor.getMappingForAttributeName("managedEmployees")).useOuterJoinFetch();
        ((ForeignReferenceMapping)descriptor.getMappingForAttributeName("projects")).useOuterJoinFetch();
        ((ForeignReferenceMapping)descriptor.getMappingForAttributeName("phoneNumbers")).useInnerJoinFetch();
        ((ForeignReferenceMapping)descriptor.getMappingForAttributeName("responsibilitiesList")).useOuterJoinFetch();
        
        addForcedRequiredSystem(system);
    }

    public void addTests() {
        addTest(EmployeeBasicTestModel.getReadObjectTestSuite());
        addTest(EmployeeBasicTestModel.getReadAllTestSuite());
    }
}

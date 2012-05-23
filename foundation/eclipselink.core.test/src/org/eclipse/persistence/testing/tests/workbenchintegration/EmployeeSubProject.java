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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;


/**
 * Project added to test several of the ProjectClassGenerator methods
 * Uses EmployeeProject as a base to add other functionality.
 * Should not be used as a
 */
public class EmployeeSubProject extends EmployeeProject {
    public EmployeeSubProject() {
        super();
        addDescriptor(buildEmployee2Descriptor());
    }

    public RelationalDescriptor buildEmployee2Descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Employee.class);
        return descriptor;
    }

    public ClassDescriptor buildEmployeeDescriptor() {
        ClassDescriptor descriptor = super.buildEmployeeDescriptor();

        DirectToFieldMapping lastNameMapping = 
            (DirectToFieldMapping)descriptor.getMappingForAttributeName("lastName");
        lastNameMapping.setNullValue(null);

        OneToManyMapping addressMapping = (OneToManyMapping)descriptor.getMappingForAttributeName("phoneNumbers");
        addressMapping.addAscendingOrdering("id");

        OneToManyMapping managedEmployeeMapping = 
            (OneToManyMapping)descriptor.getMappingForAttributeName("managedEmployees");
        managedEmployeeMapping.addDescendingOrdering("id");

        TransformationMapping normalHoursMapping = 
            (TransformationMapping)descriptor.getMappingForAttributeName("normalHours");
        normalHoursMapping.useContainerIndirection(Vector.class);

        return descriptor;
    }

    public ClassDescriptor buildEmploymentPeriodDescriptor() {
        ClassDescriptor descriptor = super.buildEmploymentPeriodDescriptor();

        DirectToFieldMapping endDateMapping = 
            (DirectToFieldMapping)descriptor.getMappingForAttributeName("endDate");
        endDateMapping.setNullValue(new java.util.Date(System.currentTimeMillis()));

        return descriptor;
    }
}

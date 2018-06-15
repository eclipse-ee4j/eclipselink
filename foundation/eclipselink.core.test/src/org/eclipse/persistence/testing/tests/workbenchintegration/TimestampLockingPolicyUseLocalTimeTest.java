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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;


/** This class has been modified as per instructions from Tom Ware and Development
 *  the test(), verify() are all inherited from the parent class
 *  We pass in the TopLink project and the string we are looking for and the superclass does the verification and testing
 */
public class TimestampLockingPolicyUseLocalTimeTest extends ProjectClassGeneratorResultFileTest {
    ClassDescriptor descriptorToModify;
    DatabaseMapping mappingToModify;

    public TimestampLockingPolicyUseLocalTimeTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(),
              "lockingPolicy.useLocalTime();");
        setDescription("Test addOptimisticLockingLines method -> TimeStampLockingPolicy usesLocalTime()");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        project = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();

        descriptorToModify = project.getDescriptors().get(Employee.class);
        TimestampLockingPolicy lockingTestPolicy = new TimestampLockingPolicy();
        lockingTestPolicy.setUsesServerTime(false);
        lockingTestPolicy.useLocalTime();
        lockingTestPolicy.setWriteLockFieldName("VERSION");
        descriptorToModify.setOptimisticLockingPolicy(lockingTestPolicy);
        generator = new ProjectClassGenerator(project);
    }
}

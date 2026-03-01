/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.employee;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;

// To make this test fail, comment a single line of code:
//   session.clearLastDescriptorAccessed();
// in org.eclipse.persistence.descriptors.ClassDescriptor.preInterfaceInitialization() method.
public class AddDescriptorsTest extends AutoVerifyTestCase {
    ClassDescriptor oldProjectDescriptor;
    ClassDescriptor newProjectDescriptor;

    public AddDescriptorsTest() {
        setDescription("Tests DatabaseSession.addDescriptors() method");
    }

    @Override
    public void reset() {
        if (newProjectDescriptor == oldProjectDescriptor) {
            // The test has failed - at least one "old" descriptor is referenced by a new descriptor.
            // To ensure consistency, remove EmployeeSystem interface descriptors first,
            // then add all EmployeeSystem descriptors again
            for (Class<?> cls : getSession().getDescriptors().keySet()) {
                String packageName = cls.getPackageName();
                if (packageName.equals("org.eclipse.persistence.testing.models.employee.interfaces")) {
                    getSession().getDescriptors().remove(cls);
                }
            }
            getDatabaseSession().addDescriptors(new EmployeeProject());
        }
    }

    @Override
    public void setup() {
        oldProjectDescriptor = getSession().getClassDescriptor(Project.class);
        if (oldProjectDescriptor == null) {
            throw (new TestWarningException("Test requires EmployeeTestSystem."));
        }
    }

    @Override
    public void test() {
        getDatabaseSession().addDescriptors(new EmployeeProject());
    }

    @Override
    public void verify() {
        newProjectDescriptor = getSession().getDescriptor(SmallProject.class).getInheritancePolicy().getParentDescriptor();
        if (newProjectDescriptor == oldProjectDescriptor) {
            throw (new TestErrorException("The old descriptor for Project.class is referenced from SmallProject.inheritancePolicy "));
        }
    }
}

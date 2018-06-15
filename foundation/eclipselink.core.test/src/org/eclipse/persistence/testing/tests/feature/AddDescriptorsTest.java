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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;

// To make this test fail, comment a single line of code:
//   session.clearLastDescriptorAccessed();
// in org.eclipse.persistence.descriptors.ClassDescriptor.preInterfaceInitialization() method.
public class AddDescriptorsTest extends AutoVerifyTestCase {
    ClassDescriptor oldProjectDescriptor;
    ClassDescriptor newProjectDescriptor;

    public AddDescriptorsTest() {
        setDescription("Tests DatabaseSession.addDescriptors() method");
    }

    public void reset() {
        if (newProjectDescriptor == oldProjectDescriptor) {
            // The test has failed - at least one "old" descriptor is referenced by a new descriptor.
            // To ensure consistency, remove EmployeeSystem interface descriptors first,
            // then add all EmployeeSystem descriptors again
            java.util.Iterator iterator = getSession().getDescriptors().keySet().iterator();
            while (iterator.hasNext()) {
                Class cls = (Class)iterator.next();
                String packageName = Helper.getPackageName(cls);
                if (packageName.equals("org.eclipse.persistence.testing.models.employee.interfaces")) {
                    getSession().getDescriptors().remove(cls);
                }
            }
            ((DatabaseSession)getSession()).addDescriptors(new EmployeeProject());
        }
    }

    public void setup() {
        oldProjectDescriptor = getSession().getClassDescriptor(Project.class);
        if (oldProjectDescriptor == null) {
            throw (new TestWarningException("Test requires EmployeeTestSystem."));
        }
    }

    public void test() {
        ((DatabaseSession)getSession()).addDescriptors(new EmployeeProject());
    }

    public void verify() {
        newProjectDescriptor = getSession().getDescriptor(SmallProject.class).getInheritancePolicy().getParentDescriptor();
        if (newProjectDescriptor == oldProjectDescriptor) {
            throw (new TestErrorException("The old descriptor for Project.class is referenced from SmallProject.inheritancePolicy "));
        }
    }
}

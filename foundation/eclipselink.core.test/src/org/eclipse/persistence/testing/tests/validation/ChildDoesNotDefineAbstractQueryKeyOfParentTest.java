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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Feb 27, 2k3

public class ChildDoesNotDefineAbstractQueryKeyOfParentTest extends ExceptionTestSaveDescriptor {

    ClassDescriptor descriptor;

    public ChildDoesNotDefineAbstractQueryKeyOfParentTest() {
        setDescription("This tests Child Does Not Define Abstract Query Key Of Parent (TL-ERROR 120)");
    }

    protected void setup() {
        super.setup();
        expectedException = DescriptorException.childDoesNotDefineAbstractQueryKeyOfParent(null, null, "dummy_key");
        ((DatabaseSession)getSession()).addDescriptor(buildProjectInterfaceDescriptor());
        descriptor = buildTestDescriptor();
    }

    public void test() {
        try {
            descriptor.interfaceInitialization((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor buildTestDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        descriptor.addTableName("PROJECT");
        descriptor.addPrimaryKeyFieldName("PROJECT.PROJ_ID");

        descriptor.getInterfacePolicy().addParentInterface(org.eclipse.persistence.testing.models.employee.interfaces.Project.class);

        return descriptor;
    }

    public RelationalDescriptor buildProjectInterfaceDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.employee.interfaces.Project.class);
        descriptor.descriptorIsForInterface();

        //old
        //    descriptor.addDirectQueryKey("description", "DESCRIP");
        //new
        descriptor.addAbstractQueryKey("testKey");

        return descriptor;
    }

}

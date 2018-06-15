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

import java.util.Hashtable;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Vesna
//Feb 2k3

public class IncorrectCollectionPolicyTest extends ExceptionTest {
    OneToManyMapping mapping;
    ContainerPolicy orgContainerPolicy;
    IntegrityChecker orgIntegrityChecker;

    public IncorrectCollectionPolicyTest() {
        setDescription("This tests Incorrect Collection Policy (TL-ERROR 163)");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        expectedException = DescriptorException.incorrectCollectionPolicy(null, null, null);

        ClassDescriptor descriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.mapping.Employee.class);
        //incorrectCollectionPolicy is thrown in CollectionMapping
        mapping = (OneToManyMapping)descriptor.getMappingForAttributeName("managedEmployees");
        //This causes the exception.  managedEmployees is a vector while MapContainerPolicy is used.
        orgContainerPolicy = mapping.getContainerPolicy();
        mapping.setContainerPolicy(new MapContainerPolicy(Hashtable.class));

        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        mapping.setContainerPolicy(orgContainerPolicy);
        getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            mapping.initialize((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}

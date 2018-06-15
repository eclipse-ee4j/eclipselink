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

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Feb 26, 2k3

public class InvalidUseOfTransparentIndirectionTest_extractPrimaryKeyForReferenceObject extends ExceptionTest {

    Employee employee;
    ClassDescriptor descriptor;
    OneToOneMapping mapping;
    IndirectionPolicy orgIndirectionPolicy;
    IntegrityChecker orgIntegrityChecker;

    public InvalidUseOfTransparentIndirectionTest_extractPrimaryKeyForReferenceObject() {
        setDescription("This tests Invalid Use Of Transparent Indirection (extractPrimaryKeyForReferenceObject) (TL-ERROR 144) " + "");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        employee = (Employee)getSession().readObject(Employee.class);

        expectedException = DescriptorException.invalidUseOfTransparentIndirection(null);

        descriptor = ((DatabaseSession)getSession()).getDescriptor(Employee.class);
        //extractPrimaryKeyForReferenceObject is used in OneToOneMapping
        mapping = (OneToOneMapping)descriptor.getMappingForAttributeName("address");
        orgIndirectionPolicy = mapping.getIndirectionPolicy();
        //Transparent indirection can only be used with CollectionMappings
        mapping.setIndirectionPolicy(new TransparentIndirectionPolicy());

        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        mapping.setIndirectionPolicy(orgIndirectionPolicy);

        getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            mapping.getIndirectionPolicy().extractPrimaryKeyForReferenceObject(employee.getAddress(), (AbstractSession)getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}

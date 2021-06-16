/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.ContainerIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Vesna
//Feb 2k3

public class InvalidIndirectionContainerClassTest extends ExceptionTest {
    ClassDescriptor descriptor;
    OneToManyMapping mapping;
    IndirectionPolicy orgIndirectionPolicy;
    IntegrityChecker orgIntegrityChecker;

    public InvalidIndirectionContainerClassTest() {
        setDescription("This tests Invalid Indirection Container Class (TL-ERROR 154)");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        expectedException = DescriptorException.invalidIndirectionContainerClass(new ContainerIndirectionPolicy(), null);

        descriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        mapping = (OneToManyMapping)descriptor.getMappingForAttributeName("phoneNumbers");
        orgIndirectionPolicy = mapping.getIndirectionPolicy();
        //An invalid indirection container class
        mapping.useContainerIndirection(Vector.class);

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
            mapping.initialize((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}

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

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Feb 26, 2k3

public class InvalidUseOfTransparentIndirectionTest_valueFromMethod extends ExceptionTest {

    ClassDescriptor descriptor;
    TransformationMapping mapping;
    IndirectionPolicy orgIndirectionPolicy;
    IntegrityChecker orgIntegrityChecker;

    public InvalidUseOfTransparentIndirectionTest_valueFromMethod() {
        setDescription("This tests Invalid Use Of Transparent Indirection (valueFromMethod) (TL-ERROR 144) " + "");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        expectedException = DescriptorException.invalidUseOfTransparentIndirection(null);

        descriptor = ((DatabaseSession)getSession()).getDescriptor(Employee.class);
        //valueFromMethod is used in TransformationMapping
        mapping = (TransformationMapping)descriptor.getMappingForAttributeName("normalHours");
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
            mapping.getIndirectionPolicy().valueFromMethod(new Employee().getNormalHours(), new DatabaseRecord(), (AbstractSession)getSession());

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}

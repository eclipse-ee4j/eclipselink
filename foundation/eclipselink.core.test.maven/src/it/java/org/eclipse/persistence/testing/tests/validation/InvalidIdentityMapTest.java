/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.identitymaps.IdentityMapManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Feb 21, 2k3

public class InvalidIdentityMapTest extends ExceptionTest {

    ClassDescriptor descriptor;
    Class orgIdentityMapClass;
    IntegrityChecker orgIntegrityChecker;

    public InvalidIdentityMapTest() {
        setDescription("This tests Invalid Identity Map (TL-ERROR 38)");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        expectedException = DescriptorException.invalidIdentityMap(null, null); //causes 7012 error

        descriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        orgIdentityMapClass = descriptor.getIdentityMapClass();
        //the following causes the correct error to occure.
        descriptor.setIdentityMapClass(null);

        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        descriptor.setIdentityMapClass(orgIdentityMapClass);
        getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            IdentityMapManager identityMapManager = new IdentityMapManager((AbstractSession)getSession());
            identityMapManager.buildNewIdentityMap(getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class));

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}

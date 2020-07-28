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

/**
 * This class is used to test an object without a descriptor in Unit of Work
 */
public class UOWWithoutDescriptorTest extends ExceptionTest {
    public void setup() {
        this.expectedException = org.eclipse.persistence.exceptions.ValidationException.missingDescriptor(null);
    }

    public void test() {
        try {
            org.eclipse.persistence.sessions.UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.registerObject(new String());
            uow.commit();

        } catch (org.eclipse.persistence.exceptions.EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}

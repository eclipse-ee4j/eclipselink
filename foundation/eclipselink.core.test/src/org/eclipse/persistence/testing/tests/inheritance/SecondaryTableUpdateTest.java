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
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.inheritance.JavaProgrammer;

public class SecondaryTableUpdateTest extends org.eclipse.persistence.testing.framework.TestCase {
    public JavaProgrammer J;

    public SecondaryTableUpdateTest() {
        setDescription("Checks if an update occurs when attributes from a non-primary table are modified");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        J = (JavaProgrammer)uow.readObject(JavaProgrammer.class);
        J.setNumberOfSupportQuestions(J.getNumberOfSupportQuestions() + 1);
        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ExpressionBuilder eb = new ExpressionBuilder();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        JavaProgrammer JP = (JavaProgrammer)uow.readObject(JavaProgrammer.class, eb.get("id").equal(J.id));
        if (JP.id != J.id) {
            throw (new org.eclipse.persistence.testing.framework.TestException("Update Failed"));
        }
    }
}

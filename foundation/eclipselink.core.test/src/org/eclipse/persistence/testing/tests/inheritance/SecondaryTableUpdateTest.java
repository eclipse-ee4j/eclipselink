/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

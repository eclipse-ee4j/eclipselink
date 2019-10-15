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
package org.eclipse.persistence.testing.tests.proxyindirection;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class UnitOfWorkUpdateWithNewObjectTest extends AutoVerifyTestCase {
    public UnitOfWorkUpdateWithNewObjectTest() {
        setDescription("Tests replacing a Proxy Indirection object with a new object in a UnitOfWork.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").like("%Rick%"));

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee eClone = (Employee)uow.registerObject(emp);

        LargeProject project = new LargeProjectImpl();
        project.setName("TopLink for Java 4.0");
        project.setDescription("Object-Relational mapping framework");
        project.setBudget(100000);
        project.setInvestor("Oracle Corp.");

        //    This doesn't work...
        //    eClone.setProject(null);
        eClone.setProject(project);

        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Rick"));

        Project project = emp.getProject();

        if (!project.getName().equals("TopLink for Java 4.0")) {
            throw new TestErrorException("Updating with a new object did not work properly.");
        }
    }
}

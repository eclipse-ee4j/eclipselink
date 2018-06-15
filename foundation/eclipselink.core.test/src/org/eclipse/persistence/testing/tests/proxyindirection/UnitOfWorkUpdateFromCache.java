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
package org.eclipse.persistence.testing.tests.proxyindirection;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class UnitOfWorkUpdateFromCache extends AutoVerifyTestCase {
    public UnitOfWorkUpdateFromCache() {
        setDescription("Tests replacing a Proxy Indirection object with a new object (from cache) in a UnitOfWork.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        UnitOfWork uow1 = getSession().acquireUnitOfWork();
        Employee emp4 = new EmployeeImpl();
        emp4.setFirstName("Jason");
        emp4.setLastName("Haley");
        emp4.setGender("Male");
        emp4.setAge(25);
        uow1.registerObject(emp4);
        uow1.commit();

        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").like("%Jason%"));

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee eClone = (Employee)uow.registerObject(emp);

        LargeProject project = new LargeProjectImpl();
        project.setName("TopLink for Java 5.5");
        project.setDescription("Enterprise Object-Relational mapping framework");
        project.setBudget(100000);
        project.setInvestor("Oracle Corp.");

        eClone.setProject(project);

        uow.commit();
    }

    public void verify() {
        //    getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Jason"));

        Project project = emp.getProject();

        if (!project.getName().equals("TopLink for Java 5.5")) {
            throw new TestErrorException("Updating with a new object did not work properly.");
        }
    }
}

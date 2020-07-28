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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.testing.models.mapping.MappingSystem;

/**
 * CR3922  Test buildSelectionCriteria in one-to-one mapping.
 */
public class SelectionCriteriaInOneToOneTest extends AutoVerifyTestCase {
    public Employee employee1;
    public Employee employee2;
    public org.eclipse.persistence.sessions.DatabaseSession newSession;
    public org.eclipse.persistence.sessions.Project project;

    public SelectionCriteriaInOneToOneTest() {
        setDescription("Verify that buildSelectionCriteria in one-to-one mapping works");
    }

    protected void setup() throws Exception {
        //Add an amendmend method to Employee
        MappingSystem mappingSystem = new MappingSystem();
        project = mappingSystem.project;
        ClassDescriptor descriptor = project.getDescriptors().get(Employee.class);
        descriptor.setAmendmentClass(MappingSystem.class);
        descriptor.setAmendmentMethodName("modifyOneToOneMappingDescriptor");
        descriptor.applyAmendmentMethod();

        //Copy the database login, create a new database session and login.
        DatabaseLogin databaseLogin = (DatabaseLogin)getSession().getLogin().clone();
        project.setLogin(databaseLogin);
        newSession = project.createDatabaseSession();
        newSession.setSessionLog(getSession().getSessionLog());
        newSession.login();
    }

    public void test() {
        modifyOneToOneMappingExample();
    }

    protected void verify() {
        if (!employee1.cubicle.location.equals("3rd floor, Section R, Third qubicle on left")) {
            throw new TestErrorException("OneToOneMapping.buildSelectionCriteria could not set the correct selection criteria. 1");
        }
        if (employee2.cubicle != null) {
            throw new TestErrorException("OneToOneMapping.buildSelectionCriteria could not set the correct selection criteria. 2");
        }
    }

    public void reset() {
        newSession.getIdentityMapAccessor().initializeIdentityMaps();
        newSession.logout();
    }

    public void modifyOneToOneMappingExample() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = (builder.get("firstName").equal("Dave")).and(builder.get("lastName").equal("Vadis"));
        employee1 = (Employee)newSession.readObject(Employee.class, exp);

        ExpressionBuilder builder2 = new ExpressionBuilder();
        Expression exp2 = (builder2.get("firstName").equal("Graham")).and(builder2.get("lastName").equal("Gooch"));
        employee2 = (Employee)newSession.readObject(Employee.class, exp2);
    }
}

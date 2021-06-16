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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.testing.models.mapping.MappingSystem;

/**
 * CR3922  Test buildSelectionCriteria in one-to-one mapping with target foreign keys.
 */
public class SelectionCriteriaInTargetOneToOneTest extends AutoVerifyTestCase {
    public Employee employee1;
    public Employee employee2;
    public org.eclipse.persistence.sessions.DatabaseSession newSession;
    public org.eclipse.persistence.sessions.Project project;

    public SelectionCriteriaInTargetOneToOneTest() {
        setDescription("Verify that buildSelectionCriteria in one-to-one mapping with target foreign keys works");
    }

    protected void setup() throws Exception {
        //Add an amendmend method to Employee
        MappingSystem mappingSystem = new MappingSystem();
        project = mappingSystem.project;
        ClassDescriptor descriptor = project.getDescriptors().get(Employee.class);
        descriptor.setAmendmentClass(MappingSystem.class);
        descriptor.setAmendmentMethodName("modifyTargetOneToOneMappingDescriptor");
        descriptor.applyAmendmentMethod();

        //Copy the database login, create a new database session and login.
        DatabaseLogin databaseLogin = (DatabaseLogin)getSession().getLogin().clone();
        project.setLogin(databaseLogin);
        newSession = project.createDatabaseSession();
        newSession.setSessionLog(getSession().getSessionLog());
        newSession.login();
    }

    public void test() {
        modifyTargetOneToOneMappingExample();
    }

    protected void verify() {
        if (!employee1.getComputer().employee.equals(employee1)) {
            throw new TestErrorException("OneToOneMapping.buildSelectionCriteria could not set the correct selection criteria. 1");
        }
        if (employee2.getComputer() != null) {
            throw new TestErrorException("OneToOneMapping.buildSelectionCriteria could not set the correct selection criteria. 2");
        }
    }

    public void reset() {
        newSession.getIdentityMapAccessor().initializeIdentityMaps();
        newSession.logout();
    }

    public void modifyTargetOneToOneMappingExample() {
        //This employee has a computer based on the selection criteria built in MappingSystem
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = (builder.get("firstName").equal("Norman")).and(builder.get("lastName").equal("Louis"));
        employee1 = (Employee)newSession.readObject(Employee.class, exp);

        //This employee does not have a computer based on the selection criteria built in MappingSystem
        ExpressionBuilder builder2 = new ExpressionBuilder();
        Expression exp2 = (builder2.get("firstName").equal("Graham")).and(builder2.get("lastName").equal("Gooch"));
        employee2 = (Employee)newSession.readObject(Employee.class, exp2);
    }
}

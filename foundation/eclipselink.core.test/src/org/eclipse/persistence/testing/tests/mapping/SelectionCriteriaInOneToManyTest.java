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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.testing.models.mapping.MappingSystem;

/**
 * CR3922  Test buildSelectionCriteria in one-to-many mapping.
 */
public class SelectionCriteriaInOneToManyTest extends AutoVerifyTestCase {
    private Employee employee1;
    private Employee employee2;
    private Employee employee3;
    private org.eclipse.persistence.sessions.DatabaseSession newSession;
    protected org.eclipse.persistence.sessions.Project project;

    public SelectionCriteriaInOneToManyTest() {
        setDescription("Verify that buildSelectionCriteria in one-to-many mapping works");
    }

    protected void setup() throws Exception {
        //Add an amendmend method to Employee
        MappingSystem mappingSystem = new MappingSystem();
        project = mappingSystem.project;
        ClassDescriptor descriptor = project.getDescriptors().get(Employee.class);
        descriptor.setAmendmentClass(MappingSystem.class);
        descriptor.setAmendmentMethodName("modifyOneToManyMappingDescriptor");
        descriptor.applyAmendmentMethod();

        //Copy the database login, create a new database session and login.
        DatabaseLogin databaseLogin = (DatabaseLogin)getSession().getLogin().clone();
        project.setLogin(databaseLogin);
        newSession = project.createDatabaseSession();
        newSession.setSessionLog(getSession().getSessionLog());
        newSession.login();
    }

    public void test() {
        modifyOneToManyMappingExample();
    }

    protected void verify() {
        if (!employee1.getManagedEmployees().contains(employee3)) {
            throw new TestErrorException("OneToManyMapping.buildSelectionCriteria could not set the correct selection criteria.1");
        }
        if (employee2.getManagedEmployees().contains(employee3)) {
            throw new TestErrorException("OneToManyMapping.buildSelectionCriteria could not set the correct selection criteria.2");
        }
    }

    public void reset() {
        newSession.getIdentityMapAccessor().initializeIdentityMaps();
        newSession.logout();
    }

    public void modifyOneToManyMappingExample() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = (builder.get("firstName").equal("Dave")).and(builder.get("lastName").equal("Vadis"));
        employee1 = (Employee)newSession.readObject(Employee.class, exp);

        ExpressionBuilder builder2 = new ExpressionBuilder();
        Expression exp2 = (builder2.get("firstName").equal("Tracy")).and(builder2.get("lastName").equal("Rue"));
        employee2 = (Employee)newSession.readObject(Employee.class, exp2);

        ExpressionBuilder builder3 = new ExpressionBuilder();
        Expression exp3 = (builder3.get("firstName").equal("Tracy")).and(builder3.get("lastName").equal("Chapman"));
        employee3 = (Employee)newSession.readObject(Employee.class, exp3);

    }
}

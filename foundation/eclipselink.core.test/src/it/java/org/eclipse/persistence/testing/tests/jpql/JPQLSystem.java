/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeTableCreator;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

import java.util.Vector;

/**
 * <b>Purpose</b>: To define system behavior.
 * <p><b>Responsibilities</b>:    <ul>
 * <li> Login and return an initialize database session.
 * <li> Create and populate the database.
 * </ul>
 */
public class JPQLSystem extends TestSystem {

    /**
     * Use the EJBQL EmployeeProject.
     */
    public JPQLSystem() {
        project = buildProject();
    }

    public EmployeeProject buildProject() {
        EmployeeProject employeeProject = new EmployeeProject();

        //add a mapping for addressId so we can use it in from "emp"
        DirectToFieldMapping addressIdMapping = new DirectToFieldMapping();
        addressIdMapping.setAttributeName("addressId");
        addressIdMapping.setFieldName("EMPLOYEE.ADDR_ID");
        addressIdMapping.setGetMethodName("getAddressId");
        addressIdMapping.setSetMethodName("setAddressId");
        addressIdMapping.setIsReadOnly(true);

        employeeProject.getDescriptor(Employee.class).addMapping(addressIdMapping);

        return employeeProject;
    }

    @Override
    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = buildProject();
        }

        session.addDescriptors(project);
    }

    @Override
    public void createTables(DatabaseSession session) {
        new EmployeeTableCreator().replaceTables(session);
    }

    /**
     * Return a connected session using the default login.
     */
    @Override
    public DatabaseSession login() {
        DatabaseSession session;

        session = project.createDatabaseSession();
        session.login();

        return session;
    }

    /**
     * This method demonstrates how a descriptor can be modified after being read with it's project (INI Files).
     * The properties of the PhoneNumber's Descriptor provide this method name to be called after the descriptor is built.
     * 1. Add a query key 'id' so that it may be used within expressions.
     * 2. Add a defined query which will retrieve all phone numbers with area code 613 (local Ottawa numbers).
     */
    public static void modifyPhoneDescriptor(ClassDescriptor descriptor) {
        // 1. Add query key 'id'
        descriptor.addDirectQueryKey("id", "EMP_ID");

        // Add a predefined query for retrieving numbers with 613 area code.
        ReadAllQuery query = new ReadAllQuery();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("id").equal(builder.getParameter("ID"));

        query.setReferenceClass(PhoneNumber.class);
        query.setSelectionCriteria(exp.and(builder.get("areaCode").equal("613")));
        query.addArgument("ID");

        descriptor.getQueryManager().removeQuery("localNumbers");
        descriptor.getQueryManager().addQuery("localNumbers", query);
    }

    /**
     * This method will instantiate all of the example instances and insert them into the database
     * using the given session.
     */
    @Override
    public void populate(DatabaseSession session) {
        org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator system = new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForAbstractClass(org.eclipse.persistence.testing.models.employee.domain.Project.class, session, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}

/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.oracle;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.employee.relational.*;

/**
 * <b>Purpose</b>: To define system behavior.
 * <p><b>Responsibilities</b>:	<ul>
 * <li> Login and return an initialize database session.
 * <li> Create and populate the database.
 * </ul>
 */
public class

EmployeeSystem extends TestSystem {

    /**
     * Use the default EmployeeProject.
     */
    public EmployeeSystem() {
        project = new EmployeeProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new EmployeeProject();
        }

        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        new EmployeeTableCreator().replaceTables(session);
    }

    /**
     * Return a connected session using the default login.
     */
    public DatabaseSession login() {
        DatabaseSession session;

        session = project.createDatabaseSession();
        session.login();

        return session;
    }

    /**
     *	This method demonstrates how a descriptor can be modified after being read with it's project (INI Files).
     *	The properties of the PhoneNumber's Descriptor provide this method name to be called after the descriptor is built.
     *	1. Add a query key 'id' so that it may be used within expressions.
     *	2. Add a defined query whic will retrieve all phone numbers with area code 613 (local Ottawa numbers).
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
     *	This method will instantiate all of the example instances and insert them into the database
     *	using the given session. 
     */
    public void populate(DatabaseSession session) {
        EmployeePopulator system = 
            new EmployeePopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(Employee.class, 
                                                                    allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForAbstractClass(Project.class, 
                                                                            session, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}

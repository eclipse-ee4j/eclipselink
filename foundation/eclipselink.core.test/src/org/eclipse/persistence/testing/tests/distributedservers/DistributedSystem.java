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
package org.eclipse.persistence.testing.tests.distributedservers;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;


/**
 *	<b>Purpose</b>: To define system behavior.
 *	<p><b>Responsibilities</b>:	<ul>
 *	<li> Login and return an initialize database session.
 *	<li> Create and populate the database.
 *	</ul>
 */
public class

DistributedSystem extends TestSystem {
    /**
     * Use the default EmployeeProject.
     */
    public

    DistributedSystem() {
        project = new DistributedProject();
    }

    public void addDescriptors(DatabaseSession session) {
        session.logMessage("Project is from generated code");
        (session).addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        // Start Build fieldTypes
        schemaManager.buildFieldTypes(Dist_Employee.tableDefinition());
        schemaManager.buildFieldTypes(Company.tableDefinition());
        schemaManager.buildFieldTypes(Item.tableDefinition());
        // end build fieldTypes

        //start drop constraints
        try {
            schemaManager.dropConstraints(Dist_Employee.tableDefinition());
            schemaManager.dropConstraints(Company.tableDefinition());
            schemaManager.dropConstraints(Item.tableDefinition());
        } catch (org.eclipse.persistence.exceptions.DatabaseException dbE) {
            //ignore
        }
        //end drop constraints

        //start replace tables
        schemaManager.replaceObject(Dist_Employee.tableDefinition());
        schemaManager.replaceObject(Company.tableDefinition());
        schemaManager.replaceObject(Item.tableDefinition());

        //end replace tables

        //start create constraints
        schemaManager.createConstraints(Dist_Employee.tableDefinition());
        schemaManager.createConstraints(Company.tableDefinition());
        schemaManager.createConstraints(Item.tableDefinition());
        //end create constraints

        schemaManager.createSequences();
    }

    /**
     * Return a connected session using the default login.
     */
    public DatabaseSession login() {
        DatabaseSession session;

        session = new DatabaseSessionImpl(project);
        session.login();

        return session;
    }

    /**
     *	This method will instantiate all of the example instances and insert them into the database
     *	using the given session. 
     */
    public void populate(DatabaseSession session) {
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        unitOfWork.registerObject(Dist_Employee.example1());
        unitOfWork.registerObject(Company.example1());
        unitOfWork.commit();
    }
}

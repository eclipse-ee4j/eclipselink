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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.testing.tests.unitofwork.changeflag.model.ALCTEmployee;
import org.eclipse.persistence.testing.tests.unitofwork.changeflag.model.ALCTEmployeeProject;
import org.eclipse.persistence.testing.tests.unitofwork.changeflag.model.ALCTEmployeeTableCreator;
import org.eclipse.persistence.testing.tests.unitofwork.changeflag.model.ALCTEmploymentPeriod;


/**
 *    <b>Purpose</b>: To define system behavior.
 *    <p><b>Responsibilities</b>:    <ul>
 *    <li> Login and return an initialize database session.
 *    <li> Create and populate the database.
 *    </ul>
 */
public class ALCTEmployeeSystem extends TestSystem {
    /**
     * Use the default EmployeeProject.
     */
    public ALCTEmployeeSystem() {
        project = new ALCTEmployeeProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new ALCTEmployeeProject();
        }

        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        dropTableConstraints(session);
        new ALCTEmployeeTableCreator().replaceTables(session);
    }

    /**
     * Drop table constraints
     */
    protected void dropTableConstraints(Session session) {
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
     *    This method will instantiate all of the example instances and insert them into the database
     *    using the given session.
     */
    public void populate(DatabaseSession session) {
        UnitOfWork unitOfWork = session.acquireUnitOfWork();
        ALCTEmployee emp = new ALCTEmployee();
        emp.setFirstName("ALCTEmp");
        emp.setLastName("NotRequired");
        ALCTEmploymentPeriod empPeriod = new ALCTEmploymentPeriod();
        empPeriod.setEndDate(Helper.dateFromYearMonthDate(2006, 12, 2));
        empPeriod.setStartDate(Helper.dateFromYearMonthDate(2004, 12, 2));
        emp.setPeriod(empPeriod);
        unitOfWork.registerObject(emp);
        unitOfWork.commit();
    }
}

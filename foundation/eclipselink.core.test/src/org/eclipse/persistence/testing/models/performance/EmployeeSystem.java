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
package org.eclipse.persistence.testing.models.performance;

import java.util.*;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;

/**
 * <b>Purpose</b>: To define system behavior.
 * <p><b>Responsibilities</b>:    <ul>
 * <li> Login and return an initialize database session.
 * <li> Create and populate the database.
 * </ul>
 */
public class EmployeeSystem extends TestSystem {
    public EmployeeSystem() {
        project = XMLProjectReader.read("org/eclipse/persistence/testing/models/performance/employee.xml", getClass().getClassLoader());
    }

    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        // Configure sequencing to be same as Hibernate defaults.
        session.getLogin().useNativeSequencing();
        session.getLogin().getDefaultSequence().setPreallocationSize(100);
        ((DatabaseSessionImpl)session).getSequencingControl().resetSequencing();
        dropTableConstraints(session);
        // Recreate sequences to help provide more consistent hash values for primary key
        // to improve test consistency.
        new SchemaManager(session).replaceSequences();
        // Seems to be issues with being the same as Hibernate, maybe +1 issue.
        session.getLogin().getDefaultSequence().setPreallocationSize(48);
        new EmployeeTableCreator().replaceTables(session);
    }

    /**
     * Drop table constraints.
     */
    protected void dropTableConstraints(Session session) {
        if (session.getLogin().getPlatform().isOracle()) {
            try {
                session.executeNonSelectingCall(new SQLCall("drop table PHONE CASCADE CONSTRAINTS"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table RESPONS CASCADE CONSTRAINTS"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table SALARY CASCADE CONSTRAINTS"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table PROJ_EMP CASCADE CONSTRAINTS"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table LPROJECT CASCADE CONSTRAINTS"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table PROJECT CASCADE CONSTRAINTS"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table EMPLOYEE CASCADE CONSTRAINTS"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table ADDRESS CASCADE CONSTRAINTS"));
            } catch (Exception e) {
            }
        } else {
            try {
                session.executeNonSelectingCall(new SQLCall("drop table PHONE"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table RESPONS"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table SALARY"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table PROJ_EMP"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table LPROJECT"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table PROJECT"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table EMPLOYEE"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("drop table ADDRESS"));
            } catch (Exception e) {
            }
        }
    }

    /**
     * This method will instantiate all of the example instances and insert them into the database using the given session.
     */
    public void populate(DatabaseSession session) {
        EmployeePopulator system = new EmployeePopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(Employee.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForAbstractClass(Project.class, session, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}

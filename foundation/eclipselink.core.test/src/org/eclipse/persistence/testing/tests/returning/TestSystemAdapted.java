/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.returning;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.inheritance.Computer;
import org.eclipse.persistence.testing.models.inheritance.Vehicle;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;

public class TestSystemAdapted extends TestSystem {

    public TestSystemAdapted() {
        super();
    }

    public TestSystemAdapted(TestSystem testSystemToBeAdapted, ProjectAndDatabaseAdapter adapter) {
        this();
        setAdapter(adapter);
        setTestSystem(testSystemToBeAdapted);
    }

    protected ProjectAndDatabaseAdapter adapter;
    protected TestSystem testSystem;

    public void setAdapter(ProjectAndDatabaseAdapter adapter) {
        this.adapter = adapter;
    }

    public ProjectAndDatabaseAdapter getAdapter() {
        return adapter;
    }

    public void setTestSystem(TestSystem testSystemToBeAdapted) {
        testSystem = testSystemToBeAdapted;
    }

    public TestSystem getTestSystem() {
        return testSystem;
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            if (getAdapter().isOriginalSetupRequired()) {
                try {
                    // If called twice, method TestSystem.addDescriptors(Session) 
                    // may behave wrong during the second call
                    // (typically because during the second call project value is no longer null,
                    // which causes problems in case more than one project is used).
                    // Therefore another instance of TestSystem is created - not to spoil the original.
                    TestSystem tempTestSystem = getTestSystem().getClass().newInstance();
                    tempTestSystem.addDescriptors(session);
                    try {
                        tempTestSystem.createTables(session);
                    } catch (Exception ex2) {
                        throw new TestProblemException("Exception thrown by " + Helper.getShortClassName(tempTestSystem) + ".createTables() ", ex2);
                    }
                } catch (Exception ex1) {
                    throw new TestProblemException("Failed to create an instance of " + getTestSystem().getClass() + " ", ex1);
                }
            }
            // This trick stores all descriptors used by testSystem into project
            DatabaseSession dummyDatabaseSession = new Project(session.getLogin().clone()).createDatabaseSession();
            getTestSystem().addDescriptors(dummyDatabaseSession);
            project = dummyDatabaseSession.getProject();

            getAdapter().updateProject(project, session);
        }
        (session).addDescriptors(project);
        afterAddDescriptors(session, getTestSystem());
    }

    public void createTables(DatabaseSession session) throws Exception {
        getTestSystem().createTables(session);
        getAdapter().updateDatabase(session);
    }

    public String toString() {
        return Helper.getShortClassName(getTestSystem()) + " using " + Helper.getShortClassName(getAdapter());
    }

    public void populate(DatabaseSession session) throws Exception {
        getTestSystem().populate(session);
    }

    protected static void afterAddDescriptors(Session session, TestSystem aTestSystem) {
        if (aTestSystem instanceof InheritanceSystem) {
            // For using read all subclasses views.
            org.eclipse.persistence.internal.databaseaccess.DatabasePlatform platform = session.getLogin().getPlatform();
            if (platform.isOracle() || platform.isSybase() || platform.isSQLAnywhere()) {
                ClassDescriptor computerDescriptor = session.getClassDescriptor(Computer.class);
                ClassDescriptor vehicleDescriptor = session.getClassDescriptor(Vehicle.class);
                if (computerDescriptor.getInheritancePolicy().requiresMultipleTableSubclassRead()) {
                    computerDescriptor.getInheritancePolicy().setReadAllSubclassesViewName("AllComputers");
                }
                if (vehicleDescriptor.getInheritancePolicy().requiresMultipleTableSubclassRead()) {
                    vehicleDescriptor.getInheritancePolicy().setReadAllSubclassesViewName("AllVehicles");
                }
            }
        }
    }
}

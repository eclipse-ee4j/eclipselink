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
package org.eclipse.persistence.testing.models.mapping;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;

/**
 * This creates a system for outer joins with multiple tables
 */
public class OuterJoinSystem extends TestSystem {
    public OuterJoinSystem() {
        project = new OuterJoinWithMultipleTablesProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new OuterJoinWithMultipleTablesProject();
        }
        (session).addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        //create added tables for outer join with multiple tables testing
        schemaManager.replaceObject(Course.tableDefinition());
        schemaManager.replaceObject(Student.tableDefinition());
        schemaManager.createSequences();

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

    public void populate(DatabaseSession session) {
        Student instance;
        PopulationManager manager = PopulationManager.getDefaultManager();
        String appendString = session.getLogin().getPlatform().getTableQualifier();
        if (appendString.length() != 0) {
            appendString = appendString + ".";
        }

        //======insert some Student Examples=====//
        instance = Student.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");

        instance = Student.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");

        instance = Student.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "example3");

        instance = Student.example4();
        session.writeObject(instance);
        manager.registerObject(instance, "example4");

        instance = Student.example5();
        session.writeObject(instance);
        manager.registerObject(instance, "example5");

        instance = Student.example6();
        session.writeObject(instance);
        manager.registerObject(instance, "example6");

        instance = Student.example7();
        session.writeObject(instance);
        manager.registerObject(instance, "example7");

        Student instance2 = Student.example15();
        Course instance1 = Course.example15();
        instance = Student.example15();
        session.writeObject(instance);
        manager.registerObject(instance, "example15");

        session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("UPDATE " + appendString + "STUDENT SET C_ID = NULL WHERE ST_ID = " + instance2.getSt_ID()));

        session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("DELETE FROM STUDENT2 WHERE COURSE_ID = " + instance1.getCourse_ID()));

        /*    instance = Student.example8();
            session.writeObject(instance);
            manager.registerObject(instance, "example8");



            instance = Student.example9();
            session.writeObject(instance);
            manager.registerObject(instance, "example9");


            instance = Student.example10();
            session.writeObject(instance);
            manager.registerObject(instance, "example10");

            instance = Student.example11();
            session.writeObject(instance);
            manager.registerObject(instance, "example11");


            instance = Student.example12();
            session.writeObject(instance);
            manager.registerObject(instance, "example12");

            instance = Student.example13();
            session.writeObject(instance);
            manager.registerObject(instance, "example13");

            instance = Student.example14();
            session.writeObject(instance);
            manager.registerObject(instance, "example14");

        */
    }
}

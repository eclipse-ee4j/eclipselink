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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class IdentitySystem extends TestSystem {
    public IdentitySystem() {
        //For Testing purposes	
        project = new NoIdentityMapProject();
    }

public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new NoIdentityMapProject();
        }
        (session).addDescriptors(project);

        //project = new Project_case2();
        //((DatabaseSession)session).addDescriptors(project);
    }

    public TestClass1 basicIdentityExample1() {
        TestClass1 example = new TestClass1();

        example.setTest1("Lauretta");
        example.setTest2("Cole");

        return example;
    }

    public TestClass1 basicIdentityExample2() {
        TestClass1 example = new TestClass1();

        example.setTest1("William");
        example.setTest2("George");
        return example;
    }

    public TestClass1 basicIdentityExample3() {
        TestClass1 example = new TestClass1();

        example.setTest1("Victoria");
        example.setTest2("Sawyerr");
        return example;
    }

    public void buildExamples() {
        testIdentityExample1();
        testIdentityExample2();
        testIdentityExample3();

    }

    private boolean containsObject(Class domainClass, String identifier) {
        PopulationManager populationManager = PopulationManager.getDefaultManager();

        return populationManager.containsObject(domainClass, identifier);
    }

public void createTables(DatabaseSession session)
{
        SchemaManager schemaManager = new SchemaManager(session);

        //create added tables for no identity map testing
        schemaManager.replaceObject(TestClass1.tableDefinition());
        schemaManager.createSequences();

    }

    private Object getObject(Class domainClass, String identifier) {
        PopulationManager populationManager = PopulationManager.getDefaultManager();
        return populationManager.getObject(domainClass, identifier);
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

public void populate(DatabaseSession session)  
{
        IdentitySystem system = new IdentitySystem();
        org.eclipse.persistence.sessions.UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(TestClass1.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }

    private void registerObject(Object domainObject, String identifier) {
        PopulationManager populationManager = PopulationManager.getDefaultManager();
        populationManager.registerObject(domainObject, identifier);
    }

    public TestClass1 testIdentityExample1() {
        TestClass1 example = basicIdentityExample1();
        registerObject(example, "100");

        return example;
    }

    public TestClass1 testIdentityExample2() {
        TestClass1 example = basicIdentityExample2();
        registerObject(example, "200");

        return example;
    }

    public TestClass1 testIdentityExample3() {
        TestClass1 example = basicIdentityExample3();
        registerObject(example, "300");

        return example;
    }
}

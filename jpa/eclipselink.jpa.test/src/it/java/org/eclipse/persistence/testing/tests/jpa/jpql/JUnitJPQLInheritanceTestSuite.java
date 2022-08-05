/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.jpql;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import jakarta.persistence.EntityManager;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;

import org.junit.Assert;

/**
 * <p>
 * <b>Purpose</b>: Test inheritance EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for inheritance EJBQL functionality
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 */
public class JUnitJPQLInheritanceTestSuite extends JUnitTestCase {
    static JUnitDomainObjectComparer comparer;        //the global comparer object used in all tests

    public JUnitJPQLInheritanceTestSuite() {
        super();
    }

    public JUnitJPQLInheritanceTestSuite(String name) {
        super(name);
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLInheritanceTestSuite");
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testSetup"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testStraightReadSuperClass"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testStraightReadSubClass"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testJoinSuperClass"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testJoinFetchSuperClass"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        //get session to start setup
        DatabaseSession session = JUnitTestCase.getServerSession();

        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator();

        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();

        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        new AdvancedTableCreator().replaceTables(session);

        //Populate the tables
        employeePopulator.buildExamples();

        //Persist the examples in the database
        employeePopulator.persistExample(session);
    }

    public void testStraightReadSuperClass() {
        EntityManager em = createEntityManager();

        Project project = (Project)em.createQuery("SELECT p from Project p").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(Project.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(project.getId()));

        Project tlProject = (Project)getServerSession().executeQuery(tlQuery);
        Assert.assertTrue("SuperClass Inheritance Test Failed", comparer.compareObjects(project, tlProject));
    }

    public void testStraightReadSubClass() {
        EntityManager em = createEntityManager();

        SmallProject project = (SmallProject)em.createQuery("SELECT s from SmallProject s").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(SmallProject.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(project.getId()));

        SmallProject tlProject = (SmallProject)getServerSession().executeQuery(tlQuery);
        Assert.assertTrue("Subclass Inheritance Test Failed", comparer.compareObjects(project, tlProject));
    }

    public void testJoinSuperClass() {
        EntityManager em = createEntityManager();

        Employee emp = (Employee)em.createQuery("SELECT e from Employee e JOIN e.projects p where e.lastName is not null").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(Employee.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(emp.getId()));
        tlQuery.addJoinedAttribute(tlQuery.getExpressionBuilder().anyOf("projects"));

        Employee tlEmp = (Employee)getServerSession().executeQuery(tlQuery);
        Assert.assertTrue("Join superclass Inheritance Test Failed", comparer.compareObjects(emp, tlEmp));
    }

    public void testJoinFetchSuperClass() {
        EntityManager em = createEntityManager();

        Employee emp = (Employee)em.createQuery("SELECT e from Employee e JOIN FETCH e.projects").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(Employee.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(emp.getId()));
        tlQuery.addJoinedAttribute(tlQuery.getExpressionBuilder().anyOf("projects"));

        Employee tlEmp = (Employee)getServerSession().executeQuery(tlQuery);
        Assert.assertTrue("Join superclass Inheritance Test Failed", comparer.compareObjects(emp, tlEmp));
    }
}

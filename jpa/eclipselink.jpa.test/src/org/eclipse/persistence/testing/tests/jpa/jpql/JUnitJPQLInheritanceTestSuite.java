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
package org.eclipse.persistence.testing.tests.jpa.jpql;

import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.EntityManager;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.inheritance.Engineer;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritancePopulator;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;

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
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testJoinSubClass"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testJoinFetchSuperClass"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testJoinFetchSubClass"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testJoinedInheritance"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testJoinedInheritanceWithLeftOuterJoin1"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testJoinedInheritanceWithLeftOuterJoin2"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testJoinedInheritanceWithLeftOuterJoin3"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testComputer"));
        suite.addTest(new JUnitJPQLInheritanceTestSuite("testAllPeople"));
        
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
        new InheritanceTableCreator().replaceTables(session);
        
        //Populate the tables
        employeePopulator.buildExamples();
        
        //Persist the examples in the database
        employeePopulator.persistExample(session);
        
        //Populate the tables
        InheritancePopulator inheritancePopulator = new InheritancePopulator();
        inheritancePopulator.buildExamples();
        
        //Persist the examples in the database
        inheritancePopulator.persistExample(session);

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

    public void testJoinSubClass() {
        EntityManager em = createEntityManager();                  
         
        Engineer emp = (Engineer)em.createQuery("SELECT e from Engineer e JOIN e.bestFriend b WHERE e.title is not null").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(Engineer.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(emp.getId()));
        tlQuery.addJoinedAttribute(tlQuery.getExpressionBuilder().get("bestFriend"));
        
        Engineer tlEmp = (Engineer)getServerSession().executeQuery(tlQuery);
        Assert.assertTrue("Join Subclass Inheritance Test Failed", comparer.compareObjects(emp, tlEmp));                 
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

    public void testJoinFetchSubClass() {
        EntityManager em = createEntityManager();                  
         
        Engineer emp = (Engineer)em.createQuery("SELECT e from Engineer e JOIN FETCH e.bestFriend").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(Engineer.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(emp.getId()));
        tlQuery.addJoinedAttribute(tlQuery.getExpressionBuilder().get("bestFriend"));
        
        Engineer tlEmp = (Engineer)getServerSession().executeQuery(tlQuery);
        Assert.assertTrue("Join Subclass Inheritance Test Failed", comparer.compareObjects(emp, tlEmp));                 
    }

    /**
     * Checks, that the selection criteria for joined inheritance is well-formed,
     * i.e. all tables are joined.
     * See issue 860.
     */
    public void testJoinedInheritance() {
        EntityManager em = createEntityManager();

        String ejbqlString = "SELECT OBJECT(b) FROM BBB b WHERE b.foo = ?1";
        // query throws exception, if result not unique!
        em.createQuery(ejbqlString).setParameter(1, "bar").getSingleResult();
    }
    
    public void testJoinedInheritanceWithLeftOuterJoin1() {
        EntityManager em = createEntityManager();        
        String ejbqlString = "SELECT t0.maxSpeed, t0.color, t0.description, t0.fuelCapacity, t0.fuelType, t0.id, t0.passengerCapacity, t1.name, t1.id FROM SportsCar t0 LEFT OUTER JOIN t0.owner t1";
        try {
            em.createQuery(ejbqlString).getResultList();
        } catch (Exception e) {
            fail("Error occurred on a left outer join sql expression on a joined inheritance test: " + e.getCause());
        }
    }
    
    public void testJoinedInheritanceWithLeftOuterJoin2() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT t0.color, t0.description, t0.fuelCapacity, t0.fuelType, t0.id, t0.passengerCapacity, t1.name, t1.id FROM FueledVehicle t0 LEFT OUTER JOIN t0.owner t1";
        try {
            em.createQuery(ejbqlString).getResultList();
        } catch (Exception e) {
            fail("Error occurred on a left outer join sql expression on a joined inheritance test: " + e.getCause());
        }
    }
    
    public void testJoinedInheritanceWithLeftOuterJoin3() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT t0.color, t0.description, t0.fuelCapacity, t0.fuelType, t0.id, t0.passengerCapacity, t1.name, t1.id FROM Bus t0 LEFT OUTER JOIN t0.busDriver t1";
        try {
            em.createQuery(ejbqlString).getResultList();
        } catch (Exception e) {
            fail("Error occurred on a left outer join sql expression on a joined inheritance test: " + e.getCause());
        }
    }
    
    public void testComputer() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT c FROM Computer c";
        List result = em.createQuery(ejbqlString).getResultList();
        if (result.size() != 4) {
            fail("Expected 4 computers got: " + result);
        }
    }
    
    public void testAllPeople() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT p FROM Person p order by p.id";
        List result = em.createQuery(ejbqlString).getResultList();
        if (result.size() != 8) {
            fail("Expected 8 people got: " + result);
        }
    }

    // Helper methods and classes for constructor query test cases
    public static boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }
}

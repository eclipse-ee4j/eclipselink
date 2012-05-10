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

package org.eclipse.persistence.testing.tests.jpa.jpql;

import java.util.Calendar;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.datetime.DateTimePopulator;
import org.eclipse.persistence.testing.models.jpa.datetime.DateTimeTableCreator;

/**
 * <p>
 * <b>Purpose</b>: Test JPQL UPDATE and DELETE queries.
 * <p>
 * <b>Description</b>: This class creates a test suite and adds tests to the
 * suite. The database gets initialized prior to each test method.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for JPQL UPDATE and DELETE queries.
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 */
 
public class JUnitJPQLModifyTestSuite extends JUnitTestCase {  
  
    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests
  
    public JUnitJPQLModifyTestSuite()
    {
        super();
    }
  
    public JUnitJPQLModifyTestSuite(String name)
    {
        super(name);
    }
  
    //This method is run at the start of EVERY test case method
    public void setUp()
    {
        //get session to start setup
        DatabaseSession session = JUnitTestCase.getServerSession();
        
        new AdvancedTableCreator().replaceTables(session);

        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator();

        //Populate the tables
        employeePopulator.buildExamples();
        
        //Persist the examples in the database
        employeePopulator.persistExample(session);

        // drop and create DateTime tables and persist dateTime test data
        new DateTimeTableCreator().replaceTables(JUnitTestCase.getServerSession());
        DateTimePopulator dateTimePopulator = new DateTimePopulator();                
        dateTimePopulator.persistExample(getServerSession());
    }
  
    //This method is run at the end of EVERY test case method
    public void tearDown()
    {
        clearCache();
    }
  
    //This suite contains all tests contained in this class
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLModifyTestSuite");
        suite.addTest(new JUnitJPQLModifyTestSuite("testSetup"));
        suite.addTest(new JUnitJPQLModifyTestSuite("simpleUpdate"));
        suite.addTest(new JUnitJPQLModifyTestSuite("updateWithSubquery"));
        suite.addTest(new JUnitJPQLModifyTestSuite("updateEmbedded"));
        suite.addTest(new JUnitJPQLModifyTestSuite("updateEmbeddedFieldTest"));
        suite.addTest(new JUnitJPQLModifyTestSuite("updateUnqualifiedAttributeInSet"));
        suite.addTest(new JUnitJPQLModifyTestSuite("updateUnqualifiedAttributeInWhere"));
        suite.addTest(new JUnitJPQLModifyTestSuite("updateUnqualifiedAttributeInWhereWithInputParameter"));
        suite.addTest(new JUnitJPQLModifyTestSuite("updateDateTimeFields"));
        suite.addTest(new JUnitJPQLModifyTestSuite("simpleDelete"));
        suite.addTest(new JUnitJPQLModifyTestSuite("simpleUpdateWithInputParameters"));

        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        //get session to start setup
        DatabaseSession session = JUnitTestCase.getServerSession();
        
        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();
        
        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

    }
    
    public void simpleUpdate()
    {          
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test simpleUpdate skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e");

        // test query
        String update = "UPDATE Employee e SET e.firstName = 'CHANGED'";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("simpleUpdate: wrong number of updated instances", 
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'CHANGED'");
            assertEquals("simpleUpdate: unexpected number of changed values in the database", 
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateWithSubquery()
    {          
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test updateWithSubquery skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e WHERE e.managedEmployees IS NOT EMPTY");

        // test query
        String update = "UPDATE Employee e SET e.firstName = 'CHANGED'" +  
                        " WHERE (SELECT COUNT(m) FROM e.managedEmployees m) > 0";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("updateWithSubquery: wrong number of updated instances", 
                         nrOfEmps, updated);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateEmbedded()
    {          
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test updateEmbedded skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();

        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e");

        // test query
        String update = "UPDATE Employee e SET e.period.startDate = NULL";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("updateEmbedded: wrong number of updated instances", 
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.period.startDate IS NULL");
            assertEquals("updateEmbedded: unexpected number of changed values in the database", 
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateEmbeddedFieldTest()
    {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test updateEmbeddedFieldTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

        EntityManager em = createEntityManager();

        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e");

        // test query
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(1905, 11, 31, 0, 0, 0);
        java.sql.Date startDate = new java.sql.Date(startCalendar.getTime().getTime());
        try {
            beginTransaction(em);

            em.createQuery("UPDATE Employee e SET e.period.startDate= :startDate")
            .setParameter("startDate", startDate)
            .executeUpdate();

            commitTransaction(em);
            // check database changes
           
            Query q = em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.period.startDate=:startDate")
            .setParameter("startDate", startDate);
            Object result = q.getSingleResult();
            int nr = ((Number)result).intValue();
            assertEquals("updateEmbedded: unexpected number of changed values in the database", nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }

    }

    public void updateUnqualifiedAttributeInSet()
    {          
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test updateUnqualifiedAttributeInSet skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e");

        // test query
        String update = "UPDATE Employee SET firstName = 'CHANGED'";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("updateUnqualifiedAttributeInSet: wrong number of updated instances", 
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'CHANGED'");
            assertEquals("updateUnqualifiedAttributeInSet: unexpected number of changed values in the database", 
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }

        // test query
        update = "UPDATE Employee SET period.startDate = NULL";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("simpleUpdate: wrong number of updated instances", 
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.period.startDate IS NULL");
            assertEquals("simpleUpdate: unexpected number of changed values in the database", 
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateUnqualifiedAttributeInWhere()
    {          
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test updateUnqualifiedAttributeInWhere skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'Bob'");

        // test query
        String update = 
            "UPDATE Employee SET firstName = 'CHANGED' WHERE firstName = 'Bob'";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("updateUnqualifiedAttributeInWhere: wrong number of updated instances", 
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'CHANGED'");
            assertEquals("simpleUnqualifiedUpdate: unexpected number of changed values in the database", 
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }

        nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e WHERE e.managedEmployees IS NOT EMPTY");
        
        // test query
        update = "UPDATE Employee SET firstName = 'MODIFIED' " + 
                 "WHERE (SELECT COUNT(m) FROM managedEmployees m) > 0";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("simpleUpdate: wrong number of updated instances", 
                         nrOfEmps, updated);
            commitTransaction(em);
            
            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'MODIFIED'");
            assertEquals("simpleUpdate: unexpected number of changed values in the database", 
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    // Bug 13972866: Test for a NPE found in Hermes in regards to unqualified path
    // expression in the where clause mixed with input parameters
    public void updateUnqualifiedAttributeInWhereWithInputParameter() {

   	 EntityManager em = createEntityManager();

        try {
           Query query = em.createQuery("update Employee set salary = :salary where version = :version");
           query.setParameter("salary",  1);
           query.setParameter("version", 2);
        }
        finally {
           if (isTransactionActive(em)){
               rollbackTransaction(em);
           }
       }
    }

    public void updateDateTimeFields()
    {
        EntityManager em = createEntityManager();
        int exp = executeJPQLReturningInt(em, "SELECT COUNT(d) FROM DateTime d");
        String jpql = null;
        int updated = 0;

        // test query setting java.sql.Date field
        try {
            jpql = "UPDATE DateTime SET date = CURRENT_DATE";
            beginTransaction(em);
            updated = em.createQuery(jpql).executeUpdate();
            assertEquals("updateDateTimeFields set date: " + 
                         "wrong number of updated instances", exp, updated);
            commitTransaction(em);
            
            // check database changes
            jpql = "SELECT COUNT(d) FROM DateTime d WHERE d.date <= CURRENT_DATE";
            assertEquals("updateDateTimeFields set date: " + 
                         "unexpected number of changed values in the database", 
                         exp, executeJPQLReturningInt(em, jpql));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
        
        // test query setting java.sql.Time field
        try {
            jpql = "UPDATE DateTime SET time = CURRENT_TIME";
            beginTransaction(em);
            updated = em.createQuery(jpql).executeUpdate();
            assertEquals("updateDateTimeFields set time: " + 
                         "wrong number of updated instances", exp, updated);
            commitTransaction(em);
            
            // check database changes
            jpql = "SELECT COUNT(d) FROM DateTime d WHERE d.time <= CURRENT_TIME";
            assertEquals("updateDateTimeFields set time: " + 
                         "unexpected number of changed values in the database", 
                         exp, executeJPQLReturningInt(em, jpql));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
        
        // test query setting java.sql.Timestamp field
        try {
            jpql = "UPDATE DateTime SET timestamp = CURRENT_TIMESTAMP";
            beginTransaction(em);
            updated = em.createQuery(jpql).executeUpdate();
            assertEquals("updateDateTimeFields set timestamp: " + 
                         "wrong number of updated instances", exp, updated);
            commitTransaction(em);

            // check database changes
            jpql = "SELECT COUNT(d) FROM DateTime d WHERE d.timestamp <= CURRENT_TIMESTAMP";
            assertEquals("updateDateTimeFields set timestamp: " + 
                         "unexpected number of changed values in the database", 
                         exp, executeJPQLReturningInt(em, jpql));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }

        // test query setting java.util.Date field
        beginTransaction(em);
        try {
            jpql = "UPDATE DateTime SET utilDate = CURRENT_TIMESTAMP";
            updated = em.createQuery(jpql).executeUpdate();
            assertEquals("updateDateTimeFields set utilDate: " +
                         "wrong number of updated instances", exp, updated);
            commitTransaction(em);

            // check database changes
            jpql = "SELECT COUNT(d) FROM DateTime d WHERE d.utilDate <= CURRENT_TIMESTAMP";
            assertEquals("updateDateTimeFields set utilDate: " + 
                         "unexpected number of changed values in the database", 
                         exp, executeJPQLReturningInt(em, jpql));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }

        // test query setting java.util.Calendar field
        beginTransaction(em);
        try {
            jpql = "UPDATE DateTime SET calendar = CURRENT_TIMESTAMP";
            updated = em.createQuery(jpql).executeUpdate();
            assertEquals("updateDateTimeFields set calendar: " +
                         "wrong number of updated instances", exp, updated);
            commitTransaction(em);

            // check database changes
            jpql = "SELECT COUNT(d) FROM DateTime d WHERE d.calendar <= CURRENT_TIMESTAMP";
            assertEquals("updateDateTimeFields set calendar: " + 
                         "unexpected number of changed values in the database", 
                         exp, executeJPQLReturningInt(em, jpql));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void simpleDelete()
    {          
        EntityManager em = createEntityManager();
        String jpql = "SELECT COUNT(p) FROM PhoneNumber p WHERE p.areaCode = '613'";
        int nrOfEmps = executeJPQLReturningInt(em, jpql);

        // test query
        String delete = "DELETE FROM PhoneNumber p WHERE p.areaCode = '613'";
        beginTransaction(em);
        try {
            Query q = em.createQuery(delete);
            int deleted = q.executeUpdate();
            assertEquals("simpleDelete: wrong number of deleted instances", 
                         nrOfEmps, deleted);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(em, jpql);
            assertEquals("simpleDelete: unexpected number of instances in the database", 
                         0, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    /** Helper method executing a JPQL query retuning an int value. */
    private int executeJPQLReturningInt(EntityManager em, String jpql) 
    {
        Query q = em.createQuery(jpql);
        Object result = q.getSingleResult();
        return ((Number)result).intValue();
    }

    public void simpleUpdateWithInputParameters() {
   	 EntityManager em = createEntityManager();
       beginTransaction(em);
       try {
      	 String jpql = "Update Employee a SET a.payScale = :acctStatus WHERE LOCATE(:acctName, a.lastName)> 0";
          Query query = em.createQuery(jpql);
          query.setParameter("acctStatus", Employee.SalaryRate.EXECUTIVE);
          query.setParameter("acctName",   "Jones");
          int updated = query.executeUpdate();
          assertEquals("simpleUpdateWithInputParameters: did not update correclty", 2, updated);
          commitTransaction(em);
       }
       finally {
      	 if (isTransactionActive(em)){
      		 rollbackTransaction(em);
      	 }
       }
    }
}

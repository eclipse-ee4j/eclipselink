/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Vector;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Room;
import org.eclipse.persistence.testing.models.jpa.advanced.SimpleRoom;
import org.eclipse.persistence.testing.models.jpa.advanced.entities.EntyA;
import org.eclipse.persistence.testing.models.jpa.advanced.entities.EntyC;

/**
 * <p>
 * <b>Purpose</b>: Test Unit EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for EJBQL functionality
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 */

//This test suite demonstrates the bug 4616218, waiting for bug fix
public class JUnitJPQLUnitTestSuite extends JUnitTestCase
{
  static JUnitDomainObjectComparer comparer;

  public JUnitJPQLUnitTestSuite()
  {
      super();
  }

  public JUnitJPQLUnitTestSuite(String name)
  {
      super(name);
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
    suite.setName("JUnitJPQLUnitTestSuite");
    suite.addTest(new JUnitJPQLUnitTestSuite("testSetup"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testSelectPhoneNumberAreaCode"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testSelectPhoneNumberAreaCodeWithEmployee"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testSelectPhoneNumberNumberWithEmployeeWithExplicitJoin"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testSelectPhoneNumberNumberWithEmployeeWithFirstNameFirst"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testSelectEmployeeWithSameParameterUsedMultipleTimes"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testOuterJoinOnOneToOne"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testOuterJoinPolymorphic"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testFirstResultOnNamedQuery"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testInvertedSelectionCriteriaNullPK"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testInvertedSelectionCriteriaInvalidQueryKey"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testMaxAndFirstResultsOnDataQuery"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testMaxAndFirstResultsOnDataQueryWithGroupBy"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testMaxAndFirstResultsOnObjectQueryOnInheritanceRoot"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testDistinctSelectForEmployeeWithNullAddress"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testObjectNullComparisonWithoutForeignKey"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testCollectionMappingInSelectClause"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testCollectionMappingInWhereClause_1"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testCollectionMappingInWhereClause_2"));
    if (!isJPA10()) {
        // JPA 2.0 Tests
        suite.addTest(new JUnitJPQLUnitTestSuite("testResetFirstResultOnQuery"));
        suite.addTest(new JUnitJPQLUnitTestSuite("testSelectNewJPQLQueryWithQueryResultsCache"));
    }

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

        new AdvancedTableCreator().replaceTables(session);

        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();

        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        //Populate the tables
        employeePopulator.buildExamples();

        //Persist the examples in the database
        employeePopulator.persistExample(session);
    }

    public Vector getAttributeFromAll(String attributeName, Vector objects, Class referenceClass){

        EntityManager em = createEntityManager();

        ClassDescriptor descriptor = getServerSession().getClassDescriptor(referenceClass);
        DirectToFieldMapping mapping = (DirectToFieldMapping)descriptor.getMappingForAttributeName(attributeName);

        Vector attributes = new Vector();
        Object currentObject;
        for(int i = 0; i < objects.size(); i++) {
            currentObject = objects.elementAt(i);
            if(currentObject.getClass() == ReportQueryResult.class) {
                attributes.addElement(
                    ((ReportQueryResult)currentObject).get(attributeName));
            } else {
                attributes.addElement(
                    mapping.getAttributeValueFromObject(currentObject));
            }
        }
        return attributes;
    }

    public void testFirstResultOnNamedQuery(){
        EntityManager em = createEntityManager();
        clearCache();

        Query query = em.createNamedQuery("findAllEmployeesByFirstName");
        List initialList = query.setParameter("firstname", "Nancy").setFirstResult(0).getResultList();

        List secondList = query.setParameter("firstname", "Nancy").setFirstResult(1).getResultList();

        Iterator i = initialList.iterator();
        while (i.hasNext()){
            assertTrue("Employee with incorrect name returned on first query.", ((Employee)i.next()).getFirstName().equals("Nancy"));
        }
        i = secondList.iterator();
        while (i.hasNext()){
            assertTrue("Employee with incorrect name returned on second query.", ((Employee)i.next()).getFirstName().equals("Nancy"));
        }
    }

    public void testResetFirstResultOnQuery() {
        // bug 362804 - firstResult in EJBQueryImpl cannot be reset to 0
        EntityManager em = createEntityManager();
        clearCache();

        Query query = em.createQuery("SELECT e FROM Employee e");
        assertEquals("Query's firstResult should be zero", 0, query.getFirstResult());

        List controlList = query.getResultList();
        List shortenedResults = query.setFirstResult(2).getResultList();
        assertEquals("Full list should be shorter than the control results list", (controlList.size() - 2), shortenedResults.size());

        query.setFirstResult(0);
        assertEquals("Query's firstResult should have been reset to zero", 0, query.getFirstResult());

        List fullResults = query.getResultList();
        assertEquals("Full list should be the same as the control results list", controlList.size(), fullResults.size());

        List shortenedResults2 = query.setFirstResult(3).getResultList();
        assertEquals("Full list should be shorter than the control results list #2", (controlList.size() - 3), shortenedResults2.size());

        query.setFirstResult(0);
        assertEquals("Query's firstResult should have been reset to zero", 0, query.getFirstResult());
    }

    public void testOuterJoinOnOneToOne(){
        EntityManager em = createEntityManager();
        clearCache();
        beginTransaction(em);
        int initialSize = em.createQuery("SELECT e from Employee e JOIN e.address a").getResultList().size();
        Employee emp = new Employee();
        emp.setFirstName("Steve");
        emp.setLastName("Harp");
        em.persist(emp);
        em.flush();
        List result = em.createQuery("SELECT e from Employee e LEFT OUTER JOIN e.address a").getResultList();
        assertTrue("Outer join was not properly added to the query", initialSize + 1 == result.size());
        rollbackTransaction(em);
    }

    public void testOuterJoinPolymorphic(){
        EntityManager em = createEntityManager();
        clearCache();
        List resultList = null;
        try{
            resultList = em.createQuery("SELECT p FROM Project p").getResultList();
        } catch (Exception exception){
            fail("Exception caught while executing polymorphic query.  This may mean that outer join is not working correctly on your database platfrom: " + exception.toString());
        }
        assertTrue("Incorrect number of projects returned.", resultList.size() == 15);
    }

  //This test case demonstrates the bug 4616218
  public void testSelectPhoneNumberAreaCode()
  {

        ExpressionBuilder employeeBuilder = new ExpressionBuilder();
        Expression phones = employeeBuilder.anyOf("phoneNumbers");
        Expression whereClause = phones.get("areaCode").equal("613");

        ReportQuery rq = new ReportQuery();
        rq.setSelectionCriteria(whereClause);
        rq.addAttribute("areaCode", new ExpressionBuilder().anyOf("phoneNumbers").get("areaCode"));
        rq.setReferenceClass(Employee.class);
        rq.dontUseDistinct(); // distinct no longer used on joins in JPQL for gf bug 1395
        EntityManager em = createEntityManager();
        Vector expectedResult = getAttributeFromAll("areaCode", (Vector)getServerSession().executeQuery(rq),Employee.class);

        clearCache();

        List result = em.createQuery("SELECT phone.areaCode FROM Employee employee, IN (employee.phoneNumbers) phone " +
            "WHERE phone.areaCode = \"613\"").getResultList();

        assertTrue("SimpleSelectPhoneNumberAreaCode test failed !", comparer.compareObjects(result,expectedResult));
  }


    public void testSelectPhoneNumberAreaCodeWithEmployee()
    {
        EntityManager em = createEntityManager();
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").equal("Bob");
        exp = exp.and(employees.get("lastName").equal("Smith"));
        Employee emp = (Employee) getServerSession().readAllObjects(Employee.class, exp).firstElement();

        PhoneNumber phone = (PhoneNumber) ((Vector)emp.getPhoneNumbers()).firstElement();
        String areaCode = phone.getAreaCode();
        String firstName = emp.getFirstName();

        ExpressionBuilder employeeBuilder = new ExpressionBuilder();
        Expression phones = employeeBuilder.anyOf("phoneNumbers");
        Expression whereClause = phones.get("areaCode").equal(areaCode).and(
        phones.get("owner").get("firstName").equal(firstName));

        ReportQuery rq = new ReportQuery();
        rq.setSelectionCriteria(whereClause);
        rq.addAttribute("areaCode", phones.get("areaCode"));
        rq.setReferenceClass(Employee.class);
        rq.dontMaintainCache();

        Vector expectedResult = getAttributeFromAll("areaCode", (Vector)getServerSession().executeQuery(rq),Employee.class);

        clearCache();

        String ejbqlString;
        ejbqlString = "SELECT phone.areaCode FROM Employee employee, IN (employee.phoneNumbers) phone " +
            "WHERE phone.areaCode = \"" + areaCode + "\" AND phone.owner.firstName = \"" + firstName + "\"";

        List result = em.createQuery(ejbqlString).getResultList();

        assertTrue("SimpleSelectPhoneNumberAreaCodeWithEmployee test failed !", comparer.compareObjects(result,expectedResult));

    }

    public void testSelectPhoneNumberNumberWithEmployeeWithExplicitJoin()
    {
        EntityManager em = createEntityManager();
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").equal("Bob");
        exp = exp.and(employees.get("lastName").equal("Smith"));
        Employee emp = (Employee) getServerSession().readAllObjects(Employee.class, exp).firstElement();

        PhoneNumber phone = (PhoneNumber) ((Vector)emp.getPhoneNumbers()).firstElement();
        String areaCode = phone.getAreaCode();
        String firstName = emp.getFirstName();

        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        Expression phones = employeeBuilder.anyOf("phoneNumbers");
        Expression whereClause = phones.get("areaCode").equal(areaCode).and(
            phones.get("owner").get("id").equal(employeeBuilder.get("id")).and(
                employeeBuilder.get("firstName").equal(firstName)));


        ReportQuery rq = new ReportQuery();
        rq.addAttribute("number", new ExpressionBuilder().anyOf("phoneNumbers").get("number"));
        rq.setSelectionCriteria(whereClause);
        rq.setReferenceClass(Employee.class);

        Vector expectedResult = getAttributeFromAll("number", (Vector)getServerSession().executeQuery(rq),Employee.class);

        clearCache();

        String ejbqlString;
        ejbqlString = "SELECT phone.number FROM Employee employee, IN (employee.phoneNumbers) phone " +
            "WHERE phone.areaCode = \"" + areaCode + "\" AND (phone.owner.id = employee.id AND employee.firstName = \"" + firstName + "\")";

        List result = em.createQuery(ejbqlString).getResultList();

        assertTrue("SimpleSelectPhoneNumberAreaCodeWithEmployee test failed !", comparer.compareObjects(result,expectedResult));

    }

    public void testSelectPhoneNumberNumberWithEmployeeWithFirstNameFirst()
    {
        EntityManager em = createEntityManager();
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").equal("Bob");
        exp = exp.and(employees.get("lastName").equal("Smith"));
        Employee emp = (Employee) getServerSession().readAllObjects(Employee.class, exp).firstElement();

        PhoneNumber phone = (PhoneNumber) ((Vector)emp.getPhoneNumbers()).firstElement();
        String areaCode = phone.getAreaCode();
        String firstName = emp.getFirstName();

        ExpressionBuilder employeeBuilder = new ExpressionBuilder();
        Expression phones = employeeBuilder.anyOf("phoneNumbers");
        Expression whereClause = phones.get("owner").get("firstName").equal(firstName).and(
                phones.get("areaCode").equal(areaCode));

        ReportQuery rq = new ReportQuery();
        rq.setSelectionCriteria(whereClause);
        rq.addAttribute("number", phones.get("number"));
        rq.setReferenceClass(Employee.class);

        Vector expectedResult = getAttributeFromAll("number", (Vector)getServerSession().executeQuery(rq),Employee.class);

        clearCache();

        String ejbqlString;
        ejbqlString = "SELECT phone.number FROM Employee employee, IN(employee.phoneNumbers) phone " +
            "WHERE phone.owner.firstName = \"" + firstName + "\" AND phone.areaCode = \"" + areaCode + "\"";

        List result = em.createQuery(ejbqlString).getResultList();

        assertTrue("SimpleSelectPhoneNumberAreaCodeWithEmployee test failed !", comparer.compareObjects(result,expectedResult));

    }

    public void testSelectEmployeeWithSameParameterUsedMultipleTimes() {
        Exception exception = null;

        try {
            String ejbqlString = "SELECT emp FROM Employee emp WHERE emp.id > :param1 OR :param1 IS null";
            createEntityManager().createQuery(ejbqlString).setParameter("param1", new Integer(1)).getResultList();
        } catch (Exception e) {
            exception = e;
        }

        assertNull("Exception was caught.", exception);
    }

    /**
     * Prior to the fix for GF 2333, the query in this test would a Null PK exception
     *
     */
    public void testInvertedSelectionCriteriaNullPK(){
        Exception exception = null;
        try {
            String jpqlString = "SELECT e, p FROM Employee e, PhoneNumber p WHERE p.id = e.id AND e.firstName = 'Bob'";
            List resultList = createEntityManager().createQuery(jpqlString).getResultList();
        } catch (Exception e) {
            logThrowable(exception);
            exception = e;
        }

        assertNull("Exception was caught.", exception);
    }

    /**
     * Tests fix for bug6070214 that using Oracle Rownum pagination with non-unique columns
     * throws an SQl exception.
     */
    public void testMaxAndFirstResultsOnDataQuery(){
        EntityManager em = createEntityManager();
        Exception exception = null;
        List resultList = null;
        clearCache();
        Query query = em.createQuery("SELECT e.id, m.id FROM Employee e LEFT OUTER JOIN e.manager m");
        try {
            query.setFirstResult(1);
            query.setMaxResults(1);
            resultList = query.getResultList();
        } catch (Exception e) {
            logThrowable(exception);
            exception = e;
        }
        assertNull("Exception was caught: " + exception, exception);
        assertTrue("Incorrect number of results returned.  Expected 1, returned "+resultList.size(), resultList.size()==1);
    }

    /**
     * Tests fix for bug6070214 that using Oracle Rownum pagination with group by
     * throws an SQl exception.
     */
    public void testMaxAndFirstResultsOnDataQueryWithGroupBy() {
        EntityManager em = createEntityManager();
        Exception exception = null;
        List resultList = null;
        clearCache();
        Query query = em.createQuery("SELECT e.id FROM Employee e group by e.id");
        try {
            query.setFirstResult(1);
            query.setMaxResults(1);
            resultList = query.getResultList();
        } catch (Exception e) {
            logThrowable(exception);
            exception = e;
        }
        assertNull("Exception was caught.", exception);
        assertTrue("Incorrect number of results returned.  Expected 1, returned "+resultList.size(), resultList.size()==1);
    }

    /**
     * Tests fix for bug 253258 that using filtering using MaxResults/FirstResult returns
     * the correct number of results on an inheritance root class.
     */
    public void testMaxAndFirstResultsOnObjectQueryOnInheritanceRoot() {
        EntityManager em = createEntityManager();
        Exception exception = null;
        List resultList = null;
        clearCache();
        Query query = em.createQuery("SELECT p FROM Project p");
        try {
            query.setFirstResult(6);
            query.setMaxResults(1);
            resultList = query.getResultList();
        } catch (Exception e) {
            exception = e;
            logThrowable(exception);
        }
        assertNull("Exception was caught.", exception);
        assertTrue("Incorrect number of results returned.  Expected 1, returned "+resultList.size(), resultList.size()==1);
    }

    /**
     * Prior to the fix for GF 2333, the query in this test would generate an invalid query key exception
     *
     */
    public void testInvertedSelectionCriteriaInvalidQueryKey(){
        Exception exception = null;
        try {
            String jpqlString = "select e, a from Employee e, Address a where a.city = 'Ottawa' and e.address.country = a.country";
            List resultList = createEntityManager().createQuery(jpqlString).getResultList();
        } catch (Exception e) {
            logThrowable(e);
            exception = e;
        }

        assertNull("Exception was caught.", exception);
    }

    /*
     * For GF3233, Distinct process fail with NPE when relationship has NULL-valued target.
     */
    public void testDistinctSelectForEmployeeWithNullAddress(){
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            Employee emp = new Employee();
            emp.setFirstName("Dummy");
            emp.setLastName("Person");
            em.persist(emp);
            em.flush();
            List resultList = em.createQuery("SELECT DISTINCT e.address FROM Employee e").getResultList();
        }finally{
            rollbackTransaction(em);
        }
    }

    /*
     * Testcase For ELBug#331352
     * To do Null Object Comparison with OneToOne relationship without Foreign Key.
     */
    public void testObjectNullComparisonWithoutForeignKey() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            EntyC c1 = new EntyC();
            c1.setName("East");
            em.persist(c1);

            EntyC c2 = new EntyC();
            c2.setName("West");
            em.persist(c2);

            EntyC c3 = new EntyC();
            c3.setName("North");
            em.persist(c3);

            EntyC c4 = new EntyC();
            c4.setName("South");
            em.persist(c4);

            EntyA a1 = new EntyA();
            a1.setName("Left");
            a1.setEntyC(c1);
            em.persist(a1);

            EntyA a2 = new EntyA();
            a2.setName("Center");
            a2.setEntyC(c2);
            em.persist(a2);

            EntyA a3 = new EntyA();
            a3.setName("Right");
            a3.setEntyC(c3);
            em.persist(a3);

            em.flush();

            String values = c1.getId() + ", " + c2.getId() + ", " + c4.getId();

            // Testcase for "is not null"
            String jpqlString = "SELECT c FROM EntyC c WHERE c.id IN (" + values + ") AND c.entyA IS NOT NULL";
            List<EntyC> result = em.createQuery(jpqlString).getResultList();
            assertEquals("Incorrect result found with \"is not null\" object comparison and source without foreign key dependency to its target in One To One Mapping",
                    2, result.size());

            // Testcase for "is null". Need to use LEFT OUTER JOIN.
            jpqlString = "SELECT c FROM EntyC c LEFT OUTER JOIN c.entyA a WHERE c.id IN (" + values + ") AND a IS NULL";
            result = em.createQuery(jpqlString).getResultList();
            assertEquals("Incorrect result found with \"is null\" object comparison and source without foreign key dependency to its target in One To One Mapping",
                    1, result.size());
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Testcase For ELBug#13942918 (Oracle BugBase)
     * The state field path 'dn.jpsentry' cannot be resolved to a collection type
     */
    public void testCollectionMappingInSelectClause() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Query query = em.createQuery("SELECT e.phoneNumbers FROM Employee e");
            query.getResultList();
        }
        finally {
            rollbackTransaction(em);
        }
    }

    /**
     * Testcase For ELBug#13942918 (Oracle BugBase)
     * The state field path 'dn.jpsentry' cannot be resolved to a collection type
     */
    public void testCollectionMappingInWhereClause_1() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Query query = em.createQuery("Select e.phoneNumbers from Employee e");
            List result = query.getResultList();

            query = em.createQuery("Select e from Employee e where e.phoneNumbers = :phone");
            query.setParameter("phone", result.get(0));
            query.getResultList();
        }
        finally {
            rollbackTransaction(em);
        }
    }

    /**
     * Testcase For ELBug#13942918 (Oracle BugBase)
     * The state field path 'dn.jpsentry' cannot be resolved to a collection type
     */
    public void testCollectionMappingInWhereClause_2() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Query query = em.createQuery("Select e from Employee e where e.phoneNumbers.areaCode = '613'");
            query.getResultList();
            fail("Failed to throw expected IllegalArgumentException for a query " +
                 "navigating a collection valued association field in the SELECT clause.");
        }
        catch (Exception e) {
            // That's what we want
        }
        finally {
            rollbackTransaction(em);
        }
    }

    /**
     * Bug 501272
     * Test a JPQL query with 'select new' syntax with a named query and query results
     * cache enabled. Tests if any SQL is generated & correct results after initial query.
     * @see SimpleRoom
     */
    public void testSelectNewJPQLQueryWithQueryResultsCache() {
        EntityManager em = createEntityManager();
        QuerySQLTracker tracker = null;
        
        // create test data and clear cache
        beginTransaction(em);
        Room testRoom = new Room();
        final int width = 10;
        final int length = 8;
        testRoom.setWidth(width);
        testRoom.setLength(length);
        testRoom.setHeight(12); // completeness
        em.persist(testRoom);
        commitTransaction(em);
        clearCache();
        
        em = createEntityManager();
        
        try {
            int numberOfExecutions = 6;
            for (int i = 0; i < numberOfExecutions; i++) {
                // Uses JPQL 'SELECT NEW' syntax, copying results into the provided Entity constructor 
                Query query = em.createNamedQuery("room.findSimpleRoomByWidthLength");
                query.setParameter("width", width);
                query.setParameter("length", length);
                SimpleRoom simpleRoom = (SimpleRoom)query.getSingleResult();
                
                assertNotNull("Returned Entity should be non-null", simpleRoom);
                assertEquals("width must be the same", simpleRoom.getWidth(), width);
                assertEquals("length must be the same", simpleRoom.getLength(), length);
                
                // create Query SQL tracker after first run, no more SQL should be executed
                if (tracker == null) {
                    tracker = new QuerySQLTracker(getServerSession());
                }
            }
            
            // SQL should not have been executed after the creation of the QuerySQLTracker, 
            // as the query results cache is enabled for this named query
            assertEquals("More SQL queries executed than expected", 0, tracker.getSqlStatements().size());
        } finally {
            if (tracker != null) {
                tracker.remove();
            }
            // remove test data if it exists
            if (testRoom != null ) {
                Room existingRoom = em.find(Room.class, testRoom.getId());
                if (existingRoom != null) {
                    beginTransaction(em);
                    em.remove(em.find(Room.class, testRoom.getId()));
                    commitTransaction(em);
                }
            }
            closeEntityManager(em);
        }
    }

}

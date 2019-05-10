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
//     July 22, 2009-2.0 Chris Delahunt
//       - TODO Bug#: Bug Description
package org.eclipse.persistence.testing.tests.jpa.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;

import org.eclipse.persistence.testing.tests.jpa.advanced.DataHolder;
import org.eclipse.persistence.testing.tests.jpa.jpql.*;

/**
 * <p>
 * <b>Purpose</b>: Test Unit Criteria API functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for Criteria API functionality
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 * @see JUnitJPQLUnitTestSuite
 */

//This test suite demonstrates the bug 4616218, waiting for bug fix
public class JUnitCriteriaUnitTestSuite extends JUnitTestCase
{
  static JUnitDomainObjectComparer comparer;

  public JUnitCriteriaUnitTestSuite()
  {
      super();
  }

  public JUnitCriteriaUnitTestSuite(String name)
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
    suite.setName("JUnitCriteriaUnitTestSuite");
    suite.addTest(new JUnitCriteriaUnitTestSuite("testSetup"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testExistWithJoin"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testSelectPhoneNumberAreaCode"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testSelectPhoneNumberAreaCodeWithEmployee"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testSelectPhoneNumberNumberWithEmployeeWithExplicitJoin"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testSelectPhoneNumberNumberWithEmployeeWithFirstNameFirst"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testSelectEmployeeWithSameParameterUsedMultipleTimes"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testOuterJoinOnOneToOne"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testOuterJoinPolymorphic"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testFirstResult"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testInvertedSelectionCriteriaNullPK"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testInvertedSelectionCriteriaInvalidQueryKey"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testMaxAndFirstResultsOnDataQuery"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testMaxAndFirstResultsOnDataQueryWithGroupBy"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testMaxAndFirstResultsOnObjectQueryOnInheritanceRoot"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testDistinctSelectForEmployeeWithNullAddress"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testConstructorWithFunctionParameters"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testNonExistentConstructorCriteriaQuery"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testIsNullAndIsNotNull"));
    suite.addTest(new JUnitCriteriaUnitTestSuite("testIsNullOrIsNull"));

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

    public Vector getAttributeFromAll(String attributeName, Vector objects, Class referenceClass) {
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

    public void testFirstResult(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            clearCache();

            //SELECT OBJECT(e) FROM Employee e WHERE e.firstName = :firstname
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root from = cq.from(Employee.class);
            cq.where(qb.equal(from.get("firstName"), qb.parameter(String.class, "firstname")));
            Query query = em.createQuery(cq);

            List initialList = query.setParameter("firstname", "Nancy").setFirstResult(0).getResultList();

            List secondList = query.setParameter("firstname", "Nancy").setFirstResult(1).getResultList();

            Iterator i = initialList.iterator();
            while (i.hasNext()){
                assertEquals("Employee with incorrect name returned on first query.", "Nancy", ((Employee)i.next()).getFirstName());
            }
            i = secondList.iterator();
            while (i.hasNext()){
                assertEquals("Employee with incorrect name returned on second query.", "Nancy", ((Employee)i.next()).getFirstName());
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //done upto here
    public void testOuterJoinOnOneToOne(){
        EntityManager em = createEntityManager();
        clearCache();
        beginTransaction(em);
        //"SELECT e from Employee e JOIN e.address a"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.from(Employee.class).join("address");
        cq.distinct(true);
        int initialSize = em.createQuery(cq).getResultList().size();

        Employee emp = new Employee();
        emp.setFirstName("Steve");
        emp.setLastName("Harp");
        em.persist(emp);
        em.flush();
        //"SELECT e from Employee e LEFT OUTER JOIN e.address a"
        qb = em.getCriteriaBuilder();
        cq = qb.createQuery(Employee.class);
        cq.from(Employee.class).join("address", JoinType.LEFT);
        cq.distinct(true);

        List result = em.createQuery(cq).getResultList();


        assertEquals("Outer join was not properly added to the query", initialSize + 1, result.size());
        rollbackTransaction(em);
    }

    public void testOuterJoinPolymorphic(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            clearCache();
            List resultList = null;
            try{
                resultList = em.createQuery(em.getCriteriaBuilder().createQuery(Project.class)).getResultList();
            } catch (Exception exception){
                fail("Exception caught while executing polymorphic query.  This may mean that outer join is not working correctly on your database platfrom: " + exception.toString());
            }
            assertEquals("Incorrect number of projects returned.", 15, resultList.size());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
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
        beginTransaction(em);
        try{
            Vector expectedResult = getAttributeFromAll("areaCode", (Vector)getServerSession().executeQuery(rq),Employee.class);
            clearCache();

           //List result = em.createQuery("SELECT phone.areaCode FROM Employee employee, IN (employee.phoneNumbers) phone " +
           //    "WHERE phone.areaCode = \"613\"").getResultList();
           CriteriaBuilder qb = em.getCriteriaBuilder();
           CriteriaQuery<String> cq = qb.createQuery(String.class);
           Root<Employee> root = cq.from(Employee.class);
           Join phone = root.join("phoneNumbers");
           cq.select(phone.get("areaCode"));
           cq.where(qb.equal(phone.get("areaCode"), "613"));
           List result = em.createQuery(cq).getResultList();

           assertTrue("SimpleSelectPhoneNumberAreaCode test failed !", comparer.compareObjects(result,expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
  }

  public void testExistWithJoin()
  {
      EntityManager em = createEntityManager();

      List expected = em.createQuery("SELECT c FROM Employee c WHERE NOT EXISTS (SELECT o1 FROM c.phoneNumbers o1)").getResultList();
      beginTransaction(em);
      try {
          CriteriaBuilder qbuilder = em.getCriteriaBuilder();
          CriteriaQuery<Employee> cquery =qbuilder.createQuery(Employee.class);
           Root<Employee> customer = cquery.from(Employee.class);
           cquery.select(customer);

           Subquery<PhoneNumber> sq = cquery.subquery(PhoneNumber.class);
           // correlate subquery root to root of main query:
           Root<Employee> sqc = sq.correlate(customer);
           Join<Employee, PhoneNumber> sqo = sqc.join("phoneNumbers");
           sq.select(sqo);
           cquery.where(qbuilder.not(qbuilder.exists(sq)));
           List result = em.createQuery(cquery).getResultList();
           assertTrue("testExistWithJoin test failed !", comparer.compareObjects(result,expected));
      } finally {
          rollbackTransaction(em);
          closeEntityManager(em);
      }
  }

    public void testSelectPhoneNumberAreaCodeWithEmployee()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
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

            //"SELECT phone.areaCode FROM Employee employee, IN (employee.phoneNumbers) phone " +
            //    "WHERE phone.areaCode = \"" + areaCode + "\" AND phone.owner.firstName = \"" + firstName + "\"";
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<String> cq = qb.createQuery(String.class);
            Root<Employee> root = cq.from(Employee.class);
            Join joinedPhone = root.join("phoneNumbers");
            cq.select(joinedPhone.get("areaCode"));
            cq.where(qb.and(qb.equal(joinedPhone.get("areaCode"), "613")), qb.equal(joinedPhone.get("owner").get("firstName"), firstName));
            List result = em.createQuery(cq).getResultList();

            assertTrue("SimpleSelectPhoneNumberAreaCodeWithEmployee test failed !", comparer.compareObjects(result,expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testSelectPhoneNumberNumberWithEmployeeWithExplicitJoin()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
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

            //"SELECT phone.number FROM Employee employee, IN (employee.phoneNumbers) phone " +
            //    "WHERE phone.areaCode = \"" + areaCode + "\" AND (phone.owner.id = employee.id AND employee.firstName = \"" + firstName + "\")";
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<String> cq = qb.createQuery(String.class);
            Root<Employee> root = cq.from(Employee.class);
            Join joinedPhone = root.join("phoneNumbers");
            cq.select(joinedPhone.get("number"));
            //cq.where(qb.equal(joinedPhone.get("areaCode"), areaCode).add(qb.equal(joinedPhone.get("owner").get("id"), root.get("id"))).add(qb.equal(root.get("firstName"), firstName)));
            Predicate firstAnd = qb.and(qb.equal(joinedPhone.get("areaCode"), areaCode), qb.equal(joinedPhone.get("owner").get("id"), root.get("id")));
            cq.where(qb.and( firstAnd, qb.equal(root.get("firstName"), firstName)));
            List result = em.createQuery(cq).getResultList();

            assertTrue("SimpleSelectPhoneNumberAreaCodeWithEmployee test failed !", comparer.compareObjects(result,expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testSelectPhoneNumberNumberWithEmployeeWithFirstNameFirst()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
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

            //"SELECT phone.number FROM Employee employee, IN(employee.phoneNumbers) phone " +
            //    "WHERE phone.owner.firstName = \"" + firstName + "\" AND phone.areaCode = \"" + areaCode + "\"";
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<String> cq = qb.createQuery(String.class);
            Root<Employee> root = cq.from(Employee.class);
            Join joinedPhone = root.join("phoneNumbers");
            cq.select(joinedPhone.get("number"));
            Predicate firstNameEquality = qb.equal(joinedPhone.get("owner").get("firstName"), firstName);
            Predicate areaCodeEquality =qb.equal(joinedPhone.get("areaCode"), areaCode);
            cq.where( qb.and(firstNameEquality, areaCodeEquality) );
            List result = em.createQuery(cq).getResultList();

            assertTrue("SimpleSelectPhoneNumberAreaCodeWithEmployee test failed !", comparer.compareObjects(result,expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testSelectEmployeeWithSameParameterUsedMultipleTimes() {
        Exception exception = null;

        try {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            try{
                //"SELECT emp FROM Employee emp WHERE emp.id > :param1 OR :param1 IS null";
                CriteriaBuilder qb = em.getCriteriaBuilder();
                CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
                Root<Employee> root = cq.from(Employee.class);
                ParameterExpression<Integer> param1 = qb.parameter(Integer.class);
                //need to cast to erase the type on the get("id") expression so it matches the type on param1
                cq.where(qb.or( qb.greaterThan(root.<Integer>get("id" ), param1), param1.isNull()) );

                em.createQuery(cq).setParameter(param1, new Integer(1)).getResultList();
            } finally {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        } catch (Exception e) {
            exception = e;
        }

        assertNull("Exception was caught.", exception);
    }

    /**
     * Prior to the fix for GF 2333, the query in this test would a Null PK exception
     *
     */
    public void testInvertedSelectionCriteriaNullPK() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            //"SELECT e, p FROM Employee e, PhoneNumber p WHERE p.id = e.id AND e.firstName = 'Bob'"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = qb.createTupleQuery();
            Root<Employee> rootEmp = cq.from(Employee.class);
            Root<PhoneNumber> rootPhone = cq.from(PhoneNumber.class);
            cq.multiselect(rootEmp, rootPhone);
            cq.where(qb.and( qb.equal(rootPhone.get("id"), rootEmp.get("id")), qb.equal(rootEmp.get("firstName"), "Bob")) );

            em.createQuery(cq).getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Tests fix for bug6070214 that using Oracle Rownum pagination with non-unique columns
     * throws an SQl exception.
     */
    public void testMaxAndFirstResultsOnDataQuery(){
        Exception exception = null;
        List resultList = null;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            clearCache();
            //"SELECT e.id, m.id FROM Employee e LEFT OUTER JOIN e.manager m"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = qb.createTupleQuery();
            Root<Employee> rootEmp = cq.from(Employee.class);
            Join joinedManager = rootEmp.join("manager", JoinType.LEFT);
            cq.multiselect(rootEmp.get("id"), joinedManager.get("id"));

            Query query = em.createQuery(cq);
            try {
                query.setFirstResult(1);
                query.setMaxResults(1);
                resultList = query.getResultList();
            } catch (Exception e) {
                logThrowable(exception);
                exception = e;
            }
            assertNull("Exception was caught: " + exception, exception);
            assertEquals("Incorrect number of results returned", 1, resultList.size());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Tests fix for bug6070214 that using Oracle Rownum pagination with group by
     * throws an SQl exception.
     */
    public void testMaxAndFirstResultsOnDataQueryWithGroupBy() {
        Exception exception = null;
        List resultList = null;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            clearCache();
            //"SELECT e.id FROM Employee e group by e.id"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery(Integer.class);
            Root<Employee> rootEmp = cq.from(Employee.class);
            Join joinedManager = rootEmp.join("manager", JoinType.LEFT);
            cq.select(rootEmp.get("id"));

            Query query = em.createQuery(cq);
            try {
                query.setFirstResult(1);
                query.setMaxResults(1);
                resultList = query.getResultList();
            } catch (Exception e) {
                logThrowable(exception);
                exception = e;
            }
            assertNull("Exception was caught: " + exception, exception);
            assertEquals("Incorrect number of results returned", 1, resultList.size());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Tests fix for bug 253258 that using filtering using MaxResults/FirstResult returns
     * the correct number of results on an inheritance root class.
     */
    public void testMaxAndFirstResultsOnObjectQueryOnInheritanceRoot() {
        Exception exception = null;
        List resultList = null;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            clearCache();
            //"SELECT p FROM Project p"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Project> cq = qb.createQuery(Project.class);

            Query query = em.createQuery(cq);
            try {
                query.setFirstResult(6);
                query.setMaxResults(1);
                resultList = query.getResultList();
            } catch (Exception e) {
                exception = e;
                logThrowable(exception);
            }
            assertNull("Exception was caught.", exception);
            assertEquals("Incorrect number of results returned", 1, resultList.size());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Prior to the fix for GF 2333, the query in this test would generate an invalid query key exception
     *
     */
    public void testInvertedSelectionCriteriaInvalidQueryKey(){
        Exception exception = null;
        try {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            try{
                //"select e, a from Employee e, Address a where a.city = 'Ottawa' and e.address.country = a.country"
                CriteriaBuilder qb = em.getCriteriaBuilder();
                CriteriaQuery<Tuple> cq = qb.createTupleQuery();
                Root<Employee> rootEmp = cq.from(Employee.class);
                Root<Address> rootAddress = cq.from(Address.class);
                cq.multiselect(rootEmp, rootAddress);
                cq.where(qb.and( qb.equal(rootAddress.get("city"), "Ottawa"), qb.equal(rootEmp.get("address").get("country"), rootAddress.get("country"))));

                List resultList =  em.createQuery(cq).getResultList();
            } finally {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
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
            //"SELECT DISTINCT e.address FROM Employee e"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery cq = qb.createQuery();
            Root<Employee> rootEmp = cq.from(Employee.class);
            cq.select(rootEmp.get("address"));
            cq.distinct(true);

            List resultList =  em.createQuery(cq).getResultList();
        }finally{
            rollbackTransaction(em);
        }
    }

    /*
     * For 297385, Constructor expressions are not working with function arguments.
     */
    public void testConstructorWithFunctionParameters(){
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            Employee emp = new Employee();
            emp.setFirstName("Very");
            emp.setLastName("Dumb");
            emp.setSalary(-100);//Employee pays to work?
            em.persist(emp);
            em.flush();
            CriteriaBuilder qb = em.getCriteriaBuilder();
            // Test constructor DataHolder(int)
            CriteriaQuery<?> cq = qb.createQuery(DataHolder.class);
            Root<Employee> from = cq.from(Employee.class);
            EntityType<Employee> Emp_ = em.getMetamodel().entity(Employee.class);
            cq.multiselect( qb.min(from.get(Emp_.getSingularAttribute("salary", int.class))));
            List resultList =  em.createQuery(cq).getResultList();
            Object resultObject = resultList.get(0);
            assertTrue("constructor expression test expected DataHolder object, got " + resultObject,(resultObject instanceof DataHolder));
            assertEquals("Expected DataHolder to contain int value of -100, Got :" + resultObject, -100, ((DataHolder)resultObject).getPrimitiveInt());
        } finally {
            rollbackTransaction(em);
        }
    }

    public void testNonExistentConstructorCriteriaQuery(){
        Exception expectedException = null;
        EntityManager em = createEntityManager();
        CriteriaBuilder qb = em.getCriteriaBuilder();
            // Test constructor object:
        CriteriaQuery<?> cq = qb.createQuery(DataHolder.class);
        Root<Employee> from = cq.from(Employee.class);
        EntityType<Employee> Emp_ = em.getMetamodel().entity(Employee.class);
            //IllegalArgumentException expected from multiselect since DataHolder(String) does not exist
        try{
            cq.multiselect( from.get(Emp_.getSingularAttribute("lastName", String.class)));
            Query query = em.createQuery(cq);
        } catch (IllegalArgumentException exception){
            expectedException = exception;
        }
        this.assertNotNull("Expected IllegalArgumentException not thrown when using a non-existing constructor", expectedException);
    }

    public void testIsNullAndIsNotNull(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        Employee emp = (Employee)em.createQuery("select e from Employee e where e.firstName = 'John' and e.lastName = 'Way'").getSingleResult();
        emp.setFirstName(null);
        emp = (Employee)em.createQuery("select e from Employee e where e.firstName = 'Charles' and e.lastName = 'Chanley'").getSingleResult();
        emp.setFirstName(null);
        emp.setLastName(null);
        em.flush();
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> from = cq.from(Employee.class);
        EntityType<Employee> Emp_ = em.getMetamodel().entity(Employee.class);

        List<Predicate> criteriaList = new ArrayList<Predicate>();
        criteriaList.add(qb.isNotNull(from.get(Emp_.getSingularAttribute("lastName", String.class))));
        criteriaList.add(qb.isNull(from.get(Emp_.getSingularAttribute("firstName", String.class))));
        Predicate criteria = qb.and(criteriaList.toArray(new Predicate[0]));
        cq.where(criteria);
        TypedQuery<Employee> query = em.createQuery(cq);
        List<Employee> results = query.getResultList();
        assertEquals(1, results.size());
        emp = results.get(0);
        assertNull(emp.getFirstName());
        assertNotNull(emp.getLastName());
        } finally {
            rollbackTransaction(em);
        }
    }

    public void testIsNullOrIsNull(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = (Employee)em.createQuery("select e from Employee e where e.firstName = 'John' and e.lastName = 'Way'").getSingleResult();
        emp.setFirstName(null);
        emp = (Employee)em.createQuery("select e from Employee e where e.firstName = 'Charles' and e.lastName = 'Chanley'").getSingleResult();
        emp.setLastName(null);
        em.flush();
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<?> cq = qb.createQuery(Employee.class);
        Root<Employee> from = cq.from(Employee.class);
        EntityType<Employee> Emp_ = em.getMetamodel().entity(Employee.class);

        List<Predicate> criteriaList = new ArrayList<Predicate>();
        criteriaList.add(qb.isNull(from.get(Emp_.getSingularAttribute("lastName", String.class))));
        criteriaList.add(qb.isNull(from.get(Emp_.getSingularAttribute("firstName", String.class))));
        Predicate criteria = qb.or(criteriaList.toArray(new Predicate[0]));
        cq.where(criteria);
        Query query = em.createQuery(cq);
        List results = query.getResultList();
        assertEquals(2, results.size());
        rollbackTransaction(em);
    }
}

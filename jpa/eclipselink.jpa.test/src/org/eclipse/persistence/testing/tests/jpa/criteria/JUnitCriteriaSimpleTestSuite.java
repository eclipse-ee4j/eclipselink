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
 *     Jun 29, 2009-1.0M6 Chris Delahunt 
 *       - TODO Bug#: Bug Description 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.criteria;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.persistence.criteria.CriteriaBuilder.Coalesce;
import javax.persistence.criteria.CriteriaBuilder.SimpleCase;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.inheritance.Car;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.BlueLight;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * @author cdelahun
 * Converted from JUnitJPQLSimpleTestSuite
 */
public class JUnitCriteriaSimpleTestSuite extends JUnitTestCase {

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public JUnitCriteriaSimpleTestSuite() {
        super();
    }

    public JUnitCriteriaSimpleTestSuite(String name) {
        super(name);
    }

    //This method is run at the end of EVERY test case method

    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitCriteriaSimpleTestSuite");
        suite.addTest(new JUnitCriteriaSimpleTestSuite("testSetup"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleJoinFetchTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleJoinFetchTest2"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("baseTestCase"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleABSTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleBetweenTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleConcatTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleConcatTestWithParameters"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleConcatTestWithConstants1"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleThreeArgConcatTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleDistinctTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleDistinctNullTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleDistinctMultipleResultTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleDoubleOrTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleEqualsTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleEqualsTestWithJoin"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("collectionMemberIdentifierEqualsTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("abstractSchemaIdentifierEqualsTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("abstractSchemaIdentifierNotEqualsTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleInOneDotTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleInTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleInListTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleLengthTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleLikeTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleLikeTestWithParameter"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleLikeEscapeTestWithParameter"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleNotBetweenTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleNotEqualsVariablesInteger"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleNotInTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleNotLikeTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleOrFollowedByAndTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleOrFollowedByAndTestWithStaticNames"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleOrTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleParameterTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleParameterTestChangingParameters"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleReverseAbsTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleReverseConcatTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleReverseEqualsTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleReverseLengthTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleReverseParameterTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleReverseSqrtTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleReverseSubstringTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleSqrtTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleSubstringTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleNullTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleNotNullTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("distinctTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleModTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleIsEmptyTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleIsNotEmptyTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleEscapeUnderscoreTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleEnumTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("smallProjectMemberOfProjectsTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("smallProjectNOTMemberOfProjectsTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("selectCountOneToOneTest")); //bug 4616218
        suite.addTest(new JUnitCriteriaSimpleTestSuite("selectOneToOneTest")); //employee.address doesnt not work
        suite.addTest(new JUnitCriteriaSimpleTestSuite("selectPhonenumberDeclaredInINClauseTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("selectPhoneUsingALLTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("selectSimpleMemberOfWithParameterTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("selectSimpleNotMemberOfWithParameterTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("selectSimpleBetweenWithParameterTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("selectSimpleInWithParameterTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("selectAverageQueryForByteColumnTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("multipleExecutionOfCriteriaQueryTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("testOneEqualsOne"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleTypeTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleAsOrderByTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleCaseInWhereTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleCaseInSelectTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("caseConditionInWhereTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("caseConditionInSelectTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleCoalesceInWhereTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleCoalesceInSelectTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("largeProjectCastTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("mapCastTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("oneToOneCastTest"));
        
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
        
        new InheritedTableManager().replaceTables(session);
        
        new InheritanceTableCreator().replaceTables(session);
    }

    
    /**
     * Tests 1=1 returns correct result.
     */
    public void testOneEqualsOne() throws Exception {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            //"SELECT e FROM Employee e"
            Query query = em.createQuery(qb.createQuery(Employee.class));            
            List<Employee> emps = query.getResultList();
    
            Assert.assertNotNull(emps);
            int numRead = emps.size();
    
            //"SELECT e FROM Employee e WHERE 1=1");
            CriteriaQuery cq = qb.createQuery(Employee.class);
            cq.where(qb.equal(qb.literal(1), 1));
            emps = em.createQuery(cq).getResultList();
    
            Assert.assertNotNull(emps);
            Assert.assertEquals(numRead, emps.size());
            
            //"SELECT e FROM Employee e WHERE :arg1=:arg2");
            cq = qb.createQuery(Employee.class);
            cq.where(qb.equal(qb.parameter(Integer.class, "arg1"), qb.parameter(Integer.class, "arg2")));
            query = em.createQuery(cq);
            
            query.setParameter("arg1", 1);
            query.setParameter("arg2", 1);
            emps = query.getResultList();
    
            Assert.assertNotNull(emps);
            Assert.assertEquals(numRead, emps.size());
            
            ExpressionBuilder builder = new ExpressionBuilder();
            query = ((JpaEntityManager)em.getDelegate()).createQuery(builder.value(1).equal(builder.value(1)), Employee.class);
            emps = query.getResultList();
    
            Assert.assertNotNull(emps);
            Assert.assertEquals(numRead, emps.size());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }    
    }
    
    //GF Bug#404
    //1.  Fetch join now works with LAZY.  The fix is to trigger the value holder during object registration.  The test is to serialize
    //the results and deserialize it, then call getPhoneNumbers().size().  It used to throw an exception because the value holder 
    //wasn't triggered and the data was in a transient attribute that was lost during serialization
    //2.  Test both scenarios of using the cache and bypassing the cache
    public void simpleJoinFetchTest() throws Exception {
        if (isOnServer()) {
            // Not work on server.
            return;
        }
        org.eclipse.persistence.jpa.JpaEntityManager em = (org.eclipse.persistence.jpa.JpaEntityManager)createEntityManager();
        simpleJoinFetchTest(em);
    }
    
    
    //bug#6130550:  
    // tests that Fetch join works when returning objects that may already have been loaded in the em/uow (without the joined relationships)
    // Builds on simpleJoinFetchTest
    public void simpleJoinFetchTest2() throws Exception {
        if (isOnServer()) {
            // Not work on server.
            return;
        }
        org.eclipse.persistence.jpa.JpaEntityManager em = (org.eclipse.persistence.jpa.JpaEntityManager)createEntityManager();
        //preload employees into the cache so that phonenumbers are not prefetched
        String ejbqlString = "SELECT e FROM Employee e";
        List result = em.createQuery(ejbqlString).getResultList();
        result.size();
        // run the simpleJoinFetchTest and verify all employees have phonenumbers fetched.
        simpleJoinFetchTest(em);
    }

    public void simpleJoinFetchTest(org.eclipse.persistence.jpa.JpaEntityManager em) throws Exception {
        //"SELECT e FROM Employee e LEFT JOIN FETCH e.phoneNumbers"

        em.createQuery("select e from Employee e left join fetch e.phoneNumbers").getResultList();
        //use the cache
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        root.fetch("phoneNumbers", JoinType.LEFT);
        List result = em.createQuery(cq).getResultList();
        
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(byteStream);

        stream.writeObject(result);
        stream.flush();
        byte arr[] = byteStream.toByteArray();
        ByteArrayInputStream inByteStream = new ByteArrayInputStream(arr);
        ObjectInputStream inObjStream = new ObjectInputStream(inByteStream);

        List deserialResult = (List)inObjStream.readObject();
        for (Iterator iterator = deserialResult.iterator(); iterator.hasNext(); ) {
            Employee emp = (Employee)iterator.next();
            emp.getPhoneNumbers().size();
        }
            
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setShouldReturnWithoutReportQueryResult(true);
        reportQuery.setReferenceClass(Employee.class);
        ExpressionBuilder builder = reportQuery.getExpressionBuilder();
        List joins = new ArrayList(1);
        joins.add(builder.anyOfAllowingNone("phoneNumbers"));
        reportQuery.addItem("emp", builder, joins);
        Vector expectedResult = (Vector)em.getUnitOfWork().executeQuery(reportQuery);

        if (!comparer.compareObjects(result, expectedResult)) {
            this.fail("simpleJoinFetchTest Failed when using cache, collections do not match: " + result + " expected: " + expectedResult);
        }

        //Bypass the cache
        clearCache();
        em.clear();

        result = em.createQuery(cq).getResultList();

        byteStream = new ByteArrayOutputStream();
        stream = new ObjectOutputStream(byteStream);

        stream.writeObject(result);
        stream.flush();
        arr = byteStream.toByteArray();
        inByteStream = new ByteArrayInputStream(arr);
        inObjStream = new ObjectInputStream(inByteStream);

        deserialResult = (List)inObjStream.readObject();
        for (Iterator iterator = deserialResult.iterator(); iterator.hasNext(); ) {
            Employee emp = (Employee)iterator.next();
            emp.getPhoneNumbers().size();
        }
            
        clearCache();

        expectedResult = (Vector)em.getUnitOfWork().executeQuery(reportQuery);

        if (!comparer.compareObjects(result, expectedResult)) {
            this.fail("simpleJoinFetchTest Failed when not using cache, collections do not match: " + result + " expected: " + expectedResult);
        }
    }

    //Test case for selecting ALL employees from the database
    public void baseTestCase() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            List expectedResult = getServerSession().readAllObjects(Employee.class);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp"
            List result = em.createQuery(em.getCriteriaBuilder().createQuery(Employee.class)).getResultList();

            Assert.assertTrue("Base Test Case Failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //Test case for ABS function in EJBQL

    public void simpleABSTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE ABS(emp.salary) = " + expectedResult.getSalary();
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where(qb.equal( qb.abs(root.<Number>get("salary")), expectedResult.getSalary()) );
            List result = em.createQuery(cq).getResultList();
        
            Assert.assertTrue("ABS test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //Test case for Between function in EJBQL

    public void simpleBetweenTest() {
        BigDecimal empId = new BigDecimal(0);

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = (Employee)(getServerSession().readAllObjects(Employee.class).lastElement());

            ExpressionBuilder builder = new ExpressionBuilder();
            Expression whereClause = builder.get("id").between(empId, employee.getId());
            ReadAllQuery raq = new ReadAllQuery();
            raq.setReferenceClass(Employee.class);
            raq.setSelectionCriteria(whereClause);

            Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id BETWEEN " + empId + "AND " + employee.getId()
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            //Cast to Expression<Comparable> since empId is BigDec and getId is Integer.  between requires Comparable types; Number is not comparable
            cq.where( qb.between(root.<Comparable>get("id"), qb.literal(empId), qb.literal(employee.getId()) ) );
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Between test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //Test case for concat function in EJBQL

    public void simpleConcatTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

            clearCache();
  
            String partOne, partTwo;

            partOne = expectedResult.getFirstName().substring(0, 2);
            partTwo = expectedResult.getFirstName().substring(2);

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = CONCAT(\"" + partOne + "\", \"" + partTwo + "\")"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.equal(root.get("firstName"), qb.concat(qb.literal(partOne), qb.literal(partTwo))) );
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Concat test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //Test case for concat function in EJBQL taking parameters

    public void simpleConcatTestWithParameters() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

            clearCache();
 
            String partOne = expectedResult.getFirstName().substring(0, 2);
            String partTwo = expectedResult.getFirstName().substring(2);

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = CONCAT( :partOne, :partTwo )"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.equal(root.get("firstName"), qb.concat(qb.parameter(String.class, "partOne"), qb.parameter(String.class, "partTwo"))) );
            Query query = em.createQuery(cq);
            query.setParameter("partOne", partOne).setParameter("partTwo", partTwo);
        
            List result = query.getResultList();

            Assert.assertTrue("Concat test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }


    //Test case for concat function with constants in EJBQL

    public void simpleConcatTestWithConstants1() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

            String partOne = emp.getFirstName();

            ExpressionBuilder builder = new ExpressionBuilder();
            Expression whereClause = builder.get("firstName").concat("Smith").like(partOne + "Smith");

            ReadAllQuery raq = new ReadAllQuery();
            raq.setReferenceClass(Employee.class);
            raq.setSelectionCriteria(whereClause);

            Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE CONCAT(emp.firstName,\"Smith\") LIKE \"" + partOne + "Smith\""
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.like(qb.concat(root.<String>get("firstName"), qb.literal("Smith") ), partOne+"Smith") );
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Concat test with constraints failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleThreeArgConcatTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

            clearCache();

            String partOne, partTwo, partThree;

            partOne = expectedResult.getFirstName().substring(0, 1);
            partTwo = expectedResult.getFirstName().substring(1, 2);
            partThree = expectedResult.getFirstName().substring(2);

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = CONCAT(\"" + partOne + "\", CONCAT(\"" + partTwo 
            //      + "\", \"" + partThree + "\") )"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.equal( root.get("firstName"), qb.concat(qb.literal(partOne), qb.concat( qb.literal(partTwo), qb.literal(partThree)) ) ) );
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Concat test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    public void simpleDistinctTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            //"SELECT DISTINCT e FROM Employee e JOIN FETCH e.phoneNumbers "
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.distinct(true);
            cq.from(Employee.class).join("phoneNumbers");
            List result = em.createQuery(cq).getResultList();
        
            Set testSet = new HashSet();
            for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
                Employee emp = (Employee)iterator.next();
                assertFalse("Result was not distinct", testSet.contains(emp));
                testSet.add(emp);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleDistinctNullTest() {
        EntityManager em = createEntityManager();
        Employee emp = (Employee)em.createQuery("SELECT e from Employee e").getResultList().get(0);
        String oldFirstName = emp.getFirstName();
        beginTransaction(em);
        try {
            emp = em.find(Employee.class, emp.getId());
            emp.setFirstName(null);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        try {
            //"SELECT DISTINCT e.firstName FROM Employee e WHERE e.lastName = '" + emp.getLastName() + "'"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<String> cq = qb.createQuery(String.class);
            cq.distinct(true);
            Root<Employee> root = cq.from(Employee.class);
            cq.select(root.<String>get("firstName"));
            cq.where( qb.equal(root.get("lastName"), qb.literal(emp.getLastName())));
            beginTransaction(em);
            try {
                List result = em.createQuery(cq).getResultList();
            
                assertTrue("Failed to return null value", result.contains(null));
            } finally {
                rollbackTransaction(em);

            }
        } finally {
            try {
                beginTransaction(em);
                emp = em.find(Employee.class, emp.getId());
                emp.setFirstName(oldFirstName);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw ex;
            }

        }
    }

    public void simpleDistinctMultipleResultTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            //"SELECT DISTINCT e, e.firstName FROM Employee e JOIN FETCH e.phoneNumbers "
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = qb.createTupleQuery();
            Root<Employee> root = cq.from(Employee.class);
            root.join("phoneNumbers");
            cq.distinct(true);
            cq.multiselect(root, root.get("firstName"));
            List result = em.createQuery(cq).getResultList();

            Set testSet = new HashSet();
            for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
                String ids = "";
                javax.persistence.Tuple row = (javax.persistence.Tuple)iterator.next();
                Employee emp = row.get(0, Employee.class);
                String string = row.get(1, String.class);
                ids = "_" + emp.getId() + "_" + string;
                assertFalse("Result was not distinct", testSet.contains(ids));
                testSet.add(ids);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //Test case for double OR function in EJBQL

    public void simpleDoubleOrTest() {
        Employee emp1, emp2, emp3;

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            emp1 = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());
            emp2 = (Employee)(getServerSession().readAllObjects(Employee.class).elementAt(1));
            emp3 = (Employee)(getServerSession().readAllObjects(Employee.class).elementAt(2));

            clearCache();
 
            Vector expectedResult = new Vector();
            expectedResult.add(emp1);
            expectedResult.add(emp2);
            expectedResult.add(emp3);

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = " + emp1.getId() + "OR emp.id = " + emp2.getId() + "OR emp.id = " + emp3.getId()
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            Predicate firstOr = qb.or(qb.equal(root.get("id"), emp1.getId()), qb.equal(root.get("id"), emp2.getId()));
            cq.where( qb.or(firstOr, qb.equal(root.get("id"), emp3.getId())) );
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Double OR test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    //Test case for equals in EJBQL

    public void simpleEqualsTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = \"" + expectedResult.getFirstName() + "\""
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.equal(root.get("firstName"), expectedResult.getFirstName() ) );
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Equals test failed", comparer.compareObjects(expectedResult, result));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    //Test case for equals with join in EJBQL

    public void simpleEqualsTestWithJoin() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression whereClause = builder.anyOf("managedEmployees").get("address").get("city").equal("Ottawa");

            Vector expectedResult = getServerSession().readAllObjects(Employee.class, whereClause);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp, IN(emp.managedEmployees) managedEmployees " + "WHERE managedEmployees.address.city = 'Ottawa'"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Join managedEmp = cq.from(Employee.class).join("managedEmployees");
            cq.where( qb.equal(managedEmp.get("address").get("city"), "Ottawa" ) );
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Equals test with Join failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    public void collectionMemberIdentifierEqualsTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            ExpressionBuilder employees = new ExpressionBuilder();
            Expression exp = employees.get("firstName").equal("Bob");
            exp = exp.and(employees.get("lastName").equal("Smith"));
            Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class, exp).firstElement();

            clearCache();

            PhoneNumber phoneNumber = (PhoneNumber)((Vector)expectedResult.getPhoneNumbers()).firstElement();

            //"SELECT OBJECT(emp) FROM Employee emp, IN (emp.phoneNumbers) phone " + "WHERE phone = ?1"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Join phones = cq.from(Employee.class).join("phoneNumbers");
            cq.where( qb.equal(phones, qb.parameter(PhoneNumber.class, "1") ) );
            List result = em.createQuery(cq).setParameter("1", phoneNumber).getResultList();

            Assert.assertTrue("CollectionMemberIdentifierEqualsTest failed", comparer.compareObjects(expectedResult, result));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    public void abstractSchemaIdentifierEqualsTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp = ?1"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.equal(root, qb.parameter(Employee.class, "1") ) );
            List result = em.createQuery(cq).setParameter("1", expectedResult).getResultList();

            Assert.assertTrue("abstractSchemaIdentifierEqualsTest failed", comparer.compareObjects(expectedResult, result));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    public void abstractSchemaIdentifierNotEqualsTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Vector expectedResult = getServerSession().readAllObjects(Employee.class);

            clearCache();

            Employee emp = (Employee)expectedResult.firstElement();

            expectedResult.removeElementAt(0);

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp <> ?1";
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.notEqual(root, qb.parameter(Employee.class, "1") ) );
            List result = em.createQuery(cq).setParameter("1", emp).getResultList();

            Assert.assertTrue("abstractSchemaIdentifierNotEqualsTest failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    public void simpleInOneDotTest() {
        //select a specifif employee using Expr Bob Smithn
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            ReadObjectQuery roq = new ReadObjectQuery(Employee.class);

            ExpressionBuilder empBldr = new ExpressionBuilder();

            Expression exp1 = empBldr.get("firstName").equal("Bob");
            Expression exp2 = empBldr.get("lastName").equal("Smith");

            roq.setSelectionCriteria(exp1.and(exp2));

            Employee expectedResult = (Employee)getServerSession().executeQuery(roq);

            clearCache();

            PhoneNumber empPhoneNumbers = (PhoneNumber)((Vector)expectedResult.getPhoneNumbers()).firstElement();

            //"SelecT OBJECT(emp) from Employee emp, in (emp.phoneNumbers) phone " + "Where phone.areaCode = \"" + empPhoneNumbers.getAreaCode() + "\"" 
            //      + "AND emp.firstName = \"" + expectedResult.getFirstName() + "\"" + "AND emp.lastName = \"" + expectedResult.getLastName() + "\""

            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            Join phone = root.join("phoneNumbers");
            Predicate firstAnd = qb.and( qb.equal(phone.get("areaCode"), empPhoneNumbers.getAreaCode()), 
                qb.equal(root.get("firstName"), expectedResult.getFirstName()));
            cq.where( qb.and(firstAnd, qb.equal(root.get("lastName"), expectedResult.getLastName())) );
            Employee result = em.createQuery(cq).getSingleResult();

            Assert.assertTrue("Simple In Dot Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void selectAverageQueryForByteColumnTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            //"Select AVG(emp.salary)from Employee emp"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Double> cq = qb.createQuery(Double.class);
            Root<Employee> root = cq.from(Employee.class);
            //casting types again.  Avg takes a number, so Path<Object> won't compile
            cq.select( qb.avg( root.<Integer>get("salary") ) );
            Object result = em.createQuery(cq).getSingleResult();

            Assert.assertTrue("AVG result type [" + result.getClass() + "] not of type Double", result.getClass() == Double.class);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    public void simpleInTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN (" + expectedResult.getId().toString() + ")"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.where( qb.in(cq.from(Employee.class).get("id")).value(expectedResult.getId()) );
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple In Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }
    
    public void simpleInListTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

            List expectedResultList = new ArrayList();
            expectedResultList.add(expectedResult.getId());
        
            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN :result"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.where(cq.from(Employee.class).get("id").in(qb.parameter(List.class, "result")));
            List result = em.createQuery(cq).setParameter("result", expectedResultList).getResultList();

            Assert.assertTrue("Simple In Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleLengthTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Assert.assertFalse("Warning SQL doesnot support LENGTH function", (JUnitTestCase.getServerSession()).getPlatform().isSQLServer());

            Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE LENGTH ( emp.firstName     ) = " + expectedResult.getFirstName().length();
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            //casting required, length takes only Expression<String>
            cq.where( qb.equal( qb.length(root.<String>get("firstName")) , expectedResult.getFirstName().length()) );
        
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Length Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }


    public void simpleLikeTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

            clearCache();

            String partialFirstName = expectedResult.getFirstName().substring(0, 3) + "%";
            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE \"" + partialFirstName + "\""
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            //casting required, like takes only Expression<String>
            cq.where( qb.like( root.<String>get("firstName"), partialFirstName) );

            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Like Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleLikeTestWithParameter() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

            String partialFirstName = "%" + emp.getFirstName().substring(0, 3) + "%";

            ReadAllQuery raq = new ReadAllQuery();
            raq.setReferenceClass(Employee.class);

            Vector parameters = new Vector();
            parameters.add(partialFirstName);

            ExpressionBuilder eb = new ExpressionBuilder();
            Expression whereClause = eb.get("firstName").like(partialFirstName);
            raq.setSelectionCriteria(whereClause);
            Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE ?1"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.like( root.<String>get("firstName"), qb.parameter(String.class, "1")) );

            List result = em.createQuery(cq).setParameter("1", partialFirstName).getResultList();

            Assert.assertTrue("Simple Like Test with Parameter failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleLikeEscapeTestWithParameter() {
        EntityManager em = createEntityManager();

            Address expectedResult = new Address();
            expectedResult.setCity("TAIYUAN");
            expectedResult.setCountry("CHINA");
            expectedResult.setProvince("SHANXI");
            expectedResult.setPostalCode("030024");
            expectedResult.setStreet("234 RUBY _Way");

            Server serverSession = JUnitTestCase.getServerSession();
            Session clientSession = serverSession.acquireClientSession();
            UnitOfWork uow = clientSession.acquireUnitOfWork();
            uow.registerObject(expectedResult);
            uow.commit();

            //test the apostrophe
            //"SELECT OBJECT(address) FROM Address address WHERE address.street LIKE :pattern ESCAPE :esc"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Address> cq = qb.createQuery(Address.class);
            Root<Address> root = cq.from(Address.class);
            cq.where( qb.like( root.<String>get("street"), qb.parameter(String.class, "pattern"), qb.parameter(Character.class, "esc")) );
  
            String patternString = null;
            Character escChar = null;
            // \ is always treated as escape in MySQL.  Therefore ESCAPE '\' is considered a syntax error
            if (getServerSession().getPlatform().isMySQL()) {
                patternString = "234 RUBY $_Way";
                escChar = new Character('$');
            } else {
                patternString = "234 RUBY \\_Way";
                escChar = new Character('\\');
            }
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("pattern", patternString).setParameter("esc", escChar).getResultList();

            Assert.assertTrue("Simple Escape Underscore test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleNotBetweenTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
            Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).lastElement();

            ReadAllQuery raq = new ReadAllQuery();
            raq.setReferenceClass(Employee.class);

            ExpressionBuilder eb = new ExpressionBuilder();
            Expression whereClause = eb.get("id").between(emp1.getId(), emp2.getId()).not();

            raq.setSelectionCriteria(whereClause);

            Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id NOT BETWEEN " + emp1.getId() + " AND "+ emp2.getId()
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.not(qb.between(root.<Integer>get("id"), emp1.getId(), emp2.getId())) );

            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Not Between Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleNotEqualsVariablesInteger() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Vector expectedResult = getServerSession().readAllObjects(Employee.class);

            clearCache();

            Employee emp = (Employee)expectedResult.elementAt(0);

            expectedResult.removeElementAt(0);

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id <> " + emp.getId()
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.where( qb.notEqual(cq.from(Employee.class).get("id"), emp.getId()) );

            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Like Test with Parameter failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleNotInTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

            ExpressionBuilder builder = new ExpressionBuilder();

            Vector idVector = new Vector();
            idVector.add(emp.getId());

            Expression whereClause = builder.get("id").notIn(idVector);
            ReadAllQuery raq = new ReadAllQuery();
            raq.setReferenceClass(Employee.class);
            raq.setSelectionCriteria(whereClause);

            Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id NOT IN (" + emp.getId().toString() + ")"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.not(qb.in(root.get("id")).value(emp.getId())) );

            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Not In Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleNotLikeTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

            String partialFirstName = emp.getFirstName().substring(0, 3) + "%";

            ExpressionBuilder builder = new ExpressionBuilder();
            Expression whereClause = builder.get("firstName").notLike(partialFirstName);

            ReadAllQuery raq = new ReadAllQuery();
            raq.setReferenceClass(Employee.class);
            raq.setSelectionCriteria(whereClause);

            Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName NOT LIKE \"" + partialFirstName + "\""
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            cq.where( qb.notLike(root.<String>get("firstName"), partialFirstName ) );

            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Not Like Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleOrFollowedByAndTest() {
        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);
        Employee emp3 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(2);

        Vector expectedResult = new Vector();
        expectedResult.add(emp1);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = " + emp1.getId() + " OR emp.id = " + emp2.getId() + " AND emp.id = " + emp3.getId()
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Predicate andOpp = qb.and(qb.equal(root.get("id"), emp2.getId()), qb.equal(root.get("id"), emp3.getId()));
        cq.where( qb.or( qb.equal(root.get("id"), emp1.getId()), andOpp ) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

		    Assert.assertTrue("Simple Or followed by And Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleOrFollowedByAndTestWithStaticNames() {
        EntityManager em = createEntityManager();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").equal("John").or(builder.get("firstName").equal("Bob").and(builder.get("lastName").equal("Smith")));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = \"John\" OR emp.firstName = \"Bob\" AND emp.lastName = \"Smith\""
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        javax.persistence.criteria.Expression empFName = root.get("firstName");
        Predicate andOpp = qb.and(qb.equal(empFName, "Bob"), qb.equal(root.get("lastName"), "Smith"));
        cq.where( qb.or( qb.equal(empFName, "John"), andOpp ) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Or followed by And With Static Names Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleOrTest() {
        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);

        Vector expectedResult = new Vector();
        expectedResult.add(emp1);
        expectedResult.add(emp2);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = " + emp1.getId() + "OR emp.id = " + emp2.getId()
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        javax.persistence.criteria.Expression empId = cq.from(Employee.class).get("id");
        cq.where( qb.or( qb.equal(empId, emp1.getId()), qb.equal(empId, emp2.getId()) ) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();
            clearCache();

            Assert.assertTrue("Simple Or Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleParameterTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        String parameterName = "firstName";
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").equal(builder.getParameter(parameterName));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.addArgument(parameterName);

        List parameters = new ArrayList();
        parameters.add(expectedResult.getFirstName());

        List employees = (List)getServerSession().executeQuery(raq, parameters);
        employees.size();

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE " + "emp.firstName = ?1 "
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.equal(cq.from(Employee.class).get("firstName"), qb.parameter(String.class,parameterName)) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter(parameterName, expectedResult.getFirstName()).getResultList();

            Assert.assertTrue("Simple Parameter Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleParameterTestChangingParameters() {

        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);

        String parameterName = "firstName";
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").equal(builder.getParameter(parameterName));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.addArgument(parameterName);

        Vector firstParameters = new Vector();
        firstParameters.add(emp1.getFirstName());
        Vector secondParameters = new Vector();
        secondParameters.add(emp2.getFirstName());

        Vector firstEmployees = (Vector)getServerSession().executeQuery(raq, firstParameters);
        clearCache();
        Vector secondEmployees = (Vector)getServerSession().executeQuery(raq, secondParameters);
        clearCache();
        Vector expectedResult = new Vector();
        expectedResult.addAll(firstEmployees);
        expectedResult.addAll(secondEmployees);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE " + "emp.firstName = ?1 "
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.equal(cq.from(Employee.class).get("firstName"), qb.parameter(String.class, "1")) );

        beginTransaction(em);
        try {
            List firstResultSet = em.createQuery(cq).setParameter("1", firstParameters.get(0)).getResultList();
            clearCache();
            List secondResultSet = em.createQuery(cq).setParameter("1", secondParameters.get(0)).getResultList();
            clearCache();
            Vector result = new Vector();
            result.addAll(firstResultSet);
            result.addAll(secondResultSet);

            Assert.assertTrue("Simple Parameter Test Changing Parameters failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleReverseAbsTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);
        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE " + expectedResult.getSalary() + " = ABS(emp.salary)"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        //equal can't take an int as the first argument, it must be an expression
        cq.where( qb.equal(qb.literal(expectedResult.getSalary()), qb.abs(cq.from(Employee.class).<Number>get("salary"))) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Reverse Abs test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleReverseConcatTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String partOne = expectedResult.getFirstName().substring(0, 2);
        String partTwo = expectedResult.getFirstName().substring(2);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE CONCAT(\""+ partOne + "\", \""+ partTwo + "\") = emp.firstName";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        //One argument to concat must be an expression
        cq.where( qb.equal(qb.concat(partOne, qb.literal(partTwo)), cq.from(Employee.class).<String>get("firstName")) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Reverse Concat test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleReverseEqualsTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE \"" + expectedResult.getFirstName() + "\" = emp.firstName"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.equal(qb.literal(expectedResult.getFirstName()), cq.from(Employee.class).get("firstName")) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Reverse Equals test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleReverseLengthTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();
        //"SELECT OBJECT(emp) FROM Employee emp WHERE " + expectedResult.getFirstName().length() + " = LENGTH(emp.firstName)"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        javax.persistence.criteria.Expression<Integer> length = qb.length(cq.from(Employee.class).<String>get("firstName"));
        cq.where( qb.equal(qb.literal(expectedResult.getFirstName().length()), length) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Reverse Length test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    public void simpleReverseParameterTest() {
        EntityManager em = createEntityManager();

        Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String parameterName = "firstName";
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").equal(builder.getParameter(parameterName));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.addArgument(parameterName);

        Vector parameters = new Vector();
        parameters.add(emp.getFirstName());

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq, parameters);
        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE ?1 = emp.firstName "
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.equal(qb.parameter(String.class, "1"), cq.from(Employee.class).get("firstName")) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", parameters.get(0)).getResultList();

            Assert.assertTrue("Simple Reverse Parameter test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleReverseSqrtTest() {
        EntityManager em = createEntityManager();

        ExpressionBuilder expbldr = new ExpressionBuilder();
        Expression whereClause = expbldr.get("firstName").equal("SquareRoot").and(expbldr.get("lastName").equal("TestCase1"));
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        double salarySquareRoot = Math.sqrt((new Double(((Employee)expectedResult.firstElement()).getSalary()).doubleValue()));

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE "+ salarySquareRoot + " = SQRT(emp.salary)"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        javax.persistence.criteria.Expression<Double> sqrt = qb.sqrt(cq.from(Employee.class).<Number>get("salary"));
        cq.where( qb.equal(qb.literal(salarySquareRoot), sqrt) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Reverse Square Root test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    public void simpleReverseSubstringTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String firstNamePart;

        firstNamePart = expectedResult.getFirstName().substring(0, 2);
        //"SELECT OBJECT(emp) FROM Employee emp WHERE \"" + firstNamePart + "\" = SUBSTRING(emp.firstName, 1, 2)";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        javax.persistence.criteria.Expression<String> substring = qb.substring(cq.from(Employee.class).<String>get("firstName"), 1, 2);
        cq.where( qb.equal(qb.literal(firstNamePart), substring) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Reverse SubString test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }


    public void simpleSqrtTest() {
        EntityManager em = createEntityManager();

        ExpressionBuilder expbldr = new ExpressionBuilder();
        Expression whereClause = expbldr.get("firstName").equal("SquareRoot").and(expbldr.get("lastName").equal("TestCase1"));
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        double salarySquareRoot = Math.sqrt((new Double(((Employee)expectedResult.firstElement()).getSalary()).doubleValue()));

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE SQRT(emp.salary) = "+ salarySquareRoot
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.equal(qb.sqrt(cq.from(Employee.class).<Integer>get("salary")), salarySquareRoot) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Square Root test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleSubstringTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String firstNamePart = expectedResult.getFirstName().substring(0, 2);
        //"SELECT OBJECT(emp) FROM Employee emp WHERE SUBSTRING(emp.firstName, 1, 2) = \"" + firstNamePart + "\""
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        javax.persistence.criteria.Expression<String> substring = qb.substring(cq.from(Employee.class).<String>get("firstName"), 1, 2);
        cq.where( qb.equal(substring, firstNamePart) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();
            Assert.assertTrue("Simple SubString test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleNullTest() {
        EntityManager em = createEntityManager();

        Employee nullEmployee = new Employee();
        nullEmployee.setFirstName(null);
        nullEmployee.setLastName("Test");

        Server serverSession = JUnitTestCase.getServerSession();
        Session clientSession = serverSession.acquireClientSession();
        UnitOfWork uow = clientSession.acquireUnitOfWork();
        uow.registerObject(nullEmployee);
        uow.commit();

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").isNull();
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName IS NULL"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.isNull(cq.from(Employee.class).get("firstName")) );

        List result = null;
        beginTransaction(em);
        try {		
		    result = em.createQuery(cq).getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

        uow = clientSession.acquireUnitOfWork();
        uow.deleteObject(nullEmployee);
        uow.commit();

        Assert.assertTrue("Simple Null test failed", comparer.compareObjects(result, expectedResult));

    }

    public void simpleNotNullTest() {
        EntityManager em = createEntityManager();
        Employee nullEmployee = new Employee();
        nullEmployee.setFirstName(null);
        nullEmployee.setLastName("Test");

        Server serverSession = JUnitTestCase.getServerSession();
        Session clientSession = serverSession.acquireClientSession();
        UnitOfWork uow = clientSession.acquireUnitOfWork();
        uow.registerObject(nullEmployee);
        uow.commit();

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").isNull().not();
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName IS NOT NULL"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.isNotNull(cq.from(Employee.class).<String>get("firstName")) );
        List result = null;

        beginTransaction(em);
        try {			
            result = em.createQuery(cq).getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        uow = clientSession.acquireUnitOfWork();
        uow.deleteObject(nullEmployee);
        uow.commit();

        Assert.assertTrue("Simple Not Null test failed", comparer.compareObjects(result, expectedResult));
    }

    public void distinctTest() {
        EntityManager em = createEntityManager();
        ReadAllQuery raq = new ReadAllQuery();

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = employee.get("lastName").equal("Smith");

        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.useDistinct();

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        //"SELECT DISTINCT OBJECT(emp) FROM Employee emp WHERE emp.lastName = \'Smith\'"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.distinct(true);
        cq.where( qb.equal(cq.from(Employee.class).<String>get("lastName"), "Smith") );

        List result = null;
        beginTransaction(em);
        try {
            result = em.createQuery(cq).getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

        Assert.assertTrue("Distinct test failed", comparer.compareObjects(result, expectedResult));
    }

    public void multipleExecutionOfCriteriaQueryTest() {
        //bug 5279859
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            //"SELECT e FROM Employee e where e.address.postalCode = :postalCode"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.where( qb.equal(cq.from(Employee.class).get("address").get("postalCode"), qb.parameter(String.class, "postalCode")) );
            Query query = em.createQuery(cq);
            query.setParameter("postalCode", "K1T3B9");
            try {
                query.getResultList();
            } catch (RuntimeException ex) {
                fail("Failed to execute query, exception resulted on first execution, not expected");
            }
            try {
                query.getResultList();
            } catch (RuntimeException ex) {
                fail("Failed to execute query, exception resulted on second execution");
            }
            query = em.createNamedQuery("findEmployeeByPostalCode", Employee.class);
            query.setParameter("postalCode", "K1T3B9");
            try {
                query.getResultList();
            } catch (RuntimeException ex) {
                fail("Failed to execute query, exception resulted on first execution, of second use of named query");
            }
            query.setMaxResults(100000);
            try {
                query.getResultList();
            } catch (RuntimeException ex) {
                fail("Failed to execute query, exception resulted after setting max results (forcing reprepare)");
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

    }

    public void simpleModTest() {
        EntityManager em = createEntityManager();

        Assert.assertFalse("Warning SQL/Sybase doesnot support MOD function", (JUnitTestCase.getServerSession()).getPlatform().isSQLServer() || (JUnitTestCase.getServerSession()).getPlatform().isSybase());

        ReadAllQuery raq = new ReadAllQuery();

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.mod(employee.get("salary"), 2).greaterThan(0);
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE MOD(emp.salary, 2) > 0"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.gt(qb.mod(cq.from(Employee.class).<Integer>get("salary"), 2), 0) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Mod test failed", comparer.compareObjects(result, expectedResult));

            // Test MOD(fieldAccess, fieldAccess) glassfish issue 2771

            expectedResult = getServerSession().readAllObjects(Employee.class);
            clearCache();

            //"SELECT emp FROM Employee emp WHERE MOD(emp.salary, emp.salary) = 0"
            qb = em.getCriteriaBuilder();
            cq = qb.createQuery(Employee.class);
            javax.persistence.criteria.Expression<Integer> salaryExp = cq.from(Employee.class).<Integer>get("salary");
            cq.where( qb.equal(qb.mod(salaryExp, salaryExp), 0) );

            result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Mod test(2) failed", comparer.compareObjects(result, expectedResult)); 
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
		
    }

    public void simpleIsEmptyTest() {
        EntityManager em = createEntityManager();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.isEmpty("phoneNumbers");

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.phoneNumbers IS EMPTY"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.isEmpty(cq.from(Employee.class).<Collection>get("phoneNumbers")) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Is empty test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleIsNotEmptyTest() {
        EntityManager em = createEntityManager();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.notEmpty("phoneNumbers");

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.phoneNumbers IS NOT EMPTY"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.isNotEmpty(cq.from(Employee.class).<Collection>get("phoneNumbers")) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple is not empty test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //JPQL parsing test, not applicable to criteria api
    //public void simpleApostrohpeTest() {

    public void simpleEscapeUnderscoreTest() {
        EntityManager em = createEntityManager();

        Address expectedResult = new Address();
        expectedResult.setCity("Perth");
        expectedResult.setCountry("Canada");
        expectedResult.setProvince("ONT");
        expectedResult.setPostalCode("Y3Q2N9");
        expectedResult.setStreet("234 Wandering _Way");

        Server serverSession = JUnitTestCase.getServerSession();
        Session clientSession = serverSession.acquireClientSession();
        UnitOfWork uow = clientSession.acquireUnitOfWork();
        uow.registerObject(expectedResult);
        uow.commit();

        Character escapeChar = null;
        //"SELECT OBJECT(address) FROM Address address WHERE address.street LIKE '234 Wandering "
            //+escapeString+"_Way' ESCAPE "+escapeString
        // \ is always treated as escape in MySQL.  Therefore ESCAPE '\' is considered a syntax error
        if (getServerSession().getPlatform().isMySQL() || getServerSession().getPlatform().isPostgreSQL()) {
            escapeChar = '$';
        } else {
            escapeChar = '\\';
        }
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Address> cq = qb.createQuery(Address.class);
        cq.where( qb.like(cq.from(Address.class).<String>get("street"), "234 Wandering "+escapeChar+"_Way", escapeChar) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Escape Underscore test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void smallProjectMemberOfProjectsTest() {
        EntityManager em = createEntityManager();

        ReadAllQuery query = new ReadAllQuery();
        Expression selectionCriteria = new ExpressionBuilder().anyOf("projects").equal(new ExpressionBuilder(SmallProject.class));
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(Employee.class);
        query.dontUseDistinct(); //gf 1395 changed jpql to not use distinct on joins

        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        clearCache();

        //"SELECT OBJECT(employee) FROM Employee employee, SmallProject sp WHERE sp MEMBER OF employee.projects";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.isMember(cq.<SmallProject>from(SmallProject.class), cq.from(Employee.class).<Collection<SmallProject>>get("projects")) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple small Project Member Of Projects test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void smallProjectNOTMemberOfProjectsTest() {
        EntityManager em = createEntityManager();

        //query for those employees with Project named "Enterprise" (which should be a SmallProject)
        ReadObjectQuery smallProjectQuery = new ReadObjectQuery();
        smallProjectQuery.setReferenceClass(SmallProject.class);
        smallProjectQuery.setSelectionCriteria(new ExpressionBuilder().get("name").equal("Enterprise"));
        SmallProject smallProject = (SmallProject)getServerSession().executeQuery(smallProjectQuery);

        ReadAllQuery query = new ReadAllQuery();
        query.addArgument("smallProject");
        Expression selectionCriteria = new ExpressionBuilder().noneOf("projects", new ExpressionBuilder().equal(new ExpressionBuilder().getParameter("smallProject")));

        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(Employee.class);

        Vector arguments = new Vector();
        arguments.add(smallProject);
        Vector expectedResult = (Vector)getServerSession().executeQuery(query, arguments);

        //"SELECT OBJECT(employee) FROM Employee employee WHERE ?1 NOT MEMBER OF employee.projects"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.isNotMember(qb.<Project>parameter(Project.class, "1"), cq.from(Employee.class).<Collection<Project>>get("projects")) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", smallProject).getResultList();

            Assert.assertTrue("Simple small Project NOT Member Of Projects test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //This test demonstrates the bug 4616218, waiting for bug fix

    public void selectCountOneToOneTest() {
        EntityManager em = createEntityManager();

        ReportQuery query = new ReportQuery();
        query.setReferenceClass(PhoneNumber.class);
        //need to specify Long return type
        query.addCount("COUNT", new ExpressionBuilder().get("owner").distinct(), Long.class);
        query.returnSingleAttribute();
        query.dontRetrievePrimaryKeys();
        query.setName("selectEmployeesThatHavePhoneNumbers");

        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        clearCache();

        //"SELECT COUNT(DISTINCT phone.owner) FROM PhoneNumber phone";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.countDistinct(cq.from(PhoneNumber.class).get("owner")));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Select Count One To One test failed", expectedResult.elementAt(0).equals(result.get(0)));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void selectOneToOneTest() {
        EntityManager em = createEntityManager();

        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Address.class);
        query.useDistinct();
        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        Expression selectionCriteria = new ExpressionBuilder(Address.class).equal(employeeBuilder.get("address")).and(employeeBuilder.get("lastName").like("%Way%"));
        query.setSelectionCriteria(selectionCriteria);
        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        clearCache();

        //"SELECT DISTINCT employee.address FROM Employee employee WHERE employee.lastName LIKE '%Way%'"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Address> cq = qb.createQuery(Address.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.distinct(true);
        cq.select(root.<Address>get("address"));
        cq.where(qb.like(root.<String>get("lastName"), "%Way%"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Select One To One test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void selectPhonenumberDeclaredInINClauseTest() {
        EntityManager em = createEntityManager();

        ReadAllQuery query = new ReadAllQuery();
        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        Expression phoneAnyOf = employeeBuilder.anyOf("phoneNumbers");
        ExpressionBuilder phoneBuilder = new ExpressionBuilder(PhoneNumber.class);
        Expression selectionCriteria = phoneBuilder.equal(employeeBuilder.anyOf("phoneNumbers")).and(phoneAnyOf.get("number").notNull());
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(PhoneNumber.class);
        query.addAscendingOrdering("number");
        query.addAscendingOrdering("areaCode");

        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        clearCache();

        //"Select Distinct Object(p) from Employee emp, IN(emp.phoneNumbers) p WHERE p.number IS NOT NULL ORDER BY p.number, p.areaCode";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<PhoneNumber> cq = qb.createQuery(PhoneNumber.class);
        Root<Employee> root = cq.from(Employee.class);
        Join phone = root.join("phoneNumbers");
        cq.select(phone);
        cq.where(qb.isNotNull(phone.get("number")));
        cq.orderBy(qb.asc(phone.get("number")), qb.asc(phone.get("areaCode")));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple select Phonenumber Declared In IN Clause test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Test the ALL function.
     * This test was added to mirror a CTS failure.
     * This currently throws an error but should be a valid (although odd) query.
     * BUG#6025292
    public void badSelectPhoneUsingALLTest()
    {
        EntityManager em = createEntityManager();

        ReadAllQuery query = new ReadAllQuery();
        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        Expression phoneAnyOf = employeeBuilder.anyOf("phoneNumbers");
        ExpressionBuilder phoneBuilder = new ExpressionBuilder(PhoneNumber.class);

        ReportQuery subQuery = new ReportQuery();
        subQuery.setReferenceClass(PhoneNumber.class);
        subQuery.addMinimum("number");//number is a string?

        //bad sql - Expression selectionCriteria = employeeBuilder.anyOf("phoneNumbers").equal(employeeBuilder.all(subQuery));
        //bad sql - Expression selectionCriteria = employeeBuilder.anyOf("phoneNumbers").equal(subQuery);
        Expression selectionCriteria = phoneBuilder.equal(employeeBuilder.anyOf("phoneNumbers")).and(
                phoneAnyOf.get("number").equal(employeeBuilder.all(subQuery)));
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(Employee.class);
        
        Vector expectedResult = (Vector)getServerSession().executeQuery(query);
        
        clearCache();

        //"Select Distinct Object(emp) from Employee emp, IN(emp.phoneNumbers) p WHERE p.number = ALL (Select MIN(pp.number) FROM PhoneNumber pp)";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.distinct(true);
        Subquery<Number> sq = cq.subquery(Number.class);
        Root<PhoneNumber> subroot = cq.from(PhoneNumber.class);
        sq.select(qb.min(subroot.<Number>get("number")));//number is a string? not sure this will work.
        
        Root<Employee> root = cq.from(Employee.class);
        Join phone = root.join("phoneNumbers");
        cq.where(qb.equal(root.get("number"), qb.all(sq)));

        List result = em.createQuery(cq).getResultList();

        Assert.assertTrue("Simple select Phonenumber Declared In IN Clause test failed", comparer.compareObjects(result, expectedResult));

    }*/

    /**
     * Test the ALL function.
     * This test was added to mirror a CTS failure.
     */
    public void selectPhoneUsingALLTest() {
        EntityManager em = createEntityManager();

        ReadAllQuery query = new ReadAllQuery();
        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);

        ReportQuery subQuery = new ReportQuery();
        subQuery.setReferenceClass(PhoneNumber.class);
        subQuery.addMinimum("number");

        Expression selectionCriteria = employeeBuilder.anyOf("phoneNumbers").get("number").equal(employeeBuilder.all(subQuery));
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(Employee.class);

        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        clearCache();

        //"Select Distinct Object(emp) from Employee emp, IN(emp.phoneNumbers) p WHERE p.number = ALL (Select MIN(pp.number) FROM PhoneNumber pp)";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.distinct(true);
        Subquery<Number> sq = cq.subquery(Number.class);
        Root<PhoneNumber> subroot = sq.from(PhoneNumber.class);
        sq.select(qb.min(subroot.<Number>get("number")));//number is a string? not sure this will work.
        
        Root<Employee> root = cq.from(Employee.class);
        Join phone = root.join("phoneNumbers");
        cq.where(qb.equal(phone.get("number"), qb.all(sq)));

        beginTransaction(em);
        try {
            Query jpqlQuery = em.createQuery(cq);
            jpqlQuery.setMaxResults(10);
            List result = jpqlQuery.getResultList();

            Assert.assertTrue("Simple select Phonenumber Declared In IN Clause test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void selectSimpleMemberOfWithParameterTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readObject(Employee.class);

        PhoneNumber phone = new PhoneNumber();
        phone.setAreaCode("613");
        phone.setNumber("1234567");
        phone.setType("cell");

        Server serverSession = JUnitTestCase.getServerSession();
        Session clientSession = serverSession.acquireClientSession();
        UnitOfWork uow = clientSession.acquireUnitOfWork();
        PhoneNumber phoneClone = (PhoneNumber)uow.registerObject(phone);
        Employee empClone = (Employee)uow.registerObject(expectedResult);

        phoneClone.setOwner(empClone);
        empClone.addPhoneNumber(phoneClone);
        uow.registerObject(phone);
        uow.commit();


        //"SELECT OBJECT(emp) FROM Employee emp " + "WHERE ?1 MEMBER OF emp.phoneNumbers";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.where(qb.isMember(qb.parameter(PhoneNumber.class, "1"), root.<Collection<PhoneNumber>>get("phoneNumbers")));

        Vector parameters = new Vector();
        parameters.add(phone);

        List result = null;
        beginTransaction(em);
        try {
            result = em.createQuery(cq).setParameter("1", phone).getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

        uow = clientSession.acquireUnitOfWork();
        uow.deleteObject(phone);
        uow.commit();

        Assert.assertTrue("Select simple member of with parameter test failed", comparer.compareObjects(result, expectedResult));
    }

    public void selectSimpleNotMemberOfWithParameterTest() {
        EntityManager em = createEntityManager();

        Vector expectedResult = getServerSession().readAllObjects(Employee.class);

        clearCache();

        Employee emp = (Employee)expectedResult.get(0);
        expectedResult.remove(0);

        PhoneNumber phone = new PhoneNumber();
        phone.setAreaCode("613");
        phone.setNumber("1234567");
        phone.setType("cell");


        Server serverSession = JUnitTestCase.getServerSession();
        Session clientSession = serverSession.acquireClientSession();
        UnitOfWork uow = clientSession.acquireUnitOfWork();
        emp = (Employee)uow.readObject(emp);
        PhoneNumber phoneClone = (PhoneNumber)uow.registerObject(phone);
        phoneClone.setOwner(emp);
        emp.addPhoneNumber(phoneClone);
        uow.commit();


        //"SELECT OBJECT(emp) FROM Employee emp " + "WHERE ?1 NOT MEMBER OF emp.phoneNumbers"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.where(qb.isNotMember(qb.parameter(PhoneNumber.class, "1"), root.<Collection<PhoneNumber>>get("phoneNumbers")));

        Vector parameters = new Vector();
        parameters.add(phone);

        List result = null;
        beginTransaction(em);
        try {        
            result = em.createQuery(cq).setParameter("1", phone).getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

        uow = clientSession.acquireUnitOfWork();
        uow.deleteObject(phone);
        uow.commit();

        Assert.assertTrue("Select simple Not member of with parameter test failed", comparer.compareObjects(result, expectedResult));
    }

    public void selectSimpleBetweenWithParameterTest() {
        EntityManager em = createEntityManager();

        Vector employees = getServerSession().readAllObjects(Employee.class);

        BigDecimal empId1 = new BigDecimal(0);

        Employee emp2 = (Employee)employees.lastElement();

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("id").between(empId1, emp2.getId());
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id BETWEEN ?1 AND ?2"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.where(qb.between(root.<Comparable>get("id"), qb.parameter(BigDecimal.class, "1"), qb.parameter(Integer.class, "2")));
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", empId1).setParameter("2", emp2.getId()).getResultList();

            Assert.assertTrue("Simple select between with parameter test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void selectSimpleInWithParameterTest() {
        EntityManager em = createEntityManager();

        Vector employees = getServerSession().readAllObjects(Employee.class);

        BigDecimal empId1 = new BigDecimal(0);

        Employee emp2 = (Employee)employees.lastElement();

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        ExpressionBuilder eb = new ExpressionBuilder();
        Vector vec = new Vector();
        vec.add(empId1);
        vec.add(emp2.getId());

        Expression whereClause = eb.get("id").in(vec);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN (?1, ?2)"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        CriteriaBuilder.In inExp = qb.in(root.<Comparable>get("id"));
        inExp.value(qb.parameter(BigDecimal.class, "1"));
        inExp.value(qb.parameter(Integer.class, "2"));
        cq.where(inExp);

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", empId1).setParameter("2", emp2.getId()).getResultList();

            Assert.assertTrue("Simple select between with parameter test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //Test case for ABS function in EJBQL

    public void simpleEnumTest() {
        EntityManager em = createEntityManager();

        //"SELECT emp FROM Employee emp WHERE emp.status =  org.eclipse.persistence.testing.models.jpa.advanced.Employee.EmployeeStatus.FULL_TIME"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where(qb.equal(cq.from(Employee.class).get("status"), org.eclipse.persistence.testing.models.jpa.advanced.Employee.EmployeeStatus.FULL_TIME));
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();
            result.size();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleTypeTest(){
        EntityManager em = createEntityManager();

        List expectedResult = getServerSession().readAllObjects(LargeProject.class);
        
        clearCache();

        //"SELECT OBJECT(proj) FROM Project proj WHERE TYPE(proj) = LargeProject"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Project> cq = qb.createQuery(Project.class);
        cq.where(qb.equal(cq.from(Project.class).type(), org.eclipse.persistence.testing.models.jpa.advanced.LargeProject.class));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("SimpleTypeTest", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    public void simpleAsOrderByTest(){
        EntityManager em = createEntityManager();

        ReportQuery query = new ReportQuery();
        query.setReferenceClass(Employee.class);
        query.addItem("firstName", query.getExpressionBuilder().get("firstName"));
        query.returnSingleAttribute();
        query.dontRetrievePrimaryKeys();
        query.addOrdering(query.getExpressionBuilder().get("firstName").ascending());
        
        Vector expectedResult = (Vector)getServerSession().executeQuery(query);
        
        clearCache();

        //"SELECT e.firstName as firstName FROM Employee e ORDER BY firstName"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = qb.createQuery(String.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.select(root.<String>get("firstName"));
        cq.orderBy(qb.asc(root.get("firstName")));
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("SimpleTypeTest", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleCoalesceInWhereTest(){
        EntityManager em = createEntityManager();
        //select e from Employee e where coalesce(e.firstName, e.lastName) = 'Bob'
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.where(qb.equal(qb.coalesce(root.get("firstName"),root.get("lastName")), "Bob"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Incorrect number of results returned.", result.size() == 1);
            assertTrue("Incorrect Employee returned", ((Employee)result.get(0)).getFirstName().equals("Bob"));
        
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }        
    }
    
    public void simpleCoalesceInSelectTest(){
        EntityManager em = createEntityManager();
        //select coalesce(e.firstName, e.lastName) from Employee e where e.firstName = 'Bob'
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Object> cq = qb.createQuery(Object.class);
        Root<Employee> root = cq.from(Employee.class);
        Coalesce<String> coalesce = qb.coalesce();
        coalesce.value(root.<String>get("firstName"));
        coalesce.value(root.<String>get("lastName"));
        coalesce.value("Bobby");
        cq.select(coalesce);

        cq.where(qb.equal(root.get("firstName"), "Bob"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Incorrect number of results returned.", result.size() == 1);
            assertTrue("Incorrect Employee returned", ((String)result.get(0)).equals("Bob"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }  
    }
    
    public void caseConditionInWhereTest(){
        EntityManager em = createEntityManager();
        //select e from Employee e where case e.firstName when 'Bob' then 'Robert' when 'Rob' then 'Robbie' else 'Not Bob' = 'Bob'
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Case<String> selectCase = qb.selectCase();
        selectCase.when(qb.equal(root.get("firstName"), "Bob"), "Robert");
        selectCase.when(qb.equal(root.get("firstName"), "Rob"), "Robbie");
        selectCase.otherwise("Not Bob");
        cq.where(qb.equal(selectCase, "Robert"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Incorrect number of results returned.", result.size() == 1);
            assertTrue("Incorrect Employee returned", ((Employee)result.get(0)).getFirstName().equals("Bob"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        } 
    }
    
    public void caseConditionInSelectTest(){
        EntityManager em = createEntityManager();
        //select coalesce(e.firstName, e.lastName) from Employee e where e.firstName = 'Bob'
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Object> cq = qb.createQuery(Object.class);
        Root<Employee> root = cq.from(Employee.class);
        Case<String> selectCase = qb.selectCase();
        selectCase.when(qb.equal(root.get("firstName"), "Bob"), "Robert");
        selectCase.when(qb.equal(root.get("firstName"), "Rob"), "Robbie");
        selectCase.otherwise("Not Bob");
        cq.select(selectCase);

        cq.where(qb.equal(root.get("firstName"), "Bob"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Incorrect number of results returned.", result.size() == 1);
            assertTrue("Incorrect Employee returned", ((String)result.get(0)).equals("Robert"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        } 
    }
    
    public void simpleCaseInWhereTest(){
        if (((Session) JUnitTestCase.getServerSession()).getPlatform().isDerby())
        {
            warning("The test simpleCaseInWhereTest is not supported on Derby, because Derby does not support simple CASE");
            return;
        }
        EntityManager em = createEntityManager();
        //select e from Employee e where case e.firstName when 'Bob' then 'Robert' when 'Rob' then 'Robbie' else 'Not Bob' = 'Bob'
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        SimpleCase<Object, String> selectCase = qb.selectCase(root.get("firstName"));
        selectCase.otherwise("Not Bob");
        selectCase.when("Bob", "Robert");
        selectCase.when("Rob", "Robbie");
        cq.where(qb.equal(selectCase, "Robert"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Incorrect number of results returned.", result.size() == 1);
            assertTrue("Incorrect Employee returned", ((Employee)result.get(0)).getFirstName().equals("Bob"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        } 
    }
    
    public void simpleCaseInSelectTest(){
        if (((Session) JUnitTestCase.getServerSession()).getPlatform().isDerby())
        {
            warning("The test simpleCaseInSelectTest is not supported on Derby, because Derby does not support simple CASE");
            return;
        }
        EntityManager em = createEntityManager();
        //select coalesce(e.firstName, e.lastName) from Employee e where e.firstName = 'Bob'
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Object> cq = qb.createQuery(Object.class);
        Root<Employee> root = cq.from(Employee.class);
        SimpleCase<Object, String> selectCase = qb.selectCase(root.get("firstName"));
        selectCase.when("Bob", "Robert");
        selectCase.when("Rob", "Robbie");
        selectCase.otherwise("Not Bob");
        cq.select(selectCase);

        cq.where(qb.equal(root.get("firstName"), "Bob"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Incorrect number of results returned.", result.size() == 1);
            assertTrue("Incorrect Employee returned", ((String)result.get(0)).equals("Robert"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }			
    }
    
    
    public void largeProjectCastTest() {
        EntityManager em = createEntityManager();

        ReadAllQuery query = new ReadAllQuery();
        Expression selectionCriteria = new ExpressionBuilder().anyOf("projects").as(LargeProject.class).get("budget").equal(5000);
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(Employee.class);
        query.dontUseDistinct(); //gf 1395 changed jpql to not use distinct on joins

        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        clearCache();
        
        //"SELECT e from Employee e join cast(e.project, LargeProject) p where p.budget = 1000
        CriteriaBuilder qb1 = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq1 = qb1.createQuery(Employee.class);

        Root<Employee> empRoot = cq1.from(Employee.class);
        Join<Employee, Project> join = empRoot.join("projects");
        Path exp = ((Path)join.as(LargeProject.class)).get("budget");
        cq1.where(qb1.equal(exp, new Integer(5000)) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq1).getResultList();
            Assert.assertTrue("LargeProject cast failed.", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        
    }
    
    public void mapCastTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        BeerConsumer bc1 = new BeerConsumer();
        bc1.setName("George");
        em.persist(bc1);
        Blue blue = new Blue();
        blue.setUniqueKey(new BigInteger("1"));
        em.persist(blue);
        bc1.addBlueBeerToConsume(blue);
        blue.setBeerConsumer(bc1);
        
        BeerConsumer bc2 = new BeerConsumer();
        bc2.setName("Scott");
        em.persist(bc2);
        BlueLight blueLight = new BlueLight();
        blueLight.setDiscount(10);
        blueLight.setUniqueKey(new BigInteger("2"));
        em.persist(blueLight);
        blueLight.setBeerConsumer(bc2);
        bc2.addBlueBeerToConsume(blueLight);
        
        commitTransaction(em);
        clearCache();
        
        ReadAllQuery query = new ReadAllQuery();
        Expression selectionCriteria = new ExpressionBuilder().anyOf("blueBeersToConsume").as(BlueLight.class).get("discount").equal(10);
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(BeerConsumer.class);
        query.dontUseDistinct();
        
        Vector expectedResult = (Vector)getServerSession().executeQuery(query);
        
        clearCache();
        
        //"SELECT e from Employee e join cast(e.project, LargeProject) p where p.budget = 1000
        CriteriaBuilder qb1 = em.getCriteriaBuilder();
        CriteriaQuery<BeerConsumer> cq1 = qb1.createQuery(BeerConsumer.class);

        Root<BeerConsumer> root = cq1.from(BeerConsumer.class);
        Join<BeerConsumer, Blue> join = root.join("blueBeersToConsume");
        Path exp = ((Path)join.as(BlueLight.class)).get("discount");
        cq1.where(qb1.equal(exp, new Integer(10)) );
        try {
            List result = em.createQuery(cq1).getResultList();
            Assert.assertTrue("LargeProject cast failed.", comparer.compareObjects(result, expectedResult));
        } finally {
            beginTransaction(em);
            blueLight = em.find(BlueLight.class, blueLight.getId());
            blueLight.getBeerConsumer().getBlueBeersToConsume().remove(blueLight);
            em.remove(blueLight.getBeerConsumer());
            blueLight.setBeerConsumer(null);
            
            blue = em.find(Blue.class, blue.getId());
            blue.getBeerConsumer().getBlueBeersToConsume().remove(blue);
            em.remove(blue.getBeerConsumer());
            blue.setBeerConsumer(null);
            
            em.remove(blueLight);
            em.remove(blue);

            commitTransaction(em);
            closeEntityManager(em);
        }
        
    }
    
    public void oneToOneCastTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Person rudy = new Person();
        rudy.setName("Rudy");
        em.persist(rudy);
        SportsCar sportsCar = new SportsCar();
        sportsCar.setMaxSpeed(200);
        em.persist(sportsCar);
        rudy.setCar(sportsCar);
        
        Person theo = new Person();
        theo.setName("Theo");
        em.persist(theo);
        Car car = new Car();
        em.persist(car);
        theo.setCar(car);

        commitTransaction(em);
        clearCache();
        
        ReadAllQuery query = new ReadAllQuery();
        Expression selectionCriteria = new ExpressionBuilder().get("car").as(SportsCar.class).get("maxSpeed").equal(200);
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(Person.class);
        query.dontUseDistinct();
        
        Vector expectedResult = (Vector)getServerSession().executeQuery(query);
        
        clearCache();
        beginTransaction(em);
        //"SELECT e from Employee e join cast(e.project, LargeProject) p where p.budget = 1000
        CriteriaBuilder qb1 = em.getCriteriaBuilder();
        CriteriaQuery<Person> cq1 = qb1.createQuery(Person.class);

        Root<Person> root = cq1.from(Person.class);
        Join<Person, Car> join = root.join("car");
        Path exp = ((Path)join.as(SportsCar.class)).get("maxSpeed");
        cq1.where(qb1.equal(exp, new Integer(200)) );
        try {
            List result = em.createQuery(cq1).getResultList();
            Assert.assertTrue("OneToOne cast failed.", comparer.compareObjects(result, expectedResult));
        } finally {
            rudy = em.find(Person.class, rudy.getId());
            rudy.setCar(null);
            
            theo = em.find(Person.class, theo.getId());
            theo.setCar(null);
            
            em.remove(rudy);
            em.remove(theo);
            commitTransaction(em);
            closeEntityManager(em);
        }
    }
}



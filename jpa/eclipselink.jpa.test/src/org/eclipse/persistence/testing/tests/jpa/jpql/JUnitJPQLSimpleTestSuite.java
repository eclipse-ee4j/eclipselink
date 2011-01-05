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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee.EmployeeStatus;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * <p>
 * <b>Purpose</b>: Test simple EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for simple EJBQL functionality
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 */
public class JUnitJPQLSimpleTestSuite extends JUnitTestCase {

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public JUnitJPQLSimpleTestSuite() {
        super();
    }

    public JUnitJPQLSimpleTestSuite(String name) {
        super(name);
    }

    //This method is run at the end of EVERY test case method

    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLSimpleTestSuite");
        suite.addTest(new JUnitJPQLSimpleTestSuite("testSetup"));

        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleSingleArgSubstringTest"));
        
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleJoinFetchTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleJoinFetchTest2"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("baseTestCase"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleABSTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleBetweenTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleConcatTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleConcatTestWithParameters"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleConcatTestWithConstants1"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleThreeArgConcatTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleDistinctTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleDistinctNullTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleDistinctMultipleResultTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleDoubleOrTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleEqualsBracketsTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleEqualsTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleEqualsTestWithJoin"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleEqualsWithAs"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("collectionMemberIdentifierEqualsTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("abstractSchemaIdentifierEqualsTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("abstractSchemaIdentifierNotEqualsTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleInOneDotTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleInTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleInListTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleInNegativeTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleLengthTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleLikeTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleLikeTestWithParameter"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleLikeEscapeTestWithParameter"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleNotBetweenTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleNotEqualsVariablesInteger"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleNotInTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleNotLikeTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleOrFollowedByAndTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleOrFollowedByAndTestWithStaticNames"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleOrTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleParameterTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleParameterTestChangingParameters"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleReverseAbsTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleReverseConcatTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleReverseEqualsTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleReverseLengthTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleReverseParameterTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleReverseSqrtTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleReverseSubstringTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleSqrtTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleSubstringTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleNullTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleNotNullTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("distinctTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("conformResultsInUnitOfWorkTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleModTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleIsEmptyTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleIsNotEmptyTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleApostrohpeTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleEscapeUnderscoreTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleEnumTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("smallProjectMemberOfProjectsTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("smallProjectNOTMemberOfProjectsTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("selectCountOneToOneTest")); //bug 4616218
        suite.addTest(new JUnitJPQLSimpleTestSuite("selectOneToOneTest")); //employee.address doesnt not work
        suite.addTest(new JUnitJPQLSimpleTestSuite("selectPhonenumberDeclaredInINClauseTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("selectPhoneUsingALLTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("selectSimpleMemberOfWithParameterTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("selectSimpleNotMemberOfWithParameterTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("selectSimpleBetweenWithParameterTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("selectSimpleInWithParameterTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("selectAverageQueryForByteColumnTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("selectUsingLockModeQueryHintTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("multipleExecutionOfNamedQueryTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("testOneEqualsOne"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("testParameterEqualsParameter"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleTypeTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleAsOrderByTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleLiteralDateTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("simpleSingleArgSubstringTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("elementCollectionIsNotEmptyTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("relationshipElementCollectionIsNotEmptyTest"));
        suite.addTest(new JUnitJPQLSimpleTestSuite("enumWithToStringTest"));

        
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
    
    /**
     * Tests 1=1 returns correct result.
     */
    public void testParameterEqualsParameter() throws Exception {
        DatabasePlatform databasePlatform = JUnitTestCase.getServerSession().getPlatform(); 
        
        if (databasePlatform.isSymfoware()) {
            getServerSession().logMessage("Test testParameterEqualsParameter skipped for this platform, " 
                    + "Symfoware doesn't allow dynamic parameters on both sides of the equals operator at the same time. (bug 304897)");
            return;
        }

        if (databasePlatform.isMaxDB()) {
            getServerSession().logMessage("Test testParameterEqualsParameter skipped for this platform, " 
                    + "MaxDB doesn't allow dynamic parameters on both sides of the equals operator at the same time. (bug 326962)");
            return;
        }
        
    	EntityManager em = createEntityManager();
    	beginTransaction(em);
    	try {
            Query query = em.createQuery("SELECT e FROM Employee e");
            List<Employee> emps = query.getResultList();
    
            Assert.assertNotNull(emps);
            int numRead = emps.size();
	        
	        query = em.createQuery("SELECT e FROM Employee e WHERE :arg1=:arg2");
	        query.setParameter("arg1", 1);
	        query.setParameter("arg2", 1);
	        emps = query.getResultList();
	
	        Assert.assertNotNull(emps);
	        Assert.assertEquals(numRead, emps.size());
    	} finally {
        	rollbackTransaction(em);
            closeEntityManager(em);
    	}    
    }
    
    /**
     * Tests 1=1 returns correct result.
     */
    public void testOneEqualsOne() throws Exception {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Query query = em.createQuery("SELECT e FROM Employee e");
            List<Employee> emps = query.getResultList();
    
            Assert.assertNotNull(emps);
            int numRead = emps.size();
    
            query = em.createQuery("SELECT e FROM Employee e WHERE 1=1");
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
        String ejbqlString = "SELECT e FROM Employee e LEFT JOIN FETCH e.phoneNumbers";

        //use the cache
        List result = em.createQuery(ejbqlString).getResultList();

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

        result = em.createQuery(ejbqlString).getResultList();

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

        List expectedResult = getServerSession().readAllObjects(Employee.class);

        clearCache();

        List result = em.createQuery("SELECT OBJECT(emp) FROM Employee emp").getResultList();

        Assert.assertTrue("Base Test Case Failed", comparer.compareObjects(result, expectedResult));
    }

    //Test case for ABS function in EJBQL

    public void simpleABSTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "ABS(emp.salary) = ";
        ejbqlString = ejbqlString + expectedResult.getSalary();

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("ABS test failed", comparer.compareObjects(result, expectedResult));
    }

    //Test case for BETWEEN function in EJBQL

    public void simpleBetweenTest() {
        BigDecimal empId = new BigDecimal(0);

        EntityManager em = createEntityManager();

        Employee employee = (Employee)(getServerSession().readAllObjects(Employee.class).lastElement());

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").between(empId, employee.getId());
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id BETWEEN " + empId + " AND " + employee.getId();
        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Between test failed", comparer.compareObjects(result, expectedResult));
    }

    //Test case for concat function in EJBQL

    public void simpleConcatTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        String partOne, partTwo;
        String ejbqlString;

        partOne = expectedResult.getFirstName().substring(0, 2);
        partTwo = expectedResult.getFirstName().substring(2);

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.firstName = ";
        ejbqlString = ejbqlString + "CONCAT(\"";
        ejbqlString = ejbqlString + partOne;
        ejbqlString = ejbqlString + "\", \"";
        ejbqlString = ejbqlString + partTwo;
        ejbqlString = ejbqlString + "\")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Concat test failed", comparer.compareObjects(result, expectedResult));
    }

    //Test case for concat function in EJBQL taking parameters

    public void simpleConcatTestWithParameters() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test simpleConcatTestWithParameters skipped for this platform, "
                    + "Symfoware doesn't allow dynamic parameters in both arguments to CONCAT at the same time. (bug 304897)");
            return;
        }
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        String partOne, partTwo;
        String ejbqlString;

        partOne = expectedResult.getFirstName().substring(0, 2);
        partTwo = expectedResult.getFirstName().substring(2);

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.firstName = ";
        ejbqlString = ejbqlString + "CONCAT(";
        ejbqlString = ejbqlString + ":partOne";
        ejbqlString = ejbqlString + ", ";
        ejbqlString = ejbqlString + ":partTwo";
        ejbqlString = ejbqlString + ")";

        List result = em.createQuery(ejbqlString).setParameter("partOne", partOne).setParameter("partTwo", partTwo).getResultList();

        Assert.assertTrue("Concat test failed", comparer.compareObjects(result, expectedResult));
    }


    //Test case for concat function with constants in EJBQL

    public void simpleConcatTestWithConstants1() {
        EntityManager em = createEntityManager();

        Employee emp = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        String partOne;
        String ejbqlString;

        partOne = emp.getFirstName();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").concat("Smith").like(partOne + "Smith");

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "CONCAT(emp.firstName,\"Smith\") LIKE ";
        ejbqlString = ejbqlString + "\"" + partOne + "Smith\"";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Concat test with constraints failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleThreeArgConcatTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        String partOne, partTwo, partThree;
        String ejbqlString;

        partOne = expectedResult.getFirstName().substring(0, 1);
        partTwo = expectedResult.getFirstName().substring(1, 2);
        partThree = expectedResult.getFirstName().substring(2);

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.firstName = ";
        ejbqlString = ejbqlString + "CONCAT(\"";
        ejbqlString = ejbqlString + partOne;
        ejbqlString = ejbqlString + "\", \"";
        ejbqlString = ejbqlString + partTwo;
        ejbqlString = ejbqlString + "\", \"";
        ejbqlString = ejbqlString + partThree;
        ejbqlString = ejbqlString + "\")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Concat test failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleDistinctTest() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT DISTINCT e FROM Employee e JOIN FETCH e.phoneNumbers ";
        List result = em.createQuery(ejbqlString).getResultList();
        Set testSet = new HashSet();
        for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
            Employee emp = (Employee)iterator.next();
            assertFalse("Result was not distinct", testSet.contains(emp));
            testSet.add(emp);
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
            String ejbqlString = "SELECT DISTINCT e.firstName FROM Employee e WHERE e.lastName = '" + emp.getLastName() + "'";
            List result = em.createQuery(ejbqlString).getResultList();
            assertTrue("Failed to return null value", result.contains(null));
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
        String ejbqlString = "SELECT DISTINCT e, e.firstName FROM Employee e JOIN FETCH e.phoneNumbers ";
        List result = em.createQuery(ejbqlString).getResultList();
        Set testSet = new HashSet();
        for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
            String ids = "";
            Object[] row = (Object[])iterator.next();
            Employee emp = (Employee)row[0];
            String string = (String)row[1];
            ids = "_" + emp.getId() + "_" + string;
            assertFalse("Result was not distinct", testSet.contains(ids));
            testSet.add(ids);
        }
    }

    //Test case for double OR function in EJBQL

    public void simpleDoubleOrTest() {
        Employee emp1, emp2, emp3;

        EntityManager em = createEntityManager();

        emp1 = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());
        emp2 = (Employee)(getServerSession().readAllObjects(Employee.class).elementAt(1));
        emp3 = (Employee)(getServerSession().readAllObjects(Employee.class).elementAt(2));

        clearCache();

        Vector expectedResult = new Vector();
        expectedResult.add(emp1);
        expectedResult.add(emp2);
        expectedResult.add(emp3);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = " + emp1.getId() + " OR emp.id = " + emp2.getId() + " OR emp.id = " + emp3.getId();

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Double OR test failed", comparer.compareObjects(result, expectedResult));
    }

    //Test case for equals brackets in EJBQL

    public void simpleEqualsBracketsTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "( emp.firstName = ";
        ejbqlString = ejbqlString + "\"" + expectedResult.getFirstName() + "\")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Equals brackets test failed", comparer.compareObjects(result, expectedResult));
    }

    //Test case for equals in EJBQL

    public void simpleEqualsTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.firstName = ";
        ejbqlString = ejbqlString + "\"" + expectedResult.getFirstName() + "\"";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Equals test failed", comparer.compareObjects(expectedResult, result));
    }

    //Test case for equals with join in EJBQL

    public void simpleEqualsTestWithJoin() {
        EntityManager em = createEntityManager();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.anyOf("managedEmployees").get("address").get("city").equal("Ottawa");

        Vector expectedResult = getServerSession().readAllObjects(Employee.class, whereClause);

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp, IN(emp.managedEmployees) managedEmployees " + "WHERE managedEmployees.address.city = 'Ottawa'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Equals test with Join failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleEqualsWithAs() {
        EntityManager em = createEntityManager();
        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        Vector employeesUsed = new Vector();
        employeesUsed.add(expectedResult);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee AS emp WHERE emp.id = " + expectedResult.getId();

        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("Equals test with As failed", comparer.compareObjects(expectedResult, result));
    }

    public void collectionMemberIdentifierEqualsTest() {
        EntityManager em = createEntityManager();

        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").equal("Bob");
        exp = exp.and(employees.get("lastName").equal("Smith"));
        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class, exp).firstElement();

        clearCache();

        PhoneNumber phoneNumber = (PhoneNumber)((Vector)expectedResult.getPhoneNumbers()).firstElement();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp, IN (emp.phoneNumbers) phone " + "WHERE phone = ?1";

        List result = em.createQuery(ejbqlString).setParameter(1, phoneNumber).getResultList();

        Assert.assertTrue("CollectionMemberIdentifierEqualsTest failed", comparer.compareObjects(expectedResult, result));
    }

    public void abstractSchemaIdentifierEqualsTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp = ?1";

        List result = em.createQuery(ejbqlString).setParameter(1, expectedResult).getResultList();

        Assert.assertTrue("abstractSchemaIdentifierEqualsTest failed", comparer.compareObjects(expectedResult, result));
    }

    public void abstractSchemaIdentifierNotEqualsTest() {
        EntityManager em = createEntityManager();

        Vector expectedResult = getServerSession().readAllObjects(Employee.class);

        clearCache();

        Employee emp = (Employee)expectedResult.firstElement();

        expectedResult.removeElementAt(0);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp <> ?1";

        List result = em.createQuery(ejbqlString).setParameter(1, emp).getResultList();

        Assert.assertTrue("abstractSchemaIdentifierNotEqualsTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleInOneDotTest() {
        //select a specifif employee using Expr Bob Smithn
        EntityManager em = createEntityManager();

        ReadObjectQuery roq = new ReadObjectQuery(Employee.class);

        ExpressionBuilder empBldr = new ExpressionBuilder();

        Expression exp1 = empBldr.get("firstName").equal("Bob");
        Expression exp2 = empBldr.get("lastName").equal("Smith");

        roq.setSelectionCriteria(exp1.and(exp2));

        Employee expectedResult = (Employee)getServerSession().executeQuery(roq);

        clearCache();

        PhoneNumber empPhoneNumbers = (PhoneNumber)((Vector)expectedResult.getPhoneNumbers()).firstElement();

        String ejbqlString = "SelecT OBJECT(emp) from Employee emp, in (emp.phoneNumbers) phone " + "Where phone.areaCode = \"" + empPhoneNumbers.getAreaCode() + "\"" + "AND emp.firstName = \"" + expectedResult.getFirstName() + "\"";
        ejbqlString = ejbqlString + "AND emp.lastName = \"" + expectedResult.getLastName() + "\"";

        Employee result = (Employee)em.createQuery(ejbqlString).getSingleResult();

        Assert.assertTrue("Simple In Dot Test failed", comparer.compareObjects(result, expectedResult));
    }

    public void selectAverageQueryForByteColumnTest() {
        EntityManager em = createEntityManager();

        String ejbqlString = "Select AVG(emp.salary)from Employee emp";
        Object result = em.createQuery(ejbqlString).getSingleResult();

        Assert.assertTrue("AVG result type [" + result.getClass() + "] not of type Double", result.getClass() == Double.class);
    }

    public void simpleInTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN (" + expectedResult.getId().toString() + ")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple In Test failed", comparer.compareObjects(result, expectedResult));
    }
    
    public void simpleInListTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        List expectedResultList = new ArrayList();
        expectedResultList.add(expectedResult.getId());
        
        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN :result";

        List result = em.createQuery(ejbqlString).setParameter("result", expectedResultList).getResultList();

        Assert.assertTrue("Simple In Test failed", comparer.compareObjects(result, expectedResult));
    }

    /**
     * Test for Bug 259974 - JPQL IN function doesn't work with negative literal values
     * This test executes a JPQL statement with an IN function with a negative literal value
     * and ensures it successfully returns the correct value.
     */
    public void simpleInNegativeTest() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee expectedResult = new Employee();
            expectedResult.setSalary(-12345);
            em.persist(expectedResult);
            em.flush();
            
            clearCache();
    
            String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary IN(-12345)";
    
            Employee result = (Employee)em.createQuery(ejbqlString).getSingleResult();
    
            Assert.assertTrue("Simple In Negative Test failed", comparer.compareObjects(result, expectedResult));
        } finally{
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    public void simpleLengthTest() {
        EntityManager em = createEntityManager();

        Assert.assertFalse("Warning SQL doesnot support LENGTH function", (JUnitTestCase.getServerSession()).getPlatform().isSQLServer());

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String ejbqlString;
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "LENGTH ( emp.firstName     ) = ";
        ejbqlString = ejbqlString + expectedResult.getFirstName().length();

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Length Test failed", comparer.compareObjects(result, expectedResult));
    }


    public void simpleLikeTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String partialFirstName = expectedResult.getFirstName().substring(0, 3) + "%";
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE \"" + partialFirstName + "\"";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Like Test failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleLikeTestWithParameter() {
        EntityManager em = createEntityManager();

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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE ?1";

        List result = em.createQuery(ejbqlString).setParameter(1, partialFirstName).getResultList();

        Assert.assertTrue("Simple Like Test with Parameter failed", comparer.compareObjects(result, expectedResult));
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
        String ejbqlString = "SELECT OBJECT(address) FROM Address address WHERE address.street LIKE :pattern ESCAPE :esc";
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

        List result = em.createQuery(ejbqlString).setParameter("pattern", patternString).setParameter("esc", escChar).getResultList();

        Assert.assertTrue("Simple Escape Underscore test failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleNotBetweenTest() {
        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).lastElement();

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);

        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("id").between(emp1.getId(), emp2.getId()).not();

        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.id NOT BETWEEN ";
        ejbqlString = ejbqlString + emp1.getId().toString();
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + emp2.getId().toString();

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Not Between Test failed", comparer.compareObjects(result, expectedResult));

    }

    public void simpleNotEqualsVariablesInteger() {
        EntityManager em = createEntityManager();

        Vector expectedResult = getServerSession().readAllObjects(Employee.class);

        clearCache();

        Employee emp = (Employee)expectedResult.elementAt(0);

        expectedResult.removeElementAt(0);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id <> " + emp.getId();

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Like Test with Parameter failed", comparer.compareObjects(result, expectedResult));

    }

    public void simpleNotInTest() {
        EntityManager em = createEntityManager();

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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id NOT IN (" + emp.getId().toString() + ")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Not In Test failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleNotLikeTest() {
        EntityManager em = createEntityManager();

        Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        String partialFirstName = emp.getFirstName().substring(0, 3) + "%";

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").notLike(partialFirstName);

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName NOT LIKE \"" + partialFirstName + "\"";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Not Like Test failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleOrFollowedByAndTest() {
        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);
        Employee emp3 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(2);

        Vector expectedResult = new Vector();
        expectedResult.add(emp1);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = " + emp1.getId() + " OR emp.id = " + emp2.getId() + " AND emp.id = " + emp3.getId();
        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Or followed by And Test failed", comparer.compareObjects(result, expectedResult));

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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = \"John\" OR emp.firstName = \"Bob\" AND emp.lastName = \"Smith\"";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Or followed by And With Static Names Test failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleOrTest() {
        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);

        Vector expectedResult = new Vector();
        expectedResult.add(emp1);
        expectedResult.add(emp2);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = " + emp1.getId() + "OR emp.id = " + emp2.getId();

        List result = em.createQuery(ejbqlString).getResultList();
        clearCache();

        Assert.assertTrue("Simple Or Test failed", comparer.compareObjects(result, expectedResult));
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

        Vector parameters = new Vector();
        parameters.add(expectedResult.getFirstName());

        List employees = (List)getServerSession().executeQuery(raq, parameters);
        employees.size();

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE " + "emp.firstName = ?1 ";

        List result = em.createQuery(ejbqlString).setParameter(1, parameters.get(0)).getResultList();


        Assert.assertTrue("Simple Parameter Test failed", comparer.compareObjects(result, expectedResult));
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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE " + "emp.firstName = ?1 ";

        List firstResultSet = em.createQuery(ejbqlString).setParameter(1, firstParameters.get(0)).getResultList();
        clearCache();
        List secondResultSet = em.createQuery(ejbqlString).setParameter(1, secondParameters.get(0)).getResultList();
        clearCache();
        Vector result = new Vector();
        result.addAll(firstResultSet);
        result.addAll(secondResultSet);

        Assert.assertTrue("Simple Parameter Test Changing Parameters failed", comparer.compareObjects(result, expectedResult));

    }

    public void simpleReverseAbsTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);
        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE " + expectedResult.getSalary() + " = ABS(emp.salary)";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Reverse Abs test failed", comparer.compareObjects(result, expectedResult));

    }

    public void simpleReverseConcatTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String partOne = expectedResult.getFirstName().substring(0, 2);
        String partTwo = expectedResult.getFirstName().substring(2);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "CONCAT(\"";
        ejbqlString = ejbqlString + partOne;
        ejbqlString = ejbqlString + "\", \"";
        ejbqlString = ejbqlString + partTwo;
        ejbqlString = ejbqlString + "\")";
        ejbqlString = ejbqlString + " = emp.firstName";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Reverse Concat test failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleReverseEqualsTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "\"" + expectedResult.getFirstName() + "\"";
        ejbqlString = ejbqlString + " = emp.firstName";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Reverse Equals test failed", comparer.compareObjects(result, expectedResult));
    }

    public void simpleReverseLengthTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + expectedResult.getFirstName().length();
        ejbqlString = ejbqlString + " = LENGTH(emp.firstName)";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Reverse Length test failed", comparer.compareObjects(result, expectedResult));

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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "?1 = emp.firstName ";

        List result = em.createQuery(ejbqlString).setParameter(1, parameters.get(0)).getResultList();

        Assert.assertTrue("Simple Reverse Parameter test failed", comparer.compareObjects(result, expectedResult));
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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + salarySquareRoot;
        ejbqlString = ejbqlString + " = SQRT(emp.salary)";

        List result = em.createQuery(ejbqlString).getResultList();


        Assert.assertTrue("Simple Reverse Square Root test failed", comparer.compareObjects(result, expectedResult));

    }

    public void simpleReverseSubstringTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String firstNamePart;
        String ejbqlString;

        firstNamePart = expectedResult.getFirstName().substring(0, 2);
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "\"" + firstNamePart + "\"";
        ejbqlString = ejbqlString + " = SUBSTRING(emp.firstName, 1, 2)";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Reverse SubString test failed", comparer.compareObjects(result, expectedResult));

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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "SQRT(emp.salary) = ";
        ejbqlString = ejbqlString + salarySquareRoot;

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Square Root test failed", comparer.compareObjects(result, expectedResult));

    }

    public void simpleSubstringTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String firstNamePart = expectedResult.getFirstName().substring(0, 2);
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "SUBSTRING(emp.firstName, 1, 2) = \""; //changed from 0, 2 to 1, 2(ZYP)
        ejbqlString = ejbqlString + firstNamePart + "\"";

        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("Simple SubString test failed", comparer.compareObjects(result, expectedResult));
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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName IS NULL";

        List result = em.createQuery(ejbqlString).getResultList();

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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName IS NOT NULL";
        List result = em.createQuery(ejbqlString).getResultList();

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

        String ejbqlString = "SELECT DISTINCT OBJECT(emp) FROM Employee emp WHERE emp.lastName = \'Smith\'";
        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Distinct test failed", comparer.compareObjects(result, expectedResult));
    }

    public void multipleExecutionOfNamedQueryTest() {
        //bug 5279859
        EntityManager em = createEntityManager();
        Query query = em.createNamedQuery("findEmployeeByPostalCode");
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
        query = em.createNamedQuery("findEmployeeByPostalCode");
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

    }

    public void conformResultsInUnitOfWorkTest() {
        ReadObjectQuery readObjectQuery = new ReadObjectQuery();

        readObjectQuery.setReferenceClass(Employee.class);
        readObjectQuery.setEJBQLString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = ?1");
        readObjectQuery.conformResultsInUnitOfWork();
        readObjectQuery.addArgument("1", Integer.class);


        //ServerSession next
        Server serverSession = getServerSession().getProject().createServerSession();
        serverSession.setSessionLog(getServerSession().getSessionLog());
        serverSession.login();
        UnitOfWork unitOfWork = serverSession.acquireUnitOfWork();
        Employee newEmployee = new Employee();
        newEmployee.setId(new Integer(9000));
        unitOfWork.registerObject(newEmployee);

        Vector testV = new Vector();
        testV.addElement(new Integer(9000));

        Employee result = (Employee)unitOfWork.executeQuery(readObjectQuery, testV);

        Assert.assertTrue("Conform Results In Unit of Work using ServerSession failed", comparer.compareObjects(result, newEmployee));

        serverSession.logout();
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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE MOD(emp.salary, 2) > 0";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Mod test failed", comparer.compareObjects(result, expectedResult));

        // Test MOD(fieldAccess, fieldAccess) glassfish issue 2771

        expectedResult = getServerSession().readAllObjects(Employee.class);
        clearCache();
        
        ejbqlString = "SELECT emp FROM Employee emp WHERE MOD(emp.salary, emp.salary) = 0";        
        result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Mod test(2) failed", comparer.compareObjects(result, expectedResult));  
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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.phoneNumbers IS EMPTY";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Is empty test failed", comparer.compareObjects(result, expectedResult));
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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.phoneNumbers IS NOT EMPTY";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple is not empty test failed", comparer.compareObjects(result, expectedResult));

    }

    public void simpleApostrohpeTest() {
        EntityManager em = createEntityManager();

        Vector addresses = getServerSession().readAllObjects(Address.class);

        clearCache();

        Address expectedResult = new Address();

        Iterator addressesIterator = addresses.iterator();
        while (addressesIterator.hasNext()) {
            expectedResult = (Address)addressesIterator.next();
            if (expectedResult.getStreet().indexOf("Lost") != -1) {
                break;
            }
        }

        String ejbqlString = "SELECT OBJECT(address) FROM Address address WHERE ";
        ejbqlString = ejbqlString + "address.street = '234 I''m Lost Lane'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple apostrophe test failed", comparer.compareObjects(result, expectedResult));

    }

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

        //test the apostrophe
        String ejbqlString = "SELECT OBJECT(address) FROM Address address WHERE ";
        // \ is always treated as escape in MySQL.  Therefore ESCAPE '\' is considered a syntax error
        if (getServerSession().getPlatform().isMySQL() || getServerSession().getPlatform().isPostgreSQL()) {
            ejbqlString = ejbqlString + "address.street LIKE '234 Wandering $_Way' ESCAPE '$'";
        } else {
            ejbqlString = ejbqlString + "address.street LIKE '234 Wandering \\_Way' ESCAPE '\\'";
        }

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Escape Underscore test failed", comparer.compareObjects(result, expectedResult));
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

        //setup the EJBQL to do the same
        String ejbqlString = "SELECT OBJECT(employee) FROM Employee employee, SmallProject sp WHERE ";
        ejbqlString = ejbqlString + "sp MEMBER OF employee.projects";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple small Project Member Of Projects test failed", comparer.compareObjects(result, expectedResult));

    }

    public void smallProjectNOTMemberOfProjectsTest() {
        EntityManager em = createEntityManager();

        //query for those employees with Project named "Enterprise" (which should be
        //a SmallProject)
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


        //setup the EJBQL to do the same
        String ejbqlString = "SELECT OBJECT(employee) FROM Employee employee WHERE ";
        ejbqlString = ejbqlString + "?1 NOT MEMBER OF employee.projects";

        List result = em.createQuery(ejbqlString).setParameter(1, smallProject).getResultList();

        Assert.assertTrue("Simple small Project NOT Member Of Projects test failed", comparer.compareObjects(result, expectedResult));

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

        //setup the EJBQL to do the same
        String ejbqlString = "SELECT COUNT(DISTINCT phone.owner) FROM PhoneNumber phone";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Select Count One To One test failed", expectedResult.elementAt(0).equals(result.get(0)));

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

        //setup the EJBQL to do the same
        String ejbqlString = "SELECT DISTINCT employee.address FROM Employee employee WHERE employee.lastName LIKE '%Way%'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple Select One To One test failed", comparer.compareObjects(result, expectedResult));

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

        // Setup the EJBQL to do the same.
        String ejbqlString = "Select Distinct Object(p) from Employee emp, IN(emp.phoneNumbers) p WHERE ";
        ejbqlString = ejbqlString + "p.number IS NOT NULL ORDER BY p.number, p.areaCode";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Simple select Phonenumber Declared In IN Clause test failed", comparer.compareObjects(result, expectedResult));

    }

    /**
     * Test the ALL function.
     * This test was added to mirror a CTS failure.
     * This currently throws an error but should be a valid (although odd) query.
     * BUG#6025292
    public void badSelectPhoneUsingALLTest()
    {
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();

        ReadAllQuery query = new ReadAllQuery();
        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        Expression phoneAnyOf = employeeBuilder.anyOf("phoneNumbers");
        ExpressionBuilder phoneBuilder = new ExpressionBuilder(PhoneNumber.class);

        ReportQuery subQuery = new ReportQuery();
        subQuery.setReferenceClass(PhoneNumber.class);
        subQuery.addMinimum("number");

        //bad sql - Expression selectionCriteria = employeeBuilder.anyOf("phoneNumbers").equal(employeeBuilder.all(subQuery));
        //bad sql - Expression selectionCriteria = employeeBuilder.anyOf("phoneNumbers").equal(subQuery);
        Expression selectionCriteria = phoneBuilder.equal(employeeBuilder.anyOf("phoneNumbers")).and(
                phoneAnyOf.get("number").equal(employeeBuilder.all(subQuery)));
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(Employee.class);
		
        Vector expectedResult = (Vector)getServerSession().executeQuery(query);
		
        clearCache();

        // Setup the EJBQL to do the same.
        String ejbqlString = "Select Distinct Object(emp) from Employee emp, IN(emp.phoneNumbers) p WHERE ";
        ejbqlString = ejbqlString + "p.number = ALL (Select MIN(pp.number) FROM PhoneNumber pp)";

        List result = em.createQuery(ejbqlString).getResultList();

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

        // Setup the EJBQL to do the same.
        String ejbqlString = "Select Distinct Object(emp) from Employee emp, IN(emp.phoneNumbers) p WHERE ";
        ejbqlString = ejbqlString + "p.number = ALL (Select MIN(pp.number) FROM PhoneNumber pp)";

        Query jpqlQuery = em.createQuery(ejbqlString);
        jpqlQuery.setMaxResults(10);
        List result = jpqlQuery.getResultList();

        Assert.assertTrue("Simple select Phonenumber Declared In IN Clause test failed", comparer.compareObjects(result, expectedResult));

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


        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp " + "WHERE ?1 MEMBER OF emp.phoneNumbers";

        Vector parameters = new Vector();
        parameters.add(phone);

        List result = em.createQuery(ejbqlString).setParameter(1, phone).getResultList();

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


        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp " + "WHERE ?1 NOT MEMBER OF emp.phoneNumbers";

        Vector parameters = new Vector();
        parameters.add(phone);

        List result = em.createQuery(ejbqlString).setParameter(1, phone).getResultList();

        uow = clientSession.acquireUnitOfWork();
        uow.deleteObject(phone);
        uow.commit();

        Assert.assertTrue("Select simple Not member of with parameter test failed", comparer.compareObjects(result, expectedResult));
    }

    public void selectUsingLockModeQueryHintTest() {
        Exception exception = null;
        EntityManager em = createEntityManager();

        Vector employees = getServerSession().readAllObjects(Employee.class);
        Employee emp1 = (Employee)employees.lastElement();
        Employee emp2 = new Employee();

        try {
            javax.persistence.Query query = em.createNamedQuery("findEmployeeByPK");
            query.setParameter("id", emp1.getId());
            query.setHint("lockMode", new Short((short)1));

            emp2 = (Employee)query.getSingleResult();
        } catch (Exception e) {
            exception = e;
        }

        Assert.assertNull("An exception was caught: " + exception, exception);
        Assert.assertTrue("The query did not return the same employee.", emp1.getId() == emp2.getId());
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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.id BETWEEN ?1 AND ?2";

        List result = em.createQuery(ejbqlString).setParameter(1, empId1).setParameter(2, emp2.getId()).getResultList();

        Assert.assertTrue("Simple select between with parameter test failed", comparer.compareObjects(result, expectedResult));
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

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.id IN (?1, ?2)";

        List result = em.createQuery(ejbqlString).setParameter(1, empId1).setParameter(2, emp2.getId()).getResultList();

        Assert.assertTrue("Simple select between with parameter test failed", comparer.compareObjects(result, expectedResult));
    }

    //Test case for ABS function in EJBQL

    public void simpleEnumTest() {
        EntityManager em = createEntityManager();

        String ejbqlString;

        ejbqlString = "SELECT emp FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.status =  org.eclipse.persistence.testing.models.jpa.advanced.Employee.EmployeeStatus.FULL_TIME";
        List result = em.createQuery(ejbqlString).getResultList();
        result.size();
    }

    public void simpleTypeTest(){
        EntityManager em = createEntityManager();

        List expectedResult = getServerSession().readAllObjects(LargeProject.class);
        
        clearCache();

        String ejbqlString = "SELECT OBJECT(proj) FROM Project proj WHERE TYPE(proj) = LargeProject";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("SimpleTypeTest", comparer.compareObjects(result, expectedResult));

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

        String ejbqlString = "SELECT e.firstName as firstName FROM Employee e ORDER BY firstName";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("SimpleTypeTest", comparer.compareObjects(result, expectedResult));
    }
    
    public void simpleLiteralDateTest(){
        EntityManager em = createEntityManager();

        Date date = Date.valueOf("1901-01-01");
        Expression exp = (new ExpressionBuilder()).get("period").get("startDate").greaterThan(date);
        List expectedResult = getServerSession().readAllObjects(Employee.class, exp);
        
        clearCache();

        String ejbqlString = "SELECT e FROM Employee e where e.period.startDate > {d '1901-01-01'}";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("simpleLiteralDateTest", comparer.compareObjects(result, expectedResult));
    }
    
    public void simpleSingleArgSubstringTest(){
        EntityManager em = createEntityManager();

        Expression exp = (new ExpressionBuilder()).get("firstName").equal("Bob");
        List expectedResult = getServerSession().readAllObjects(Employee.class, exp);
        expectedResult.size();
        
        clearCache();

        String ejbqlString = "SELECT e FROM Employee e where substring(e.firstName, 2) = 'ob'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("simpleSingleArgSubstringTest", comparer.compareObjects(result, expectedResult));
    }
    
    // bug 318195
    public void elementCollectionIsNotEmptyTest(){
        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.responsibilities IS NOT EMPTY");
        List results  = query.getResultList(); 
        Iterator i = results.iterator();
        while (i.hasNext()){
            Employee emp = (Employee)i.next();
            assertTrue(emp.getResponsibilities() != null && !emp.getResponsibilities().isEmpty());
        }
    }
    
    // bug 318195
    public void relationshipElementCollectionIsNotEmptyTest(){
        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT distinct o FROM PhoneNumber p join p.owner o WHERE o.responsibilities IS NOT EMPTY");
        List results  = query.getResultList();
        Iterator i = results.iterator();
        while (i.hasNext()){
            Employee emp = (Employee)i.next();
            assertTrue(emp.getResponsibilities() != null && !emp.getResponsibilities().isEmpty());
        }
    }
    
    // Bug 332207
    public void enumWithToStringTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("Nick");
        emp.setPayScale(Employee.SalaryRate.SENIOR);
        em.persist(emp);
        em.flush();
        em.clear();
        clearCache();
        Query query = em.createQuery("SELECT e from Employee e where e.payScale = org.eclipse.persistence.testing.models.jpa.advanced.Employee.SalaryRate.SENIOR");
        emp = (Employee)query.getSingleResult();
        assertTrue("Enumeration not properly returned", emp.getPayScale() == Employee.SalaryRate.SENIOR);
        rollbackTransaction(em);
    }
}


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
 *     07/05/2010-2.1.1 Michael O'Brien 
 *       - 321716: modelgen and jpa versions of duplicate code in both copies of
 *       JUnitCriteriaSimpleTestSuite must be kept in sync (to avoid only failing on WebSphere under Derby)
 *       (ideally there should be only one copy of the code - the other suite should reference or subclass for changes)
 *       see
 *       org.eclipse.persistence.testing.tests.jpa.criteria.JUnitCriteriaSimpleTestSuite.simpleModTest():1796
 *       org.eclipse.persistence.testing.tests.jpa.criteria.metamodel.JUnitCriteriaSimpleTestSuite.simpleModTest():1766
 *       - 321902: this copied code should be renamed, merged or subclassed off the original
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.criteria.metamodel;

import java.util.Set;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Address_;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee_;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber_;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * @author cdelahun
 * Converted from JUnitJPQLSimpleTestSuite
 */
@SuppressWarnings("unchecked")
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
        suite.setName("JUnitJPQLSimpleTestSuite");
        suite.addTest(new JUnitCriteriaSimpleTestSuite("testSetup"));
		suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleModTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleJoinFetchTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleJoinFetchTest2"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("baseTestCase"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleABSTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleBetweenTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleConcatTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleConcatTestWithParameters"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleConcatTestWithConstants1"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleCountTest"));
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
        //suite.addTest(new JUnitCriteriaSimpleTestSuite("testOneEqualsOne"));//Doesn't use canonical model
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleTypeTest"));
        suite.addTest(new JUnitCriteriaSimpleTestSuite("simpleAsOrderByTest"));
        
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
        em.createQuery(ejbqlString).getResultList();
        // run the simpleJoinFetchTest and verify all employees have phonenumbers fetched.
        simpleJoinFetchTest(em);
    }

    public void simpleJoinFetchTest(org.eclipse.persistence.jpa.JpaEntityManager em) throws Exception {
        //"SELECT e FROM Employee e LEFT JOIN FETCH e.phoneNumbers"

        em.createQuery("select e from Employee e left join fetch e.phoneNumbers").getResultList();
        //use the cache
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        root.fetch(Employee_.phoneNumbers, JoinType.LEFT);
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
            Assert.fail("simpleJoinFetchTest Failed when using cache, collections do not match: " + result + " expected: " + expectedResult);
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
            Assert.fail("simpleJoinFetchTest Failed when not using cache, collections do not match: " + result + " expected: " + expectedResult);
        }
    }

    //Test case for selecting ALL employees from the database

    public void baseTestCase() {
        EntityManager em = createEntityManager();

        List expectedResult = getServerSession().readAllObjects(Employee.class);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp"
        beginTransaction(em);
        try {
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

        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE ABS(emp.salary) = " + expectedResult.getSalary();
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        getEntityManagerFactory().getMetamodel().getEntities();
        getEntityManagerFactory().getMetamodel().managedType(Employee.class).getDeclaredSingularAttribute("manager", Employee.class).getType();
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where(qb.equal( qb.abs(root.get(Employee_.salary)), expectedResult.getSalary()) );

        beginTransaction(em);
        try {
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        //Cast to Expression<Comparable> since empId is BigDec and getId is Integer.  between requires Comparable types; Number is not comparable
        cq.where( qb.between(root.get(Employee_.id).as(Comparable.class), qb.literal(empId), qb.literal(employee.getId()) ) );
        beginTransaction(em);
        try {
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

        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        String partOne, partTwo;

        partOne = expectedResult.getFirstName().substring(0, 2);
        partTwo = expectedResult.getFirstName().substring(2);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = CONCAT(\"" + partOne + "\", \"" + partTwo + "\")"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.equal(root.get(Employee_.firstName), qb.concat(qb.literal(partOne), qb.literal(partTwo))) );
        beginTransaction(em);
        try {
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
        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        String partOne = expectedResult.getFirstName().substring(0, 2);
        String partTwo = expectedResult.getFirstName().substring(2);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = CONCAT( :partOne, :partTwo )"
        beginTransaction(em);
        try {  
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
            cq.where( qb.equal(root.get(Employee_.firstName), qb.concat(qb.parameter(String.class, "partOne"), qb.parameter(String.class, "partTwo"))) );
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.like(qb.concat(root.get(Employee_.firstName), qb.literal("Smith") ), partOne+"Smith") );
        beginTransaction(em);
        try {  
            List result = em.createQuery(cq).getResultList();
            Assert.assertTrue("Concat test with constraints failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
  //Test case for concat function with constants in EJBQL

    public void simpleCountTest() {
        EntityManager em = createEntityManager();

        ReportQuery query = new ReportQuery();
        query.setReferenceClass(PhoneNumber.class);
        //need to specify Long return type
        query.addCount("COUNT", new ExpressionBuilder().get("owner").get("id"), Long.class);
        query.returnSingleAttribute();
        query.dontRetrievePrimaryKeys();
        query.setName("selectPhoneNumbersAssociatedToEmployees");

        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        clearCache();

        //"SELECT COUNT(phone.owner) FROM PhoneNumber phone";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<PhoneNumber> root = cq.from(PhoneNumber.class);
        cq.select(qb.count(root.get(getEntityManagerFactory().getMetamodel().entity(PhoneNumber.class).getSingularAttribute("owner", Employee.class))
                .get(getEntityManagerFactory().getMetamodel().entity(Employee.class).getSingularAttribute("id", Integer.class))));
        //cq.select(qb.count(root.get(PhoneNumber_.owner).get(Employee_.id)));

        beginTransaction(em);
        try { 
            List result = em.createQuery(cq).getResultList();
            System.out.println(" results are :"+result);
        
            qb = em.getCriteriaBuilder();
            cq = qb.createQuery(Long.class);
            root = cq.from(PhoneNumber.class);
            cq.select(qb.count(root.get(PhoneNumber_.owner).get(Employee_.id)));
            result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Count test failed", expectedResult.elementAt(0).equals(result.get(0)));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleThreeArgConcatTest() {
        EntityManager em = createEntityManager();

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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.equal( root.get(Employee_.firstName), qb.concat(qb.literal(partOne), qb.concat( qb.literal(partTwo), qb.literal(partThree)) ) ) );
        beginTransaction(em);
        try { 
            List result = em.createQuery(cq).getResultList();
            Assert.assertTrue("Concat test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleDistinctTest() {
        EntityManager em = createEntityManager();
        //"SELECT DISTINCT e FROM Employee e JOIN FETCH e.phoneNumbers "
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.distinct(true);
        cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).join(Employee_.phoneNumbers);
        beginTransaction(em);
        try { 
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
            Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
            cq.select(root.get(Employee_.firstName));
            cq.where( qb.equal(root.get(Employee_.lastName), qb.literal(emp.getLastName())));
            beginTransaction(em);
            try {
                List result = em.createQuery(cq).getResultList();
            
                assertTrue("Failed to return null value", result.contains(null));
            } finally {
                rollbackTransaction(em);
               // closeEntityManager(em);
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
            } finally {
                closeEntityManager(em);
            }
        }
    }

    public void simpleDistinctMultipleResultTest() {
        EntityManager em = createEntityManager();
        //"SELECT DISTINCT e, e.firstName FROM Employee e JOIN FETCH e.phoneNumbers "
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = qb.createTupleQuery();
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        root.join(Employee_.phoneNumbers);
        cq.distinct(true);
        cq.multiselect(root, root.get(Employee_.firstName));
        beginTransaction(em);
        try {
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        Predicate firstOr = qb.or(qb.equal(root.get(Employee_.id), emp1.getId()), qb.equal(root.get(Employee_.id), emp2.getId()));
        cq.where( qb.or(firstOr, qb.equal(root.get(Employee_.id), emp3.getId())) );
        beginTransaction(em);
        try {
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

        Employee expectedResult = (Employee)(getServerSession().readAllObjects(Employee.class).firstElement());

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = \"" + expectedResult.getFirstName() + "\""
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.equal(root.get(Employee_.firstName), expectedResult.getFirstName() ) );
        beginTransaction(em);
        try {
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

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.anyOf("managedEmployees").get("address").get("city").equal("Ottawa");

        Vector expectedResult = getServerSession().readAllObjects(Employee.class, whereClause);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp, IN(emp.managedEmployees) managedEmployees " + "WHERE managedEmployees.address.city = 'Ottawa'"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Join managedEmp = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).join(Employee_.managedEmployees);
        cq.where( qb.equal(managedEmp.get(Employee_.address).get(Address_.city), "Ottawa" ) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Equals test with Join failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void collectionMemberIdentifierEqualsTest() {
        EntityManager em = createEntityManager();

        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").equal("Bob");
        exp = exp.and(employees.get("lastName").equal("Smith"));
        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class, exp).firstElement();

        clearCache();

        PhoneNumber phoneNumber = (PhoneNumber)((Vector)expectedResult.getPhoneNumbers()).firstElement();

        //"SELECT OBJECT(emp) FROM Employee emp, IN (emp.phoneNumbers) phone " + "WHERE phone = ?1"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Join phones = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).join(Employee_.phoneNumbers);
        cq.where( qb.equal(phones, qb.parameter(PhoneNumber.class, "1") ) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", phoneNumber).getResultList();

            Assert.assertTrue("CollectionMemberIdentifierEqualsTest failed", comparer.compareObjects(expectedResult, result));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void abstractSchemaIdentifierEqualsTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp = ?1"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.equal(root, qb.parameter(Employee.class, "1") ) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", expectedResult).getResultList();

            Assert.assertTrue("abstractSchemaIdentifierEqualsTest failed", comparer.compareObjects(expectedResult, result));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void abstractSchemaIdentifierNotEqualsTest() {
        EntityManager em = createEntityManager();

        Vector expectedResult = getServerSession().readAllObjects(Employee.class);

        clearCache();

        Employee emp = (Employee)expectedResult.firstElement();

        expectedResult.removeElementAt(0);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp <> ?1";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.notEqual(root, qb.parameter(Employee.class, "1") ) );
        beginTransaction(em);
        try {
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        Join phone = root.join(Employee_.phoneNumbers);
        Predicate firstAnd = qb.and( qb.equal(phone.get(PhoneNumber_.areaCode), empPhoneNumbers.getAreaCode()), 
                qb.equal(root.get(Employee_.firstName), expectedResult.getFirstName()));
        cq.where( qb.and(firstAnd, qb.equal(root.get(Employee_.lastName), expectedResult.getLastName())) );
        beginTransaction(em);
        try {
            Employee result = em.createQuery(cq).getSingleResult();

            Assert.assertTrue("Simple In Dot Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void selectAverageQueryForByteColumnTest() {
        EntityManager em = createEntityManager();

        //"Select AVG(emp.salary)from Employee emp"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Double> cq = qb.createQuery(Double.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        //casting types again.  Avg takes a number, so Path<Object> won't compile
        cq.select( qb.avg( root.get(Employee_.salary) ) );
        beginTransaction(em);
        try {
            Object result = em.createQuery(cq).getSingleResult();

            Assert.assertTrue("AVG result type [" + result.getClass() + "] not of type Double", result.getClass() == Double.class);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleInTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN (" + expectedResult.getId().toString() + ")"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.in(cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).get(Employee_.id)).value(expectedResult.getId()) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple In Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    public void simpleInListTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        List expectedResultList = new ArrayList();
        expectedResultList.add(expectedResult.getId());
        
        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN :result"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        //passing a collection to IN might not be supported in criteria api, trying to get around it by hidding the type
        ParameterExpression exp = qb.parameter(List.class, "result");
        cq.where( qb.in(root.get(Employee_.id)).value(exp) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("result", expectedResultList).getResultList();

            Assert.assertTrue("Simple In Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleLengthTest() {
        EntityManager em = createEntityManager();

        Assert.assertFalse("Warning SQL doesnot support LENGTH function", (JUnitTestCase.getServerSession()).getPlatform().isSQLServer());

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        //String ejbqlString;
        //"SELECT OBJECT(emp) FROM Employee emp WHERE LENGTH ( emp.firstName     ) = " + expectedResult.getFirstName().length();
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.equal( qb.length(root.get(Employee_.firstName)) , expectedResult.getFirstName().length()) );
        beginTransaction(em);
        try {        
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Length Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }


    public void simpleLikeTest() {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

        clearCache();

        String partialFirstName = expectedResult.getFirstName().substring(0, 3) + "%";
        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE \"" + partialFirstName + "\""
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.like( root.get(Employee_.firstName), partialFirstName) );
        beginTransaction(em);
        try { 
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Like Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
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

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE ?1"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.like( root.get(Employee_.firstName), qb.parameter(String.class, "1")) );
        beginTransaction(em);
        try { 
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
        Root<Address> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Address.class));
        cq.where( qb.like( root.get(Address_.street), qb.parameter(String.class, "pattern"), qb.parameter(Character.class, "esc")) );
        
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.not(qb.between(root.get(Employee_.id), emp1.getId(), emp2.getId())) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Not Between Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleNotEqualsVariablesInteger() {
        EntityManager em = createEntityManager();

        Vector expectedResult = getServerSession().readAllObjects(Employee.class);

        clearCache();

        Employee emp = (Employee)expectedResult.elementAt(0);

        expectedResult.removeElementAt(0);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id <> " + emp.getId()
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.notEqual(cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).get(Employee_.id), emp.getId()) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Like Test with Parameter failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
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

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id NOT IN (" + emp.getId().toString() + ")"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.not(qb.in(root.get(Employee_.id)).value(emp.getId())) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple Not In Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
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

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName NOT LIKE \"" + partialFirstName + "\""
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.notLike(root.get(Employee_.firstName), partialFirstName ) );
        beginTransaction(em);
        try {
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        Predicate andOpp = qb.and(qb.equal(root.get(Employee_.id), emp2.getId()), qb.equal(root.get(Employee_.id), emp3.getId()));
        cq.where( qb.or( qb.equal(root.get(Employee_.id), emp1.getId()), andOpp ) );
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        javax.persistence.criteria.Expression empFName = root.get(Employee_.firstName);
        Predicate andOpp = qb.and(qb.equal(empFName, "Bob"), qb.equal(root.get(Employee_.lastName), "Smith"));
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        javax.persistence.criteria.Expression empId = root.get(Employee_.id);
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

        Vector parameters = new Vector();
        parameters.add(expectedResult.getFirstName());

        getServerSession().executeQuery(raq, parameters);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE " + "emp.firstName = ?1 "
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.equal(root.get(Employee_.firstName), qb.parameter(String.class,parameterName)) );
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.equal(root.get(Employee_.firstName), qb.parameter(String.class, "1")) );

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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        //equal can't take an int as the first argument, it must be an expression
        cq.where( qb.equal(qb.literal(expectedResult.getSalary()), qb.abs(root.get(Employee_.salary))) );
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        //One argument to concat must be an expression
        cq.where( qb.equal(qb.concat(partOne, qb.literal(partTwo)), root.get(Employee_.firstName)) );
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.equal(qb.literal(expectedResult.getFirstName()), root.get(Employee_.firstName)) );
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        javax.persistence.criteria.Expression<Integer> length = qb.length(root.get(Employee_.firstName));
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.equal(qb.parameter(String.class, "1"), root.get(Employee_.firstName)) );
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        javax.persistence.criteria.Expression<Double> sqrt = qb.sqrt(root.get(Employee_.salary));
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        javax.persistence.criteria.Expression<String> substring = qb.substring(root.get(Employee_.firstName), 1, 2);
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.equal(qb.sqrt(root.get(Employee_.salary)), salarySquareRoot) );
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        javax.persistence.criteria.Expression<String> substring = qb.substring(root.get(Employee_.firstName), 1, 2);
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
        cq.where( qb.isNull(cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).get(Employee_.firstName)) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            uow = clientSession.acquireUnitOfWork();
            uow.deleteObject(nullEmployee);
            uow.commit();

            Assert.assertTrue("Simple Null test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
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
        cq.where( qb.isNotNull(cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).get(Employee_.firstName)) );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            uow = clientSession.acquireUnitOfWork();
            uow.deleteObject(nullEmployee);
            uow.commit();

            Assert.assertTrue("Simple Not Null test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
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
        cq.where( qb.equal(cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).get(Employee_.lastName), "Smith") );
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Distinct test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void multipleExecutionOfCriteriaQueryTest() {
        //bug 5279859
        EntityManager em = createEntityManager();
        //"SELECT e FROM Employee e where e.address.postalCode = :postalCode"
        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
            cq.where( qb.equal(root.get(Employee_.address).get(Address_.postalCode), qb.parameter(String.class, "postalCode")) );
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
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    // 321716: merged from original in jpa test
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where( qb.isEmpty(root.get(Employee_.phoneNumbers)) );
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
        cq.where( qb.isNotEmpty(cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).get(Employee_.phoneNumbers)) );
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
        Root<Address> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Address.class));
        cq.where( qb.like(root.get(Address_.street), "234 Wandering "+escapeChar+"_Way", escapeChar) );
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
        Root<SmallProject> projRoot = cq.from(getEntityManagerFactory().getMetamodel().entity(SmallProject.class));

        Root<Employee> empRoot = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where(qb.isMember(projRoot.as(Project.class), empRoot.get(Employee_.projects)) );
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
        cq.where( qb.isNotMember(qb.parameter(Project.class, "1"), cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).get(Employee_.projects)) );
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
        cq.select(qb.countDistinct(cq.from(getEntityManagerFactory().getMetamodel().entity(PhoneNumber.class)).get(PhoneNumber_.owner)));
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.distinct(true);
        cq.select(root.get(Employee_.address));
        cq.where(qb.like(root.get(Employee_.lastName), "%Way%"));
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        Join phone = root.join(Employee_.phoneNumbers);
        cq.select(phone);
        cq.where(qb.isNotNull(phone.get(PhoneNumber_.number)));
        cq.orderBy(qb.asc(phone.get(PhoneNumber_.number)), qb.asc(phone.get(PhoneNumber_.areaCode)));
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
        
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        Join phone = root.join("phoneNumbers");
        cq.where(qb.equal(root.get("number"), qb.all(sq)));
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("Simple select Phonenumber Declared In IN Clause test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
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
        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.distinct(true);
            Subquery<Number> sq = cq.subquery(Number.class);
            Root<PhoneNumber> subroot = sq.from(PhoneNumber.class);
            sq.select(qb.min(subroot.<Number>get("number")));//number is a string? not sure this will work.
        
            Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
            Join phone = root.join(Employee_.phoneNumbers);
            cq.where(qb.equal(phone.get(PhoneNumber_.number), qb.all(sq)));

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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where(qb.isMember(qb.parameter(PhoneNumber.class, "1"), root.get(Employee_.phoneNumbers)));

        Vector parameters = new Vector();
        parameters.add(phone);

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", phone).getResultList();
            Assert.assertTrue("Select simple member of with parameter test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

        uow = clientSession.acquireUnitOfWork();
        uow.deleteObject(phone);
        uow.commit();
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where(qb.isNotMember(qb.parameter(PhoneNumber.class, "1"), root.get(Employee_.phoneNumbers)));

        Vector parameters = new Vector();
        parameters.add(phone);

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", phone).getResultList();

            Assert.assertTrue("Select simple Not member of with parameter test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

        uow = clientSession.acquireUnitOfWork();
        uow.deleteObject(phone);
        uow.commit();
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.where(qb.between(root.get(Employee_.id).as(Comparable.class), qb.parameter(BigDecimal.class, "1"), qb.parameter(Integer.class, "2")));
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        CriteriaBuilder.In inExp = qb.in(root.get(Employee_.id));
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
        cq.where(qb.equal(cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class)).get(Employee_.status), org.eclipse.persistence.testing.models.jpa.advanced.Employee.EmployeeStatus.FULL_TIME));
        beginTransaction(em);
        try {
            @SuppressWarnings("unused")
            List result = em.createQuery(cq).getResultList();
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
        cq.where(qb.equal(cq.from(getEntityManagerFactory().getMetamodel().entity(Project.class)).type(), org.eclipse.persistence.testing.models.jpa.advanced.LargeProject.class));
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
        Root<Employee> root = cq.from(getEntityManagerFactory().getMetamodel().entity(Employee.class));
        cq.select(root.get(Employee_.firstName));
        cq.orderBy(qb.asc(root.get(Employee_.firstName)));
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            Assert.assertTrue("SimpleTypeTest", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
}



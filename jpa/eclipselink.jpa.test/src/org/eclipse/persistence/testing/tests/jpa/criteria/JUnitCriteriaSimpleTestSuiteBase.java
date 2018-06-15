/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Jun 29, 2009-1.0M6 Chris Delahunt
//       - TODO Bug#: Bug Description
//     07/05/2010-2.1.1 Michael O'Brien
//       - 321716: modelgen and jpa versions of duplicate code in both copies of
//       JUnitCriteriaSimpleTestSuite must be kept in sync (to avoid only failing on WebSphere under Derby)
//       (ideally there should be only one copy of the code - the other suite should reference or subclass for changes)
//       see
//       org.eclipse.persistence.testing.tests.jpa.criteria.JUnitCriteriaSimpleTestSuite.simpleModTest():1796
//       org.eclipse.persistence.testing.tests.jpa.criteria.metamodel.JUnitCriteriaSimpleTestSuite.simpleModTest():1766
//       - 321902: this copied code should be renamed, merged or subclassed off the original
package org.eclipse.persistence.testing.tests.jpa.criteria;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.persistence.criteria.CriteriaBuilder.Coalesce;
import javax.persistence.criteria.CriteriaBuilder.SimpleCase;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.HugeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.inheritance.Car;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.BlueLight;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail;

import static org.eclipse.persistence.testing.tests.jpa.criteria.JUnitCriteriaSimpleTestSuiteBase.Attributes.*;

/**
 * @author cdelahun
 * Converted from JUnitJPQLSimpleTestSuite
 */
public abstract class JUnitCriteriaSimpleTestSuiteBase<T> extends JUnitTestCase {
    public enum Attributes {
        Employee_id, Employee_firstName, Employee_lastName, Employee_salary, Employee_normalHours, Employee_phoneNumbers,
        Employee_managedEmployees, Employee_projects, Employee_address, Employee_status, Employee_hugeProject,
        PhoneNumber_number, PhoneNumber_areaCode, PhoneNumber_owner,
        Address_street, Address_postalCode, Address_city,
        LargeProject_budget, BeerConsumer_blueBeersToConsume, BlueLight_discount, Person_car, SportsCar_maxSpeed
    }

    protected interface CriteriaQueryWrapper {
        <X,Y> Root<X> from(CriteriaQuery<Y> query, Class<X> entityClass);
        <X,Y> Fetch<X,Y> fetch(Root<X> root, Attributes attributeKey, JoinType joinType);
        <X,Y> javax.persistence.criteria.Expression<Y> get(Path<X> path, Attributes attributeKey);
        <X,Y> Join<X,Y> join(Root<X> root, Attributes attributeKey);
        <X,Y> Join<X,Y> join(Root<X> root, Attributes attributeKey, JoinType joinType);
    }

    protected CriteriaQueryWrapper wrapper;

    protected abstract void setWrapper();

    protected Map<Attributes,T> attributes;

    protected abstract void populateAttributes();

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public JUnitCriteriaSimpleTestSuiteBase() {
        super();
        setWrapper();
        populateAttributes();
    }

    public JUnitCriteriaSimpleTestSuiteBase(String name) {
        super(name);
        setWrapper();
        populateAttributes();
    }

    //This method is run at the end of EVERY test case method

    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class

    public static <T extends JUnitCriteriaSimpleTestSuiteBase> Test suite(Class<T> implementingClass) {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitCriteriaSimpleTestSuite");

        try {
            Constructor<T> constructor = implementingClass.getConstructor(String.class);
            suite.addTest(constructor.newInstance("testSetup"));
            suite.addTest(constructor.newInstance("simpleJoinFetchTest"));
            suite.addTest(constructor.newInstance("simpleJoinFetchTest2"));
            suite.addTest(constructor.newInstance("executeSameCriteriaQueryWithJoinTwice"));
            suite.addTest(constructor.newInstance("baseTestCase"));
            suite.addTest(constructor.newInstance("simpleABSTest"));
            suite.addTest(constructor.newInstance("simpleBetweenTest"));
            suite.addTest(constructor.newInstance("simpleConcatTest"));
            suite.addTest(constructor.newInstance("simpleConcatTestWithParameters"));
            suite.addTest(constructor.newInstance("simpleConcatTestWithConstants1"));
            suite.addTest(constructor.newInstance("simpleCountTest"));
            suite.addTest(constructor.newInstance("simpleThreeArgConcatTest"));
            suite.addTest(constructor.newInstance("simpleDistinctTest"));
            suite.addTest(constructor.newInstance("simpleDistinctNullTest"));
            suite.addTest(constructor.newInstance("simpleDistinctMultipleResultTest"));
            suite.addTest(constructor.newInstance("simpleDoubleOrTest"));
            suite.addTest(constructor.newInstance("simpleEqualsTest"));
            suite.addTest(constructor.newInstance("simpleEqualsTestWithJoin"));
            suite.addTest(constructor.newInstance("collectionMemberIdentifierEqualsTest"));
            suite.addTest(constructor.newInstance("abstractSchemaIdentifierEqualsTest"));
            suite.addTest(constructor.newInstance("abstractSchemaIdentifierNotEqualsTest"));
            suite.addTest(constructor.newInstance("simpleInOneDotTest"));
            suite.addTest(constructor.newInstance("simpleInTest"));
            suite.addTest(constructor.newInstance("simpleInListTest"));
            suite.addTest(constructor.newInstance("simpleLengthTest"));
            suite.addTest(constructor.newInstance("simpleLikeTest"));
            suite.addTest(constructor.newInstance("simpleLikeTestWithParameter"));
            suite.addTest(constructor.newInstance("simpleLikeEscapeTestWithParameter"));
            suite.addTest(constructor.newInstance("simpleNotBetweenTest"));
            suite.addTest(constructor.newInstance("simpleNotEqualsVariablesInteger"));
            suite.addTest(constructor.newInstance("simpleNotInTest"));
            suite.addTest(constructor.newInstance("simpleNotLikeTest"));
            suite.addTest(constructor.newInstance("simpleOrFollowedByAndTest"));
            suite.addTest(constructor.newInstance("simpleOrFollowedByAndTestWithStaticNames"));
            suite.addTest(constructor.newInstance("simpleOrTest"));
            suite.addTest(constructor.newInstance("simpleParameterTest"));
            suite.addTest(constructor.newInstance("simpleParameterTestChangingParameters"));
            suite.addTest(constructor.newInstance("simpleReverseAbsTest"));
            suite.addTest(constructor.newInstance("simpleReverseConcatTest"));
            suite.addTest(constructor.newInstance("simpleReverseEqualsTest"));
            suite.addTest(constructor.newInstance("simpleReverseLengthTest"));
            suite.addTest(constructor.newInstance("simpleReverseParameterTest"));
            suite.addTest(constructor.newInstance("simpleReverseSqrtTest"));
            suite.addTest(constructor.newInstance("simpleReverseSubstringTest"));
            suite.addTest(constructor.newInstance("simpleSqrtTest"));
            suite.addTest(constructor.newInstance("simpleSubstringTest"));
            suite.addTest(constructor.newInstance("simpleNullTest"));
            suite.addTest(constructor.newInstance("simpleNotNullTest"));
            suite.addTest(constructor.newInstance("distinctTest"));
            suite.addTest(constructor.newInstance("simpleModTest"));
            suite.addTest(constructor.newInstance("simpleIsEmptyTest"));
            suite.addTest(constructor.newInstance("simpleIsNotEmptyTest"));
            suite.addTest(constructor.newInstance("simpleEscapeUnderscoreTest"));
            suite.addTest(constructor.newInstance("simpleEnumTest"));
            suite.addTest(constructor.newInstance("smallProjectMemberOfProjectsTest"));
            suite.addTest(constructor.newInstance("smallProjectNOTMemberOfProjectsTest"));
            suite.addTest(constructor.newInstance("selectCountOneToOneTest")); //bug 4616218
            suite.addTest(constructor.newInstance("selectOneToOneTest")); //employee.address doesnt not work
            suite.addTest(constructor.newInstance("selectPhonenumberDeclaredInINClauseTest"));
            suite.addTest(constructor.newInstance("selectPhoneUsingALLTest"));
            suite.addTest(constructor.newInstance("selectSimpleMemberOfWithParameterTest"));
            suite.addTest(constructor.newInstance("selectSimpleNotMemberOfWithParameterTest"));
            suite.addTest(constructor.newInstance("selectSimpleBetweenWithParameterTest"));
            suite.addTest(constructor.newInstance("selectSimpleInWithParameterTest"));
            suite.addTest(constructor.newInstance("selectAverageQueryForByteColumnTest"));
            suite.addTest(constructor.newInstance("multipleExecutionOfCriteriaQueryTest"));
            suite.addTest(constructor.newInstance("simpleTypeTest"));
            suite.addTest(constructor.newInstance("simpleAsOrderByTest"));
            suite.addTest(constructor.newInstance("simpleCaseInWhereTest"));
            suite.addTest(constructor.newInstance("simpleCaseInSelectTest"));
            suite.addTest(constructor.newInstance("caseConditionInWhereTest"));
            suite.addTest(constructor.newInstance("caseConditionInSelectTest"));
            suite.addTest(constructor.newInstance("simpleCoalesceInWhereTest"));
            suite.addTest(constructor.newInstance("simpleCoalesceInSelectTest"));
            suite.addTest(constructor.newInstance("largeProjectCastTest"));
            suite.addTest(constructor.newInstance("mapCastTest"));
            suite.addTest(constructor.newInstance("oneToOneCastTest"));
            suite.addTest(constructor.newInstance("testTupleQuery"));
            suite.addTest(constructor.newInstance("testTupleIndexValidation"));
            suite.addTest(constructor.newInstance("testTupleIndexTypeValidation"));
            suite.addTest(constructor.newInstance("testTupleStringValidation"));
            suite.addTest(constructor.newInstance("testTupleStringTypeValidation"));
            suite.addTest(constructor.newInstance("testCriteriaBuilderTupleValidation"));
            suite.addTest(constructor.newInstance("testCriteriaBuilderArrayValidation"));
            suite.addTest(constructor.newInstance("testCriteriaBuilderConstructValidation"));
            suite.addTest(constructor.newInstance("testLiteralValidation"));
            suite.addTest(constructor.newInstance("testCompoundSelectionAliasValidation"));
            suite.addTest(constructor.newInstance("testEmptyLeftJoinInCriteriaQuery"));
        }
        catch (Exception x) {
            fail(Helper.printStackTraceToString(x));
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

        new InheritedTableManager().replaceTables(session);

        new InheritanceTableCreator().replaceTables(session);
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
            CriteriaBuilder qb = em.getCriteriaBuilder();
            //"SELECT e FROM Employee e"
            TypedQuery<Employee> query = em.createQuery(qb.createQuery(Employee.class));
            List<Employee> emps = query.getResultList();

            assertNotNull(emps);
            int numRead = emps.size();


            //"SELECT e FROM Employee e WHERE :arg1=:arg2");
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.where(qb.equal(qb.parameter(Integer.class, "arg1"), qb.parameter(Integer.class, "arg2")));
            query = em.createQuery(cq);

            query.setParameter("arg1", 1);
            query.setParameter("arg2", 1);
            emps = query.getResultList();

            assertNotNull(emps);
            assertEquals(numRead, emps.size());
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
        JpaEntityManager em = (JpaEntityManager)createEntityManager();
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
        JpaEntityManager em = (JpaEntityManager)createEntityManager();
        //preload employees into the cache so that phonenumbers are not prefetched
        String ejbqlString = "SELECT e FROM Employee e";
        List result = em.createQuery(ejbqlString).getResultList();
        result.size();
        // run the simpleJoinFetchTest and verify all employees have phonenumbers fetched.
        simpleJoinFetchTest(em);
    }

    public void simpleJoinFetchTest(JpaEntityManager em) throws Exception {
        //"SELECT e FROM Employee e LEFT JOIN FETCH e.phoneNumbers"

        em.createQuery("select e from Employee e left join fetch e.phoneNumbers").getResultList();
        //use the cache
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = wrapper.from(cq, Employee.class);
        wrapper.fetch(root, Employee_phoneNumbers, JoinType.LEFT);
        List result = em.createQuery(cq).getResultList();

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(byteStream);

        stream.writeObject(result);
        stream.flush();
        byte arr[] = byteStream.toByteArray();
        ByteArrayInputStream inByteStream = new ByteArrayInputStream(arr);
        ObjectInputStream inObjStream = new ObjectInputStream(inByteStream);

        List deserialResult = (List)inObjStream.readObject();
        for (Object aDeserialResult : deserialResult) {
            Employee emp = (Employee) aDeserialResult;
            emp.getPhoneNumbers().size();
        }

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setShouldReturnWithoutReportQueryResult(true);
        reportQuery.setReferenceClass(Employee.class);
        ExpressionBuilder builder = reportQuery.getExpressionBuilder();
        List<Expression> joins = new ArrayList<>(1);
        joins.add(builder.anyOfAllowingNone("phoneNumbers"));
        reportQuery.addItem("emp", builder, joins);
        Vector expectedResult = (Vector)em.getUnitOfWork().executeQuery(reportQuery);

        if (!comparer.compareObjects(result, expectedResult)) {
            fail("simpleJoinFetchTest Failed when using cache, collections do not match: " + result + " expected: " + expectedResult);
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
        for (Object aDeserialResult : deserialResult) {
            Employee emp = (Employee) aDeserialResult;
            emp.getPhoneNumbers().size();
        }

        clearCache();

        expectedResult = (Vector)em.getUnitOfWork().executeQuery(reportQuery);

        if (!comparer.compareObjects(result, expectedResult)) {
            fail("simpleJoinFetchTest Failed when not using cache, collections do not match: " + result + " expected: " + expectedResult);
        }
    }

    public void executeSameCriteriaQueryWithJoinTwice() {
        //"SELECT e FROM Employee e LEFT JOIN FETCH e.phoneNumbers"

        EntityManager em = createEntityManager();
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = wrapper.from(cq, Employee.class);
        wrapper.fetch(root, Employee_phoneNumbers, JoinType.LEFT);
        em.createQuery(cq).getResultList();
        // This used to throw IndexOutOfBoundsException due to ObjectLevelReadQuery.prepareFromQuery not copying joinAttributes:
        // in ObjectBuildingQuery.triggerJoinExpressions there were joinExpressionAttributes but not joinAttributes.
        em.createQuery(cq).getResultList();
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

            assertTrue("Base Test Case Failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where(qb.equal( qb.abs(wrapper.get(root, Employee_salary)), expectedResult.getSalary()) );
            List result = em.createQuery(cq).getResultList();

            assertTrue("ABS test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //Test case for Between function in EJBQL

    @SuppressWarnings("unchecked")
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.between(wrapper.<Employee,Comparable>get(root, Employee_id),
                    qb.<Comparable>literal(empId), qb.<Comparable>literal(employee.getId()) ) );
            List result = em.createQuery(cq).getResultList();

            assertTrue("Between test failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.equal(wrapper.get(root, Employee_firstName), qb.concat(qb.literal(partOne), qb.literal(partTwo))) );
            List result = em.createQuery(cq).getResultList();

            assertTrue("Concat test failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.equal(wrapper.get(root, Employee_firstName), qb.concat(qb.parameter(String.class, "partOne"), qb.parameter(String.class, "partTwo"))) );
            Query query = em.createQuery(cq);
            query.setParameter("partOne", partOne).setParameter("partTwo", partTwo);

            List result = query.getResultList();

            assertTrue("Concat test failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.like(qb.concat(wrapper.get(root, Employee_firstName), qb.literal("Smith") ), partOne+"Smith") );
            List result = em.createQuery(cq).getResultList();

            assertTrue("Concat test with constraints failed", comparer.compareObjects(result, expectedResult));
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
            root = wrapper.from(cq, PhoneNumber.class);
            cq.select(qb.count(wrapper.get((Path) wrapper.get(root, PhoneNumber_owner), Employee_id)));
            result = em.createQuery(cq).getResultList();

            assertTrue("Simple Count test failed", expectedResult.elementAt(0).equals(result.get(0)));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.equal( wrapper.get(root, Employee_firstName), qb.concat(qb.literal(partOne), qb.concat( qb.literal(partTwo), qb.literal(partThree)) ) ) );
            List result = em.createQuery(cq).getResultList();

            assertTrue("Concat test failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            wrapper.fetch(root, Employee_phoneNumbers, JoinType.INNER);
            List<Employee> result = em.createQuery(cq).getResultList();

            Set<Employee> testSet = new HashSet<>();
            for (Employee emp : result) {
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.select(wrapper.get(root, Employee_firstName));
            cq.where( qb.equal(wrapper.get(root, Employee_lastName), qb.literal(emp.getLastName())));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            wrapper.join(root, Employee_phoneNumbers);
            cq.distinct(true);
            cq.multiselect(root, wrapper.get(root, Employee_firstName));
            List<Tuple> result = em.createQuery(cq).getResultList();

            Set<String> testSet = new HashSet<>();
            for (Tuple row : result) {
                Employee emp = row.get(0, Employee.class);
                String string = row.get(1, String.class);
                String ids = "_" + emp.getId() + "_" + string;
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

            Vector<Employee> expectedResult = new Vector<>();
            expectedResult.add(emp1);
            expectedResult.add(emp2);
            expectedResult.add(emp3);

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = " + emp1.getId() + "OR emp.id = " + emp2.getId() + "OR emp.id = " + emp3.getId()
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = wrapper.from(cq, Employee.class);
            Predicate firstOr = qb.or(qb.equal(wrapper.get(root, Employee_id), emp1.getId()), qb.equal(wrapper.get(root, Employee_id), emp2.getId()));
            cq.where( qb.or(firstOr, qb.equal(wrapper.get(root, Employee_id), emp3.getId())) );
            List result = em.createQuery(cq).getResultList();

            assertTrue("Double OR test failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.equal(wrapper.get(root, Employee_firstName), expectedResult.getFirstName() ) );
            List result = em.createQuery(cq).getResultList();

            assertTrue("Equals test failed", comparer.compareObjects(expectedResult, result));
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
            Join<Employee, Employee> managedEmp =  wrapper.join(wrapper.from(cq, Employee.class), Employee_managedEmployees);
            cq.where( qb.equal(wrapper.get((Path) wrapper.get(managedEmp, Employee_address), Address_city), "Ottawa" ) );
            List result = em.createQuery(cq).getResultList();

            assertTrue("Equals test with Join failed", comparer.compareObjects(result, expectedResult));
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
            Join<Employee,PhoneNumber> phones = wrapper.join(wrapper.from(cq, Employee.class), Employee_phoneNumbers);
            cq.where( qb.equal(phones, qb.parameter(PhoneNumber.class, "1") ) );
            List result = em.createQuery(cq).setParameter("1", phoneNumber).getResultList();

            assertTrue("CollectionMemberIdentifierEqualsTest failed", comparer.compareObjects(expectedResult, result));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.equal(root, qb.parameter(Employee.class, "1") ) );
            List result = em.createQuery(cq).setParameter("1", expectedResult).getResultList();

            assertTrue("abstractSchemaIdentifierEqualsTest failed", comparer.compareObjects(expectedResult, result));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.notEqual(root, qb.parameter(Employee.class, "1") ) );
            List result = em.createQuery(cq).setParameter("1", emp).getResultList();

            assertTrue("abstractSchemaIdentifierNotEqualsTest failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            Join phone = wrapper.join(root, Employee_phoneNumbers);
            Predicate firstAnd = qb.and( qb.equal(wrapper.get(phone, PhoneNumber_areaCode), empPhoneNumbers.getAreaCode()),
                    qb.equal(wrapper.get(root, Employee_firstName), expectedResult.getFirstName()));
            cq.where( qb.and(firstAnd, qb.equal(wrapper.get(root, Employee_lastName), expectedResult.getLastName())) );
            Employee result = em.createQuery(cq).getSingleResult();

            assertTrue("Simple In Dot Test failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            //casting types again.  Avg takes a number, so Path<Object> won't compile
            cq.select( qb.avg( wrapper.get(root, Employee_salary) ) );
            Object result = em.createQuery(cq).getSingleResult();

            assertTrue("AVG result type [" + result.getClass() + "] not of type Double", result.getClass() == Double.class);
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
            cq.where( qb.in(wrapper.get(wrapper.from(cq, Employee.class), Employee_id)).value(expectedResult.getId()) );
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple In Test failed", comparer.compareObjects(result, expectedResult));
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

            List<Integer> expectedResultList = new ArrayList<>();
            expectedResultList.add(expectedResult.getId());

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN :result"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.where(wrapper.get(wrapper.from(cq, Employee.class), Employee_id).in(qb.parameter(List.class, "result")));
            List result = em.createQuery(cq).setParameter("result", expectedResultList).getResultList();

            assertTrue("Simple In Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleLengthTest() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSQLServer()) {
            getServerSession().logMessage("Warning SQL doesnot support LENGTH function");
            return;
        }

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE LENGTH ( emp.firstName     ) = " + expectedResult.getFirstName().length();
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.equal( qb.length(wrapper.get(root, Employee_firstName)) , expectedResult.getFirstName().length()) );

            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Length Test failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.like( wrapper.get(root, Employee_firstName), partialFirstName) );

            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Like Test failed", comparer.compareObjects(result, expectedResult));
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

            ExpressionBuilder eb = new ExpressionBuilder();
            Expression whereClause = eb.get("firstName").like(partialFirstName);
            raq.setSelectionCriteria(whereClause);
            Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

            clearCache();

            //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE ?1"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.like( wrapper.get(root, Employee_firstName), qb.parameter(String.class, "1")) );

            List result = em.createQuery(cq).setParameter("1", partialFirstName).getResultList();

            assertTrue("Simple Like Test with Parameter failed", comparer.compareObjects(result, expectedResult));
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
        uow.release();

        //test the apostrophe
        //"SELECT OBJECT(address) FROM Address address WHERE address.street LIKE :pattern ESCAPE :esc"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Address> cq = qb.createQuery(Address.class);
        Root<Address> root = wrapper.from(cq, Address.class);
        cq.where( qb.like( wrapper.get(root, Address_street), qb.parameter(String.class, "pattern"), qb.parameter(Character.class, "esc")) );

        String patternString;
        Character escChar;
        // \ is always treated as escape in MySQL.  Therefore ESCAPE '\' is considered a syntax error
        if (getServerSession().getPlatform().isMySQL()) {
            patternString = "234 RUBY $_Way";
            escChar = '$';
        } else {
            patternString = "234 RUBY \\_Way";
            escChar = '\\';
        }
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("pattern", patternString).setParameter("esc", escChar).getResultList();

            assertTrue("Simple Escape Underscore test failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.not(qb.between(wrapper.get(root, Employee_id), emp1.getId(), emp2.getId())) );

            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Not Between Test failed", comparer.compareObjects(result, expectedResult));
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
            cq.where( qb.notEqual(wrapper.get(wrapper.from(cq, Employee.class), Employee_id), emp.getId()) );

            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Like Test with Parameter failed", comparer.compareObjects(result, expectedResult));
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

            Vector<Integer> idVector = new Vector<>();
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.not(qb.in(wrapper.get(root, Employee_id)).value(emp.getId())) );

            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Not In Test failed", comparer.compareObjects(result, expectedResult));
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
            Root<Employee> root = wrapper.from(cq, Employee.class);
            cq.where( qb.notLike(wrapper.get(root, Employee_firstName), partialFirstName ) );

            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Not Like Test failed", comparer.compareObjects(result, expectedResult));
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

        Vector<Employee> expectedResult = new Vector<>();
        expectedResult.add(emp1);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = " + emp1.getId() + " OR emp.id = " + emp2.getId() + " AND emp.id = " + emp3.getId()
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = wrapper.from(cq, Employee.class);
        Predicate andOpp = qb.and(qb.equal(wrapper.get(root, Employee_id), emp2.getId()), qb.equal(wrapper.get(root, Employee_id), emp3.getId()));
        cq.where( qb.or( qb.equal(wrapper.get(root, Employee_id), emp1.getId()), andOpp ) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Or followed by And Test failed", comparer.compareObjects(result, expectedResult));
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
        Root<Employee> root = wrapper.from(cq, Employee.class);
        javax.persistence.criteria.Expression empFName = wrapper.get(root, Employee_firstName);
        Predicate andOpp = qb.and(qb.equal(empFName, "Bob"), qb.equal(wrapper.get(root, Employee_lastName), "Smith"));
        cq.where( qb.or( qb.equal(empFName, "John"), andOpp ) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Or followed by And With Static Names Test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleOrTest() {
        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(0);
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);

        Vector<Employee> expectedResult = new Vector<>();
        expectedResult.add(emp1);
        expectedResult.add(emp2);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = " + emp1.getId() + "OR emp.id = " + emp2.getId()
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        javax.persistence.criteria.Expression empId = wrapper.get(wrapper.from(cq, Employee.class), Employee_id);
        cq.where( qb.or( qb.equal(empId, emp1.getId()), qb.equal(empId, emp2.getId()) ) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();
            clearCache();

            assertTrue("Simple Or Test failed", comparer.compareObjects(result, expectedResult));
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

        List<String> parameters = new ArrayList<>();
        parameters.add(expectedResult.getFirstName());

        List employees = (List)getServerSession().executeQuery(raq, parameters);
        employees.size();

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE " + "emp.firstName = ?1 "
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.equal(wrapper.get(wrapper.from(cq, Employee.class), Employee_firstName), qb.parameter(String.class,parameterName)) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter(parameterName, expectedResult.getFirstName()).getResultList();

            assertTrue("Simple Parameter Test failed", comparer.compareObjects(result, expectedResult));
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

        Vector<String> firstParameters = new Vector<>();
        firstParameters.add(emp1.getFirstName());
        Vector<String> secondParameters = new Vector<>();
        secondParameters.add(emp2.getFirstName());

        @SuppressWarnings("unchecked")
        Vector<Employee> firstEmployees = (Vector<Employee>)getServerSession().executeQuery(raq, firstParameters);
        clearCache();
        @SuppressWarnings("unchecked")
        Vector<Employee> secondEmployees = (Vector<Employee>)getServerSession().executeQuery(raq, secondParameters);
        clearCache();
        Vector<Employee> expectedResult = new Vector<>();
        expectedResult.addAll(firstEmployees);
        expectedResult.addAll(secondEmployees);

        //"SELECT OBJECT(emp) FROM Employee emp WHERE " + "emp.firstName = ?1 "
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.equal(wrapper.get(wrapper.from(cq, Employee.class), Employee_firstName), qb.parameter(String.class, "1")) );

        beginTransaction(em);
        try {
            List<Employee> firstResultSet = em.createQuery(cq).setParameter("1", firstParameters.get(0)).getResultList();
            clearCache();
            List<Employee> secondResultSet = em.createQuery(cq).setParameter("1", secondParameters.get(0)).getResultList();
            clearCache();
            Vector<Employee> result = new Vector<>();
            result.addAll(firstResultSet);
            result.addAll(secondResultSet);

            assertTrue("Simple Parameter Test Changing Parameters failed", comparer.compareObjects(result, expectedResult));
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
        cq.where( qb.equal(qb.literal(expectedResult.getSalary()), qb.abs(wrapper.get(wrapper.from(cq, Employee.class), Employee_salary))) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Reverse Abs test failed", comparer.compareObjects(result, expectedResult));
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
        cq.where( qb.equal(qb.concat(partOne, qb.literal(partTwo)), wrapper.get(wrapper.from(cq, Employee.class), Employee_firstName)) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Reverse Concat test failed", comparer.compareObjects(result, expectedResult));
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
        cq.where( qb.equal(qb.literal(expectedResult.getFirstName()), wrapper.get(wrapper.from(cq, Employee.class), Employee_firstName)) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Reverse Equals test failed", comparer.compareObjects(result, expectedResult));
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
        javax.persistence.criteria.Expression<Integer> length = qb.length(wrapper.get(wrapper.from(cq, Employee.class), Employee_firstName));
        cq.where( qb.equal(qb.literal(expectedResult.getFirstName().length()), length) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Reverse Length test failed", comparer.compareObjects(result, expectedResult));
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

        Vector<String> parameters = new Vector<>();
        parameters.add(emp.getFirstName());

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq, parameters);
        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE ?1 = emp.firstName "
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.equal(qb.parameter(String.class, "1"), wrapper.get(wrapper.from(cq, Employee.class), Employee_firstName)) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", parameters.get(0)).getResultList();

            assertTrue("Simple Reverse Parameter test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleReverseSqrtTest() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test simpleReverseSqrtTest skipped for this platform, "
                    + "Symfoware doesn't support SQRT, COS, SIN, TAN functions.");
            return;
        }
        EntityManager em = createEntityManager();

        ExpressionBuilder expbldr = new ExpressionBuilder();
        Expression whereClause = expbldr.get("firstName").equal("SquareRoot").and(expbldr.get("lastName").equal("TestCase1"));
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        double salarySquareRoot = Math.sqrt((double) ((Employee) expectedResult.firstElement()).getSalary());

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE "+ salarySquareRoot + " = SQRT(emp.salary)"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        javax.persistence.criteria.Expression<Double> sqrt = qb.sqrt(wrapper.get(wrapper.from(cq, Employee.class), Employee_salary));
        cq.where( qb.equal(qb.literal(salarySquareRoot), sqrt) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Reverse Square Root test failed", comparer.compareObjects(result, expectedResult));
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
        javax.persistence.criteria.Expression<String> substring = qb.substring(wrapper.get(wrapper.from(cq, Employee.class), Employee_firstName), 1, 2);
        cq.where( qb.equal(qb.literal(firstNamePart), substring) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Reverse SubString test failed", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }


    public void simpleSqrtTest() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test simpleSqrtTest skipped for this platform, "
                    + "Symfoware doesn't support SQRT, COS, SIN, TAN functions.");
            return;
        }
        EntityManager em = createEntityManager();

        ExpressionBuilder expbldr = new ExpressionBuilder();
        Expression whereClause = expbldr.get("firstName").equal("SquareRoot").and(expbldr.get("lastName").equal("TestCase1"));
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        double salarySquareRoot = Math.sqrt((double) ((Employee) expectedResult.firstElement()).getSalary());

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE SQRT(emp.salary) = "+ salarySquareRoot
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.equal(qb.sqrt(wrapper.get(wrapper.from(cq, Employee.class), Employee_salary)), salarySquareRoot) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Square Root test failed", comparer.compareObjects(result, expectedResult));
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
        javax.persistence.criteria.Expression<String> substring = qb.substring(wrapper.get(wrapper.from(cq, Employee.class), Employee_firstName), 1, 2);
        cq.where( qb.equal(substring, firstNamePart) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();
            assertTrue("Simple SubString test failed", comparer.compareObjects(result, expectedResult));
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
        uow.release();

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
        cq.where( qb.isNull(wrapper.get(wrapper.from(cq, Employee.class), Employee_firstName)) );

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
        uow.release();

        assertTrue("Simple Null test failed", comparer.compareObjects(result, expectedResult));
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
        uow.release();

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
        cq.where( qb.isNotNull(wrapper.get(wrapper.from(cq, Employee.class), Employee_firstName)) );
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
        uow.release();

        assertTrue("Simple Not Null test failed", comparer.compareObjects(result, expectedResult));
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
        cq.where( qb.equal(wrapper.get(wrapper.from(cq, Employee.class), Employee_lastName), "Smith") );

        List result = null;
        beginTransaction(em);
        try {
            result = em.createQuery(cq).getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

        assertTrue("Distinct test failed", comparer.compareObjects(result, expectedResult));
    }

    public void multipleExecutionOfCriteriaQueryTest() {
        //bug 5279859
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            //"SELECT e FROM Employee e where e.address.postalCode = :postalCode"
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.where( qb.equal(wrapper.get((Path) wrapper.get(wrapper.from(cq, Employee.class), Employee_address), Address_postalCode), qb.parameter(String.class, "postalCode")) );
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

    // 321716: merged into copy in modegen test
    public void simpleModTest() {
        EntityManager em = createEntityManager();

        //assertFalse("Warning SQL/Sybase doesnot support MOD function", (JUnitTestCase.getServerSession()).getPlatform().isSQLServer() || (JUnitTestCase.getServerSession()).getPlatform().isSybase());

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
        cq.where( qb.gt(qb.mod(wrapper.get(wrapper.from(cq, Employee.class), Employee_salary), 2), 0) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Mod test failed", comparer.compareObjects(result, expectedResult));

            // Test MOD(fieldAccess, fieldAccess) glassfish issue 2771

            expectedResult = getServerSession().readAllObjects(Employee.class);
            clearCache();

            //"SELECT emp FROM Employee emp WHERE MOD(emp.salary, emp.salary) = 0"
            qb = em.getCriteriaBuilder();
            cq = qb.createQuery(Employee.class);
            javax.persistence.criteria.Expression<Integer> salaryExp = wrapper.get(wrapper.from(cq, Employee.class), Employee_salary);
            cq.where( qb.equal(qb.mod(salaryExp, salaryExp), 0) );

            result = em.createQuery(cq).getResultList();

            assertTrue("Simple Mod test(2) failed", comparer.compareObjects(result, expectedResult));
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
        cq.where( qb.isEmpty(wrapper.get(wrapper.from(cq, Employee.class), Employee_phoneNumbers)) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Is empty test failed", comparer.compareObjects(result, expectedResult));
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
        cq.where( qb.isNotEmpty(wrapper.get(wrapper.from(cq, Employee.class), Employee_phoneNumbers)) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple is not empty test failed", comparer.compareObjects(result, expectedResult));
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
        uow.release();

        Character escapeChar;
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
        cq.where( qb.like(wrapper.get(wrapper.from(cq, Address.class), Address_street), "234 Wandering "+escapeChar+"_Way", escapeChar) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Escape Underscore test failed", comparer.compareObjects(result, expectedResult));
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
        cq.where( qb.isMember(wrapper.from(cq, SmallProject.class),
                wrapper.<Employee,Collection<SmallProject>>get(wrapper.from(cq, Employee.class), Employee_projects)) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple small Project Member Of Projects test failed", comparer.compareObjects(result, expectedResult));
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

        Vector<SmallProject> arguments = new Vector<>();
        arguments.add(smallProject);
        Vector expectedResult = (Vector)getServerSession().executeQuery(query, arguments);

        //"SELECT OBJECT(employee) FROM Employee employee WHERE ?1 NOT MEMBER OF employee.projects"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.where( qb.isNotMember(qb.parameter(Project.class, "1"),
                wrapper.<Employee,Collection<Project>>get(wrapper.from(cq, Employee.class), Employee_projects)) );

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", smallProject).getResultList();

            assertTrue("Simple small Project NOT Member Of Projects test failed", comparer.compareObjects(result, expectedResult));
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
        cq.select(qb.countDistinct(wrapper.get(wrapper.from(cq, PhoneNumber.class), PhoneNumber_owner)));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Select Count One To One test failed", expectedResult.elementAt(0).equals(result.get(0)));
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
        if (usesSOP() && getServerSession().getPlatform().isOracle()) {
            // distinct is incompatible with blob in selection clause on Oracle
            query.setShouldUseSerializedObjectPolicy(false);
        }
        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        clearCache();

        //"SELECT DISTINCT employee.address FROM Employee employee WHERE employee.lastName LIKE '%Way%'"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Address> cq = qb.createQuery(Address.class);
        Root<Employee> root = wrapper.from(cq, Employee.class);
        cq.distinct(true);
        cq.select(wrapper.get(root, Employee_address));
        cq.where(qb.like(wrapper.get(root, Employee_lastName), "%Way%"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple Select One To One test failed", comparer.compareObjects(result, expectedResult));
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
        Root<Employee> root = wrapper.from(cq, Employee.class);
        Join<Employee, PhoneNumber> phone = wrapper.join(root, Employee_phoneNumbers);
        cq.select(phone);
        cq.where(qb.isNotNull(phone.get("number")));
        cq.orderBy(qb.asc(phone.get("number")), qb.asc(phone.get("areaCode")));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Simple select Phonenumber Declared In IN Clause test failed", comparer.compareObjects(result, expectedResult));
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

        assertTrue("Simple select Phonenumber Declared In IN Clause test failed", comparer.compareObjects(result, expectedResult));

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
        if (usesSOP() && getServerSession().getPlatform().isOracle()) {
            // distinct is incompatible with blob in selection clause on Oracle
            query.dontUseDistinct();
        }

        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        clearCache();

        //"Select Distinct Object(emp) from Employee emp, IN(emp.phoneNumbers) p WHERE p.number = ALL (Select MIN(pp.number) FROM PhoneNumber pp)";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        cq.distinct(true);
        Subquery<Number> sq = cq.subquery(Number.class);
        Root<PhoneNumber> subroot = sq.from(PhoneNumber.class);
        sq.select(qb.min(subroot.<Number>get("number")));//number is a string? not sure this will work.

        Root<Employee> root = wrapper.from(cq, Employee.class);
        Join phone = wrapper.join(root, Employee_phoneNumbers);
        cq.where(qb.equal(wrapper.get(phone, PhoneNumber_number), qb.all(sq)));

        beginTransaction(em);
        try {
            Query jpqlQuery = em.createQuery(cq);
            jpqlQuery.setMaxResults(10);
        if (usesSOP() && getServerSession().getPlatform().isOracle()) {
            // distinct is incompatible with blob in selection clause on Oracle
            jpqlQuery.setHint(QueryHints.SERIALIZED_OBJECT, "false");
        }
            List result = jpqlQuery.getResultList();

            assertTrue("Simple select Phonenumber Declared In IN Clause test failed", comparer.compareObjects(result, expectedResult));
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
        uow.release();


        //"SELECT OBJECT(emp) FROM Employee emp " + "WHERE ?1 MEMBER OF emp.phoneNumbers";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = wrapper.from(cq, Employee.class);
        cq.where(qb.isMember(qb.parameter(PhoneNumber.class, "1"), wrapper.<Employee,Collection<PhoneNumber>>get(root, Employee_phoneNumbers)));

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
        uow.release();

        assertTrue("Select simple member of with parameter test failed", comparer.compareObjects(result, expectedResult));

        clearCache();
    }

    public void selectSimpleNotMemberOfWithParameterTest() {
        EntityManager em = createEntityManager();

        Vector expectedResult = getServerSession().readAllObjects(Employee.class);

        clearCache();

        Employee emp = (Employee)expectedResult.get(0);
        expectedResult.remove(0);

        boolean shouldCleanUp = false;
        PhoneNumber phone;
        if (emp.getPhoneNumbers().isEmpty()) {
            phone = new PhoneNumber();
            phone.setAreaCode("613");
            phone.setNumber("1234567");
            phone.setType("cell");

            Server serverSession = JUnitTestCase.getServerSession();
            Session clientSession = serverSession.acquireClientSession();
            UnitOfWork uow = clientSession.acquireUnitOfWork();
            emp = (Employee)uow.readObject(emp);
            PhoneNumber phoneClone = (PhoneNumber)uow.registerObject(phone);
            emp.addPhoneNumber(phoneClone);
            if (usesSOP()) {
                // In SOP is used then the phone is never read back (it's saved in sopObject),
                // therefore its ownerId (mapped as read only) is never set.
                // If phone.ownerId is not set, then the next query (that takes phone as a parameter) would return all the employees,
                // it supposed to return all the employees minus emp.
                phoneClone.setId(emp.getId());
            }
            uow.commit();
            uow.release();
            phone = emp.getPhoneNumbers().iterator().next();
            shouldCleanUp = true;
        } else {
            phone = emp.getPhoneNumbers().iterator().next();
        }

        //"SELECT OBJECT(emp) FROM Employee emp " + "WHERE ?1 NOT MEMBER OF emp.phoneNumbers"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = wrapper.from(cq, Employee.class);
        cq.where(qb.isNotMember(qb.parameter(PhoneNumber.class, "1"), wrapper.<Employee,Collection<PhoneNumber>>get(root, Employee_phoneNumbers)));

        List result = null;
        beginTransaction(em);
        try {
            result = em.createQuery(cq).setParameter("1", phone).getResultList();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }

        boolean ok = comparer.compareObjects(result, expectedResult);
        if (shouldCleanUp) {
            Server serverSession = JUnitTestCase.getServerSession();
            Session clientSession = serverSession.acquireClientSession();
            UnitOfWork uow = clientSession.acquireUnitOfWork();
            emp = (Employee)uow.readObject(emp);
            PhoneNumber phoneToRemove = emp.getPhoneNumbers().iterator().next();
            emp.removePhoneNumber(phoneToRemove);
            uow.deleteObject(phoneToRemove);
            uow.commit();
            uow.release();
        }
        if (!ok) {
            fail("unexpected query result");
        }
    }

    @SuppressWarnings("unchecked")
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
        Root<Employee> root = wrapper.from(cq, Employee.class);
        cq.where(qb.between(wrapper.get(root, Employee_id).as(Comparable.class),
                qb.parameter(BigDecimal.class, "1").as(Comparable.class), qb.parameter(Integer.class, "2").as(Comparable.class)));
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", empId1).setParameter("2", emp2.getId()).getResultList();

            assertTrue("Simple select between with parameter test failed", comparer.compareObjects(result, expectedResult));
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
        Vector<Number> vec = new Vector<>();
        vec.add(empId1);
        vec.add(emp2.getId());

        Expression whereClause = eb.get("id").in(vec);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        //"SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN (?1, ?2)"
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = wrapper.from(cq, Employee.class);
        CriteriaBuilder.In<Comparable> inExp = qb.in(wrapper.get(root, Employee_id));
        inExp.value(qb.parameter(BigDecimal.class, "1"));
        inExp.value(qb.parameter(Integer.class, "2"));
        cq.where(inExp);

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).setParameter("1", empId1).setParameter("2", emp2.getId()).getResultList();

            assertTrue("Simple select between with parameter test failed", comparer.compareObjects(result, expectedResult));
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
        cq.where(qb.equal(wrapper.get(wrapper.from(cq, Employee.class), Employee_status), Employee.EmployeeStatus.FULL_TIME));
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
        cq.where(qb.equal(wrapper.from(cq, Project.class).type(), LargeProject.class));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("SimpleTypeTest", comparer.compareObjects(result, expectedResult));
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
        Root<Employee> root = wrapper.from(cq, Employee.class);
        cq.select(wrapper.get(root, Employee_firstName));
        cq.orderBy(qb.asc(wrapper.get(root, Employee_firstName)));
        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("SimpleTypeTest", comparer.compareObjects(result, expectedResult));
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
        Root<Employee> root = wrapper.from(cq, Employee.class);
        cq.where(qb.equal(qb.coalesce(wrapper.get(root, Employee_firstName), wrapper.get(root, Employee_lastName)), "Bob"));

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
        Root<Employee> root = wrapper.from(cq, Employee.class);
        Coalesce<String> coalesce = qb.coalesce();
        coalesce.value(wrapper.get(root, Employee_firstName));
        coalesce.value(wrapper.get(root, Employee_lastName));
        coalesce.value("Bobby");
        cq.select(coalesce);

        cq.where(qb.equal(wrapper.get(root, Employee_firstName), "Bob"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Incorrect number of results returned.", result.size() == 1);
            assertTrue("Incorrect Employee returned", result.get(0).equals("Bob"));
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
        Root<Employee> root = wrapper.from(cq, Employee.class);
        Case<String> selectCase = qb.selectCase();
        selectCase.when(qb.equal(wrapper.get(root, Employee_firstName), "Bob"), "Robert");
        selectCase.when(qb.equal(wrapper.get(root, Employee_firstName), "Rob"), "Robbie");
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
        Root<Employee> root = wrapper.from(cq, Employee.class);
        Case<String> selectCase = qb.selectCase();
        selectCase.when(qb.equal(wrapper.get(root, Employee_firstName), "Bob"), "Robert");
        selectCase.when(qb.equal(wrapper.get(root, Employee_firstName), "Rob"), "Robbie");
        selectCase.otherwise("Not Bob");
        cq.select(selectCase);

        cq.where(qb.equal(wrapper.get(root, Employee_firstName), "Bob"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Incorrect number of results returned.", result.size() == 1);
            assertTrue("Incorrect Employee returned", result.get(0).equals("Robert"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void simpleCaseInWhereTest(){
        if ((JUnitTestCase.getServerSession()).getPlatform().isDerby())
        {
            warning("The test simpleCaseInWhereTest is not supported on Derby, because Derby does not support simple CASE");
            return;
        }
        EntityManager em = createEntityManager();
        //select e from Employee e where case e.firstName when 'Bob' then 'Robert' when 'Rob' then 'Robbie' else 'Not Bob' = 'Bob'
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = wrapper.from(cq, Employee.class);
        SimpleCase<Object, String> selectCase = qb.selectCase(wrapper.get(root, Employee_firstName));
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
        if ((JUnitTestCase.getServerSession()).getPlatform().isDerby())
        {
            warning("The test simpleCaseInSelectTest is not supported on Derby, because Derby does not support simple CASE");
            return;
        }
        EntityManager em = createEntityManager();
        //select coalesce(e.firstName, e.lastName) from Employee e where e.firstName = 'Bob'
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Object> cq = qb.createQuery(Object.class);
        Root<Employee> root = wrapper.from(cq, Employee.class);
        SimpleCase<Object, String> selectCase = qb.selectCase(wrapper.get(root, Employee_firstName));
        selectCase.when("Bob", "Robert");
        selectCase.when("Rob", "Robbie");
        selectCase.otherwise("Not Bob");
        cq.select(selectCase);

        cq.where(qb.equal(wrapper.get(root, Employee_firstName), "Bob"));

        beginTransaction(em);
        try {
            List result = em.createQuery(cq).getResultList();

            assertTrue("Incorrect number of results returned.", result.size() == 1);
            assertTrue("Incorrect Employee returned", result.get(0).equals("Robert"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }


    public void largeProjectCastTest() {
        EntityManager em = createEntityManager();

        ReadAllQuery query = new ReadAllQuery();
        Expression selectionCriteria = new ExpressionBuilder().anyOf("projects").treat(LargeProject.class).get("budget").equal(5000);
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(Employee.class);
        query.dontUseDistinct(); //gf 1395 changed jpql to not use distinct on joins

        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        clearCache();

        //"SELECT e from Employee e join cast(e.project, LargeProject) p where p.budget = 1000
        CriteriaBuilder qb1 = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq1 = qb1.createQuery(Employee.class);

        Root<Employee> empRoot = wrapper.from(cq1, Employee.class);
        Join<Employee, Project> join = wrapper.join(empRoot, Employee_projects);
        javax.persistence.criteria.Expression exp = wrapper.get((Path)join.as(LargeProject.class), LargeProject_budget);
        cq1.where(qb1.equal(exp, 5000));
        beginTransaction(em);
        try {
            List result = em.createQuery(cq1).getResultList();
            assertTrue("LargeProject cast failed.", comparer.compareObjects(result, expectedResult));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mapCastTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

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

            em.flush();
            em.clear();
            clearCache();

            ReadAllQuery query = new ReadAllQuery();
            Expression selectionCriteria = new ExpressionBuilder().anyOf("blueBeersToConsume").treat(BlueLight.class).get("discount").equal(10);
            query.setSelectionCriteria(selectionCriteria);
            query.setReferenceClass(BeerConsumer.class);
            query.dontUseDistinct();

            Query jpaQuery = ((org.eclipse.persistence.internal.jpa.EntityManagerImpl)em.getDelegate()).createQuery(query);
            List expectedResult = jpaQuery.getResultList();

            clearCache();
            em.clear();

            //"SELECT e from Employee e join cast(e.project, LargeProject) p where p.budget = 1000
            CriteriaBuilder qb1 = em.getCriteriaBuilder();
            CriteriaQuery<BeerConsumer> cq1 = qb1.createQuery(BeerConsumer.class);

            Root<BeerConsumer> root = wrapper.from(cq1, BeerConsumer.class);
            Join<BeerConsumer, Blue> join = wrapper.join(root, BeerConsumer_blueBeersToConsume);
            javax.persistence.criteria.Expression exp = wrapper.get((Path)join.as(BlueLight.class), BlueLight_discount);
            cq1.where(qb1.equal(exp, 10) );

            List result = em.createQuery(cq1).getResultList();
            assertTrue("LargeProject cast failed.", comparer.compareObjects(result, expectedResult));
        } finally {
            if (isTransactionActive(em)) {
                this.rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void oneToOneCastTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

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

            em.flush();
            em.clear();
            clearCache();

            ReadAllQuery query = new ReadAllQuery();
            Expression selectionCriteria = new ExpressionBuilder().get("car").treat(SportsCar.class).get("maxSpeed").equal(200);
            query.setSelectionCriteria(selectionCriteria);
            query.setReferenceClass(Person.class);
            query.dontUseDistinct();
            //query.setShouldFilterDuplicates(false);
            Query jpaQuery = ((org.eclipse.persistence.internal.jpa.EntityManagerImpl)em.getDelegate()).createQuery(query);
            List expectedResult = jpaQuery.getResultList();

            clearCache();
            em.clear();
            //"SELECT e from Employee e join cast(e.project, LargeProject) p where p.budget = 1000
            CriteriaBuilder qb1 = em.getCriteriaBuilder();
            CriteriaQuery<Person> cq1 = qb1.createQuery(Person.class);

            Root<Person> root = wrapper.from(cq1, Person.class);
            Join<Person, Car> join = wrapper.join(root, Person_car);
            javax.persistence.criteria.Expression exp = wrapper.get((Path)join.as(SportsCar.class), SportsCar_maxSpeed);
            cq1.where(qb1.equal(exp, 200) );

            List result = em.createQuery(cq1).getResultList();
            assertTrue("OneToOne cast failed.", comparer.compareObjects(result, expectedResult));
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Test a Tuple query with a where clause and a conjunction.
     */
    public void testTupleQuery() {
        EntityManager em = createEntityManager();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = qb.createTupleQuery();

        Root<Employee> emp = wrapper.from(criteria, Employee.class);
        javax.persistence.criteria.Expression exp = wrapper.get(emp, Employee_lastName);

        criteria.multiselect(exp, qb.count(exp));
        criteria.where(qb.conjunction());
        criteria.groupBy(exp);

        TypedQuery<Tuple> query = em.createQuery(criteria);

        query.getResultList();
    }

    /**
     * bug 366104 : Tuple.get(int) validation
     */
    public void testTupleIndexValidation() {
        EntityManager em = createEntityManager();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = qb.createTupleQuery();

        Root<Employee> emp = wrapper.from(criteria, Employee.class);
        criteria.multiselect(wrapper.get(emp, Employee_lastName), wrapper.get(emp, Employee_firstName));

        TypedQuery<Tuple> query = em.createQuery(criteria);
        List<Tuple> list = query.getResultList();

        Tuple row = list.get(0);
        try {
            Object result = row.get(-1);
            fail("IllegalArgumentException not thrown when using index of -1 on a Tuple.  returned: "+result);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }

        try {
            row.get(2);
            fail("IllegalArgumentException expected. Array:"+ Arrays.toString(row.toArray()));
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }
    }

    /**
     * bug 366112: test Tuple.get(int, Class) validation
     */
    public void testTupleIndexTypeValidation() {
        EntityManager em = createEntityManager();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = qb.createTupleQuery();

        Root<Employee> emp = wrapper.from(criteria, Employee.class);
        criteria.multiselect(wrapper.get(emp, Employee_lastName), wrapper.get(emp, Employee_firstName));
        criteria.where(qb.isNotNull(wrapper.get(emp, Employee_firstName)));//make sure firstName !=null for type checking

        TypedQuery<Tuple> query = em.createQuery(criteria);
        List<Tuple> list = query.getResultList();

        Tuple row = list.get(0);
        //verify it doesn't throw an exception in a valid case first:
        row.get(0, String.class);

        try {
            Object result = row.get(1, java.sql.Date.class);
            fail("IllegalArgumentException expected using a Date to get a String. Result returned:"+result);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }

        try {
            row.get(2, Object.class);
            fail("IllegalArgumentException expected for index=2. Array:"+ Arrays.toString(row.toArray()));
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }
    }

    /**
     * bug 366179 : test Tuple.get(string) validation
     */
    public void testTupleStringValidation() {
        EntityManager em = createEntityManager();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = qb.createTupleQuery();

        Root<Employee> emp = wrapper.from(criteria, Employee.class);
        criteria.multiselect(wrapper.get(emp, Employee_lastName).alias("lastName"), wrapper.get(emp, Employee_firstName));

        TypedQuery<Tuple> query = em.createQuery(criteria);
        List<Tuple> list = query.getResultList();
        Tuple row = list.get(0);

        //verify it doesn't throw an exception in a valid case first:
        row.get("lastName");
        try {
            Object result = row.get("non-existing-name");
            fail("IllegalArgumentException expected using an invalid value. Result returned:"+result);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }
    }

    /**
     * bug 366179 : test Tuple.get(string, Type) validation
     */
    public void testTupleStringTypeValidation() {
        EntityManager em = createEntityManager();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = qb.createTupleQuery();

        Root<Employee> emp = wrapper.from(criteria, Employee.class);
        criteria.multiselect(wrapper.get(emp, Employee_lastName).alias("lastName"), wrapper.get(emp, Employee_firstName).alias("firstName"));
        criteria.where(qb.isNotNull(wrapper.get(emp, Employee_firstName)));//make sure firstName !=null for type checking

        TypedQuery<Tuple> query = em.createQuery(criteria);
        List<Tuple> list = query.getResultList();
        Tuple row = list.get(0);

        //verify it doesn't throw an exception in a valid case first:
        row.get("lastName", String.class);

        try {
            Object result = row.get("firstName", java.sql.Date.class);
            fail("IllegalArgumentException expected using an invalid value. Result returned:"+result);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }
        closeEntityManager(em);
    }

    //366199 - CriteriaBuilder.tuple(Selection) does not throw IllegalArgumentException
    public void testCriteriaBuilderTupleValidation() {
        EntityManager em = createEntityManager();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cquery = qb.createTupleQuery();

        Root<Employee> emp = wrapper.from(cquery, Employee.class);
        Selection[] s = {wrapper.get(emp, Employee_id), wrapper.get(emp, Employee_lastName), wrapper.get(emp, Employee_firstName)};
        Selection<Tuple> item = qb.tuple(s);
        cquery.select(item);

        TypedQuery<Tuple> query = em.createQuery(cquery);
        //verify they work and can be used:
        List<Tuple> list = query.getResultList();
        list.get(0);
        try {
            //verify tuple throws an exception when passed a tuple
            Object unexpectedResult = qb.tuple(item);
            fail("IllegalArgumentException expected using an invalid value to CriteriaBuilder.tuple(). Result returned:"+unexpectedResult);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }

        try {
            //verify array throws an exception when passed a tuple
            Object unexpectedResult = qb.array(item);
            fail("IllegalArgumentException expected using an invalid value to CriteriaBuilder.array(). Result returned:"+unexpectedResult);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }
        closeEntityManager(em);
    }

    //366206 - CriteriaBuilder.array(Selection)
    public void testCriteriaBuilderArrayValidation() {
        EntityManager em = createEntityManager();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cquery = qb.createQuery(Object[].class);

        Root<Employee> emp = wrapper.from(cquery, Employee.class);
        Selection[] s = {wrapper.get(emp, Employee_id), wrapper.get(emp, Employee_lastName), wrapper.get(emp, Employee_firstName)};
        Selection<Object[]> item = qb.array(s);
        cquery.select(item);

        TypedQuery<Object[]> query = em.createQuery(cquery);
        //verify they work and can be used:
        List<Object[]> list = query.getResultList();
        list.get(0);
        try {
            //verify tuple throws an exception when passed an array
            Object unexpectedResult = qb.tuple(item);
            fail("IllegalArgumentException expected using an invalid value to CriteriaBuilder.tuple(). Result returned:"+unexpectedResult);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }

        try {
            //verify array throws an exception when passed an array
            Object unexpectedResult = qb.array(item);
            fail("IllegalArgumentException expected using an invalid value to CriteriaBuilder.array(). Result returned:"+unexpectedResult);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }
        closeEntityManager(em);
    }

    //366248 - CriteriaBuilder.construct(Class, Selection)
    public void testCriteriaBuilderConstructValidation()
    {
        EntityManager em = createEntityManager();
        Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
        EmployeeDetail expectedResult = new EmployeeDetail(emp.getFirstName(), emp.getLastName());

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<EmployeeDetail> cquery = qb.createQuery(EmployeeDetail.class);

        Root<Employee> root = wrapper.from(cquery, Employee.class);
        Selection[] s = {wrapper.get(root, Employee_firstName), wrapper.get(root, Employee_lastName)};
        Selection<EmployeeDetail> item = qb.construct(EmployeeDetail.class, s);
        cquery.select(item);
        cquery.where(qb.equal(root.get("id"), emp.getId()));

        TypedQuery<EmployeeDetail> query = em.createQuery(cquery);
        //verify the query works:
        List<EmployeeDetail> list = query.getResultList();
        EmployeeDetail result = list.get(0);
        assertEquals("Constructor criteria query Failed", expectedResult, result);

        try {
            //verify construct throws an exception when passed an array
            Object unexpectedResult = qb.construct(EmployeeDetail.class, qb.array(s));
            fail("IllegalArgumentException expected using an invalid value to CriteriaBuilder.tuple(). Result returned:"+unexpectedResult);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }

        try {
            //verify construct throws an exception when passed a tuple
            Object unexpectedResult = qb.construct(EmployeeDetail.class, qb.tuple(s));
            fail("IllegalArgumentException expected using an invalid value to CriteriaBuilder.tuple(). Result returned:"+unexpectedResult);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }

        closeEntityManager(em);
    }

    //366100 - cbuilder.literal(null)
    public void testLiteralValidation() {
        EntityManager em = createEntityManager();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        try {
            Object result = qb.literal(null);
            fail("IllegalArgumentException expected calling literal(null). Result returned:"+result);
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }
        closeEntityManager(em);
    }

    //366386 - IllegalArgumentException for duplicated aliases
    public void testCompoundSelectionAliasValidation() {
        EntityManager em = createEntityManager();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = qb.createTupleQuery();
        Root<Employee> emp = wrapper.from(criteria, Employee.class);
        Selection[] s = {wrapper.get(emp, Employee_lastName).alias("duplicateAlias"), wrapper.get(emp, Employee_firstName).alias("duplicateAlias")};

        try {
            criteria.multiselect(s);
            fail("IllegalArgumentException expected using multiselect on items using duplicate aliases");
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }

        Selection<Tuple> tupleItem = qb.tuple(s);
        try {
            criteria.select(tupleItem);
            fail("IllegalArgumentException expected on select using a Tuple with items using duplicate aliases");
        } catch (Exception iae) {
            assertEquals(iae.getClass(), IllegalArgumentException.class);
        }

        closeEntityManager(em);
    }

    // Bug# 412582 - CriteriaBuilder.construct(...) fails with JoinType.LEFT and null.
    // Employee and HugeProject relation is one to one with no existing HugeProject instances.
    // Those are conditions for bug# 412582 reproduction scenario.
    /**
     * Criteria query result container for <code>testEmptyLeftJoinInCriteriaQuery</code> test.
     */
    public static class EntityDTO {
        public EntityDTO(Employee employee, HugeProject hugeProject) {}
    }

    // Bug# 412582 - CriteriaBuilder.construct(...) fails with JoinType.LEFT and null.
    /**
     * Verify criteria query with left outer join on empty table.
     */
    public void testEmptyLeftJoinInCriteriaQuery() {
        EntityManager em = createEntityManager();

        // Make sure that no HugeProject instance exists.
        TypedQuery<Long> q1 = em.createQuery("SELECT COUNT(hp.id) FROM HugeProject hp", Long.class);
        Long count = q1.getSingleResult();
        assertEquals("There should be no HugeProject", 0, count.intValue());

        // Execute left outer join on empty table as criteria query.
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<EntityDTO> criteriaQuery = builder.createQuery(EntityDTO.class);
        Root<Employee> rootEmployee = wrapper.from(criteriaQuery, Employee.class);
        Join<Employee, HugeProject> joinHugeProject = wrapper.join(rootEmployee, Employee_hugeProject, JoinType.LEFT);
        criteriaQuery.select(builder.construct(EntityDTO.class, rootEmployee, joinHugeProject));

        TypedQuery<EntityDTO> query = em.createQuery(criteriaQuery);
        query.getResultList();
        closeEntityManager(em);
    }

}



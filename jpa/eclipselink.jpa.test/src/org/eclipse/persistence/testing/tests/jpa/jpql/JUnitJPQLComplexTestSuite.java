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

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.PessimisticLock;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.IndirectCollection;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.oc4j.Oc4jPlatform;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Buyer;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee.SalaryRate;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.Man;
import org.eclipse.persistence.testing.models.jpa.advanced.PartnerLinkPopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.advanced.Woman;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.CompositePKTableCreator;
import org.eclipse.persistence.testing.models.jpa.datatypes.DataTypesTableCreator;
import org.eclipse.persistence.testing.models.jpa.datatypes.WrapperTypes;
import org.eclipse.persistence.testing.models.jpa.inherited.Accredidation;
import org.eclipse.persistence.testing.models.jpa.inherited.Becks;
import org.eclipse.persistence.testing.models.jpa.inherited.BecksTag;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Birthday;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.Corona;
import org.eclipse.persistence.testing.models.jpa.inherited.CoronaTag;
import org.eclipse.persistence.testing.models.jpa.inherited.ExpertBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
import org.eclipse.persistence.testing.models.jpa.inherited.TelephoneNumber;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredFunctionDefinition;

/**
 * <p>
 * <b>Purpose</b>: Test complex EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for complex EJBQL functionality
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 */

public class JUnitJPQLComplexTestSuite extends JUnitTestCase
{
    static JUnitDomainObjectComparer comparer;        //the global comparer object used in all tests

    public JUnitJPQLComplexTestSuite()
    {
        super();
    }

    public JUnitJPQLComplexTestSuite(String name)
    {
        super(name);
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown()
    {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLComplexTestSuite");
        suite.addTest(new JUnitJPQLComplexTestSuite("testSetup"));

        List<String> tests = new ArrayList<String>();
        tests.add("compexInTest2");
        tests.add("complexABSTest");
        tests.add("complexABSWithParameterTest");
        tests.add("compexInTest");
        tests.add("complexLengthTest");
        tests.add("complexLikeTest");
        tests.add("complexNotInTest");
        tests.add("complexNotLikeTest");
        tests.add("complexParameterTest");
        tests.add("complexReverseAbsTest");
        tests.add("complexReverseLengthTest");
        tests.add("complexReverseParameterTest");
        tests.add("complexReverseSqrtTest");
        tests.add("complexSqrtTest");
        tests.add("complexStringInTest");
        tests.add("complexStringNotInTest");
        tests.add("complexSubstringTest");
        tests.add("complexLocateTest");
        tests.add("complexNestedOneToManyUsingInClause");
        tests.add("complexUnusedVariableTest");
        tests.add("complexJoinTest");
        tests.add("complexJoinTest2");
        tests.add("complexJoinTest3");
        tests.add("complexMultipleJoinOfSameRelationship");
        tests.add("complexMultipleLeftOuterJoinOfSameRelationship");
        tests.add("complexFetchJoinTest");
        tests.add("complexOneToOneFetchJoinTest");
        tests.add("complexSelectRelationshipTest");
        tests.add("complexConstructorTest");
        tests.add("complexConstructorVariableTest");
        tests.add("complexConstructorRelationshipTest");
        tests.add("complexConstructorEmbeddableTest");
        tests.add("complexConstructorAggregatesTest");
        tests.add("complexConstructorCountOnJoinedVariableTest");
        tests.add("complexConstructorConstantTest");
        tests.add("complexConstructorCaseTest");
        tests.add("complexConstructorMapTest");
        tests.add("complexResultPropertiesTest");
        tests.add("complexInSubqueryTest");
        tests.add("complexExistsTest");
        tests.add("complexNotExistsTest");
        tests.add("complexInSubqueryJoinTest");
        tests.add("complexInSubqueryJoinInTest");
        tests.add("complexMemberOfTest");
        tests.add("complexNotMemberOfTest");
        tests.add("complexNotMemberOfPathTest");
        tests.add("complexMemberOfElementCollectionTest");
        tests.add("complexNavigatingEmbedded");
        tests.add("complexNavigatingTwoLevelOfEmbeddeds");
        tests.add("complexNamedQueryResultPropertiesTest");
        tests.add("complexOuterJoinQuery");

        tests.add("complexInheritanceTest");
        tests.add("complexInheritanceUsingNamedQueryTest");

        tests.add("mapContainerPolicyMapKeyInSelectTest");
        tests.add("mapContainerPolicyMapValueInSelectTest");
        tests.add("mapContainerPolicyMapEntryInSelectTest");
        tests.add("mapContainerPolicyMapKeyInSelectionCriteriaTest");
        tests.add("mapContainerPolicyMapValueInSelectionCriteriaTest");
        tests.add("mappedKeyMapContainerPolicyMapKeyInSelectionCriteriaTest");
        tests.add("mappedKeyMapContainerPolicyMapKeyInSelectTest");
        tests.add("mappedKeyMapContainerPolicyMapEntryInSelectTest");
        tests.add("mappedKeyMapContainerPolicyEmbeddableMapKeyInSelectionCriteriaTest");
        tests.add("mappedKeyMapContainerPolicyElementCollectionSelectionCriteriaTest");
        tests.add("mappedKeyMapContainerPolicyNavigateMapKeyInEntityTest");
        tests.add("mappedKeyMapContainerPolicyNavigateMapKeyInEmbeddableTest");
        tests.add("complexThreeLevelJoinOneTest");
        tests.add("complexThreeLevelJoinManyTest");
        tests.add("complexIndexOfInSelectClauseTest");
        tests.add("complexIndexOfInWhereClauseTest");
        tests.add("complexCoalesceInWhereTest");
        tests.add("complexCoalesceInSelectTest");
        tests.add("complexNullIfInWhereTest");
        tests.add("complexNullIfInSelectTest");
        tests.add("complexSimpleCaseInSelectTest");
        tests.add("complexSimpleCaseInWhereTest");
        tests.add("complexConditionCaseInSelectTest");
        tests.add("complexConditionCaseInWhereTest");
        tests.add("complexConditionCaseInUpdateTest");
        tests.add("complexTypeInParamTest");
        tests.add("complexTypeInTest");
        tests.add("complexTypeParameterTest");
        tests.add("absInSelectTest");
        tests.add("modInSelectTest");
        tests.add("sqrtInSelectTest");
        tests.add("sizeInSelectTest");
        tests.add("mathInSelectTest");
        tests.add("paramNoVariableTest");
        tests.add("mappedContainerPolicyCompoundMapKeyTest");
        tests.add("updateWhereExistsTest");
        tests.add("deleteWhereExistsTest");

        tests.add("caseTypeTest");
        tests.add("variableReferencedOnlyInParameterTest");
        tests.add("standardFunctionCreateQueryTest");
        tests.add("customFunctionNVLTest");
        tests.add("testFuncWithStoredFunc");
        tests.add("testFuncWithMySQLFuncs");
        tests.add("testNestedFUNCs");

        tests.add("testFunctionInSelect");
        tests.add("testFunctionInOrderBy");
        tests.add("testFunctionInGroupBy");
        tests.add("testBrackets");
        tests.add("testComplexBetween");
        tests.add("testComplexLike");
        tests.add("testComplexIn");
        tests.add("testQueryKeys");
        tests.add("complexOneToOneJoinOptimization");
        tests.add("testCountOneToManyQueryKey");
        tests.add("testEnumNullNotNull");
        tests.add("testPessimisticLock");
        tests.add("testAliasedFunction");

        tests.add("testSubselectInGroupBy");
        tests.add("testSubselectInSelect");
        tests.add("testSubselectInFrom");
        tests.add("testParralelFrom");
        tests.add("testGroupByInIn");

        tests.add("testJoinFetchAlias");
        tests.add("testNestedJoinFetch");
        tests.add("testNestedJoinFetchAlias");

        tests.add("testDistinctOrderByEmbedded");
        tests.add("testElementCollection");
        tests.add("testDoubleAggregateManyToMany");
        tests.add("testGroupByHavingFunction");
        if (!isJPA10()) {
            tests.add("testSubSelect");
            tests.add("testSubSelect2");
        }
        tests.add("testOrderPackage");
        tests.add("testSubselectStackOverflow");
        tests.add("testAliasPlus");
        tests.add("testFunctionsWithParameters");
        tests.add("testEmbeddableDistinct");
        tests.add("testSingleEncapsulatedInputParameter");
        tests.add("testObjectIn");
        tests.add("testMemberOf");
        tests.add("testOrderByDistinct");
        tests.add("testJoinFetchWithJoin");
        tests.add("testNestedSubqueries");
        tests.add("testOnClause");
        tests.add("testFUNCTIONWithStoredFunc");
        tests.add("testSQLCast");
        tests.add("testOPERATOR");
        tests.add("testCOLUMN");
        tests.add("testClassNameInFrom");
        tests.add("testLeftParameters");
        tests.add("testCast");
        tests.add("testExtract");
        tests.add("testNullOrdering");
        tests.add("testRegexp");
        tests.add("testUnion");
        tests.add("testComplexPathExpression");

        Collections.sort(tests);
        for (String test : tests) {
            suite.addTest(new JUnitJPQLComplexTestSuite(test));
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

        //create a new PartnerLinkPopulator
        PartnerLinkPopulator partnerLinkPopulator = new PartnerLinkPopulator();
        new AdvancedTableCreator().replaceTables(session);

        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();

        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        //Populate the tables
        employeePopulator.buildExamples();

        //Persist the examples in the database
        employeePopulator.persistExample(session);

        //Populate the tables
        partnerLinkPopulator.buildExamples();

        //Persist the examples in the database
        partnerLinkPopulator.persistExample(session);

        new InheritedTableManager().replaceTables(session);

        new DataTypesTableCreator().replaceTables(session);

        //create stored function when database supports it
        if (supportsStoredFunctions()){
            SchemaManager schema = new SchemaManager(session);
            schema.replaceObject(buildStoredFunction());
        }

        new CompositePKTableCreator().replaceTables(JUnitTestCase.getServerSession());
    }

    public StoredFunctionDefinition buildStoredFunction() {
        StoredFunctionDefinition func = new StoredFunctionDefinition();
        func.setName("StoredFunction_In");
        func.addArgument("P_IN", Long.class);
        func.setReturnType(Long.class);
        func.addStatement("RETURN P_IN * 1000");
        return func;
    }

    public void complexABSTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).lastElement();
        clearCache();
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "(ABS(emp.salary) = ";
        ejbqlString = ejbqlString + emp1.getSalary() + ")";
        ejbqlString = ejbqlString + " OR (ABS(emp.salary) = ";
        ejbqlString = ejbqlString + emp2.getSalary() + ")";

        Vector expectedResult = new Vector();
        expectedResult.add(emp1);
        expectedResult.add(emp2);

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex ABS test failed", comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexABSWithParameterTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
            Employee emp2 = em.merge(emp);
            clearCache();
            Query q = em.createQuery("SELECT emp FROM Employee emp WHERE emp.salary = ABS(:sal)");
            q.setParameter("sal", emp.getSalary());
            List<Employee> result = q.getResultList();
            boolean found = false;
            for (Employee e : result) {
                if (e.equals(emp2)) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue("Complex ABS with parameter test failed", found);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void compexInTest2()
    {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN :param";

        List params = new ArrayList();
        params.add(-1);
        params.add(0);
        params.add(1);
        em.createQuery(ejbqlString).setParameter("param", params)/*.setParameter("param2", 0).setParameter("param3", 1)*/.getResultList();
    }

    public void compexInTest()
    {
        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);
        Employee emp3 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(2);

        Vector expectedResult = new Vector();
        Vector idVector = new Vector();
        idVector.add(emp1.getId());
        idVector.add(emp2.getId());
        idVector.add(emp3.getId());

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("id").in(idVector);
        raq.setSelectionCriteria(whereClause);
        expectedResult = (Vector)getServerSession().executeQuery(raq);
        clearCache();
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id IN (";
        ejbqlString = ejbqlString + emp1.getId().toString() + ", ";
        ejbqlString = ejbqlString + emp2.getId().toString() + ", ";
        ejbqlString = ejbqlString + emp3.getId().toString();
        ejbqlString = ejbqlString + ")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex IN test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexLengthTest()
    {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSQLServer()) {
            getServerSession().logMessage("Warning SQL doesnot support LENGTH function");
            return;
        }

        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
        clearCache();
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "(LENGTH(emp.firstName) = ";
        ejbqlString = ejbqlString + expectedResult.getFirstName().length() + ")";
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + "(LENGTH(emp.lastName) = ";
        ejbqlString = ejbqlString + expectedResult.getLastName().length() + ")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Length test failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexLikeTest()
    {
        EntityManager em = createEntityManager();

        Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();

        String firstName = emp.getFirstName();
        String partialFirstName = emp.getFirstName().substring(0, 1);
        partialFirstName = partialFirstName + "_";
        partialFirstName = partialFirstName + firstName.substring(2, Math.min(4, (firstName.length() - 1)));
        partialFirstName = partialFirstName + "%";

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("firstName").like(partialFirstName);
        raq.setSelectionCriteria(whereClause);
        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);
        clearCache();
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE \"" + partialFirstName + "\"";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Like test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexNotInTest()
    {
        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);
        Employee emp3 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(2);

        ExpressionBuilder builder = new ExpressionBuilder();

        Vector idVector = new Vector();
        idVector.add(emp1.getId());
        idVector.add(emp2.getId());
        idVector.add(emp3.getId());

        Expression whereClause = builder.get("id").notIn(idVector);

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);
        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id NOT IN (";
        ejbqlString = ejbqlString + emp1.getId().toString() + ", ";
        ejbqlString = ejbqlString + emp2.getId().toString() + ", ";
        ejbqlString = ejbqlString + emp3.getId().toString();
        ejbqlString = ejbqlString + ")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Not IN test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexNotLikeTest()
    {
        EntityManager em = createEntityManager();

        Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();

        String firstName = emp.getFirstName();
        String partialFirstName = emp.getFirstName().substring(0, 1);
        partialFirstName = partialFirstName + "_";
        partialFirstName = partialFirstName + firstName.substring(2, Math.min(4, (firstName.length() - 1)));
        partialFirstName = partialFirstName + "%";

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").notLike(partialFirstName);

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);
        clearCache();
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName NOT LIKE \"" + partialFirstName + "\"";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Not LIKE test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexParameterTest()
    {
        EntityManager em = createEntityManager();

        Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();

        String firstName = "firstName";
        String lastName = "lastName";

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get(firstName).equal(builder.getParameter(firstName));
        whereClause = whereClause.and(builder.get(lastName).equal(builder.getParameter(lastName)));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.addArgument(firstName);
        raq.addArgument(lastName);

        Vector parameters = new Vector();
        parameters.add(emp.getFirstName());
        parameters.add(emp.getLastName());

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq, parameters);
        clearCache();

        emp = (Employee)expectedResult.firstElement();

        // Set up the EJBQL using the retrieved employees
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.firstName = ?1 ";
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + "emp.lastName = ?2";

        List result = em.createQuery(ejbqlString).setParameter(1,emp.getFirstName()).setParameter(2,emp.getLastName()).getResultList();

        Assert.assertTrue("Complex Paramter test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexReverseAbsTest()
    {
       EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);
        clearCache();
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + emp1.getSalary();
        ejbqlString = ejbqlString + " = ABS(emp.salary)";
        ejbqlString = ejbqlString + " OR ";
        ejbqlString = ejbqlString + emp2.getSalary();
        ejbqlString = ejbqlString + " = ABS(emp.salary)";

        Vector expectedResult = new Vector();
        expectedResult.add(emp1);
        expectedResult.add(emp2);

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex reverse ABS test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexReverseLengthTest()
    {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSQLServer()) {
            getServerSession().logMessage("Warning SQL doesnot support LENGTH function");
            return;
        }

        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee) getServerSession().readAllObjects(Employee.class).firstElement();
        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + expectedResult.getFirstName().length();
        ejbqlString = ejbqlString + " = LENGTH(emp.firstName)";
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + expectedResult.getLastName().length();
        ejbqlString = ejbqlString + " = LENGTH(emp.lastName)";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex reverse Length test failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexReverseParameterTest()
    {
        EntityManager em = createEntityManager();

        Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();

        String firstName = "firstName";
        String lastName = "lastName";

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get(firstName).equal(builder.getParameter(firstName));
        whereClause = whereClause.and(builder.get(lastName).equal(builder.getParameter(lastName)));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.addArgument(firstName);
        raq.addArgument(lastName);

        Vector parameters = new Vector();
        parameters.add(emp.getFirstName());
        parameters.add(emp.getLastName());

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq, parameters);

        clearCache();

        emp = (Employee)expectedResult.firstElement();

        // Set up the EJBQL using the retrieved employees
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "?1 = emp.firstName";
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + "?2 = emp.lastName";

        List result = em.createQuery(ejbqlString).setParameter(1,emp.getFirstName()).setParameter(2,emp.getLastName()).getResultList();

        Assert.assertTrue("Complex Reverse Paramter test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexReverseSqrtTest()
    {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test complexReverseSqrtTest skipped for this platform, "
                    + "Symfoware doesn't support SQRT, COS, SIN, TAN functions.");
            return;
        }
        EntityManager em = createEntityManager();

        ReadAllQuery raq = new ReadAllQuery();
        ExpressionBuilder expbldr = new ExpressionBuilder();
        Expression whereClause1 = expbldr.get("lastName").equal("TestCase1");
        Expression whereClause2 = expbldr.get("lastName").equal("TestCase2");
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause1.or(whereClause2));

        Vector expectedResult = (Vector) getServerSession().executeQuery(raq);

        clearCache();

        Employee emp1 = (Employee) expectedResult.elementAt(0);
        Employee emp2 = (Employee) expectedResult.elementAt(1);

        double salarySquareRoot1 = Math.sqrt((new Double(emp1.getSalary()).doubleValue()));
        double salarySquareRoot2 = Math.sqrt((new Double(emp2.getSalary()).doubleValue()));

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + salarySquareRoot1;
        ejbqlString = ejbqlString + " = SQRT(emp.salary)";
        ejbqlString = ejbqlString + " OR ";
        ejbqlString = ejbqlString + salarySquareRoot2;
        ejbqlString = ejbqlString + " = SQRT(emp.salary)";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Reverse Square Root test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexSqrtTest()
    {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test complexSqrtTest skipped for this platform, "
                    + "Symfoware doesn't support SQRT, COS, SIN, TAN functions.");
            return;
        }
        EntityManager em = createEntityManager();

        ReadAllQuery raq = new ReadAllQuery();
        ExpressionBuilder expbldr = new ExpressionBuilder();
        Expression whereClause1 = expbldr.get("lastName").equal("TestCase1");
        Expression whereClause2 = expbldr.get("lastName").equal("TestCase2");
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause1.or(whereClause2));

        Vector expectedResult = (Vector) getServerSession().executeQuery(raq);

        clearCache();

        Employee emp1 = (Employee) expectedResult.elementAt(0);
        Employee emp2 = (Employee) expectedResult.elementAt(1);

        double salarySquareRoot1 = Math.sqrt((new Double(emp1.getSalary()).doubleValue()));
        double salarySquareRoot2 = Math.sqrt((new Double(emp2.getSalary()).doubleValue()));

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "(SQRT(emp.salary) = ";
        ejbqlString = ejbqlString + salarySquareRoot1 + ")";
        ejbqlString = ejbqlString + " OR ";
        ejbqlString = ejbqlString + "(SQRT(emp.salary) = ";
        ejbqlString = ejbqlString + salarySquareRoot2 + ")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Square Root test failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexStringInTest()
    {
        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);
        Employee emp3 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(2);

        Vector fnVector = new Vector();
        fnVector.add(emp1.getFirstName());
        fnVector.add(emp2.getFirstName());
        fnVector.add(emp3.getFirstName());

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("firstName").in(fnVector);
        raq.setSelectionCriteria(whereClause);
        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName IN (";
        ejbqlString = ejbqlString + "\"" + emp1.getFirstName() + "\"" + ", ";
        ejbqlString = ejbqlString + "\"" + emp2.getFirstName() + "\"" + ", ";
        ejbqlString = ejbqlString + "\"" + emp3.getFirstName() + "\"" ;
        ejbqlString = ejbqlString + ")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex String In test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexStringNotInTest()
    {
        EntityManager em = createEntityManager();

        Employee emp1 = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();
        Employee emp2 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(1);
        Employee emp3 = (Employee)getServerSession().readAllObjects(Employee.class).elementAt(2);


        ExpressionBuilder builder = new ExpressionBuilder();

        Vector nameVector = new Vector();
        nameVector.add(emp1.getFirstName());
        nameVector.add(emp2.getFirstName());
        nameVector.add(emp3.getFirstName());


        Expression whereClause = builder.get("firstName").notIn(nameVector);
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector expectedResult = (Vector)getServerSession().executeQuery(raq);

        clearCache();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName NOT IN (";
        ejbqlString = ejbqlString + "\"" + emp1.getFirstName() + "\"" + ", ";
        ejbqlString = ejbqlString + "\"" + emp2.getFirstName() + "\"" + ", ";
        ejbqlString = ejbqlString + "\"" + emp3.getFirstName() + "\"" ;
        ejbqlString = ejbqlString + ")";

         List result = em.createQuery(ejbqlString).getResultList();

         Assert.assertTrue("Complex String Not In test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexSubstringTest()
    {
        EntityManager em = createEntityManager();

        Employee expectedResult = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();

        String firstNamePart, lastNamePart;
        String ejbqlString;

        firstNamePart = expectedResult.getFirstName().substring(0, 2);

        lastNamePart = expectedResult.getLastName().substring(0, 1);

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "(SUBSTRING(emp.firstName, 1, 2) = ";//changed from 0, 2 to 1, 2(ZYP)
        ejbqlString = ejbqlString + "\"" + firstNamePart + "\")";
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + "(SUBSTRING(emp.lastName, 1, 1) = ";//changed from 0, 1 to 1, 1(ZYP)
        ejbqlString = ejbqlString + "\"" + lastNamePart + "\")";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Sub String test failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexLocateTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        String jpql = "SELECT e FROM Employee e WHERE e.firstName = 'Emanual' AND e.lastName = 'Smith'";
        Employee expectedResult = (Employee)em.createQuery(jpql).getSingleResult();

        jpql = "SELECT e FROM Employee e WHERE LOCATE('manual', e.firstName) = 2 AND e.lastName = 'Smith'";
        Employee result = (Employee)em.createQuery(jpql).getSingleResult();
        Assert.assertTrue("Complex LOCATE(String, String) test failed", result.equals(expectedResult));

        jpql = "SELECT e FROM Employee e WHERE LOCATE('a', e.firstName, 4) = 6 AND e.lastName = 'Smith'";
        result = (Employee)em.createQuery(jpql).getSingleResult();
        Assert.assertTrue("Complex LOCATE(String, String) test failed", result.equals(expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexNestedOneToManyUsingInClause()
    {
        EntityManager em = createEntityManager();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.anyOf("managedEmployees").anyOf("projects").
            get("name").equal("Enterprise");
        ReadAllQuery readQuery = new ReadAllQuery();
        readQuery.dontMaintainCache();
        readQuery.setReferenceClass(Employee.class);
        readQuery.setSelectionCriteria(whereClause);

        List expectedResult = (List)getServerSession().executeQuery(readQuery);

        clearCache();

        String ejbqlString;
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp, " +
            "IN(emp.managedEmployees) mEmployees, IN(mEmployees.projects) projects " +
            "WHERE projects.name = 'Enterprise'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Nested One To Many Using In Clause test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexUnusedVariableTest()
    {
        EntityManager em = createEntityManager();

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.dontMaintainCache();
        reportQuery.setShouldReturnWithoutReportQueryResult(true);
        reportQuery.setReferenceClass(Employee.class);
        ExpressionBuilder builder = reportQuery.getExpressionBuilder();
        reportQuery.addNonFetchJoinedAttribute(builder.get("address"));
        reportQuery.addItem("emp", builder);
        Vector expectedResult = (Vector)getServerSession().executeQuery(reportQuery);

        clearCache();

        String ejbqlString;
        ejbqlString = "SELECT emp FROM Employee emp JOIN emp.address a";
        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Unused Variable test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexJoinTest()
    {
        EntityManager em = createEntityManager();
        Collection emps = getServerSession().readAllObjects(Employee.class);
        Employee empWithManager = null;
        Employee empWithOutManager = null;
        // find an employee w/ and w/o manager
        for (Iterator i = emps.iterator(); i.hasNext();) {
            Employee e = (Employee)i.next();
            Employee manager = e.getManager();
            if (manager != null) {
                if (empWithManager == null) {
                    empWithManager = e;
                }
            } else {
                if (empWithOutManager == null) {
                    empWithOutManager = e;
                }
            }
            if ((empWithManager != null) && (empWithOutManager != null)) {
                break;
            }
        }

        // Select the related manager of empWithOutManager and empWithManager
        // This should return empWithManager's manager, because the manager
        // identification variable m is defined as inner join
        String ejbqlString = "SELECT m FROM Employee emp JOIN emp.manager m WHERE emp.id IN (:id1, :id2)";
        Query query = em.createQuery(ejbqlString);
        query.setParameter("id1", empWithOutManager.getId());
        query.setParameter("id2", empWithManager.getId());
        List result = query.getResultList();
        List expectedResult = Arrays.asList(new Employee[] {empWithManager.getManager()});
        Assert.assertTrue("Complex Join test failed", comparer.compareObjects(result, expectedResult));

        // Select the related manager of empWithOutManager and empWithManager
        // This should return empWithManager's manager, because the manager
        // identification variable m is defined as outer join
        ejbqlString = "SELECT m FROM Employee emp LEFT OUTER JOIN emp.manager m WHERE emp.id IN (:id1, :id2)";
        query = em.createQuery(ejbqlString);
        query.setParameter("id1", empWithOutManager.getId());
        query.setParameter("id2", empWithManager.getId());
        result = query.getResultList();
        expectedResult = Arrays.asList(new Employee[] {empWithManager.getManager(), null});
        Assert.assertTrue("Complex Join test failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexJoinTest2()
    {
        EntityManager em = createEntityManager();
        Collection emps = getServerSession().readAllObjects(Employee.class);
        Employee empWithManager = null;
        Employee empWithOutManager = null;
        // find an employee w/ and w/o manager
        for (Iterator i = emps.iterator(); i.hasNext();) {
            Employee e = (Employee)i.next();
            Employee manager = e.getManager();
            if (manager != null) {
                if (empWithManager == null) {
                    empWithManager = e;
                }
            } else {
                if (empWithOutManager == null) {
                    empWithOutManager = e;
                }
            }
            if ((empWithManager != null) && (empWithOutManager != null)) {
                break;
            }
        }

        // Select the related manager of empWithOutManager and empWithManager
        // This should return empWithManager's manager, because the manager
        // identification variable m is defined as inner join
        String ejbqlString = "SELECT m FROM Employee emp JOIN emp.manager m WHERE emp.id IN (:id1, :id2)";
        Query query = em.createQuery(ejbqlString);
        query.setMaxResults(5);
        query.setParameter("id1", empWithOutManager.getId());
        query.setParameter("id2", empWithManager.getId());
        List result = query.getResultList();
        List expectedResult = Arrays.asList(new Employee[] {empWithManager.getManager()});
        Assert.assertTrue("Complex Join test failed", comparer.compareObjects(result, expectedResult));

        // Select the related manager of empWithOutManager and empWithManager
        // This should return empWithManager's manager, because the manager
        // identification variable m is defined as outer join
        ejbqlString = "SELECT m FROM Employee emp LEFT OUTER JOIN emp.manager m WHERE emp.id IN (:id1, :id2)";
        query = em.createQuery(ejbqlString);
        query.setMaxResults(5);
        query.setParameter("id1", empWithOutManager.getId());
        query.setParameter("id2", empWithManager.getId());
        result = query.getResultList();
        expectedResult = Arrays.asList(new Employee[] {empWithManager.getManager(), null});
        Assert.assertTrue("Complex Join test failed", comparer.compareObjects(result, expectedResult));
    }

    /**
     * Test that query cloning that occurs when max results is set works correctly.
     */
    public void complexJoinTest3()
    {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT e FROM Employee e");
        List expectedResult = query.getResultList();
        for (Iterator iterator = expectedResult.iterator(); iterator.hasNext(); ) {
            Employee employee = (Employee)iterator.next();
            if (employee.getManager() == null) {
                iterator.remove();
            }
        }

        query = em.createQuery("SELECT e FROM Employee e join e.manager m");
        query.setMaxResults(500);
        List result = query.getResultList();
        if (expectedResult.size() != result.size()) {
            fail("Join did not filter employees without managers:" + result);
        }

        query = em.createQuery("SELECT e.id FROM Employee e join e.manager m");
        query.setMaxResults(500);
        result = query.getResultList();
        if (expectedResult.size() != result.size()) {
            fail("Join did not filter employees without managers:" + result);
        }

        // These used to trigger SQL errors.
        query = em.createQuery("SELECT count(p.number) FROM PhoneNumber p group by p.owner.id");
        query.setMaxResults(500);
        result = query.getResultList();
        query = em.createQuery("SELECT count(e.id) FROM Employee e group by e.address.ID");
        query.setMaxResults(500);
        result = query.getResultList();
    }

    /**
     * glassfish issue 2867
     */
    public void complexMultipleJoinOfSameRelationship()
    {
        EntityManager em = createEntityManager();
        String jpql = "SELECT p1, p2 FROM Employee emp JOIN emp.phoneNumbers p1 JOIN emp.phoneNumbers p2 " +
                      "WHERE p1.type = 'Pager' AND p2.areaCode = '613'";
        Query query = em.createQuery(jpql);
        Object[] result = (Object[]) query.getSingleResult();
        Assert.assertTrue("Complex multiple JOIN of same relationship test failed",
                          (result[0] != result[1]));
    }

    /**
     * glassfish issue 3580
     */
    public void complexMultipleLeftOuterJoinOfSameRelationship()
    {
        EntityManager em = createEntityManager();
        String jpql = "SELECT p1, p2 FROM Employee emp LEFT JOIN emp.phoneNumbers p1 LEFT JOIN emp.phoneNumbers p2 " +
                      "WHERE p1.type = 'Pager' AND p2.areaCode = '613'";
        Query query = em.createQuery(jpql);
        Object[] result = (Object[]) query.getSingleResult();
        Assert.assertTrue("Complex multiple JOIN of same relationship test failed",
                          (result[0] != result[1]));
    }

    public void complexFetchJoinTest()
    {
        EntityManager em = createEntityManager();

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.dontMaintainCache();
        reportQuery.setShouldReturnWithoutReportQueryResult(true);
        reportQuery.setReferenceClass(Employee.class);
        ExpressionBuilder builder = reportQuery.getExpressionBuilder();
        List joins = new ArrayList(1);
        joins.add(builder.get("address"));
        reportQuery.addItem("emp", builder, joins);
        Vector expectedResult = (Vector)getServerSession().executeQuery(reportQuery);

        clearCache();

        String ejbqlString;
        ejbqlString = "SELECT emp FROM Employee emp JOIN FETCH emp.address";
        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Fetch Join test failed", comparer.compareObjects(result, expectedResult));

    }

    /**
     * Testing glassfish issue 2881
     */
    public void complexOneToOneFetchJoinTest()
    {
        EntityManager em = createEntityManager();

        List<Man> allMen = getServerSession().readAllObjects(Man.class);
        List<Integer> allMenIds = new ArrayList(allMen.size());
        for (Man man : allMen) {
            allMenIds.add((man != null) ? man.getId() : null);
        }
        Collections.sort(allMenIds);
        clearCache();

        String ejbqlString = "SELECT m FROM Man m LEFT JOIN FETCH m.partnerLink";
        List<Man> result = em.createQuery(ejbqlString).getResultList();
        List<Integer> ids = new ArrayList(result.size());
        for (Man man : result) {
            ids.add((man != null) ? man.getId() : null);
        }
        Collections.sort(ids);

        // compare ids, because comparer does not know class Man
        Assert.assertEquals("Complex OneToOne Fetch Join test failed",
                            allMenIds, ids);
    }

    public void complexSelectRelationshipTest()
    {
        if (isOnServer()) {
            // Cannot create parallel entity managers in the server.
            return;
        }
        JpaEntityManager em = (JpaEntityManager) createEntityManager();
        Collection emps = em.getActiveSession().readAllObjects(Employee.class);
        Employee empWithManager = null;
        Employee empWithOutManager = null;
        // find an employee w/ and w/o manager
        for (Iterator i = emps.iterator(); i.hasNext();) {
            Employee e = (Employee)i.next();
            Employee manager = e.getManager();
            if (manager != null) {
                if (empWithManager == null) {
                    empWithManager = e;
                }
            } else {
                if (empWithOutManager == null) {
                    empWithOutManager = e;
                }
            }
            if ((empWithManager != null) && (empWithOutManager != null)) {
                break;
            }
        }

        // constructor query including relationship field
        String ejbqlString = "SELECT emp.manager FROM Employee emp WHERE emp.id = :id";
        Query query = em.createQuery(ejbqlString);

        // execute query using employee with manager
        query.setParameter("id", empWithManager.getId());
        Employee result = (Employee)query.getSingleResult();
        Assert.assertEquals("Select Relationship Test Case Failed (employee with manager)",
                            empWithManager.getManager(), result);

        // execute query using employee with manager
        query.setParameter("id", empWithOutManager.getId());
        result = (Employee)query.getSingleResult();
        Assert.assertNull("Select Relationship Test Case Failed (employee without manager)",
                          result);
    }

    public void complexConstructorTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = (Employee)getServerSession().readAllObjects(Employee.class).firstElement();

        // simple constructor query
        String ejbqlString = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail(emp.firstName, emp.lastName) FROM Employee emp WHERE emp.id = :id";
        Query query = em.createQuery(ejbqlString);
        query.setParameter("id", emp.getId());
        EmployeeDetail result = (EmployeeDetail)query.getSingleResult();
        EmployeeDetail expectedResult = new EmployeeDetail(emp.getFirstName(), emp.getLastName());

        Assert.assertTrue("Constructor Test Case Failed", result.equals(expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);

    }

    public void complexConstructorVariableTest()
    {
        if (isOnServer()) {
            // Not work on the server.
            return;
        }
        JpaEntityManager em = (JpaEntityManager) createEntityManager();

        Employee emp = (Employee)em.getActiveSession().readAllObjects(Employee.class).firstElement();

        // constructor query using a variable as argument
        String jpqlString = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail(emp) FROM Employee emp WHERE emp.id = :id";
        Query query = em.createQuery(jpqlString);
        query.setParameter("id", emp.getId());
        EmployeeDetail result = (EmployeeDetail)query.getSingleResult();
        EmployeeDetail expectedResult = new EmployeeDetail(emp);

        Assert.assertTrue("Constructor with variable argument Test Case Failed", result.equals(expectedResult));
    }

    public void complexConstructorRelationshipTest()
    {
        if (isOnServer()) {
            // Cannot create parallel entity managers in the server.
            return;
        }
        JpaEntityManager em = (JpaEntityManager) createEntityManager();

        Collection emps = em.getActiveSession().readAllObjects(Employee.class);
        Employee empWithManager = null;
        Employee empWithOutManager = null;
        // find an employee w/ and w/o manager
        for (Iterator i = emps.iterator(); i.hasNext();) {
            Employee e = (Employee)i.next();
            Employee manager = e.getManager();
            if (manager != null) {
                if (empWithManager == null) {
                    empWithManager = e;
                }
            } else {
                if (empWithOutManager == null) {
                    empWithOutManager = e;
                }
            }
            if ((empWithManager != null) && (empWithOutManager != null)) {
                break;
            }
        }

        // constructor query including relationship field
        String ejbqlString = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail(emp.firstName, emp.lastName, emp.manager) FROM Employee emp WHERE emp.id = :id";
        Query query = em.createQuery(ejbqlString);

        // execute query using employee with manager
        query.setParameter("id", empWithManager.getId());
        EmployeeDetail result = (EmployeeDetail)query.getSingleResult();
        EmployeeDetail expectedResult = new EmployeeDetail(
            empWithManager.getFirstName(), empWithManager.getLastName(),
            empWithManager.getManager());
        Assert.assertTrue("Constructor Relationship Test Case Failed (employee with manager)",
                          result.equals(expectedResult));

        // execute query using employee with manager
        query.setParameter("id", empWithOutManager.getId());
        result = (EmployeeDetail)query.getSingleResult();
        expectedResult = new EmployeeDetail(
            empWithOutManager.getFirstName(), empWithOutManager.getLastName(),
            empWithOutManager.getManager());
        Assert.assertTrue("Constructor Relationship Test Case Failed (employee without manager)",
                          result.equals(expectedResult));
    }

    public void complexConstructorAggregatesTest()
    {
        EntityManager em = createEntityManager();

        Collection emps = getServerSession().readAllObjects(Employee.class);
        Employee emp = null;
        // find an employee with managed employees
        for (Iterator i = emps.iterator(); i.hasNext();) {
            Employee e = (Employee)i.next();
            Collection managed = e.getManagedEmployees();
            if ((managed != null) && (managed.size() > 0)) {
                emp = e;
                break;
            }
        }

        // constructor query using aggregates
        String ejbqlString = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.LongHolder(SUM(emp.salary), COUNT(emp)) FROM Employee emp WHERE emp.manager.id = :id";
        Query query = em.createQuery(ejbqlString);
        query.setParameter("id", emp.getId());
        LongHolder result = (LongHolder)query.getSingleResult();

        // calculate expected result
        Collection managed = emp.getManagedEmployees();
        int count = 0;
        int sum = 0;
        if (managed != null) {
            count = managed.size();
            for (Iterator i = managed.iterator(); i.hasNext();) {
                Employee e = (Employee)i.next();
                sum += e.getSalary();
            }
        }
        LongHolder expectedResult = new LongHolder(new Long(sum), new Long(count));

        Assert.assertTrue("Constructor with aggregates argument Test Case Failed", result.equals(expectedResult));
    }

    public void complexConstructorCountOnJoinedVariableTest()
    {
        EntityManager em = createEntityManager();

        // find all employees with managed employees
        Collection emps = getServerSession().readAllObjects(Employee.class);
        List<EmployeeDetail> expectedResult = new ArrayList<EmployeeDetail>();
        for (Iterator i = emps.iterator(); i.hasNext();) {
            Employee e = (Employee)i.next();
            Collection managed = e.getManagedEmployees();
            if ((managed != null) && (managed.size() > 0)) {
                EmployeeDetail d = new EmployeeDetail(
                    e.getFirstName(), e.getLastName(), new Long(managed.size()));
                expectedResult.add(d);
            }
        }

        // constructor query using aggregates
        String jpql = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail(emp.firstName, emp.lastName, COUNT(m)) FROM Employee emp JOIN emp.managedEmployees m GROUP BY emp.firstName, emp.lastName";
        Query query = em.createQuery(jpql);
        List<EmployeeDetail> result = query.getResultList();

        Assert.assertTrue("complexConstructorCountOnJoinedVariableTest Failed",
                          comparer.compareObjects(result, expectedResult));
    }

    public void complexConstructorConstantTest()
    {
        if (isOnServer()) {
            // Not work on the server.
            return;
        }
        JpaEntityManager em = (JpaEntityManager) createEntityManager();

        Employee emp = (Employee)em.getActiveSession().readAllObjects(Employee.class).firstElement();

        // constructor query using a constant as an argument
        String jpqlString = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail(emp.firstName, 'Ott') FROM Employee emp WHERE emp.id = :id";
        Query query = em.createQuery(jpqlString);
        query.setParameter("id", emp.getId());
        EmployeeDetail result = (EmployeeDetail)query.getSingleResult();
        EmployeeDetail expectedResult = new EmployeeDetail(emp.getFirstName(), "Ott");

        Assert.assertTrue("complexConstructorConstantTest Failed", result.equals(expectedResult));
    }

    public void complexConstructorCaseTest()
    {
        if (isOnServer()) {
            // Not work on the server.
            return;
        }
        if (((Session) JUnitTestCase.getServerSession()).getPlatform().isDerby())
        {
            warning("The test simpleCaseInWhereTest is not supported on Derby, because Derby does not support simple CASE");
            return;
        }
        JpaEntityManager em = (JpaEntityManager) createEntityManager();
        Expression exp = (new ExpressionBuilder()).get("firstName").equal("Bob");
        Employee emp = (Employee)em.getActiveSession().readAllObjects(Employee.class, exp).firstElement();

        // constructor query using a case statement as an argument
        String jpqlString = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail(case emp.firstName when 'Bob' then 'Robert' else '' end, 'Ott') FROM Employee emp WHERE emp.id = :id";
        Query query = em.createQuery(jpqlString);
        query.setParameter("id", emp.getId());
        EmployeeDetail result = (EmployeeDetail)query.getSingleResult();
        EmployeeDetail expectedResult = new EmployeeDetail("Robert", "Ott");

        Assert.assertTrue("complexConstructorCaseTest Failed", result.equals(expectedResult));
    }

    //303703 - SELECT NEW query yields NoSuchMethodException for embedded
    public void complexConstructorEmbeddableTest()
    {
        if (isOnServer()) {
            // does not work on the server.
            return;
        }
        JpaEntityManager em = (JpaEntityManager) createEntityManager();

        Employee emp = (Employee)em.getActiveSession().readAllObjects(Employee.class).firstElement();

        String jpqlString = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail(emp.period) FROM Employee emp WHERE emp.id = :id";
        Query query = em.createQuery(jpqlString);
        query.setParameter("id", emp.getId());
        EmployeeDetail result = (EmployeeDetail)query.getSingleResult();
        EmployeeDetail expectedResult = new EmployeeDetail(emp.getPeriod());
        Assert.assertTrue("Constructor with Embeddable parameter Test Case Failed to return", result.equals(expectedResult));
    }

    public void complexConstructorMapTest()
    {
        if (isOnServer()) {
            // Not work on the server.
            return;
        }
        JpaEntityManager em = (JpaEntityManager) createEntityManager();

        beginTransaction(em);
        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        em.flush();


        // constructor query using a map key
        String jpqlString = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail('Mel', 'Ott', Key(b)) FROM BeerConsumer bc join bc.blueBeersToConsume b";
        Query query = em.createQuery(jpqlString);
        EmployeeDetail result = (EmployeeDetail)query.getSingleResult();
        EmployeeDetail expectedResult = new EmployeeDetail("Mel", "Ott", BigInteger.ONE);

        rollbackTransaction(em);
        Assert.assertTrue("Constructor with variable argument Test Case Failed", result.equals(expectedResult));
    }

    public void complexResultPropertiesTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        String ejbql = "SELECT e FROM Employee e ORDER BY e.id";
        Query query = em.createQuery(ejbql);
        List allEmps = query.getResultList();
        int nrOfEmps = allEmps.size();
        List result = null;
        List expectedResult = null;
        int firstResult = 2;
        int maxResults = nrOfEmps - 1;

        // Test setFirstResult
        query = em.createQuery(ejbql);
        query.setFirstResult(firstResult);
        result = query.getResultList();
        expectedResult = allEmps.subList(firstResult, nrOfEmps);
        Assert.assertTrue("Query.setFirstResult Test Case Failed", result.equals(expectedResult));

        // Test setMaxResults
        query = em.createQuery(ejbql);
        query.setMaxResults(maxResults);
        result = query.getResultList();
        expectedResult = allEmps.subList(0, maxResults);
        Assert.assertTrue("Query.setMaxResult Test Case Failed", result.equals(expectedResult));

        // Test setFirstResult and setMaxResults
        maxResults = nrOfEmps - firstResult - 1;
        query = em.createQuery(ejbql);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        result = query.getResultList();
        expectedResult = allEmps.subList(firstResult, nrOfEmps - 1);
        Assert.assertTrue(
                "Query.setFirstResult and Query.setMaxResults Test Case Failed (result="
                        + result + "; expected=" + expectedResult + ")", result
                        .equals(expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexNamedQueryResultPropertiesTest()
    {
        //This new added test case is for glassFish bug 2689
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Query query = em.createNamedQuery("findAllEmployeesOrderById");
        List allEmps = query.getResultList();
        int nrOfEmps = allEmps.size();
        int firstResult = 2;

        // Test case 0.  MaxResults
        Query query1 = em.createNamedQuery("findAllEmployeesOrderById");
        query1.setMaxResults(nrOfEmps - 2);
        List result1 = query1.getResultList();
        List expectedResult1 = allEmps.subList(0, nrOfEmps - 2);
        Assert.assertTrue("Query1 set  MaxResults Test Case Failed", result1.equals(expectedResult1));

        // Test case 1. setFirstResult and MaxResults
        query1.setFirstResult(firstResult);
        query1.setMaxResults(nrOfEmps - 1);
        result1 = query1.getResultList();
        expectedResult1 = allEmps.subList(firstResult, nrOfEmps);
        Assert.assertTrue(
                "Query1 set FirstResult and MaxResults Test Case Failed (result="
                        + result1 + "; expected=" + expectedResult1 + ")", result1
                        .equals(expectedResult1));

        // Test case 2. The expected result should be exactly same as test case 1
        // since the firstResult and maxresults setting keep unchange.
        result1 = query1.getResultList();
        expectedResult1= allEmps.subList(firstResult, nrOfEmps);
        Assert.assertTrue("Query1 without setting Test Case Failed", result1.equals(expectedResult1));

        // Test case 3. The FirstResult and MaxResults are changed for same query1.
        query1.setFirstResult(1);
        query1.setMaxResults(nrOfEmps - 2);
        result1 = query1.getResultList();
        expectedResult1 = allEmps.subList(1, nrOfEmps-1);
        Assert.assertTrue("Query1.setFirstResult Test Case Failed", result1.equals(expectedResult1));


        // Test case 4. Create new query2, the query2 setting should be nothing to do
        // with query1's. In this case, query2 should use default values.
        Query query2 = em.createNamedQuery("findAllEmployeesOrderById");
        List result2 = query2.getResultList();
        List expectedResult2 = allEmps.subList(0, nrOfEmps);
        Assert.assertTrue("Query2 without setting", result2.equals(expectedResult2));

        // Test case 5. Create query3, only has FirstResult set as zero. the maxReults use
        // default value.
        Query query3 = em.createNamedQuery("findAllEmployeesOrderById");
        query3.setFirstResult(0);
        List result3 = query3.getResultList();
        List expectedResult3 = allEmps.subList(0, nrOfEmps);
        Assert.assertTrue("Query3.set FirstResult and MaxResults Test Case Failed", result3.equals(expectedResult3));

        //Test case 6. Create query 4. firstResult should use default one.
        Query query4 = em.createNamedQuery("findAllEmployeesOrderById");
        query4.setMaxResults(nrOfEmps-3);
        List result4 = query4.getResultList();
        List expectedResult4 = allEmps.subList(0, nrOfEmps-3);
        Assert.assertTrue("Query4 set MaxResult only Test Case Failed", result4.equals(expectedResult4));
        rollbackTransaction(em);
        closeEntityManager(em);

    }

    public void complexInSubqueryTest()
    {
        EntityManager em = createEntityManager();

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.dontMaintainCache();
        reportQuery.setShouldReturnWithoutReportQueryResult(true);
        reportQuery.setReferenceClass(Employee.class);
        ExpressionBuilder builder = reportQuery.getExpressionBuilder();
        reportQuery.setSelectionCriteria(builder.get("address").get("city").equal("Ottawa"));
        reportQuery.addItem("id", builder.get("id"));
        Vector expectedResult = (Vector)getServerSession().executeQuery(reportQuery);

        String ejbqlString = "SELECT e.id FROM Employee e WHERE e.address.city IN (SELECT a.city FROM e.address a WHERE a.city = 'Ottawa')";
        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("Complex IN Subquery Test Case Failed", result.equals(expectedResult));
    }

    public void complexExistsTest()
    {
        EntityManager em = createEntityManager();

        Collection allEmps = getServerSession().readAllObjects(Employee.class);
        List expectedResult = new ArrayList();
        // find an employees with projects
        for (Iterator i = allEmps.iterator(); i.hasNext();) {
            Employee e = (Employee)i.next();
            Collection projects = e.getProjects();
            if ((projects != null) && (projects.size() > 0)) {
                expectedResult.add(e.getId());
            }
        }

        String ejbqlString = "SELECT e.id FROM Employee e WHERE EXISTS (SELECT p FROM e.projects p)";
        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Not Exists test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexNotExistsTest()
    {
        EntityManager em = createEntityManager();

        Collection allEmps = getServerSession().readAllObjects(Employee.class);
        List expectedResult = new ArrayList();
        // find an employees with projects
        for (Iterator i = allEmps.iterator(); i.hasNext();) {
            Employee e = (Employee)i.next();
            Collection projects = e.getProjects();
            if ((projects == null) || (projects.size() == 0)) {
                expectedResult.add(e.getId());
            }
        }

        String ejbqlString = "SELECT e.id FROM Employee e WHERE NOT EXISTS (SELECT p FROM e.projects p)";
        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Complex Not Exists test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexInSubqueryJoinTest()
    {
        EntityManager em = createEntityManager();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("manager").isNull().not();
        ReadAllQuery query = new ReadAllQuery(Employee.class, exp);
        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        String ejbqlString = "SELECT e FROM Employee e WHERE e.firstName IN (SELECT emps.firstName FROM Employee emp join emp.managedEmployees emps)";
        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("Complex IN Subquery with join Test Case Failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexInSubqueryJoinInTest()
    {
        EntityManager em = createEntityManager();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("manager").isNull().not();
        ReadAllQuery query = new ReadAllQuery(Employee.class, exp);
        Vector expectedResult = (Vector)getServerSession().executeQuery(query);

        String ejbqlString = "SELECT e FROM Employee e WHERE e.firstName IN (SELECT emps.firstName FROM Employee emp, in(emp.managedEmployees) emps)";
        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("Complex IN Subquery with join Test Case Failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexMemberOfTest()
    {
        EntityManager em = createEntityManager();

        Collection allEmps = getServerSession().readAllObjects(Employee.class);

        // MEMBER OF using self-referencing relationship
        // return employees who are incorrectly entered as reporting to themselves
        List expectedResult = new ArrayList();
        String ejbqlString = "SELECT e FROM Employee e WHERE e MEMBER OF e.managedEmployees";
        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("Complex MEMBER OF test failed",
                          comparer.compareObjects(result, expectedResult));

        // find an employees with projects
        for (Iterator i = allEmps.iterator(); i.hasNext();) {
            Employee e = (Employee)i.next();
            Collection projects = e.getProjects();
            if ((projects != null) && (projects.size() > 0)) {
                expectedResult.add(e);
            }
        }
        // MEMBER of using identification variable p that is not the base
        // variable of the query
        ejbqlString = "SELECT DISTINCT e FROM Employee e, Project p WHERE p MEMBER OF e.projects";
        result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("Complex MEMBER OF test failed",
                          comparer.compareObjects(result, expectedResult));
    }

    public void complexNotMemberOfTest()
    {
        EntityManager em = createEntityManager();

        Collection allEmps = getServerSession().readAllObjects(Employee.class);
        String ejbqlString = "SELECT e FROM Employee e WHERE e NOT MEMBER OF e.managedEmployees";
        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("Complex MEMBER OF test failed", comparer.compareObjects(result, allEmps));
    }

    public void complexNotMemberOfPathTest()
    {
        EntityManager em = createEntityManager();

        Collection allEmps = getServerSession().readAllObjects(Employee.class);
        String ejbqlString = "SELECT e FROM Employee e  WHERE e.manager NOT MEMBER OF e.managedEmployees";
        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("Complex MEMBER OF test failed", comparer.compareObjects(result, allEmps));
    }

    public void complexMemberOfElementCollectionTest()
    {
        EntityManager em = createEntityManager();

        beginTransaction(em);

        Buyer buyer = new Buyer();
        buyer.setName("RBCL buyer");
        buyer.setDescription("RBCL buyer");
        buyer.addRoyalBankCreditLine(10);
        em.persist(buyer);
        em.flush();

        List expectedResult = new ArrayList();
        expectedResult.add(buyer);

        String ejbqlString = "SELECT b FROM Buyer b  WHERE 10 MEMBER OF b.creditLines";
        List result = em.createQuery(ejbqlString).getResultList();

        rollbackTransaction(em);
        Assert.assertTrue("Complex MEMBER OF test failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexInheritanceTest()
    {

        EntityManager em = createEntityManager();

        ((AbstractSession) getServerSession()).addAlias("ProjectBaseClass", getServerSession().getDescriptor(Project.class));

        Project expectedResult = (Project)getServerSession().readAllObjects(Project.class).firstElement();
        String projectName = expectedResult.getName();

        //Set criteria for EJBQL and call super-class method to construct the EJBQL query
        String ejbqlString = "SELECT OBJECT(project) FROM ProjectBaseClass project WHERE project.name = \"" + projectName +"\"";

        List result = em.createQuery(ejbqlString).getResultList();

        ((AbstractSession)getServerSession()).getAliasDescriptors().remove("ProjectBaseClass");

        Assert.assertTrue("Complex Inheritance test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexInheritanceUsingNamedQueryTest()
    {
        Project expectedResult = (Project)getServerSession().readAllObjects(Project.class).firstElement();

        String argument = expectedResult.getName();

        String queryName = "findLargeProjectByNameEJBQL";

        Session uow = getServerSession();

        if (!(getServerSession().containsQuery(queryName))) {
            ((AbstractSession)getServerSession()).addAlias("ProjectBaseClass", getServerSession().getDescriptor(Project.class));

            //Named query must be built and registered with the session
            ReadObjectQuery query = new ReadObjectQuery();
            query.setEJBQLString("SELECT OBJECT(project) FROM ProjectBaseClass project WHERE project.name = ?1");
            query.setName(queryName);
            query.addArgument("1");
            query.setReferenceClass(Project.class);
            uow.addQuery("findLargeProjectByNameEJBQL", query);
        }

        Project result = (Project)uow.executeQuery("findLargeProjectByNameEJBQL",argument);

        getServerSession().removeQuery("findLargeProjectByBudgetEJBQL");
        ((AbstractSession)getServerSession()).getAliasDescriptors().remove("ProjectBaseClass");

        Assert.assertTrue("Complex Inheritance using named query test failed", comparer.compareObjects(result, expectedResult));

    }

    public void complexNavigatingEmbedded ()
    {
        EntityManager em = createEntityManager();
        String jpqlString = "SELECT e.formerEmployment.formerCompany FROM Employee e WHERE e.formerEmployment.formerCompany = 'Former company'";
        Query query = em.createQuery(jpqlString);
        List result = query.getResultList();

        String expected = "Former company";
        Assert.assertTrue("Complex navigation of embedded in the select/where clause failed", result.contains(expected));
    }


    public void complexNavigatingTwoLevelOfEmbeddeds ()
    {
        EntityManager em = createEntityManager();
        String jpqlString = "SELECT emp.formerEmployment.period.startDate FROM Employee emp";
        Query query = em.createQuery(jpqlString);
        List result = query.getResultList();

        Calendar cal = Calendar.getInstance();
        cal.set(1990, 1, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected = new Date(cal.getTime().getTime());
        Assert.assertTrue("Complex navigation of two level of embeddeds in the select clause failed", result.contains(expected));
    }

    /**
     * Test glassfish issue 3041.
     */
    public void complexOuterJoinQuery()
    {
        EntityManager em = createEntityManager();

        // JPQL query using one INNER JOIN and three OUTER JOINs

        String jpql =
            "SELECT t3.firstName, t2.number, t4.firstName, t1.name " +
            "FROM Employee t0 " +
            "INNER JOIN t0.projects t1 " +
            "LEFT OUTER JOIN t0.phoneNumbers t2 " +
            "LEFT OUTER JOIN t1.teamLeader t3 " +
            "LEFT OUTER JOIN t1.teamMembers t4 " +
            "WHERE t0.firstName = 'Nancy' AND t0.lastName = 'White' " +
            "ORDER BY t4.firstName ASC ";
        Query q = em.createQuery(jpql);
        List<Object[]> result = q.getResultList();

        // check expected result
        // {[null, "5551234", "Marcus", "Swirly Dirly"],
        //  [null, "5551234", "Nancy", "Swirly Dirly"]}
        Assert.assertEquals("Complex outer join query (1): unexpected result size", 2, result.size());
        Object[] result0 = result.get(0);
        Assert.assertNull  ("Complex outer join query (1): unexpected result value (0, 0):", result0[0]);
        Assert.assertEquals("Complex outer join query (1): unexpected result value (0, 1):", result0[1], "5551234");
        Assert.assertEquals("Complex outer join query (1): unexpected result value (0, 2):", result0[2], "Marcus");
        Assert.assertEquals("Complex outer join query (1): unexpected result value (0, 3):", result0[3], "Swirly Dirly");
        Object[] result1 = result.get(1);
        Assert.assertNull  ("Complex outer join query (1): unexpected result value (1, 0):", result1[0]);
        Assert.assertEquals("Complex outer join query (1): unexpected result value (1, 1):", result1[1], "5551234");
        Assert.assertEquals("Complex outer join query (1): unexpected result value (1, 2):", result1[2], "Nancy");
        Assert.assertEquals("Complex outer join query (1): unexpected result value (1, 3):", result1[3], "Swirly Dirly");


        // JPQL query using only OUTER JOINs

        jpql =
            "SELECT t3.firstName, t2.number, t4.firstName, t1.name " +
            "FROM Employee t0 " +
            "LEFT OUTER JOIN t0.projects t1 " +
            "LEFT OUTER JOIN t0.phoneNumbers t2 " +
            "LEFT OUTER JOIN t1.teamLeader t3 " +
            "LEFT OUTER JOIN t1.teamMembers t4 " +
            "WHERE t0.firstName = 'Jill' AND t0.lastName = 'May' " +
            "ORDER BY t2.number ASC ";
        q = em.createQuery(jpql);
        result = q.getResultList();

        // check expected result
        // {[null, 2255943, null, null]
        //  [null, 2258812, null, null]}
        Assert.assertEquals("Complex outer join query (2): unexpected result size", 2, result.size());
        result0 = result.get(0);
        Assert.assertNull  ("Complex outer join query (2): unexpected result value (0, 0):", result0[0]);
        Assert.assertEquals("Complex outer join query (2): unexpected result value (0, 1):", result0[1], "2255943");
        Assert.assertNull  ("Complex outer join query (2): unexpected result value (0, 2):", result0[2]);
        Assert.assertNull  ("Complex outer join query (2): unexpected result value (0, 3):", result0[3]);
        result1 = result.get(1);
        Assert.assertNull  ("Complex outer join query (2): unexpected result value (1, 0):", result1[0]);
        Assert.assertEquals("Complex outer join query (2): unexpected result value (1, 1):", result1[1], "2258812");
        Assert.assertNull  ("Complex outer join query (2): unexpected result value (1, 2):", result1[2]);
        Assert.assertNull  ("Complex outer join query (2): unexpected result value (1, 3):", result1[3]);

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

    public static class EmployeeDetail {
        public String firstName;
        public String lastName;
        public Employee manager;
        public Long count;
        public BigInteger code;
        public EmploymentPeriod period;
        public EmployeeDetail(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
        public EmployeeDetail(String firstName, String lastName, Employee manager) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.manager = manager;
        }
        public EmployeeDetail(EmploymentPeriod e) {
            this.period = e;
        }
        public EmployeeDetail(Employee e) {
            this.firstName = e.getFirstName();
            this.lastName = e.getLastName();
            this.manager = e.getManager();
        }
        public EmployeeDetail(String firstName, String lastName, Long count) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.count = count;
        }
        public EmployeeDetail(String firstName, String lastName, BigInteger code) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.code = code;
        }
        @Override
		public int hashCode() {
            int result = 0;
            result += (firstName != null) ? firstName.hashCode() : 0;
            result += (lastName != null) ? lastName.hashCode() : 0;
            result += (manager != null) ? manager.hashCode() : 0;
            result += (count != null) ? count.hashCode() : 0;
            result += (code != null) ? code.hashCode() : 0;
            result += (period != null) ? period.hashCode() : 0;
            return result;
        }
        @Override
		public boolean equals(Object o) {
            if ((o == null) || (!(o instanceof EmployeeDetail))) {
                return false;
            }
            EmployeeDetail other = (EmployeeDetail) o;
            return JUnitJPQLComplexTestSuite.equals(this.firstName, other.firstName) &&
                JUnitJPQLComplexTestSuite.equals(this.lastName, other.lastName) &&
                JUnitJPQLComplexTestSuite.equals(this.manager, other.manager) &&
                JUnitJPQLComplexTestSuite.equals(this.count, other.count) &&
                JUnitJPQLComplexTestSuite.equals(this.code, other.code) &&
                JUnitJPQLComplexTestSuite.equals(this.period, other.period);
        }
        @Override
		public String toString() {
            return "EmployeeDetail(" + firstName + ", " + lastName + ", " +
                                    manager + ", " + count + ", " + code + ", "+ period+ ")";
        }
    }

    public static class LongHolder {
        public Long value1;
        public Long value2;
        public LongHolder(Long value1, Long value2) {
            this.value1 = value1;
            this.value2 = value2;
        }
        @Override
		public int hashCode() {
            int result = 0;
            result += value1 != null ? value1.hashCode() : 0;
            result += value2 != null ? value2.hashCode() : 0;
            return result;
        }
        @Override
		public boolean equals(Object o) {
            if ((o == null) || (!(o instanceof LongHolder))) {
                return false;
            }
            LongHolder other = (LongHolder) o;
            return JUnitJPQLComplexTestSuite.equals(this.value1, other.value1) &&
                JUnitJPQLComplexTestSuite.equals(this.value2, other.value2);
        }
        @Override
		public String toString() {
            return "LongHolder(" + value1 + ", " + value2 + ")";
        }
    }

    public void mapContainerPolicyMapKeyInSelectTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Blue blue2 = new Blue();
        blue2.setAlcoholContent(5.0f);
        blue2.setUniqueKey(BigInteger.valueOf(2));
        consumer2.addBlueBeerToConsume(blue2);
        em.persist(blue2);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add(BigInteger.ONE);

        clearCache();
        String ejbqlString = "SELECT KEY(b) FROM BeerConsumer bc join bc.blueBeersToConsume b where bc.name = 'Marvin Monroe'";

        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("mapContainerPolicyMapKeyInSelectTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mapContainerPolicyMapValueInSelectTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Blue blue2 = new Blue();
        blue2.setAlcoholContent(5.0f);
        blue2.setUniqueKey(BigInteger.valueOf(2));
        consumer2.addBlueBeerToConsume(blue2);
        em.persist(blue2);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add(blue);

        clearCache();
        String ejbqlString = "SELECT VALUE(b) FROM BeerConsumer bc join bc.blueBeersToConsume b where bc.name = 'Marvin Monroe'";

        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("mapContainerPolicyMapValueInSelectTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mapContainerPolicyMapEntryInSelectTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Blue blue2 = new Blue();
        blue2.setAlcoholContent(5.0f);
        blue2.setUniqueKey(BigInteger.valueOf(2));
        consumer2.addBlueBeerToConsume(blue2);
        em.persist(blue2);
        em.flush();


        clearCache();
        String ejbqlString = "SELECT ENTRY(b) FROM BeerConsumer bc join bc.blueBeersToConsume b where bc.name = 'Marvin Monroe'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("Incorrect number of values returned", result.size() == 1);
        Assert.assertTrue("Did not return a Map.Entry", result.get(0) instanceof Map.Entry);
        Map.Entry entry = (Map.Entry)result.get(0);
        Assert.assertTrue("Keys do not match", entry.getKey().equals(BigInteger.ONE));
        Assert.assertTrue("Values do not match", comparer.compareObjects(entry.getValue(), blue));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mapContainerPolicyMapKeyInSelectionCriteriaTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Blue blue2 = new Blue();
        blue2.setAlcoholContent(5.0f);
        blue2.setUniqueKey(BigInteger.valueOf(2));
        consumer2.addBlueBeerToConsume(blue2);
        em.persist(blue2);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add(consumer);

        clearCache();
        String ejbqlString = "SELECT bc FROM BeerConsumer bc join bc.blueBeersToConsume b where KEY(b) = 1";

        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("mapContainerPolicyMapKeyInSelectionCriteriaTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mapContainerPolicyMapValueInSelectionCriteriaTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Blue blue2 = new Blue();
        blue2.setAlcoholContent(5.0f);
        blue2.setUniqueKey(BigInteger.valueOf(2));
        consumer2.addBlueBeerToConsume(blue2);
        em.persist(blue2);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add(consumer);

        clearCache();
        String ejbqlString = "SELECT bc FROM BeerConsumer bc join bc.blueBeersToConsume b where VALUE(b).uniqueKey = 1";

        List result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("mapContainerPolicyMapValueInSelectionCriteriaTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyMapKeyInSelectionCriteriaTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Becks becks = new Becks();
        becks.setAlcoholContent(5.0);
        BecksTag tag = new BecksTag();
        tag.setCallNumber("123");
        consumer.addBecksBeerToConsume(becks, tag);
        em.persist(becks);
        em.persist(tag);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer.setName("Marvin Monroe2");
        em.persist(consumer2);
        Becks becks2 = new Becks();
        becks2.setAlcoholContent(5.0);
        BecksTag tag2 = new BecksTag();
        tag2.setCallNumber("1234");
        consumer2.addBecksBeerToConsume(becks2, tag2);
        em.persist(becks2);
        em.persist(tag2);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add(consumer);

        clearCache();
        String ejbqlString = "SELECT bc FROM BeerConsumer bc join bc.becksBeersToConsume b where Key(b).callNumber = '123'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyMapKeyInSelectionCriteriaTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyMapKeyInSelectTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Becks becks = new Becks();
        becks.setAlcoholContent(5.0);
        BecksTag tag = new BecksTag();
        tag.setCallNumber("123");
        consumer.addBecksBeerToConsume(becks, tag);
        em.persist(becks);
        em.persist(tag);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer.setName("Marvin Monroe2");
        em.persist(consumer2);
        Becks becks2 = new Becks();
        becks2.setAlcoholContent(5.0);
        BecksTag tag2 = new BecksTag();
        tag2.setCallNumber("1234");
        consumer2.addBecksBeerToConsume(becks2, tag2);
        em.persist(becks2);
        em.persist(tag2);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add(tag);

        clearCache();
        String ejbqlString = "SELECT Key(b) FROM BeerConsumer bc join bc.becksBeersToConsume b where Key(b).callNumber = '123'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyMapKeyInSelectTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyMapEntryInSelectTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Becks becks = new Becks();
        becks.setAlcoholContent(5.0);
        BecksTag tag = new BecksTag();
        tag.setCallNumber("123");
        consumer.addBecksBeerToConsume(becks, tag);
        em.persist(becks);
        em.persist(tag);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Becks becks2 = new Becks();
        becks2.setAlcoholContent(5.0);
        BecksTag tag2 = new BecksTag();
        tag2.setCallNumber("1234");
        consumer2.addBecksBeerToConsume(becks2, tag2);
        em.persist(becks2);
        em.persist(tag2);
        em.flush();

        clearCache();
        String ejbqlString = "SELECT ENTRY(b) FROM BeerConsumer bc join bc.becksBeersToConsume b where Key(b) = :becksTag";

        List result = em.createQuery(ejbqlString).setParameter("becksTag", tag).getResultList();

        Assert.assertTrue("Incorrect number of values returned", result.size() == 1);
        Assert.assertTrue("Did not return a Map.Entry", result.get(0) instanceof Map.Entry);
        Map.Entry entry = (Map.Entry)result.get(0);
        Assert.assertTrue("Keys do not match", comparer.compareObjects(entry.getKey(), tag));
        Assert.assertTrue("Values do not match", comparer.compareObjects(entry.getValue(), becks));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyEmbeddableMapKeyInSelectionCriteriaTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Corona corona = new Corona();
        corona.setAlcoholContent(5.0);
        CoronaTag tag = new CoronaTag();
        tag.setCode("123");
        tag.setNumber(123);
        consumer.addCoronaBeerToConsume(corona, tag);
        em.persist(corona);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Corona corona2 = new Corona();
        corona2.setAlcoholContent(5.0);
        CoronaTag tag2 = new CoronaTag();
        tag2.setCode("1234");
        tag2.setNumber(1234);
        consumer2.addCoronaBeerToConsume(corona2, tag2);
        em.persist(corona2);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add(consumer);

        clearCache();
        String ejbqlString = "SELECT bc FROM BeerConsumer bc join bc.coronaBeersToConsume b where Key(b).code = :key";

        List result = em.createQuery(ejbqlString).setParameter("key", "123").getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyEmbeddableMapKeyInSelectionCriteriaTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyElementCollectionSelectionCriteriaTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        ExpertBeerConsumer consumer = new ExpertBeerConsumer();
        consumer.setAccredidation(new Accredidation());
        consumer.setName("Marvin Monroe");
        Birthday bday = new Birthday();
        bday.setDay(25);
        bday.setMonth(6);
        bday.setYear(2009);
        consumer.addCelebration(bday, "Lots of Cake!");
        ExpertBeerConsumer consumer2 = new ExpertBeerConsumer();
        consumer2.setAccredidation(new Accredidation());
        consumer2.setName("Marvin Monroe2");
        Birthday bday2 = new Birthday();
        bday2.setDay(25);
        bday2.setMonth(6);
        bday2.setYear(2001);
        consumer2.addCelebration(bday, "Lots of food!");

        em.persist(consumer);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add(consumer);

        clearCache();
        String ejbqlString = "SELECT bc FROM EXPERT_CONSUMER bc join bc.celebrations c where Key(c).day = :celebration";

        List result = em.createQuery(ejbqlString).setParameter("celebration", 25).getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyElementCollctionSelectionCriteriaTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyNavigateMapKeyInEntityTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Becks becks = new Becks();
        becks.setAlcoholContent(5.0);
        BecksTag tag = new BecksTag();
        tag.setCallNumber("123");
        consumer.addBecksBeerToConsume(becks, tag);
        em.persist(becks);
        em.persist(tag);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Becks becks2 = new Becks();
        becks2.setAlcoholContent(5.0);
        BecksTag tag2 = new BecksTag();
        tag2.setCallNumber("1234");
        consumer2.addBecksBeerToConsume(becks2, tag2);
        em.persist(becks2);
        em.persist(tag2);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add("123");

        clearCache();
        String ejbqlString = "SELECT KEY(becks).callNumber from BeerConsumer bc join bc.becksBeersToConsume becks where bc.name = 'Marvin Monroe'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyNavigateMapKeyInEntityTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyNavigateMapKeyInEmbeddableTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Corona corona = new Corona();
        corona.setAlcoholContent(5.0);
        CoronaTag tag = new CoronaTag();
        tag.setCode("123");
        tag.setNumber(123);
        consumer.addCoronaBeerToConsume(corona, tag);
        em.persist(corona);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Corona corona2 = new Corona();
        corona2.setAlcoholContent(5.0);
        CoronaTag tag2 = new CoronaTag();
        tag2.setCode("1234");
        tag2.setNumber(1234);
        consumer2.addCoronaBeerToConsume(corona2, tag2);
        em.persist(corona2);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add("123");

        clearCache();
        String ejbqlString = "SELECT KEY(c).code from BeerConsumer bc join bc.coronaBeersToConsume c where bc.name = 'Marvin Monroe'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyNavigateMapKeyInEmbeddableTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void complexTypeParameterTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        List expectedResult = getServerSession().readAllObjects(LargeProject.class);
        clearCache();
        String ejbqlString = "select p from Project p where TYPE(p) = :param";

        List result = em.createQuery(ejbqlString).setParameter("param", LargeProject.class).getResultList();

        Assert.assertTrue("complexTypeParameterTest failed", comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexTypeInTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        List expectedResult = getServerSession().readAllObjects(LargeProject.class);
        expectedResult.addAll(getServerSession().readAllObjects(SmallProject.class));
        clearCache();
        String ejbqlString = "select p from Project p where TYPE(p) in(LargeProject, SmallProject)";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexTypeParameterTest failed", comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexTypeInParamTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        List expectedResult = getServerSession().readAllObjects(LargeProject.class);
        expectedResult.addAll(getServerSession().readAllObjects(SmallProject.class));
        clearCache();
        String ejbqlString = "select p from Project p where TYPE(p) in :param";

        ArrayList params = new ArrayList(2);
        params.add(LargeProject.class);
        params.add(SmallProject.class);

        List result = em.createQuery(ejbqlString).setParameter("param", params).getResultList();

        Assert.assertTrue("complexTypeParameterTest failed", comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexThreeLevelJoinOneTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Expression exp = (new ExpressionBuilder()).get("manager").get("address").get("city").equal("Ottawa");
        List expectedResult = getServerSession().readAllObjects(Employee.class, exp);
        clearCache();
        String ejbqlString = "select e from Employee e join e.manager.address a where a.city = 'Ottawa'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexThreeLevelJoinOneTest failed", comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexThreeLevelJoinManyTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Expression exp = (new ExpressionBuilder()).get("manager").anyOf("phoneNumbers").get("areaCode").equal("613");
        List expectedResult = getServerSession().readAllObjects(Employee.class, exp);
         clearCache();
        String ejbqlString = "select distinct e from Employee e join e.manager.phoneNumbers p where p.areaCode = '613'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexThreeLevelJoinOneTest failed", comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexIndexOfInSelectClauseTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ExpertBeerConsumer consumer = new ExpertBeerConsumer();
        consumer.setAccredidation(new Accredidation());
        consumer.getDesignations().add("guru");
        consumer.getDesignations().add("beer-meister");
        em.persist(consumer);
        em.flush();
        List expectedResult = new ArrayList();
        expectedResult.add(new Integer(0));
        expectedResult.add(new Integer(1));
        clearCache();
        String ejbqlString = "select index(d) from EXPERT_CONSUMER e join e.designations d";

        List result = em.createQuery(ejbqlString).getResultList();

        rollbackTransaction(em);
        Assert.assertTrue("complexIndexOfInSelectClauseTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexIndexOfInWhereClauseTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ExpertBeerConsumer consumer = new ExpertBeerConsumer();
        consumer.setAccredidation(new Accredidation());
        consumer.getDesignations().add("guru");
        consumer.getDesignations().add("beer-meister");
        em.persist(consumer);
        em.flush();
        String expectedResult = "guru";
        clearCache();
        String ejbqlString = "select d from EXPERT_CONSUMER e join e.designations d where index(d) = 0";

        String result = (String)em.createQuery(ejbqlString).getSingleResult();

        rollbackTransaction(em);
        Assert.assertTrue("complexIndexOfInWhereClauseTest failed", result.equals(expectedResult));
    }

    public void complexCoalesceInWhereTest(){
        EntityManager em = createEntityManager();

        Expression exp = (new ExpressionBuilder()).get("firstName").equal("Bob");
        List expectedResult = getServerSession().readAllObjects(Employee.class, exp);

        clearCache();
        String ejbqlString = "select e from Employee e where coalesce(e.firstName, e.lastName) = 'Bob'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexIndexOfInWhereClauseTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexCoalesceInSelectTest(){
        EntityManager em = createEntityManager();

        ReportQuery reportQuery = new ReportQuery();
        reportQuery.dontMaintainCache();
        reportQuery.setShouldReturnWithoutReportQueryResult(true);
        reportQuery.setReferenceClass(Employee.class);
        ExpressionBuilder builder = reportQuery.getExpressionBuilder();
        reportQuery.addItem("firstName", builder.get("firstName"));
        Vector expectedResult = (Vector)getServerSession().executeQuery(reportQuery);

        clearCache();
        String ejbqlString = "select coalesce(e.firstName, e.lastName) from Employee e";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexIndexOfInWhereClauseTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexNullIfInWhereTest(){
        EntityManager em = createEntityManager();

        Expression exp = (new ExpressionBuilder()).get("firstName").equal("Bob");
        List expectedResult = getServerSession().readAllObjects(Employee.class, exp);

        clearCache();
        String ejbqlString = "select e from Employee e where nullIf(e.firstName, 'Bob') is null";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexIndexOfInWhereClauseTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexNullIfInSelectTest(){
        EntityManager em = createEntityManager();

        Vector expectedResult = new Vector();
        expectedResult.add(null);

        clearCache();
        String ejbqlString = "select nullIf(e.firstName, 'Bob') from Employee e where e.firstName = 'Bob'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexIndexOfInWhereClauseTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexSimpleCaseInSelectTest(){
        if (((Session) JUnitTestCase.getServerSession()).getPlatform().isDerby())
        {
            warning("The test complexSimpleCaseInSelectTest is not supported on Derby, because Derby does not support simple CASE");
            return;
        }
        EntityManager em = createEntityManager();
        Vector expectedResult = new Vector(2);
        expectedResult.add("Robert");
        expectedResult.add("Gillian");

        clearCache();
        String ejbqlString = "select case e.firstName when 'Bob' then 'Robert' when 'Jill' then 'Gillian' else '' end from Employee e where e.firstName = 'Bob' or e.firstName = 'Jill'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexSimpleCaseInSelectTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexSimpleCaseInWhereTest(){
        if (((Session) JUnitTestCase.getServerSession()).getPlatform().isDerby())
        {
            warning("The test complexSimpleCaseInWhereTest is not supported on Derby, because Derby does not support simple CASE");
            return;
        }
        EntityManager em = createEntityManager();
        Expression exp = (new ExpressionBuilder()).get("firstName").equal("Bob");
        List expectedResult = getServerSession().readAllObjects(Employee.class, exp);

        clearCache();
        String ejbqlString = "select e from Employee e where case e.firstName when 'Bob' then 'Robert' when 'Jill' then 'Gillian' else '' end = 'Robert'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexSimpleCaseInWhereTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexConditionCaseInSelectTest(){
        EntityManager em = createEntityManager();

        Vector expectedResult = new Vector(2);
        expectedResult.add("Robert");
        expectedResult.add("Gillian");

        clearCache();
        String ejbqlString = "select case when e.firstName = 'Bob' then 'Robert' when e.firstName = 'Jill' then 'Gillian' else '' end from Employee e  where e.firstName = 'Bob' or e.firstName = 'Jill'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexConditionCaseInSelectTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexConditionCaseInWhereTest(){
        EntityManager em = createEntityManager();

        Expression exp = (new ExpressionBuilder()).get("firstName").equal("Bob");
        List expectedResult = getServerSession().readAllObjects(Employee.class, exp);

        clearCache();
        String ejbqlString = "select e from Employee e where case when e.firstName = 'Bob' then 'Robert' when e.firstName = 'Jill' then 'Gillian' else '' end = 'Robert'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexConditionCaseInWhereTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexConditionCaseInUpdateTest(){
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test complexConditionCaseInUpdateTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();

        beginTransaction(em);

        clearCache();
        String ejbqlString = "Update Employee e set e.lastName = case when e.firstName = 'Bob' then 'Jones' when e.firstName = 'Jill' then 'Jones' else '' end";

        em.createQuery(ejbqlString).executeUpdate();

        String verificationString = "select e from Employee e where e.lastName = 'Jones'";
        List results = em.createQuery(verificationString).getResultList();

        rollbackTransaction(em);
        assertTrue("complexConditionCaseInUpdateTest - wrong number of results", results.size() == 2);
        Iterator i = results.iterator();
        while (i.hasNext()){
            Employee e = (Employee)i.next();
            assertTrue("complexConditionCaseInUpdateTest wrong last name for - " + e.getFirstName(), e.getLastName().equals("Jones"));
        }

    }

    public void absInSelectTest(){
        EntityManager em = createEntityManager();

        String ejbqlString = "select abs(e.salary) from Employee e where e.firstName = 'Bob' and e.lastName = 'Smith'";

        List result = em.createQuery(ejbqlString).getResultList();

        assertTrue("The wrong absolute value was returned.", ((Integer)result.get(0)).intValue() == 35000);
    }

    public void modInSelectTest(){
        EntityManager em = createEntityManager();

        String ejbqlString = "select mod(e.salary, 10) from Employee e where e.firstName = 'Bob' and e.lastName = 'Smith'";

        List result = em.createQuery(ejbqlString).getResultList();

        assertTrue("The wrong mod value was returned.", ((Integer)result.get(0)).intValue() == 0);
    }

    public void sqrtInSelectTest(){
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test sqrtInSelectTest skipped for this platform, "
                    + "Symfoware doesn't support SQRT, COS, SIN, TAN functions.");
            return;
        }
        EntityManager em = createEntityManager();

        String ejbqlString = "select sqrt(e.salary) from Employee e where e.firstName = 'Bob' and e.lastName = 'Smith'";

        List result = em.createQuery(ejbqlString).getResultList();

        assertTrue("The wrong square root value was returned.", ((Double)result.get(0)).doubleValue() > 187);
        assertTrue("The wrong square root value was returned.", ((Double)result.get(0)).doubleValue() < 188);
    }

    public void sizeInSelectTest(){
        EntityManager em = createEntityManager();

        String ejbqlString = "select size(e.phoneNumbers) from Employee e where e.firstName = 'Betty'";

        List result = em.createQuery(ejbqlString).getResultList();

        assertTrue("The wrong absolute value was returned.", ((Integer)result.get(0)).intValue() == 2);
    }

    public void mathInSelectTest(){
        EntityManager em = createEntityManager();

        String ejbqlString = "select e.salary + 100 from Employee e where e.firstName = 'Bob' and e.lastName = 'Smith'";

        List result = em.createQuery(ejbqlString).getResultList();

        assertTrue("The wrong value was returned.", ((Integer)result.get(0)).intValue() == 35100);
    }

    public void paramNoVariableTest(){
        EntityManager em = createEntityManager();

        List expectedResult = em.createQuery("select e from Employee e where e.firstName = 'Bob'").getResultList();

        String ejbqlString = "select e from Employee e where :arg = 1 or e.firstName = 'Bob'";

        List result = em.createQuery(ejbqlString).setParameter("arg", 2).getResultList();

        assertTrue("The wrong number of employees returned, expected:" + expectedResult + " got:" + result, result.size() == expectedResult.size());
    }

    public void mappedContainerPolicyCompoundMapKeyTest(){
        // skip test on OC4j some this test fails on some OC4j versions because of an issue with Timestamp
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof Oc4jPlatform){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        TelephoneNumber number = new TelephoneNumber();
        number.setType("Home");
        number.setAreaCode("975");
        number.setNumber("1234567");
        em.persist(number);
        consumer.addTelephoneNumber(number);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        TelephoneNumber number2 = new TelephoneNumber();
        number2.setType("Home");
        number2.setAreaCode("974");
        number2.setNumber("1234567");
        em.persist(number2);
        consumer2.addTelephoneNumber(number2);
        em.flush();
        Vector expectedResult = new Vector();
        expectedResult.add(number);

        clearCache();
        String ejbqlString = "SELECT KEY(number) from BeerConsumer bc join bc.telephoneNumbers number where bc.name = 'Marvin Monroe'";

        List result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("mappedContainerPolicyCompoundMapKeyTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void updateWhereExistsTest() {
        whereExistsTest(true);
    }

    public void deleteWhereExistsTest() {
        whereExistsTest(false);
    }

    void whereExistsTest(boolean shouldUpdate) {
        String lastName = (shouldUpdate ? "update" : "delete") + "WhereExistsTest";
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test " + lastName + " skipped for this platform, "
                                    + "Symfoware doesn't support UpdateAll/DeleteAll queries that involve a subquery "
                                    + "that includes a reference to a table in the outer query (see rfe 298193).");
            return;
        }
        int nMen = 4;
        int nWomen = 2;
        Assert.assertTrue("Test setup problem: nMen should be greater than nWomen", nMen > nWomen);

        EntityManager em = createEntityManager();
        String whereExists = " WHERE EXISTS (SELECT w FROM Woman w WHERE w.firstName = m.firstName AND w.lastName = m.lastName AND m.lastName = '"+lastName+"')";
        String jpqlCount = "SELECT COUNT(m) FROM Man m" + whereExists;
        String jpqlUpdateOrDelete = (shouldUpdate ? "UPDATE Man m SET m.firstName = 'New'" : "DELETE FROM Man m") + whereExists;

        beginTransaction(em);
        try {
            // setup
            for(int i=0; i < nMen; i++) {
                em.persist(new Man(Integer.toString(i), lastName));
                if(i < nWomen) {
                    em.persist(new Woman(Integer.toString(i), lastName));
                }
            }
            em.flush();
            em.clear();
            Query countQuery = em.createQuery(jpqlCount);
            long nMenRead = (Long)countQuery.getSingleResult();
            Assert.assertTrue("Test setup problem: nMenRead should be equal to nWomen before update/delete, but nMenRead ="+nMenRead+"; nWomen ="+nWomen, nWomen == nMenRead);

            // test
            Query updateOrDeleteQuery = em.createQuery(jpqlUpdateOrDelete);
            int nMenUpdatedOrDeleted = updateOrDeleteQuery.executeUpdate();
            em.flush();
            em.clear();
            nMenRead = (Long)countQuery.getSingleResult();

            // verify
            Assert.assertTrue("nMenUpdatedOrDeleted should be equal to nWomen, but nMenUpdatedOrDeleted ="+nMenUpdatedOrDeleted+"; nWomen ="+nWomen, nMenUpdatedOrDeleted == nWomen);
            Assert.assertTrue("nMenRead should be 0 after deletion, but nMenRead ="+nMenRead, nMenRead == 0);
        } finally {
            // clean-up
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    // Bug 300512 - Add FUNCTION support to extended JPQL
    public void caseTypeTest() {
        if (((Session) JUnitTestCase.getServerSession()).getPlatform().isDerby())
        {
            warning("The test caseTypeTest is not supported on Derby, because Derby does not support simple CASE");
            return;
        }

        EntityManager em = createEntityManager();

        String jpqlString1 = "SELECT CONCAT(case e.firstName when 'Bob' then 'Robert' when 'Jill' then 'Gillian' else '' end, ' - full name') FROM Employee e";
        em.createQuery(jpqlString1).getResultList();

        // Bug 372178 - JPQL: query fails on Symfoware
        if (((Session) JUnitTestCase.getServerSession()).getPlatform().isSymfoware())
        {
            warning("The test 'caseTypeTest' is not supported on Symfoware, "  
                  + "EclipseLink will convert some (not all) Integer into String, "
                  + "and because Symfoware does not support implicit type conversion, " 
                  + "we ended up with the addition between String and Integer which is illegal on Symfoware.");
            closeEntityManager(em);
            return;
        }
        String jpqlString2 = "SELECT case e.firstName when 'Bob' then 1 when 'Jill' then 2 else 0 end + 1 FROM Employee e";
        em.createQuery(jpqlString2).getResultList();

        closeEntityManager(em);
    }

    // Bug 303540 - JPQL: query fails to compile if variable found only in function parameters
    public void variableReferencedOnlyInParameterTest() {
        if (getDatabaseSession().getPlatform().isSymfoware()) {
            warning("Test variableReferencedOnlyInParameterTest skipped for this platform, "
                    + "Symfoware doesn't support SQRT, COS, SIN, TAN functions.");
            return;
        }
        EntityManager em = createEntityManager();
        String[] jpqlString = {
                "SELECT (e.id + 1) FROM Employee e",

                "SELECT ABS(e.id) FROM Employee e",
                "SELECT LENGTH(e.firstName) FROM Employee e",
                "SELECT MOD(e.id, 2) FROM Employee e",
                "SELECT SQRT(e.id) FROM Employee e",
                "SELECT LOCATE(e.firstName, 'a') FROM Employee e",
                "SELECT SIZE(e.phoneNumbers) FROM Employee e",

                "SELECT CONCAT(e.firstName, e.lastName) FROM Employee e",
                "SELECT CONCAT('a', e.lastName) FROM Employee e",
                "SELECT CONCAT(e.firstName, 'b') FROM Employee e",
                "SELECT SUBSTRING(e.firstName, 1, 2) FROM Employee e",
                "SELECT TRIM(e.firstName) FROM Employee e",
                "SELECT TRIM(LEADING FROM e.firstName) FROM Employee e",
                "SELECT TRIM(TRAILING FROM e.firstName) FROM Employee e",
                "SELECT TRIM(LEADING 'A' FROM e.firstName) FROM Employee e",
                "SELECT TRIM(TRAILING 'A' FROM e.firstName) FROM Employee e",
                "SELECT UPPER(e.firstName) FROM Employee e",
                "SELECT LOWER(e.firstName) FROM Employee e",
        };
        String jpql = null;
        for(int i=0; i < jpqlString.length; i++) {
            jpql = jpqlString[i];
            if (getDatabaseSession().getPlatform().isSybase() || getDatabaseSession().getPlatform().isSQLServer()) {
                if (jpql == "SELECT TRIM(LEADING 'A' FROM e.firstName) FROM Employee e" ||
                        jpql == "SELECT TRIM(TRAILING 'A' FROM e.firstName) FROM Employee e") {
                    warning("Not supported on Sybase/SQL Server " + jpql);
                    continue;
                }
            }
            Query query = em.createQuery(jpql);
            query.getResultList();
        }

        closeEntityManager(em);
    }

    // Bug 300512 - Add FUNCTION support to extended JPQL
    // Bug 246598 - Unable to parse TRIM in JPA NamedQuery
    public void standardFunctionCreateQueryTest() {
        // for debug
        boolean shouldPrintJpql = false;
        boolean shouldPrintStackTrace = false;
        EntityManager em = createEntityManager();
        String[] jpqlString = {
                "SELECT e.id FROM Employee e WHERE ABS(:param0) = e.id",
                "SELECT e.id FROM Employee e WHERE ABS(e.id) = :param0",
                "SELECT e.id FROM Employee e WHERE SQRT(:param0) = e.id",
                "SELECT e.id FROM Employee e WHERE SQRT(e.id) = :param",
                "SELECT e.id FROM Employee e WHERE LENGTH(:param0) = e.id",
                "SELECT e.id FROM Employee e WHERE LENGTH(e.firstName) = :param0",
                "SELECT e.id FROM Employee e WHERE MOD(e.id, :param0) = 1",
                "SELECT e.id FROM Employee e WHERE MOD(:param0, :param1) = e.id",
                "SELECT e.id FROM Employee e WHERE LOCATE(e.firstName, :param0, :param1) = :param2",
                "SELECT e.id FROM Employee e WHERE CONCAT(:param0, e.firstName, :param1) = :param2",
                "SELECT e.id FROM Employee e WHERE SUBSTRING(e.firstName, :param1, :param2) = :param3",
                "SELECT e.id FROM Employee e WHERE SUBSTRING(e.firstName, :param1, :param2) = e.lastName",
                "SELECT e.id FROM Employee e WHERE SUBSTRING(:param0, :param1, :param2) = e.firstName",
                "SELECT e.id FROM Employee e WHERE TRIM(:param0) = e.firstName",
                "SELECT e.id FROM Employee e WHERE TRIM(e.firstName) = :param0",
                "SELECT e.id FROM Employee e WHERE TRIM(LEADING FROM e.firstName) = :param0",
                "SELECT e.id FROM Employee e WHERE TRIM(BOTH FROM :param0) = e.firstName",
                "SELECT e.id FROM Employee e WHERE TRIM(LEADING 'a' FROM e.firstName) = :param0",
                "SELECT e.id FROM Employee e WHERE TRIM(TRAILING 'a' FROM e.firstName) = :param0",
                "SELECT e.id FROM Employee e WHERE TRIM(' ' FROM e.firstName) = :param0",
                "SELECT e.id FROM Employee e WHERE TRIM('a' FROM e.firstName) = :param0",
                "SELECT e.id FROM Employee e WHERE TRIM(BOTH 'a' FROM e.firstName) = :param0",
                "SELECT e.id FROM Employee e WHERE TRIM(LEADING :param0 FROM :param1) = e.firstName",
                "SELECT e.id FROM Employee e WHERE LOWER(:param0) = e.firstName",
                "SELECT e.id FROM Employee e WHERE UPPER(:param0) = e.lastName",

                "SELECT ABS(e.id) FROM Employee e",
                "SELECT SQRT(e.id) FROM Employee e",
                "SELECT LENGTH(e.firstName) FROM Employee e",
                "SELECT MOD(e.id, 2) FROM Employee e",
                "SELECT LOCATE('abc', e.firstName, 2) FROM Employee e",
                "SELECT LOCATE('abc', e.firstName) FROM Employee e",
                "SELECT LOCATE(e.firstName, 'abc', 2) FROM Employee e",
                "SELECT LOCATE(e.firstName, 'abc') FROM Employee e",
                "SELECT CONCAT(e.firstName, 'A', 'Blah') FROM Employee e",
                "SELECT SUBSTRING(e.firstName, 1, 2) FROM Employee e",
                "SELECT TRIM(LEADING 'a' FROM e.firstName) FROM Employee e",
                "SELECT TRIM(TRAILING 'a' FROM e.firstName) FROM Employee e",
                "SELECT TRIM(e.firstName) FROM Employee e",
                "SELECT TRIM(' ' FROM e.firstName) FROM Employee e",
                "SELECT TRIM(BOTH 'a' FROM e.firstName) FROM Employee e",
                "SELECT TRIM(LEADING FROM e.firstName) FROM Employee e",
                "SELECT TRIM('a' FROM e.firstName) FROM Employee e",
                "SELECT TRIM(e.firstName) FROM Employee e",
                "SELECT LOWER(e.firstName) FROM Employee e",
                "SELECT UPPER(e.firstName) FROM Employee e",
        };

        String errorMsg = "";
        String jpql = null;
        for(int i=0; i < jpqlString.length; i++) {
            try {
                jpql = jpqlString[i];
                em.createQuery(jpql);
            } catch (Exception ex) {
                if(shouldPrintJpql) {
                    System.out.println(jpql);
                }
                if(shouldPrintStackTrace) {
                    ex.printStackTrace();
                }
                errorMsg += '\t' + jpql + " - "+ex+'\n';
            }
        }

        closeEntityManager(em);

        if(errorMsg.length() > 0) {
            errorMsg = "Failed:\n" + errorMsg;
            fail(errorMsg);
        }
    }

    // Bug 300512 - Add FUNCTION support to extended JPQL
    public void customFunctionNVLTest() {
        // NVL is Oracle database function, therefore the test runs on Oracle only.
        if(!getServerSession().getPlatform().isOracle()) {
            warning("The test customFunctionNVLTest is supported on Oracle only");
            return;
        }
        EntityManager em = createEntityManager();
        String[] jpqlString = {
                "SELECT FUNC('NVL', e.firstName, 'NoFirstName'), func('NVL', e.lastName, 'NoLastName') FROM Employee e",
                "SELECT FUNC('NVL', e.firstName, :param0), func('NVL', :param1, e.lastName) FROM Employee e",
                "SELECT FUNC('NVL', e.firstName, SUBSTRING(e.lastName, 1)), func('NVL', e.lastName, CONCAT(e.lastName, ' no name')) FROM Employee e",
                "SELECT CONCAT(FUNC('NVL', e.firstName, 'NoFirstName'), func('NVL', e.lastName, 'NoLastName')) FROM Employee e",

                "SELECT e.id FROM Employee e WHERE FUNC('NVL', e.firstName, 'NoFirstName') = func('NVL', e.lastName, 'NoLastName')",
                "SELECT e.id FROM Employee e WHERE FUNC('NVL', e.firstName, :param0) = func('NVL', :param1, e.lastName)",
                "SELECT e.id FROM Employee e WHERE FUNC('NVL', e.firstName, SUBSTRING(e.lastName, 1)) = func('NVL', e.lastName, CONCAT(e.lastName, ' no name'))",
                "SELECT e.id FROM Employee e WHERE CONCAT(FUNC('NVL', e.firstName, 'NoFirstName'), func('NVL', e.lastName, 'NoLastName')) = 'NoFirstNameNoLastName'"
        };

        String errorMsg = "";
        String jpql = null;
        Query query;
        for(int i=0; i < jpqlString.length; i++) {
            try {
                jpql = jpqlString[i];
                query = em.createQuery(jpql);
                if(i == 1 || i == 5) {
                    query.setParameter("param0", "Blah0");
                    query.setParameter("param1", "Blah1");
                }
                query.getResultList();
            } catch (Exception ex) {
                ex.printStackTrace();
                errorMsg += '\t' + jpql + " - "+ex+'\n';
            }
        }

        closeEntityManager(em);

        if(errorMsg.length() > 0) {
            errorMsg = "Failed:\n" + errorMsg;
            fail(errorMsg);
        }
    }

    /* Test FUNC with stored function 'StoredFunction_In'*/
    public void testFuncWithStoredFunc() {
        if (!supportsStoredFunctions()) {
            warning("this test is not suitable for running on dbs that don't support stored function");
            return;
        } else {
            String sqlString = "SELECT e.id FROM Employee e WHERE e.salary = FUNC('StoredFunction_In', 75)";
            EntityManager em = createEntityManager();
            Query query;
            try {
                query = em.createQuery(sqlString);
                List result = query.getResultList();
                Employee emp = em.find(Employee.class, result.get(0));
                assertTrue("Func is not working properly with stored function.", emp.getSalary()==75000);
            } finally {
                closeEntityManager(em);
            }
        }
    }

    /* Test FUNCTION with stored function 'StoredFunction_In'*/
    public void testFUNCTIONWithStoredFunc() {
        if (!isHermesParser()) {
            warning("testJoinFetchAlias only works with Hermes");
            return;
        }
        if (!supportsStoredFunctions()) {
            warning("this test is not suitable for running on dbs that don't support stored function");
            return;
        } else {
            String jpql = "SELECT e.id FROM Employee e WHERE e.salary = FUNCTION('StoredFunction_In', 75)";
            EntityManager em = createEntityManager();
            Query query;
            try {
                query = em.createQuery(jpql);
                List result = query.getResultList();
                Employee emp = em.find(Employee.class, result.get(0));
                assertTrue("FUNCTION is not working properly with stored function.", emp.getSalary()==75000);

                query = em.createQuery("SELECT FUNCTION('StoredFunction_In', e.id) + 5 FROM Employee e WHERE FUNCTION('StoredFunction_In', 75) + 5 = e.salary");
                query.getResultList();
            } finally {
                closeEntityManager(em);
            }
        }
    }

    /* Test SQL with CAST*/
    public void testSQLCast() {
        if (!isHermesParser()) {
            warning("testSQLCast only works with Hermes");
            return;
        }
        if (!getPlatform().isOracle()) {
            warning("testSQLCast only works with Oracle");
            return;
        }
        EntityManager em = createEntityManager();
        try {
            Query query = em.createQuery("Select SQL('CAST(? AS INTEGER)', e.id) from Employee e where SQL('CAST(? AS INTEGER)', e.id) > 0");
            query.getResultList();
            query = em.createQuery("Select e from Employee e order by SQL('? NULLS LAST', e.id)");
            query.getResultList();
            query = em.createQuery("Select e from Employee e where e.id > 0 AND SQL('? = ? + ?', e.id, e.id, e.id)");
            query.getResultList();
            query = em.createQuery("Select e from Employee e where SQL('1 = 1')");
            query.getResultList();
            query = em.createQuery("Select e from Employee e where SQL('? = ?', 1, 2)");
            query.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    /* Test CAST function in JPQL. */
    public void testCast() {
        if (!isHermesParser()) {
            warning("testCast only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        try {
           Query query = null;
           if (getDatabaseSession().getPlatform().isOracle()) {
               query = em.createQuery("Select cast(e.firstName as char) from Employee e where cast(e.firstName as char) = 'Bob'");
           } else {
               query = em.createQuery("Select cast(e.firstName as char(3)) from Employee e where cast(e.firstName as char(3)) = 'Bob'");
           }
           query.getResultList();
           // Most databases require a size on char.
           if (getDatabaseSession().getPlatform().isMySQL()) {
               query = em.createQuery("Select cast(e.firstName as char) from Employee e where cast(e.firstName as char) = 'Bob'");
               query.getResultList();
           }
       } finally {
           closeEntityManager(em);
       }
    }

    /* Test REGEXP function in JPQL. */
    public void testRegexp() {
        if (!isHermesParser()) {
            warning("testRegexp only works with Hermes");
            return;
        }
        if (!getDatabaseSession().getPlatform().isOracle() && !getDatabaseSession().getPlatform().isMySQL()) {
            warning("REGEXP only supported on Oracle, MySQL.");
            return;
        }
        EntityManager em = createEntityManager();
        try {
            Query query = em.createQuery("Select e from Employee e where e.firstName regexp '^B.*'");
            query.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    /* Test NULLS FIRST function in JPQL. */
    public void testNullOrdering() {
        if (!isHermesParser()) {
            warning("testNullOrdering only works with Hermes");
            return;
        }
        if (!getDatabaseSession().getPlatform().isOracle()) {
            warning("NULLS FIRST only supported on Oracle.");
            return;
        }
        EntityManager em = createEntityManager();
        try {
            Query query = em.createQuery("Select e from Employee e order by e.id desc");
            query.getResultList();
            query = em.createQuery("Select e from Employee e order by e.id asc");
            query.getResultList();
            query = em.createQuery("Select e from Employee e order by e.id");
            query.getResultList();
            query = em.createQuery("Select e from Employee e order by e.id asc nulls first");
            query.getResultList();
            query = em.createQuery("Select e from Employee e order by e.id desc nulls last");
            query.getResultList();
            query = em.createQuery("Select e from Employee e order by e.id nulls last");
            query.getResultList();
            query = em.createQuery("Select e from Employee e order by e.id nulls last, e.firstName nulls first");
            query.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    /* Test EXTRACT function in JPQL. */
    public void testExtract() {
        if (!isHermesParser()) {
            warning("testExtract only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        try {
            Query query = em.createQuery("Select extract(year from e.period.startDate) from Employee e where extract(day from e.period.startDate) = 4");
            query.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    /* Test UNION function in JPQL. */
    public void testUnion() {
        if (!isHermesParser()) {
            warning("testUnion only works with Hermes");
            return;
        }
        if (getDatabaseSession().getPlatform().isMySQL() || getDatabaseSession().getPlatform().isSybase() || getDatabaseSession().getPlatform().isSymfoware() ) {
            warning("INTERSECT not supported on this database.");
            return;
        }
        EntityManager em = createEntityManager();
        try {
            Query query = em.createQuery("Select a from Address a where a.city = 'Ottawa'"
            		+ " union Select a2 from Address a2"
                        + " union all Select a2 from Address a2"
            		+ " intersect Select a from Address a where a.city = 'Ottawa'"
                        + " except Select a from Address a where a.city = 'Ottawa'");
            List result = query.getResultList();
            if (result.size() > 0) {
                fail("Expected no results: " + result);
            }
        } finally {
            closeEntityManager(em);
        }

    }

    /* Test COLUMN */
    public void testCOLUMN() {
        if (!isHermesParser()) {
            warning("testCOLUMN only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        try {
            Query query = em.createQuery("Select COLUMN('F_NAME', e) from Employee e where COLUMN('EMP_ID', e) > 0");
            query.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    /* Test OPERATOR with CAST*/
    public void testOPERATOR() {
        if (!isHermesParser()) {
            warning("testOPERATOR only works with Hermes");
            return;
        }
        if (!getPlatform().isOracle()) {
            warning("testOPERATOR only works with Oracle");
            return;
        }
        EntityManager em = createEntityManager();
        try {
            Query query = em.createQuery("Select OPERATOR('Ceil', e.id) from Employee e where OPERATOR('Least', e.id, 0) > 0");
            query.getResultList();
            query = em.createQuery("Select e from Employee e order by OPERATOR('Mod', e.id, 2)");
            query.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    /* Test FUNC with specific MySQL functions*/
    public void testFuncWithMySQLFuncs(){
        if(!getServerSession().getPlatform().isMySQL()) {
            warning("The test testFuncWithMySQLFuncs is supported on MySQL only");
            return;
        }else{
            String[] jpqlStrings = {"Select e.id from Employee e Order By FUNC('UNHEX', e.firstName)",
                                    "Select FUNC('CRC32', e.lastName) from Employee e",
                                    "Select FUNC('BINARY', e.firstName) from Employee e",
                                    "Select FUNC('ENCRYPT', e.firstName), FUNC('ENCRYPT', e.lastName) from Employee e",
                                    "SELECT FUNC('UTC_DATE'), e.id FROM Employee e",
                                    "SELECT FUNC('CONCAT','GroupA', FUNC('SUBSTRING',e.firstName, 1, 2)) FROM Employee e WHERE TRIM(' ' FROM e.firstName) = e.firstName"
                                   };
            EntityManager em = createEntityManager();
            String errorMsg = "";
            Query query;
            for (int i = 0; i < jpqlStrings.length; i++) {
                try {
                    query = em.createQuery(jpqlStrings[i]);
                    query.getResultList();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    errorMsg += '\t' + jpqlStrings[i] + " - " + ex + '\n';
                }
            }
            closeEntityManager(em);
            if(errorMsg.length() > 0) {
                errorMsg = "Failed:\n" + errorMsg;
                fail(errorMsg);
            }
        }
    }

    /* Test nested functions with FUNC*/
    public void testNestedFUNCs() {
        if (!getServerSession().getPlatform().isOracle()) {
            warning("The test testNestedFUNCs is supported on Oracle only");
            return;
        } else {
            String jpqlString = "select FUNC('NVL',FUNC('TO_NUMBER', FUNC('DECODE', FUNC('SUBSTRB', e.firstName,1,1),' ', NULL,FUNC('SUBSTRB',e.lastName,1,10)), null), -99) from Employee e";
            EntityManager em = createEntityManager();
            Query query;
            try {
                query = em.createQuery(jpqlString);
                query.getResultList();
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("nested fucntions don't work, the error is" + ex.getMessage());
            } finally {
                closeEntityManager(em);
            }
        }
    }

    public void testFunctionInSelect() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select UPPER(e.firstName) from Employee e");
        query.getResultList();
        query = em.createQuery("Select AVG(e.salary + 10) + 10 from Employee e");
        query.getResultList();
        query = em.createQuery("Select ABS(MAX(ABS(e.salary))) + 10 from Employee e");
        query.getResultList();
        query = em.createQuery("Select new org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail(e.firstName, e.lastName) from Employee e");
        query.getResultList();
        query = em.createQuery("Select new org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail(UPPER(e.firstName), e.lastName) from Employee e");
        query.getResultList();
        query = em.createQuery("Select new org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite.EmployeeDetail(MAX(e.firstName), MAX(e.lastName)) from Employee e");
        query.getResultList();
        query = em.createQuery("Select 1 from Employee e");
        query.getResultList();
        // Bug#354344
        if (!isHermesParser()) {
            warning("testFunctionInSelect only works with Hermes");
            return;
        }
        query = em.createQuery("Select concat(e2.firstName, e2.lastName) from Employee e2");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.firstName in (Select concat(e2.firstName, e2.lastName) from Employee e2)");
        query.getResultList();
        closeEntityManager(em);
    }

    public void testFunctionInOrderBy() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e order by UPPER(e.firstName)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e order by e.salary / 2");
        query.getResultList();
        query = em.createQuery("Select COUNT(a) from Employee e join e.address a group by a order by COUNT(a) desc");
        query.getResultList();
        query = em.createQuery("SELECT wt FROM WrapperTypes wt order by wt.booleanData");
        // Cast to ensure that server test compile picks up class.
        List<WrapperTypes> result = query.getResultList();
        result.toString();
        closeEntityManager(em);
    }

    public void testFunctionInGroupBy() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select UPPER(e.firstName), COUNT(e) from Employee e group by UPPER(e.firstName)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e group by e");
        query.getResultList();
        query = em.createQuery("SELECT e FROM Employee e JOIN e.phoneNumbers p GROUP BY e");
        query.getResultList();
        query = em.createQuery("SELECT e, COUNT(p) FROM Employee e JOIN e.projects p GROUP BY e HAVING COUNT(p) >= 2");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 350597
    // Test that subselects can be used in the groupby clause.
    public void testSubselectInGroupBy() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e.firstName, COUNT(e) from Employee e group by e.firstName having count(e) > (Select count(e2) from Employee e2)");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 350597
    // Test that subselects can be used in the select clause.
    public void testSubselectInSelect() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testSubselectInSelect is skipped on this platform, , "
            + "Symfoware doesn't support sub-select. (see rfe 372172)");
            return;
        }
        if (!isHermesParser()) {
            warning("testSubselectInSelect only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select (Select count(e2) from Employee e2 where e2.firstName = e.firstName), e.firstName from Employee e");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 350597
    // Test that subselects can be used in the from clause.
    public void testSubselectInFrom() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select avg(e3.c) from Employee e, (Select count(e2) as c, e2.firstName from Employee e2 group by e2.firstName) e3 where e.firstName = e3.firstName");
        query.getResultList();
        closeEntityManager(em);
    }

    // Test using parameters on the left vs right.
    public void testLeftParameters() {
        EntityManager em = createEntityManager();
        // Bug 378313 - JPQL: query fails on Symfoware
        if (((Session) JUnitTestCase.getServerSession()).getPlatform().isSymfoware())
        {
            warning("The test 'testLeftParameters' is not supported on Symfoware, "
                        + "Symfoware doesn't support SQL left parameters. ");
            closeEntityManager(em);
            return;
        }
        Query query = em.createQuery("Select e from Employee e where :id = 1");
        query.setParameter("id", 1);
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e = :e");
        Employee e = new Employee();
        e.setId(1234);
        query.setParameter("e", e);
        query.getResultList();
        query = em.createQuery("Select e from Employee e where :e = e");
        query.setParameter("e", e);
        query.getResultList();
        query = em.createQuery("Select e from Employee e where :a = e.address");
        Address a = new Address();
        a.setID(1234);
        query.setParameter("a", a);
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address = e.address");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where (Select a from Address a where a = e.address) = e.address");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where (Select a.ID from Address a where a = e.address) = (Select a2.ID from Address a2 where a2 = e.address)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address is null");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address = null");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where null = e.address");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where null = e");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 243698
    // Test that a full class name can be used in the from clause.
    public void testClassNameInFrom() {
        if (!isHermesParser()) {
            warning("testClassNameInFrom only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from org.eclipse.persistence.testing.models.jpa.advanced.Employee e where e.id > 0");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 350597 - fixed in Hermes.
    // Test that an alias not used in the where clause is not ignored.
    public void testParralelFrom() {
        if (!isHermesParser()) {
            warning("testParralelFrom only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select count(e) from Employee e");
        long count = (Long)query.getSingleResult();
        Query query2 = em.createQuery("Select count(e) from Employee e, Employee e2");
        long count2 = (Long)query2.getSingleResult();
        if ((count * count) != count2) {
            fail("Incorrect count returned from parralel, got: " + count2 + " expected: n^2 " + count);
        }
        closeEntityManager(em);
    }

    // Bug 333645
    // Test that brackets are parsed when using a group by and an in.
    public void testGroupByInIn() {
        if (!isHermesParser()) {
            warning("testGroupByInIn only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.id <> 4 and (e.id in (select max(e2.id) from Employee e2 group by e2.id having max(e2.id) > 1))");
        //Query query = em.createQuery("Select e from Employee e where e.id <> 4 and (e.id in (select max(e2.id) from Employee e2 group by e2.id having max(e2.id) > 1) or not exists (select e3 from Employee e3))");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 346729
    // Test that distinct order by columns are not duplicated (fails on SQL Server 2005)
    public void testDistinctOrderByEmbedded() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select distinct e from Employee e order by e.period.startDate");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 321722
    // Test that join fetch allows an alias.
    public void testJoinFetchAlias() {
        if (!isHermesParser()) {
            warning("testJoinFetchAlias only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e join fetch e.address a order by a.city");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 312146
    // Test join on clause
    public void testOnClause() {
        if (!isHermesParser()) {
            warning("testOnClause only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e left join e.address a on a.city = 'Ottawa'");
        int size = query.getResultList().size();
        query = em.createQuery("Select e from Employee e left join e.address a where a.city = 'Ottawa'");
        int size2 = query.getResultList().size();
        if (size == size2) {
            fail("ON clause did not contain join");
        }
        query = em.createQuery("Select e from Employee e join e.address a on (a.city = 'Ottawa')");
        size = query.getResultList().size();
        if (size != size2) {
            fail("ON clause join not used");
        }
        query = em.createQuery("Select e from Employee e left join e.manager m on m.id > 0 join m.address a on a.city = 'Ottawa' where a.city <> 'Ottawa'");
        query.getResultList();
        query = em.createQuery("Select e from Employee e left join Employee e2 on e.id = e2.id");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 321722
    // Test that nested join fetching works through using dot notation.
    public void testNestedJoinFetch() {
        if (!isHermesParser()) {
            warning("testNestedJoinFetch only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e join fetch e.manager.address");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 321722
    // Test that nested join fetching works through using an alias.
    public void testNestedJoinFetchAlias() {
        if (!isHermesParser()) {
            warning("testNestedJoinFetchAlias only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e join fetch e.manager m join fetch m.address");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 362063
    // Test nested subqueries.
    public void testNestedSubqueries() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.id = (Select Max(e2.id) from Employee e2 join e2.phoneNumbers p where e2.id = e.id and p.type in (Select p2.type from PhoneNumber p2))");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 303767 - fixed with Hermes.
    // Test that the same attribute can be joined and join fetched.
    public void testJoinFetchWithJoin() {
        if (!isHermesParser()) {
            warning("testJoinFetchWithJoin only works with Hermes");
            return;
        }
        clearCache();
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e join fetch e.phoneNumbers join e.phoneNumbers p where p.areaCode <> '613'");
        Employee employee = (Employee)query.getResultList().get(0);
        if (isWeavingEnabled()) {
            if ((employee.getPhoneNumbers() instanceof IndirectCollection) && !((IndirectCollection)employee.getPhoneNumbers()).isInstantiated()) {
                fail("Join fetch did not occur.");
            }
        }
        clearCache();
        em.clear();
        query = em.createQuery("Select e from Employee e join e.phoneNumbers p join fetch e.phoneNumbers where p.areaCode <> '613'");
        employee = (Employee)query.getResultList().get(0);
        if (isWeavingEnabled()) {
            if ((employee.getPhoneNumbers() instanceof IndirectCollection) && !((IndirectCollection)employee.getPhoneNumbers()).isInstantiated()) {
                fail("Join fetch did not occur.");
            }
        }
        closeEntityManager(em);
    }

    // Bug 331124
    // Test that join to elemenet collections work.
    public void testElementCollection() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select b from Buyer b join b.creditLines l where l > 0");
        query.getResultList();
        query = em.createQuery("Select b from Buyer b join b.creditLines l where Key(l) <> ''");
        query.getResultList();
        query = em.createQuery("Select d from Department d join d.competencies c where c.rating > 0");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.commentLookup c where KEY(c).number > 0");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.commentLookup c where c <> ''");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.redStripes r where r.alcoholContent > 0");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.redStripes r where KEY(r) <> ''");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.redStripesByAlcoholContent r where KEY(r) > 0");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.redStripesByAlcoholContent r where r.alcoholContent > 0");
        query.getResultList();
        query = em.createQuery("Select b from Buyer b join b.creditLines l where l in :arg");
        List args = new ArrayList();
        args.add(0);
        args.add(1);
        query.setParameter("arg", args);
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 331969
    // Test that the correct aliases are used in the select clause when joining the same relationship twice.
    // ... even if the correct aliases are used, the SQL result is the same because of join semantics...
    public void testDoubleAggregateManyToMany() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e.id, sum(p1.budget), sum(p2.budget) from Employee e join treat(e.projects as LargeProject) p1 join treat(e.projects as LargeProject) p2 where p1.budget > 200 and p2.budget > 0 group by e.id");
        List<Object[]> results = query.getResultList();
        for (Object[] result : results) {
            if (result[1].equals(result[2])) {
                //fail("Sums should be different: " + result[0] + " : " + result[1]);
            }
        }
        closeEntityManager(em);
    }

    // Bug 347562 - fixed on Hermes.
    // Test group by having works with aggregate functions (it disappears).
    public void testGroupByHavingFunction() {
        if (!isHermesParser()) {
            warning("testGroupByHavingFunction only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e join e.phoneNumbers p group by e having count(p)   > 100");
        if (query.getResultList().size() > 0) {
            fail("Group by not included");
        }
        closeEntityManager(em);
    }

    // Bug 300625
    // Test subselect does not join table twice.
    public void testSubSelect() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.id in (Select p.id from PhoneNumber p where p.owner = e and e.id is not null)");
        query.getResultList().size();
        String sql = query.unwrap(DatabaseQuery.class).getSQLString();
        int index = sql.indexOf("CMP3_EMPLOYEE");
        if (index == -1) {
            fail("CMP3_EMPLOYEE table missing.");
        }
        index = sql.indexOf("CMP3_EMPLOYEE", index + 1);
        if (index != -1) {
            fail("CMP3_EMPLOYEE table incorrectly joined twice.");
        }
        if (isHermesParser()) {
            // Also check that is not null is used.
            // Fixed on Hermes
            index = sql.indexOf("IS NOT NULL");
            if (index == -1) {
                fail("IS NOT NULL missing.");
            }
        } else {
            warning("testSubSelect only works with Hermes");
        }
        closeEntityManager(em);
    }

    // Bug 301741
    // Test subselect does not join table twice.
    // Fixed in Hermes
    public void testSubSelect2() {
        if (!isHermesParser()) {
            warning("testSubSelect2 only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.address in (Select a from e.address a)");
        query.getResultList().size();
        String sql = query.unwrap(DatabaseQuery.class).getSQLString();
        int index = sql.indexOf("CMP3_EMPLOYEE");
        if (index == -1) {
            fail("CMP3_EMPLOYEE table missing.");
        }
        index = sql.indexOf("CMP3_EMPLOYEE", index + 1);
        if (index != -1) {
            fail("CMP3_EMPLOYEE table incorrectly joined twice.");
        }
        index = sql.indexOf("CMP3_ADDRESS");
        if (index == -1) {
            fail("CMP3_ADDRESS table missing.");
        }
        index = sql.indexOf("CMP3_ADDRESS", index + 1);
        if (index != -1) {
            fail("CMP3_ADDRESS table incorrectly joined twice.");
        }
        query = em.createQuery("Select e from Employee e where e.address.ID in (Select a.ID from e.address a)");
        query.getResultList().size();
        sql = query.unwrap(DatabaseQuery.class).getSQLString();
        index = sql.indexOf("CMP3_EMPLOYEE");
        if (index == -1) {
            fail("CMP3_EMPLOYEE table missing.");
        }
        index = sql.indexOf("CMP3_EMPLOYEE", index + 1);
        if (index != -1) {
            fail("CMP3_EMPLOYEE table incorrectly joined twice.");
        }
        query = em.createQuery("Select e from Employee e where e is null");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e is not null");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address in (Select a from Address a where a is null)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address in (Select a from Address a where a is not null)");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 300625
    // Test reserved words can be used in package names.
    public void testOrderPackage() {
        if (!isHermesParser()) {
            warning("testOrderPackage only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select new org.eclipse.persistence.testing.models.jpa.advanced.order.select.where.Order(e.id) from Employee e");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 301905
    // Test subselect does not cause stack overflow.
    public void testSubselectStackOverflow() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select object(e) from Employee e where :id in (select p.id from in(e.projects) p)");
        query.setParameter("id", 123);
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 326848
    // Test that plus nodes can be aliased.
    public void testAliasPlus() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select (e.id + 5) as p from Employee e order by p");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 327848
    // Test functions with parameters.
    public void testFunctionsWithParameters() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e where exists (Select a from e.address a  where UPPER(:arg) = UPPER(:arg2))");
        query.setParameter("arg", "foo");
        query.setParameter("arg2", "FOO");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 307412
    // Test member of.
    public void testMemberOf() {
        if (!isHermesParser()) {
            warning("testMemberOf only works with Hermes");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e, IN(e.managedEmployees) m, IN(m.projects) p where e.id = 1234 and p not member of e.projects");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 303268
    // Test order by with a distinct and object comparison, had issues with expression builder on right.
    public void testOrderByDistinct() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select distinct(e) from Employee e, PhoneNumber p where p.owner = e order by e.firstName");
        query.getResultList();
        closeEntityManager(em);
    }

    // Hermes bug where the input parameter type was not calculated correctly
    public void testSingleEncapsulatedInputParameter() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.address.city in (:city)");
        query.setParameter("city", "Ottawa");
        query.getResultList();
        closeEntityManager(em);
    }

    // Bug 314025
    // Test IN with subselects and objects.
    public void testObjectIn() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.id in (Select e2.id from Employee e2)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e in (Select e2 from Employee e2)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address in (Select a from Address a)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address = (Select a from Address a where a.ID = (Select Max(a2.ID) from Address a2))");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address in (:a, :a2)");
        Address address = new Address();
        address.setID(123);
        query.setParameter("a", address);
        query.setParameter("a2", address);
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address in :a");
        List addresses = new ArrayList();
        addresses.add(address);
        addresses.add(address);
        query.setParameter("a", addresses);
        query.getResultList();
        query = em.createQuery("Select e from Employee e join e.phoneNumbers p where p in (Select p2 from PhoneNumber p2)");
        query.getResultList();
        if (getPlatform().isOracle()) {
            // IN on composite requires arrays, which is not supported on all databases.
            query = em.createQuery("Select e from Employee e join e.phoneNumbers p where p in (:p, :p2)");
            PhoneNumber phone = new PhoneNumber();
            phone.setId(123);
            phone.setType("fax");
            query.setParameter("p", phone);
            query.setParameter("p2", phone);
            query.getResultList();
            query = em.createQuery("Select e from Employee e join e.phoneNumbers p where p in :p");
            List phones = new ArrayList();
            phones.add(phone);
            phones.add(phone);
            query.setParameter("p", phones);
            query.getResultList();
        }
        // NOT IN
        query = em.createQuery("Select e from Employee e where e.id not in (Select e2.id from Employee e2)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e not in (Select e2 from Employee e2)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address not in (Select a from Address a)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address <> (Select a from Address a where a.ID = (Select Max(a2.ID) from Address a2))");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address not in (:a, :a2)");
        query.setParameter("a", address);
        query.setParameter("a2", address);
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.address not in :a");
        query.setParameter("a", addresses);
        query.getResultList();
        query = em.createQuery("Select e from Employee e join e.phoneNumbers p where p not in (Select p2 from PhoneNumber p2)");
        query.getResultList();
        if (getPlatform().isOracle()) {
            // IN on composite requires arrays, which is not supported on all databases.
            query = em.createQuery("Select e from Employee e join e.phoneNumbers p where p not in (:p, :p2)");
            PhoneNumber phone = new PhoneNumber();
            phone.setId(123);
            phone.setType("fax");
            query.setParameter("p", phone);
            query.setParameter("p2", phone);
            query.getResultList();
            query = em.createQuery("Select e from Employee e join e.phoneNumbers p where p not in :p");
            List phones = new ArrayList();
            phones.add(phone);
            phones.add(phone);
            query.setParameter("p", phones);
            query.getResultList();
        }
        closeEntityManager(em);
    }

    // Bug 245652
    // Test distinct on an embeddable.
    public void testEmbeddableDistinct() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select count(e) from Employee e");
        long count = ((Number)query.getSingleResult()).longValue();
        query = em.createQuery("Select count(distinct e) from Employee e");
        query.getResultList();
        query = em.createQuery("Select e, count(distinct e) from Employee e group by e");
        query.getResultList();
        query = em.createQuery("Select g, count(distinct g) from Golfer g group by g");
        query.getResultList();
        query = em.createQuery("Select count(c) from PhoneNumber c");
        long count2 = ((Number)query.getSingleResult()).longValue();
        query = em.createQuery("Select count(distinct p) from PhoneNumber  p");
        query.getResultList();
        query = em.createQuery("Select count(distinct c) from Captain c");
        query.getResultList();
        query = em.createQuery("Select count(distinct c) from Captain c where c.id.name <> ''");
        query.getResultList();
        Object[] results = null;
        if (!getPlatform().isDerby()) {
            query = em.createQuery("Select count(distinct e2), count(distinct e) from Employee e2, Employee e");
            results = (Object[])query.getSingleResult();
            if (!(((Number)results[1]).longValue() == (count))) {
                fail("Employee count not correct: " + count + " was " + results[1]);
            }
            if (!(((Number)results[0]).longValue() == (count))) {
                fail("Employee 2 count not correct: " + count + " was " + results[0]);
            }
        }
        query = em.createQuery("Select count(distinct p), count(distinct e) from PhoneNumber p, Employee e");
        results = (Object[])query.getSingleResult();
        if (!(((Number)results[1]).longValue() == (count))) {
            fail("Employee count not correct: " + count + " was " + results[1]);
        }
        if (!(((Number)results[0]).longValue() == (count2))) {
            // TODO - this does not throw an error, but the result is not correct,
            // should either work, or throw an error.
            if (getPlatform().isMySQL()) {
                fail("PhoneNumber count not correct: " + count2 + " was " + results[0]);
            }
        }
        if (getPlatform().isMySQL()) {
            // This only works on MySQL currently.
            query = em.createQuery("Select count(distinct p) from PhoneNumber p group by p.areaCode");
            query.getResultList();
            query = em.createQuery("Select c, count(distinct c) from Captain c group by c");
            query.getResultList();
        }
        closeEntityManager(em);
    }

    public void testBrackets() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select UPPER(e.firstName) from Employee e");
        query.getResultList();
        query = em.createQuery("Select UPPER(e.firstName) from Employee e");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where ((e.firstName = 'Bob'))");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where (((e.firstName = 'Bob') and (not (e.firstName = 'Bob'))))");
        query.getResultList();
        query = em.createQuery("Select (1 + 2) from Employee e where (((e.firstName = 'Bob') and (not (e.firstName = 'Bob'))))");
        query.getResultList();
        query = em.createQuery("Select (1 + 2) from Employee e where e = e");
        query.getResultList();
        query = em.createQuery("Select (1 + 2) * 1 from Employee e where e = e");
        query.getResultList();
        query = em.createQuery("Select 1 + (2 * 1) from Employee e where e = e");
        query.getResultList();
        closeEntityManager(em);
    }

    public void testComplexLike() {
        if (!getServerSession().getPlatform().isOracle()) {
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e where UPPER(e.firstName) like UPPER('b%')");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.firstName like (Select e2.firstName from Employee e2 where e = e2)");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.firstName like UPPER('b%') escape UPPER('_')");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.salary like '123%'");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.firstName like e.firstName");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.firstName like 'foox_' escape 'x'");
        query.getResultList();
        closeEntityManager(em);
    }

    public void testComplexBetween() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testComplexBetween skipped for this platform, "
                    + "Symfoware doesn't support subquery used in BETWEEN, LIKE, or NULL predicates.");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.firstName between 'L' and 'Z'");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.firstName between UPPER('L') and UPPER('Z')");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.salary between 10 and 10000");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.firstName between (Select e2.firstName from Employee e2 where e = e2) and (Select e3.firstName from Employee e3 where e = e3)");
        query.getResultList();
        closeEntityManager(em);
    }

    public void testComplexIn() {
        if (getDatabaseSession().getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testComplexIn skipped for this platform, "
                    + "Symfoware doesn't support the use of UPPER/LOWER in limit list of IN predicate.");
            return;
        }
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e where e.firstName in (UPPER('L'), LOWER('Z'))");
        query.getResultList();
        // Derby does not support multiple sub-selects in an IN
        if (!(getDatabaseSession().getPlatform().isDerby() || getDatabaseSession().getPlatform().isH2())) {
            query = em.createQuery("Select e from Employee e where e.firstName in ((Select e2.firstName from Employee e2 where e = e2), (Select e3.firstName from Employee e3 where e = e3))");
            query.getResultList();
        }
        query = em.createQuery("Select e from Employee e where e.firstName in :arg");
        query.setParameter("arg", Arrays.asList(new int[]{1,2}));
        query.getResultList();
        closeEntityManager(em);
    }

    public void testQueryKeys() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e.startTime from Employee e where e.startTime = :time");
        query.setParameter("time", new java.util.Date());
        query.getResultList();
        query = em.createQuery("Select boss from Employee e join e.boss boss");
        query.getResultList();
        query = em.createQuery("Select boss from Employee e left join e.boss boss");
        query.getResultList();
        query = em.createQuery("Select e from Employee e where e.boss.firstName like '%'");
        query.getResultList();
        closeEntityManager(em);
    }

    /**
     * Test that id comparisons across relationships avoid the join.
     */
    public void complexOneToOneJoinOptimization()
    {
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            EntityManager em = createEntityManager();
            String jpql = "SELECT e FROM Employee e where e.address.ID = 5";
            Query query = em.createQuery(jpql);
            query.getResultList();
            String sql = (String)counter.getSqlStatements().get(0);
            if (sql.indexOf("CMP3_ADDRESS") != -1) {
                fail("Join to address should have been optimized.");
            }
        } finally {
            if (counter != null) {
                counter.remove();
            }
        }
    }


    // bug 325400
    public void testCountOneToManyQueryKey(){
        EntityManager em = createEntityManager();
        Employee emp = (Employee)em.createQuery("select e from Employee e where e.firstName = 'John' and e.lastName = 'Way'").getSingleResult();
        Long result = (Long)em.createQuery("select count(pn) from Employee e join e.phoneQK pn where e.id = :id").setParameter("id", emp.getId()).getSingleResult();
        assertTrue("Incorrect number of results returned", result.equals(Long.valueOf(2)));
    }

    // Bug 306766
    public void testEnumNullNotNull(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setFirstName("Ellen");
            emp.setPayScale(SalaryRate.MANAGER);
            em.persist(emp);
            em.flush();
            em.clear();
            clearCache();

            List results = em.createQuery("select e from Employee e where (e.payScale = :payScale and not :payScale IS NULL) " +
                    "or (:payScale IS NULL AND e.payScale IS NULL)").setParameter("payScale", SalaryRate.MANAGER).getResultList();
            assertTrue(results.size() == 1);
            assertTrue(((Employee)results.get(0)).getFirstName().equals("Ellen"));

            if (getServerSession().getPlatform().isDerby() || getServerSession().getPlatform().isSymfoware()){
                getServerSession().getSessionLog().log(SessionLog.INFO, "Derby, Symfoware do not support the format 'NULL IS NULL'.");
            } else {
                results = em.createQuery("select e from Employee e where (e.payScale = :payScale and not :payScale IS NULL) " +
                "or (:payScale IS NULL AND e.payScale IS NULL)").setParameter("payScale", null).getResultList();
                assertTrue(results.size() == 15);
            }

            results = em.createQuery("select e from Employee e where (e.payScale = :payScale and not :payScale IS NULL) " +
            "or (:payScale = org.eclipse.persistence.testing.models.jpa.advanced.Employee.SalaryRate.MANAGER)").setParameter("payScale", SalaryRate.MANAGER).getResultList();
            assertTrue(results.size() == 16);
        } finally {
            rollbackTransaction(em);
        }
    }

    //Bug 347592
    /*
     * The test should assert that the following phenomenon does not occur after
     * a row has been locked by T1:
     *
     * - P2 (Non-repeatable read): Transaction T1 reads a row. Another
     * transaction T2 then modifies or deletes that row, before T1 has committed
     * or rolled back.
     */
    public void testPessimisticLock() throws InterruptedException {
        // test uses entity managers in a way that is disallowed in our server framework
        if (isOnServer() || !isSelectForUpateSupported()){
            return;
        }
        EntityManager em = createEntityManager();
        try {
            Query query = em.createQuery("select e from Employee e where e.firstName=:name");
            query.setParameter("name", "Bob");
            Employee bob = (Employee) query.getSingleResult();
            final int bobId = bob.getId();
            clearCache();
            em.clear();

            query = em.createQuery("select e from Employee e where e.id=:id");
            query.setParameter("id", bobId);
            query.setHint(QueryHints.PESSIMISTIC_LOCK, PessimisticLock.Lock);
            beginTransaction(em);
            bob = (Employee) query.getSingleResult();

            final EntityManager em2 = createEntityManager();
            try {
                // P2 (Non-repeatable read)
                Runnable runnable = new Runnable() {
                    public void run() {
                        try {
                            beginTransaction(em2);
                            Query query2 = em2.createQuery("select e from Employee e where e.id = :id");
                            query2.setParameter("id", bobId);
                            query2.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 10);
                            Employee emp = (Employee) query2.getSingleResult(); // might wait for lock to be released
                            emp.setFirstName("Robert");
                            commitTransaction(em2); // might wait for lock to be released
                        } catch (javax.persistence.RollbackException ex) {
                            if (ex.getMessage().indexOf("org.eclipse.persistence.exceptions.DatabaseException") == -1) {
                                ex.printStackTrace();
                                fail("it's not the right exception:" + ex);
                            }
                        }
                    }
                };

                Thread t2 = new Thread(runnable);
                t2.start();
                Thread.sleep(1000); // allow t2 to attempt update
                em.refresh(bob);
                assertTrue("pessimistic lock failed: parallel transaction modified locked entity (non-repeatable read)", "Bob".equals(bob.getFirstName()));
                rollbackTransaction(em); // release lock
                t2.join(); // wait until t2 finished
            } finally {
                if (isTransactionActive(em2)) {
                    rollbackTransaction(em2);
                }
                closeEntityManager(em2);
            }
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    // Bug 331575
    public void testAliasedFunction(){
        EntityManager em = createEntityManager();
        Query query = em.createQuery("select MOD(e.salary, 10) as m from Employee e where m = 0");
        List result = query.getResultList();
        assertTrue("incorrect results returned for /", result.size() == 9);

        query = em.createQuery("select e.salary / 10D s from Employee e where s > 1000");
        result = query.getResultList();
        assertTrue("incorrect results returned for /", result.size() == 12);

        query = em.createQuery("select Type(p) t from Project p where t = LargeProject");
        result = query.getResultList();
        assertTrue("incorrect results returned for Type", result.size() == 5);

        closeEntityManager(em);
    }

    public void testComplexPathExpression() {
       EntityManager em = createEntityManager();
       Query query = em.createQuery("select e from Employee e join e.projects p where treat(p as LargeProject).budget > 10000");
       query.getResultList();
       closeEntityManager(em);
    }
}
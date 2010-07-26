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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.clientserver.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class QueryFrameworkTestSuite extends TestSuite {
    public QueryFrameworkTestSuite() {
        setDescription("This suite tests all of the functionality of the query framework.");
    }

    public QueryFrameworkTestSuite(boolean isSRG) {
        super(isSRG);
        setDescription("This suite tests all of the functionality of the query framework.");
    }

    public void addTests() {
        addSRGTests();
        //Add new tests here, if any.
        addTest(new ServerSessionTestAdapter(new PessimisticLockNoLockJoinedTest()));
        addTest(new ReadAllNoDistinctTest());
        addTest(new PartialAttributeTestWithJoinAttribute());
        addTest(new PartialAttributeDistinctOrderByTest());
        addTest(new FourPartialAttributeTestsWithJoinAttribute());
        addTest(buildReadOnlyQueryTest());
        addTest(buildGetSQLTest());
        addTest(buildJoinSubclassesQueryTest());
        addTest(buildRecordTest());
        // Bug 6450867 - TOPLINK-6069 ON READOBJECTQUERY ON ENTITY USING MULTIPLE TABLE MAPPING
        addTest(new ConformResultsWithMultitableAndJoiningTest());
        // EL Bug 235484 - EclipseLink is holding onto old unit of work
        addTest(new CachedUpdateCallsQueryClearTest());
        addTest(new ZeroPrimaryKeyExistenceCheckTest(true));
        addTest(new ZeroPrimaryKeyExistenceCheckTest(false));
        // EL Bug 244241 - connection not released on query timeout when cursor used
        addTest(new QueryTimeoutConnectionReleasedTest());
        // EL Bug 245448 - Add regression tests for querying across  relationships using nested 
        // joining and DailyCacheInvalidationPolicy
        addTest(new QueryExecutionTimeSetOnBuildObjectTest());
        // EL Bug 245986 - Add regression testing for queries using custom SQL and partial attribute population
        addTest(new PartialAttributeWithCustomSQLTest());
        addTest(buildArgumentValuesTest());
        addTest(new ScrollableCursorForwardOnlyResultSetTest()); // Bug 309142
    }

    //SRG test set is maintained by QA only, do NOT add any new test cases into it.
    public void addSRGTests() {
        ReadAllQuery raq2 = new ReadAllQuery();
        raq2.setReferenceClass(Employee.class);
        raq2.useCollectionClass(ArrayList.class);
        addTest(new CollectionReadAllTest(Employee.class, 12, raq2));

        ReadAllQuery raq3 = new ReadAllQuery();
        raq3.setReferenceClass(Employee.class);
        raq3.useCollectionClass(Vector.class);
        addTest(new CollectionReadAllTest(Employee.class, 12, raq3));

        ReadAllQuery raq4 = new ReadAllQuery();
        raq4.setReferenceClass(Employee.class);
        raq4.useCollectionClass(HashSet.class);
        addTest(new CollectionReadAllTest(Employee.class, 12, raq4));

        ReadAllQuery raq6 = new ReadAllQuery();
        raq6.setReferenceClass(Employee.class);
        raq6.useMapClass(HashMap.class, "getFirstName");
        addTest(new CollectionReadAllTest(Employee.class, 12, raq6));

        ReadAllQuery raq9 = new ReadAllQuery();
        raq9.setReferenceClass(Employee.class);
        raq9.useMapClass(Hashtable.class, "getId");
        addTest(new CollectionReadAllTest(Employee.class, 12, raq9));

        ReadAllQuery raq10 = new ReadAllQuery();
        raq10.setReferenceClass(Employee.class);
        raq10.useCollectionClass(ArrayList.class);
        addTest(new CollectionReadAllTest(Employee.class, 12, raq10));

        ReadAllQuery raq20 = new ReadAllQuery();
        raq20.setReferenceClass(Employee.class);
        raq20.useMapClass(Hashtable.class, "getFirstName");
        addTest(new MapReadAllTest(Employee.class, 12, raq20));

        ReadAllQuery raq21 = new ReadAllQuery();
        raq21.setReferenceClass(Employee.class);
        raq21.useMapClass(TreeMap.class, "getId");
        addTest(new MapReadAllTest(Employee.class, 12, raq21));

        addTest(new RefreshNoCacheWithNoIdentityMapTest());
        addTest(new ShallowModifyTest());
        addTest(new DontMaintainCacheTest());
        addTest(new CursoredStreamTest(Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("address").get("city").equal("Ottawa")));
        addTest(new CursoredStreamTest(Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("firstName").like("B%")));
        addTest(new CursoredStreamTest(LargeProject.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("teamLeader").get("firstName").like("Sarah%")));

        addTest(new CursoredStreamWithUselessConformTest(Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("address").get("city").equal("Ottawa")));
        addTest(new CursoredStreamWithUselessConformTest(Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("firstName").like("B%")));
        addTest(new CursoredStreamWithUselessConformTest(LargeProject.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("teamLeader").get("firstName").like("Sarah%")));

        addTest(new CursoredStreamCustomSQLTest());
        addTest(new CursoredStreamAnyOfTest());
        addTest(new CursoredStreamWithUnitOfWorkTest());
        addTest(new CursoredStreamDistinctTest());

        //addTest(new CursoredStreamReleaseConnectionsTest(Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("address").get("city").equal("Ottawa")));				
        addTest(new CursoredStreamReleaseConnectionsTest(false));
        addTest(new CursoredStreamReleaseConnectionsTest(true));

        addTest(new ScrollableCursorTest(Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("firstName").like("B%")));
        addTest(new ScrollableCursorTest(LargeProject.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("teamLeader").get("firstName").like("Sarah%")));
        addTest(new ScrollableCursorAPITest());
        addTest(new ScrollableCursorBackwardReadingTest());
        addTest(new ScrollableCursorStatementCachingReadTest());
        addTest(new ScrollableCursorNavigationAPITest());
        
        // Cursor conforming tests...
        addTest(new CursoredStreamConformingTest());
        addTest(new CursoredStreamConformingTest(true));
        addTest(new ScrollableCursorBackwardReadingTest(true));
        addTest(new ScrollableCursorNavigationAPITest(true));

        addTest(new OrderingTest());
        addTest(new OrderingMutipleTableTest());
        addTest(new OrderingSuperClassTest());
        addTest(new OrderingByExpressionTest());
        addTest(new OrderingWithAnyOfTest());
        addTest(new ShallowRefreshTest());
        addTest(new RefreshTest());
        addTest(new PessimisticLockTest(ObjectLevelReadQuery.LOCK));
        addTest(new PessimisticLockTest(ObjectLevelReadQuery.LOCK_NOWAIT));
        addTest(new PessimisticLockFineGrainedTest(ObjectLevelReadQuery.LOCK_NOWAIT));
        addTest(new PessimisticLockOutsideUnitOfWorkTest(ObjectLevelReadQuery.LOCK_NOWAIT));
        addTest(new PessimisticLockInheritanceTest(ObjectLevelReadQuery.LOCK_NOWAIT));
        addTest(new ServerSessionTestAdapter(new PessimisticLockJoinedAttributeTest()));
        addTest(new ServerSessionTestAdapter(new PessimisticLockBatchAttributeTest()));
        addTest(new PessimisticLockEmptyTransactionTest());
        addTest(new PessimisticLockIndirectionJoiningTest(ObjectLevelReadQuery.LOCK_NOWAIT));
        addTest(new PessimisticLockRefreshTest(ObjectLevelReadQuery.LOCK_NOWAIT));
        addTest(new OnlyRefreshIfNewTest());
        addTest(new DeepRefreshTest());
        addTest(new PredefinedQueryReadAllTest(Employee.class, 12));
        addTest(new PredefinedInQueryReadAllTest(Employee.class, 1));
        addTest(new PredefinedQueryReadObjectTest(PopulationManager.getDefaultManager().getObject(Employee.class, "0001")));
        addTest(new PredefinedQueryLikeIgnoreCaseTest());
        addTest(buildPredefinedObjectComparisonTest());
        addTest(buildPredefinedInTest());
        addTest(buildPredefinedEqualNullTest());
        addTest(buildPredefinedContainsSubStringTest());
        addTest(buildPredefinedAnyOfObjectComparisonTest());
        addTest(buildPredefinedObjectTypeTest());
        addTest(buildPredefinedNestedParameterTest());
        addTest(buildPredefinedRedirectorTest());
        addTest(buildPredefinedMethodRedirectorTest());
        addTest(new PredefinedQueryInheritanceTest());
        addTest(new PredefinedQueryToUpperOnParameterTest(PopulationManager.getDefaultManager().getObject(Employee.class, "0001")));
        addTest(new ReloadSelectionObjectTest(new EmployeePopulator().basicEmployeeExample1(), true));
        addTest(new ReloadSelectionObjectTest(new EmployeePopulator().basicEmployeeExample1(), false));
        addTest(new BadQueryTest());
        addTest(new Jdk12ScrollableCursorTest());
        //Bug#2839852  Refreshing is not possible if the query uses checkCacheOnly.        
        addTest(new RefreshWithCheckCacheOnlyTest());

        /** Test cascaded read queries */
        addTest(new CascadingAllCacheTest());
        addTest(new CascadingAllNoCacheTest());
        addTest(new CascadingNoneCacheTest());
        addTest(new CascadingNoneNoCacheTest());
        addTest(new CascadingPrivateCacheTest());
        addTest(new CascadingPrivateNoCacheTest());

        /** Test cascaded modify queries */
        addTest(new DeepModifyTest());
        addTest(new PrivateModifyTest());

        /** Test cascaded delete queries */
        addTest(new DeepDeleteTest());
        addTest(new PrivateDeleteTest());
        addTest(new DoesExistTest());
        addTest(new DataReadQueryTest1());
        addTest(new DirectReadQueryTest1());
        addTest(new ValueReadQueryTest1());

        addTest(new GetValueFromObject());
        addTest(new CursoredStreamClientSessionTest());

        addTest(new QBEObjectWithAllFieldsFilled());
        addTest(new QBESpecialOperators());
        addTest(new QBEExcludedValues());

        // Conforming tests
        // For bug 3568141: shoudn't ignore policy if set to triggerIndirection()
        addTest(new ConformingShouldTriggerIndirectionTest());
        // For bug 3570561: shouldn't throw valueholder exceptions
        addTest(new ConformingThrowConformExceptionsTest());
        addTest(new ConformResultsAcrossOneToOneTest(ConformResultsAcrossOneToOneTest.EQUAL));
        addTest(new ConformResultsAcrossOneToOneTest(ConformResultsAcrossOneToOneTest.NOT_EQUAL));
        addTest(new ConformResultsAcrossOneToOneTest(ConformResultsAcrossOneToOneTest.IS_NULL));
        addTest(new ConformResultsAcrossOneToOneTest(ConformResultsAcrossOneToOneTest.NOT_NULL));
        // Added for bug 3324757: REDIRECT QUERY RESULTS DO NOT CONFORM TO A UNITOFWORK
        addTest(new ConformResultsRedirectorTest());
        addTest(new ConformResultsWithSelectionObjectTest());
        // Added for bug 2782991: Conforming find by primary key takes linear time.
        addTests(ConformResultsWithPrimaryKeyExpressionTest.buildTests());
        addTest(new ConformResultsAcrossOneToManyAcrossOneToOneTest());
        addTest(new ConformResultsAcrossOneToManyTest());
        addTest(new ConformResultsAcrossWrappedOneToManyTest());
        addTest(new ShouldRegisterResultsInUnitOfWorkTest(false));
        addTest(new ShouldRegisterResultsInUnitOfWorkTest(true));
        addTest(new ConformResultsPerformanceTest());

        addTest(new OneToManyMaxRowsSettingTest());

        addTest(new QueryTimeoutTest());
        // Created for BUG# 2745106
        addTest(new QueryExceptionStringTest());
        addTest(new NamedQueriesClientSessionTest());
        addTest(new NamedQueriesDescriptorQueryManagerTest());
        addTest(new NamedQueriesUOWTest());
        addTest(new NamedQueryGetQueryNoArgumentsTest());
        addTest(new ServerClientNamedQueryTest());
        addTest(new GetTableGetFieldTest());
        //code coverage
        addTest(new NamedQueryNotFoundInUOWTest());

        // Created for CR# 4286
        addTest(new DeleteAllQueryTest());
        
        // Created for bug 5840824
        addTest(new DeleteObjectPrivatelyOwningUntriggeredIndirection());

        // Created for BUG# 2692956
        addTest(new RedirectQueryOnUOWTest());
        addTest(new RedirectorOnDescriptorTest());

        //created for BUG# 3037982
        addTest(new SetReferenceClassTest());

        // Created for BUG# 3136413
        addTest(new QueryByExampleInUOWTest());

        addTest(new InheritancePrepareTest());
        addTest(new InheritanceViewPrepareTest());

        // Create for BUG# 3337003
        addTest(new DataReadQueryContainerPolicyTest());

        // New tests added for the UpdateAllQuery feature work
        addTest(new UpdateAllQueryTest());
        addTest(new UpdateAllQueryUOWTest());
        addTest(new UpdateAllQueryUOWTest(false));
        addTest(new UpdateAllQueryExpressionMathTest());
        addTest(new UpdateAllQueryRollbackTest());

        addTest(new UOWgetQueryTest());
        addTest(new CascadeNoBindingOnQuery());
        addTest(new IncorrectPartialAttributeTest());

        addTest(new FirstResultAndMaxRowsTest(0, 0, 12));
        addTest(new FirstResultAndMaxRowsTest(1, 0, 11));
        addTest(new FirstResultAndMaxRowsTest(0, 5, 5));
        addTest(new FirstResultAndMaxRowsTest(2, 5, 3));

        addTest(new QueryCacheTest());

        // Created for Bug 4318924
        addTest(new TranslatedStringsTest());

    }

    /**
     * Test the read-only query feature.
     */
    public TestCase buildReadOnlyQueryTest() {
        TestCase test = new TestCase() {
            public void test() {
                UnitOfWork uow = getSession().acquireUnitOfWork();
                // Test read alls.
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.setIsReadOnly(true);
                List result = (List) uow.executeQuery(query);
                if (((UnitOfWorkImpl)uow).isObjectRegistered(result.get(0))) {
                    throwError("Read-only result was registered.");
                }
                // Test read objects.
                ReadObjectQuery objectQuery = new ReadObjectQuery(result.get(0));
                objectQuery.setIsReadOnly(true);
                Employee employee = (Employee)uow.executeQuery(objectQuery);
                if (((UnitOfWorkImpl)uow).isObjectRegistered(employee)) {
                    throwError("Read-only result was registered.");
                }
                if (employee != result.get(0)) {
                    throwError("Read-only result identity not maintained.");
                }
                // Test object works.
                employee.getAddress();
            }
        };
        test.setName("ReadOnlyQueryTest");
        return test;
    }

    /**
     * Query argument values.
     */
    public TestCase buildArgumentValuesTest() {
        TestCase test = new TestCase() {
            public void test() {
                // Test read alls.
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal(
                        query.getExpressionBuilder().getParameter("firstName")));
                query.addArgument("firstName");
                query.addArgumentValue("Bob");                
                List<Employee> result = (List) getSession().executeQuery(query);
                for (Employee employee : result) {
                    if (!employee.getFirstName().equals("Bob")) {
                        throwError("Incorrect result: " + employee);
                    }
                }
            }
        };
        test.setName("ArgumentValuesTest");
        return test;
    }
    
    /**
     * Test getting the SQL for a parameterized query.
     */
    public TestCase buildGetSQLTest() {
        TestCase test = new TestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                ExpressionBuilder builder = query.getExpressionBuilder();
                query.setSelectionCriteria(builder.get("firstName").equal(builder.getParameter("name")));
                query.addArgument("name");
                Record record = new DatabaseRecord();
                record.put("name", "Bob");
                String sql = query.getTranslatedSQLString(getSession(), record);
                if (sql.indexOf("?") != -1) {
                    throwError("SQL was not translated.");
                }
            }
        };
        test.setName("GetSQLTest");
        return test;
    }

    /**
     * Test the join-subclasses query feature.
     */
    public TestCase buildJoinSubclassesQueryTest() {
        TestCase test = new TestCase() {
            public void test() {
                UnitOfWork uow = getSession().acquireUnitOfWork();
                ReadAllQuery query = new ReadAllQuery(Project.class);
                boolean TYPE_SCROLL_INSENSITIVE_isSupported = true;
                boolean CONCUR_UPDATABLE_isSupported = true;
                if(getSession().getPlatform().isSQLServer()) {
                    // In case either TYPE_SCROLL_INSENSITIVE or CONCUR_UPDATABLE used  
                    // MS SQL Server  Version: 9.00.2050;  MS SQL Server 2005 JDBC Driver  Version: 1.2.2828.100 throws exception:
                    // com.microsoft.sqlserver.jdbc.SQLServerException: The cursor type/concurrency combination is not supported.
                    TYPE_SCROLL_INSENSITIVE_isSupported = false;
                    CONCUR_UPDATABLE_isSupported = false;
                }
                if(getSession().getPlatform().isSymfoware()) {
                    // Symfoware supports updatable cursors, but not in this way. Also,
                    // it considers SQL queries that select from multiple tables as
                    // non-updatable, thus raising an exception for this test.
                    TYPE_SCROLL_INSENSITIVE_isSupported = false;
                    CONCUR_UPDATABLE_isSupported = false;
                }
                if(TYPE_SCROLL_INSENSITIVE_isSupported && CONCUR_UPDATABLE_isSupported) {
                    query.useScrollableCursor();
                } else {
                    ScrollableCursorPolicy policy = new ScrollableCursorPolicy();
                    if(!TYPE_SCROLL_INSENSITIVE_isSupported) {
                        policy.setResultSetType(ScrollableCursorPolicy.TYPE_SCROLL_SENSITIVE);
                    }
                    if(!CONCUR_UPDATABLE_isSupported) {
                        policy.setResultSetConcurrency(ScrollableCursorPolicy.CONCUR_READ_ONLY);
                    }
                    policy.setPageSize(10);
                    query.useScrollableCursor(policy);
                }
                query.setShouldOuterJoinSubclasses(true);
                Cursor result = (Cursor) uow.executeQuery(query);
                result.nextElement();
                result.close();
            }
        };
        test.setName("JoinSubclassesQueryTest");
        return test;
    }
    
    public PredefinedQueryTest buildPredefinedAnyOfObjectComparisonTest() {
        Employee employee = (Employee)PopulationManager.getDefaultManager().getObject(Employee.class, "0002");

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setName("findEmployeeByPhone");
        query.setSelectionCriteria(query.getExpressionBuilder().anyOf("phoneNumbers").equal(query.getExpressionBuilder().getParameter("phone")));
        query.addArgument("phone");

        Vector arguments = new Vector();
        arguments.addElement(employee.getPhoneNumbers().firstElement());

        PredefinedQueryTest test = new PredefinedQueryTest(query, employee, arguments);
        test.setName("PredefinedAnyOfObjectComparisonTest");
        test.setDescription("Test that any of object comparisons can be parameterized.");

        return test;
    }

    public PredefinedQueryTest buildPredefinedContainsSubStringTest() {
        Employee employee = (Employee)PopulationManager.getDefaultManager().getObject(Employee.class, "0002");

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setName("findEmployeeWithNameContaining");
        query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").containsSubstringIgnoringCase(query.getExpressionBuilder().getParameter("name")));
        query.addArgument("name");

        Vector arguments = new Vector();
        arguments.addElement(employee.getFirstName().toLowerCase());

        PredefinedQueryTest test = new PredefinedQueryTest(query, employee, arguments);
        test.setName("PredefinedContainsSubStringTest");
        test.setDescription("Test that complex function can be parameterized.");

        return test;
    }

    public PredefinedQueryTest buildPredefinedEqualNullTest() {
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setName("findEmployeeByName");
        query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal(query.getExpressionBuilder().getParameter("name")));
        query.addArgument("name");
        query.setShouldPrepare(false);

        Vector arguments = new Vector();
        arguments.addElement(null);

        PredefinedQueryTest test = new PredefinedQueryTest(query, null, arguments);
        test.setName("PredefinedEqualNullTest");
        test.setDescription("Test that equal null can be parameterized.");

        return test;
    }

    public PredefinedQueryTest buildPredefinedInTest() {
        Employee employee = (Employee)PopulationManager.getDefaultManager().getObject(Employee.class, "0002");

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setName("findEmployeeByName");
        query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").in(query.getExpressionBuilder().getParameter("names")));
        query.addArgument("names");
        query.setShouldPrepare(false);
        query.bindAllParameters();

        Vector arguments = new Vector();
        Vector names = new Vector();
        names.addElement(employee.getFirstName());
        names.addElement("Hommer-Simpson");
        arguments.addElement(names);

        PredefinedQueryTest test = new PredefinedQueryTest(query, employee, arguments);
        test.setName("PredefinedInTest");
        test.setDescription("Test that in's can be parameterized.");

        return test;
    }

    public PredefinedQueryTest buildPredefinedMethodRedirectorTest() {
        final Employee employee = (Employee)PopulationManager.getDefaultManager().getObject(Employee.class, "0002");

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setName("findEmployeeByAnEmployee");
        query.addArgument("employee");
        MethodBaseQueryRedirector redirector = new MethodBaseQueryRedirector(QueryFrameworkTestSuite.class, "findEmployeeByAnEmployee");
        query.setRedirector(redirector);
        Vector arguments = new Vector();
        arguments.addElement(employee);

        PredefinedQueryTest test = new PredefinedQueryTest(query, employee, arguments);
        test.setName("PredefinedMethodRedirectorTest");
        test.setDescription("Test query redirectors.");

        return test;
    }

    public PredefinedQueryTest buildPredefinedNestedParameterTest() {
        Employee employee = (Employee)PopulationManager.getDefaultManager().getObject(Employee.class, "0002");

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setName("findEmployeeInSameCity");
        query.setSelectionCriteria(query.getExpressionBuilder().get("address").get("city").equal(query.getExpressionBuilder().getParameter("employee").get("address").get("city")));
        query.addArgument("employee");

        Vector arguments = new Vector();
        arguments.addElement(employee);

        PredefinedQueryTest test = new PredefinedQueryTest(query, employee, arguments);
        test.setName("PredefinedNestedParameterTest");
        test.setDescription("Test nested parameters.");

        return test;
    }

    public PredefinedQueryTest buildPredefinedObjectComparisonTest() {
        Employee employee = (Employee)PopulationManager.getDefaultManager().getObject(Employee.class, "0002");

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setName("findEmployeeByAddress");
        query.setSelectionCriteria(query.getExpressionBuilder().get("address").equal(query.getExpressionBuilder().getParameter("address")));
        query.addArgument("address");

        Vector arguments = new Vector();
        arguments.addElement(employee.getAddress());

        PredefinedQueryTest test = new PredefinedQueryTest(query, employee, arguments);
        test.setName("PredefinedObjectComparisonTest");
        test.setDescription("Test that object comparisons can be parameterized.");

        return test;
    }

    public PredefinedQueryTest buildPredefinedObjectTypeTest() {
        Employee employee = (Employee)PopulationManager.getDefaultManager().getObject(Employee.class, "0002");

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setName("findEmployeeByGender");
        query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal(query.getExpressionBuilder().getParameter("firstName")).and(query.getExpressionBuilder().get("gender").equal(query.getExpressionBuilder().getParameter("gender"))));
        query.addArgument("firstName");
        query.addArgument("gender");

        Vector arguments = new Vector();
        arguments.addElement(employee.getFirstName());
        arguments.addElement(employee.getGender());

        PredefinedQueryTest test = new PredefinedQueryTest(query, employee, arguments);
        test.setName("PredefinedObjectTypeTest");
        test.setDescription("Test that object type mappings can be parameterized.");

        return test;
    }

    public PredefinedQueryTest buildPredefinedRedirectorTest() {
        final Employee employee = (Employee)PopulationManager.getDefaultManager().getObject(Employee.class, "0002");

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setName("findEmployeeByEmployee");
        query.addArgument("employee");
        query.setRedirector(new QueryRedirector() {
                public Object invokeQuery(DatabaseQuery query, org.eclipse.persistence.sessions.Record arguments, org.eclipse.persistence.sessions.Session session) {
                    return arguments.get("employee");
                }
            });

        Vector arguments = new Vector();
        arguments.addElement(employee);

        PredefinedQueryTest test = new PredefinedQueryTest(query, employee, arguments);
        test.setName("PredefinedRedirectorTest");
        test.setDescription("Test query redirectors.");

        return test;
    }
    
    /**
     * Test the record Map API.
     */
    public TestCase buildRecordTest() {
        TestCase test = new TestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.setShouldIncludeData(true);
                ComplexQueryResult result = (ComplexQueryResult) getSession().executeQuery(query);
                DatabaseRecord record = (DatabaseRecord)((List)result.getData()).get(0);
                Iterator keySetKeys = record.keySet().iterator();
                Iterator keys = record.getFields().iterator();
                while (keys.hasNext() || keySetKeys.hasNext()) {
                    if (keys.next() != keySetKeys.next()) {
                        throwError("KeySet is incorrect.");
                    }
                }
                Iterator valuesSetValues = record.values().iterator();
                Iterator values = record.getValues().iterator();
                while (values.hasNext() || valuesSetValues.hasNext()) {
                    if (values.next() != valuesSetValues.next()) {
                        throwError("ValuesSet is incorrect.");
                    }
                }
                Iterator entries = record.entrySet().iterator();
                keys = record.getFields().iterator();
                values = record.getValues().iterator();
                while (entries.hasNext() || keys.hasNext()) {
                    Map.Entry entry = (Map.Entry)entries.next();
                    if ((entry.getKey() != keys.next()) || (entry.getValue() != values.next())) {
                        throwError("EntrySet is incorrect.");
                    }
                }
            }
        };
        test.setName("BuildRecordTest");
        return test;
    }

    public static Object findEmployeeByAnEmployee(DatabaseQuery query, org.eclipse.persistence.sessions.DatabaseRecord arguments, org.eclipse.persistence.sessions.Session session) {
        ((ReadObjectQuery)query).setSelectionObject(arguments.get("employee"));

        return session.executeQuery(query);
    }

    public static Object findEmployeeByAnEmployee(org.eclipse.persistence.sessions.Session session, Vector arguments) {
        return arguments.firstElement();
    }
}

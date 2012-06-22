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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.NoExpiryCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.queries.QueryResultsCachePolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.TestExecutor;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

/**
 *  This test system uses the Employee test system to test the integration 
 *  between the Mapping Workbench and the Foundation Library. To do this, it 
 *  writes our test project to an XML file and then reads the XML file and runs 
 *  the employee tests on it.
 *  @author Tom Ware
 */
public class EmployeeWorkbenchIntegrationSystem extends EmployeeSystem {
    public static String PROJECT_FILE = "MWIntegrationTestEmployeeProject";

    // A large enough value to ensure queries do not timeout, but a value we can test for
    public static int QUERY_MANAGER_TIMEOUT = 250;

    private Project initialProject;
    
    /**
     * Override the constructor for employee system to allow us to read and write XML
     */
    public EmployeeWorkbenchIntegrationSystem() {
        super();
        
        buildInitialProject();
        buildProject();
    }

    protected void buildProject() {
        project = WorkbenchIntegrationSystemHelper.buildProjectXML(getInitialProject(), PROJECT_FILE);
    }

    public org.eclipse.persistence.sessions.Project getInitialProject() {
        return initialProject;
    }
    
    protected void buildInitialProject() {
        initialProject = new EmployeeProject();
        Map<Class, ClassDescriptor> descriptors = initialProject.getDescriptors();
        
        // Amend the employee descriptor
        ClassDescriptor employeeDescriptor = descriptors.get(Employee.class);
        DescriptorQueryManager queryManager = employeeDescriptor.getQueryManager();
        
        queryManager.addQuery("PersistenceTestGetEqual", buildPersistenceTestGetEqualQuery());
        queryManager.addQuery("PersistenceTestAnyOfEqual", buildPersistenceTestAnyOfEqualQuery());
        queryManager.addQuery("PersistenceTestGetAllowingNullEqual", buildPersistenceTestGetAllowingNullEqualQuery());
        queryManager.addQuery("PersistenceTestAnyOfAllowingNoneEqual", buildPersistenceTestAnyOfAllowingNoneEqualQuery());
        queryManager.addQuery("PersistenceTestGetGreaterThan", buildPersistenceTestGetGreaterThanQuery());
        queryManager.addQuery("PersistenceTestGetGreaterThanEqual", buildPersistenceTestGetGreaterThanEqualQuery());
        queryManager.addQuery("PersistenceTestGetIsNull", buildPersistenceTestGetIsNullQuery());
        queryManager.addQuery("PersistenceTestGetEqualIgnoringCase", buildPersistenceTestGetEqualIgnoringCaseQuery());
        queryManager.addQuery("PersistenceTestGetLessThan", buildPersistenceTestGetLessThanQuery());
        queryManager.addQuery("PersistenceTestGetLessThanEqual", buildPersistenceTestGetLessThanEqualQuery());
        queryManager.addQuery("PersistenceTestGetLike", buildPersistenceTestGetLikeQuery());
        queryManager.addQuery("PersistenceTestGetNot", buildPersistenceTestGetNotQuery());
        queryManager.addQuery("PersistenceTestGetNotEqual", buildPersistenceTestGetNotEqualQuery());
        queryManager.addQuery("PersistenceTestGetNotLike", buildPersistenceTestGetNotLikeQuery());
        queryManager.addQuery("PersistenceTestGetNotNull", buildPersistenceTestGetNotNullQuery());
        queryManager.addQuery("PersistenceTestEmptyStringAndNull", buildPersistenceTestEmptyStringAndNull());
        queryManager.addQuery("PersistenceTestGreaterThanEqualDate", buildPersistenceTestGreaterThanEqualDateQuery());

        //ReportQuery
        queryManager.addQuery("AddAttributeReportQuery", buildAddAttributeReportQueryTest());
        queryManager.addQuery("AddAverageReportQuery", buildAddAverageReportQueryTest());
        queryManager.addQuery("AddCountReportQuery", buildAddCountReportQueryTest());
        queryManager.addQuery("AddFunctionItemReportQuery", buildAddFunctionItemReportQueryTest());
        queryManager.addQuery("AddGroupingReportQuery", buildAddGroupingReportQueryTest());
        queryManager.addQuery("AddItemReportQuery", buildAddItemReportQueryTest());
        queryManager.addQuery("AddMaximumReportQuery", buildAddMaximumReportQueryTest());
        queryManager.addQuery("AddMinimumReportQuery", buildAddMinimumReportQueryTest());
        queryManager.addQuery("AddStandardDeviationReportQuery", buildAddStandardDeviationReportQueryTest());
        queryManager.addQuery("AddSumReportQuery", buildAddSumReportQueryTest());
        queryManager.addQuery("AddVarianceReportQuery", buildAddVarianceReportQueryTest());
        queryManager.addQuery("AddJoinedObjectLevelReadQuery", buildAddJoinedReportQueryTest());
        queryManager.addQuery("AddOrderingReadAllQuery", buildAddOrderingReportQueryTest());
        queryManager.addQuery("AddBatchReadReadAllQuery", buildAddBatchReadReportQueryTest());

        // Query options        
        buildMemoryQueryReturnConfirmedQuery(queryManager);
        buildMemoryQueryThrowExceptionQuery(queryManager);
        buildMemoryQueryReturnNotConfirmedQuery(queryManager);
        buildMemoryQueryTriggerIndirectionQuery(queryManager);
        buildCacheQueryResultsQuery(queryManager);
        buildRefreshIdentityMapResultsQuery(queryManager);
        buildMaxRowsQuery(queryManager);
        buildFirstResultQuery(queryManager);
        buildQueryTimeOutQuery(queryManager);
        buildUseDistinctQuery(queryManager);
        buildDoNotUseDistinctQuery(queryManager);
        buildShouldPrepareQuery(queryManager);
        buildReadOnlyQuery(queryManager);
        
        // Amend the project descriptor
        ClassDescriptor projectDescriptor = descriptors.get(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        // Postgres throws an error if you try to set the timeout.
        if (!TestExecutor.getDefaultExecutor().getSession().getPlatform().isPostgreSQL()) {
            projectDescriptor.getQueryManager().setQueryTimeout(QUERY_MANAGER_TIMEOUT);
        }
        projectDescriptor.setCacheSynchronizationType(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES); // Setting added for validation only - no tests currently run against this setting - Bug 3599101
        projectDescriptor.setCacheInvalidationPolicy(new NoExpiryCacheInvalidationPolicy()); // Setting added for validation only - no tests currently run against this setting - Bug 3599101
    
        // Query options
        buildJoinSubclassesQuery(projectDescriptor.getQueryManager());

        // Setting invalidation policies
        employeeDescriptor.setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000000));
        descriptors.get(Address.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000000));
        descriptors.get(PhoneNumber.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000000));
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        descriptors.get(LargeProject.class).setCacheInvalidationPolicy(new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), 
                                                                                                                                               calendar.get(Calendar.MINUTE), 
                                                                                                                                               calendar.get(Calendar.SECOND), 
                                                                                                                                               calendar.get(Calendar.MILLISECOND)));
        descriptors.get(SmallProject.class).setCacheInvalidationPolicy(new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), 
                                                                                                                                               calendar.get(Calendar.MINUTE), 
                                                                                                                                               calendar.get(Calendar.SECOND), 
                                                                                                                                               calendar.get(Calendar.MILLISECOND)));
    }

    public static DatabaseQuery buildPersistenceTestGetEqualQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("address").get("city").equal("Toronto");
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetEqualIgnoringCaseQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("address").get("city").equalsIgnoreCase("Toronto");
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestAnyOfEqualQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.anyOf("phoneNumbers").get("areaCode").equal("613");
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetAllowingNullEqualQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.getAllowingNull("address").get("city").equal("Toronto");
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestAnyOfAllowingNoneEqualQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.anyOfAllowingNone("phoneNumbers").get("areaCode").equal("613");
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetGreaterThanQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("salary").greaterThan(0);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetGreaterThanEqualQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("salary").greaterThanEqual(0);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetIsNullQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("address").isNull();
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetLessThanQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("salary").lessThan(15);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetLessThanEqualQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("salary").lessThanEqual(15);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetLikeQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("address").get("city").like("Toronto");
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetNotQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("address").get("city").equal("Toronto").not();
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetNotEqualQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("address").get("city").notEqual("Toronto");
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetNotLikeQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("address").get("city").notLike("Toronto");
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestEmptyStringAndNull() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("firstName").lessThan(null).or(builder.get("firstName").equal(""));
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGetNotNullQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("firstName").notNull();
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildPersistenceTestGreaterThanEqualDateQuery() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        GregorianCalendar month = new GregorianCalendar();
        month.set(2001, 6, 1, 11, 24, 36);
        Expression expression = builder.get("period").get("startDate").greaterThanEqual(month);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(expression);
        return query;
    }

    public static DatabaseQuery buildAddAttributeReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("gender");
        return reportQuery;
    }

    public static DatabaseQuery buildAddAverageReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAverage("salary");
        return reportQuery;
    }

    public static DatabaseQuery buildAddCountReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addCount("id");
        return reportQuery;
    }

    public static DatabaseQuery buildAddFunctionItemReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addFunctionItem("salary", reportQuery.getExpressionBuilder().get("salary"), "AVG");
        return reportQuery;
    }

    public static DatabaseQuery buildAddGroupingReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("gender");
        reportQuery.addGrouping("gender");
        return reportQuery;
    }

    public static DatabaseQuery buildAddItemReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addItem("name", reportQuery.getExpressionBuilder().get("firstName"));
        return reportQuery;
    }

    public static DatabaseQuery buildAddMaximumReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addMaximum("salary");
        return reportQuery;
    }

    public static DatabaseQuery buildAddMinimumReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addMinimum("managerSalary", reportQuery.getExpressionBuilder().get("manager").get("salary"));
        return reportQuery;
    }

    public static DatabaseQuery buildAddStandardDeviationReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addStandardDeviation("salary");
        return reportQuery;
    }

    public static DatabaseQuery buildAddSumReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addSum("salary");
        return reportQuery;
    }

    public static DatabaseQuery buildAddVarianceReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addVariance("id");
        return reportQuery;
    }

    public static DatabaseQuery buildAddJoinedReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("gender");
        reportQuery.addJoinedAttribute("address");
        return reportQuery;
    }

    public static DatabaseQuery buildAddOrderingReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("gender");
        reportQuery.addDescendingOrdering("firstName");
        return reportQuery;
    }

    public static DatabaseQuery buildAddBatchReadReportQueryTest() {
        ReportQuery reportQuery = new ReportQuery(new ExpressionBuilder());
        reportQuery.setReferenceClass(Employee.class);
        reportQuery.addAttribute("gender");
        reportQuery.addBatchReadAttribute("phoneNumbers");
        return reportQuery;
    }

    public void buildMemoryQueryReturnConfirmedQuery(DescriptorQueryManager queryManager) {
        ReadAllQuery namedQuery = new ReadAllQuery(Employee.class);
        namedQuery.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(2));
        queryManager.addQuery("memoryQueryReturnConfirmedQuery", namedQuery);
    }

    public void buildMemoryQueryThrowExceptionQuery(DescriptorQueryManager queryManager) {
        ReadObjectQuery namedQuery = new ReadObjectQuery(Employee.class);
        namedQuery.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
        queryManager.addQuery("memoryQueryThrowExceptionQuery", namedQuery);
    }

    public void buildMemoryQueryReturnNotConfirmedQuery(DescriptorQueryManager queryManager) {
        ReadAllQuery namedQuery = new ReadAllQuery(Employee.class);
        namedQuery.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(3));
        queryManager.addQuery("memoryQueryReturnNotConfirmedQuery", namedQuery);
    }

    public void buildMemoryQueryTriggerIndirectionQuery(DescriptorQueryManager queryManager) {
        ReadAllQuery namedQuery = new ReadAllQuery(Employee.class);
        namedQuery.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(1));
        queryManager.addQuery("memoryQueryTriggerIndirectionQuery", namedQuery);
    }

    public void buildCacheQueryResultsQuery(DescriptorQueryManager queryManager) {
        ReadObjectQuery namedQuery = new ReadObjectQuery(Employee.class);
        namedQuery.setQueryResultsCachePolicy(new QueryResultsCachePolicy());
        queryManager.addQuery("cacheQueryResultsQuery", namedQuery);
    }

    public void buildRefreshIdentityMapResultsQuery(DescriptorQueryManager queryManager) {
        ReadObjectQuery namedQuery = new ReadObjectQuery(Employee.class);
        namedQuery.setShouldRefreshIdentityMapResult(true);
        queryManager.addQuery("refreshIdentityMapResultsQuery", namedQuery);
    }

    public void buildReadOnlyQuery(DescriptorQueryManager queryManager) {
        ReadObjectQuery namedQuery = new ReadObjectQuery(Employee.class);
        namedQuery.setIsReadOnly(true);
        queryManager.addQuery("readOnlyQuery", namedQuery);
    }

    public void buildJoinSubclassesQuery(DescriptorQueryManager queryManager) {
        ReadAllQuery namedQuery = new ReadAllQuery(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        namedQuery.useCursoredStream();
        namedQuery.setShouldOuterJoinSubclasses(true);
        queryManager.addQuery("joinSubclassesQuery", namedQuery);
    }

    public void buildMaxRowsQuery(DescriptorQueryManager queryManager) {
        ReadAllQuery namedQuery = new ReadAllQuery(Employee.class);
        namedQuery.setMaxRows(4);
        queryManager.addQuery("maxRowsQuery", namedQuery);
    }

    public void buildFirstResultQuery(DescriptorQueryManager queryManager) {
        ReadAllQuery namedQuery = new ReadAllQuery(Employee.class);
        namedQuery.setFirstResult(2);
        queryManager.addQuery("firstResultQuery", namedQuery);
    }

    public void buildQueryTimeOutQuery(DescriptorQueryManager queryManager) {
        DataReadQuery namedQuery = new DataReadQuery();
        namedQuery.setSQLString("SELECT SUM(e.EMP_ID) from EMPLOYEE e , EMPLOYEE b, EMPLOYEE c, EMPLOYEE d, EMPLOYEE f, EMPLOYEE g, EMPLOYEE h");
        namedQuery.setQueryTimeout(1);
        queryManager.addQuery("queryTimeOutQuery", namedQuery);
    }

    public void buildUseDistinctQuery(DescriptorQueryManager queryManager) {
        ReadAllQuery namedQuery = new ReadAllQuery(Employee.class);
        namedQuery.setDistinctState((short)1);
        queryManager.addQuery("useDistinctQuery", namedQuery);
    }

    public void buildDoNotUseDistinctQuery(DescriptorQueryManager queryManager) {
        ReadAllQuery namedQuery = new ReadAllQuery(Employee.class);
        namedQuery.setDistinctState((short)2);
        queryManager.addQuery("doNotUseDistinctQuery", namedQuery);
    }

    public void buildShouldPrepareQuery(DescriptorQueryManager queryManager) {
        ReadObjectQuery namedQuery = new ReadObjectQuery(Employee.class);
        queryManager.addQuery("shouldPrepareQuery", namedQuery);
    }
}

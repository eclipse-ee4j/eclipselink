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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.QueryResultsCachePolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;



public class QueryRuntimeProject {

	private Project runtimeProject;

	public QueryRuntimeProject() {
		this.runtimeProject = new Project();
		this.runtimeProject.setName("Query");
		applyLogin();

		this.runtimeProject.addDescriptor(buildEmployeeDescriptor());
		this.runtimeProject.addDescriptor(buildEmploymentPeriodDescriptor());
		this.runtimeProject.addDescriptor(buildPhoneNumberDescriptor());
	}

	public void applyLogin() {
		DatabaseLogin login = new DatabaseLogin();
		login.usePlatform(new MySQLPlatform());
		login.setDriverClassName(TestDatabases.mySQLDriverClassName());
		login.setConnectionString(TestDatabases.mySQLServerURL());
		login.setUserName(TestDatabases.userName());
		login.setPassword(TestDatabases.password());

		// Configuration properties.
		((TableSequence) login.getDefaultSequence()).setTableName("SEQUENCE");
		((TableSequence) login.getDefaultSequence()).setNameFieldName("SEQ_NAME");
		((TableSequence) login.getDefaultSequence()).setCounterFieldName("SEQ_COUNT");
		login.setShouldCacheAllStatements(false);
		login.setUsesByteArrayBinding(true);
		login.setUsesStringBinding(false);
		if (login.shouldUseByteArrayBinding()) { // Can only be used with binding.
			login.setUsesStreamsForBinding(false);
		}
		login.setShouldForceFieldNamesToUpperCase(false);
		login.setShouldOptimizeDataConversion(true);
		login.setShouldTrimStrings(true);
		login.setUsesBatchWriting(false);
		if (login.shouldUseBatchWriting()) { // Can only be used with batch writing.
			login.setUsesJDBCBatchWriting(true);
		}
		login.setUsesExternalConnectionPooling(false);
		login.setUsesExternalTransactionController(false);
		this.runtimeProject.setLogin(login);
	}

	public RelationalDescriptor buildEmployeeDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.query.Employee.class.getName());
		descriptor.addTableName("EMPLOYEE");
		descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

		// Descriptor properties.
		descriptor.useSoftCacheWeakIdentityMap();
		descriptor.setIdentityMapSize(100);
		descriptor.setIsIsolated(false);
		descriptor.setAlias("Employee");

		// Query manager.
		descriptor.getDescriptorQueryManager().checkCacheForDoesExist();

		//Named Queries
		//Named Query -- myQuery9
		ReadObjectQuery namedQuery0 = new ReadObjectQuery();
		namedQuery0.setName("myQuery9");
		namedQuery0.setShouldMaintainCache(true);
		namedQuery0.setShouldOuterJoinSubclasses(false);
		namedQuery0.setCascadePolicy(1);
		namedQuery0.setShouldUseWrapperPolicy(true);
		namedQuery0.setShouldPrepare(true);
		namedQuery0.setMaxRows(0);
		namedQuery0.setShouldBindAllParameters(true);
		namedQuery0.setShouldRefreshIdentityMapResult(false);
		namedQuery0.setCacheUsage(2);
		namedQuery0.setLockMode((short)-1);
		namedQuery0.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery0.setDistinctState((short)0);
		namedQuery0.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		ExpressionBuilder expBuilder0 = namedQuery0.getExpressionBuilder();
		Expression firstExpression = expBuilder0.get("lastName").likeIgnoreCase(new ConstantExpression("Moore", expBuilder0));
		expBuilder0.derivedExpressions = null;
		namedQuery0.setSelectionCriteria(firstExpression.and(expBuilder0.get("manager").get("firstName").equal("").or(expBuilder0.get("lastName").equal(""))));
		descriptor.getDescriptorQueryManager().addQuery("myQuery9", namedQuery0);

		//Named Query -- myQuery8
		ReadObjectQuery namedQuery1 = new ReadObjectQuery();
		namedQuery1.setName("myQuery8");
		namedQuery1.setShouldMaintainCache(true);
		namedQuery1.setCascadePolicy(1);
		namedQuery1.setShouldUseWrapperPolicy(true);
		namedQuery1.setShouldPrepare(true);
		namedQuery1.setMaxRows(0);
		namedQuery1.setShouldBindAllParameters(true);
		namedQuery1.setShouldRefreshIdentityMapResult(false);
		namedQuery1.setCacheUsage(2);
		namedQuery1.setLockMode((short)-1);
		namedQuery1.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery1.setDistinctState((short)0);
		namedQuery1.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		ExpressionBuilder expBuilder1 = namedQuery1.getExpressionBuilder();
		namedQuery1.setSelectionCriteria(expBuilder1.get("firstName").like("Moore"));
		descriptor.getDescriptorQueryManager().addQuery("myQuery8", namedQuery1);

		//Named Query -- myQuery7
		ReadObjectQuery namedQuery2 = new ReadObjectQuery();
		namedQuery2.setName("myQuery7");
		namedQuery2.setCascadePolicy(1);
		namedQuery2.setShouldUseWrapperPolicy(true);
		namedQuery2.setShouldMaintainCache(false);
		namedQuery2.setShouldPrepare(true);
		namedQuery2.setMaxRows(0);
		namedQuery2.setShouldBindAllParameters(true);
		namedQuery2.setShouldRefreshIdentityMapResult(false);
		namedQuery2.setCacheUsage(2);
		namedQuery2.setLockMode((short)-1);
		namedQuery2.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery2.setDistinctState((short)0);
		namedQuery2.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		ExpressionBuilder expBuilder2 = namedQuery2.getExpressionBuilder();
		namedQuery2.setSelectionCriteria(expBuilder2.get("manager").get("lastName").notEqual(""));
		descriptor.getDescriptorQueryManager().addQuery("myQuery7", namedQuery2);

		//Named Query -- myQuery6
		ReadObjectQuery namedQuery3 = new ReadObjectQuery();
		namedQuery3.setName("myQuery6");
		namedQuery3.setQueryTimeout(DescriptorQueryManager.NoTimeout);
		namedQuery3.setShouldMaintainCache(true);
		namedQuery3.setCascadePolicy(1);
		namedQuery3.setShouldUseWrapperPolicy(true);
		namedQuery3.setShouldBindAllParameters(false);
		namedQuery3.setShouldPrepare(true);
		namedQuery3.setMaxRows(7);
		namedQuery3.setShouldRefreshIdentityMapResult(false);
		namedQuery3.setCacheUsage(3);
		namedQuery3.setLockMode((short)-1);
		namedQuery3.setShouldRefreshRemoteIdentityMapResult(true);
		namedQuery3.setDistinctState((short)1);
		namedQuery3.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(3));
		ExpressionBuilder expBuilder3 = namedQuery3.getExpressionBuilder();
		namedQuery3.setSelectionCriteria(expBuilder3.get("firstName").greaterThan("Karen").and(expBuilder3.get("lastName").notLike("moore")).not());
		descriptor.getDescriptorQueryManager().addQuery("myQuery6", namedQuery3);

		//Named Query -- myQuery5
		ReadObjectQuery namedQuery4 = new ReadObjectQuery();
		namedQuery4.setName("myQuery5");
		namedQuery4.setCascadePolicy(1);
		namedQuery4.setQueryTimeout(2);
		namedQuery4.setShouldUseWrapperPolicy(true);
		namedQuery4.setShouldBindAllParameters(false);
		namedQuery4.setShouldCacheStatement(false);
		namedQuery4.setShouldMaintainCache(false);
		namedQuery4.setShouldPrepare(true);
		namedQuery4.setMaxRows(0);
		namedQuery4.setShouldRefreshIdentityMapResult(true);
		namedQuery4.setCacheUsage(1);
		namedQuery4.setLockMode((short)-1);
		namedQuery4.setShouldRefreshRemoteIdentityMapResult(true);
		namedQuery4.setDistinctState((short)1);
		namedQuery4.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(2));
		ExpressionBuilder expBuilder4 = namedQuery4.getExpressionBuilder();
		namedQuery4.setSelectionCriteria(expBuilder4.get("firstName").lessThan("Karen").or(expBuilder4.get("lastName").equalsIgnoreCase(new ConstantExpression("moore", expBuilder4))));
		descriptor.getDescriptorQueryManager().addQuery("myQuery5", namedQuery4);

		//Named Query -- myQuery14
		ReadAllQuery namedQuery5 = new ReadAllQuery();
		namedQuery5.setName("myQuery14");
		namedQuery5.setShouldMaintainCache(true);
		namedQuery5.setCascadePolicy(1);
		namedQuery5.setShouldUseWrapperPolicy(true);
		namedQuery5.setShouldPrepare(true);
		namedQuery5.setMaxRows(0);
		namedQuery5.setShouldBindAllParameters(true);
		namedQuery5.setShouldRefreshIdentityMapResult(false);
		namedQuery5.setCacheUsage(2);
		namedQuery5.setLockMode((short)-1);
		namedQuery5.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery5.setDistinctState((short)0);
		namedQuery5.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		ExpressionBuilder expBuilder5 = namedQuery5.getExpressionBuilder();
		namedQuery5.setSelectionCriteria(expBuilder5.get("firstName").equal("").not());
		descriptor.getDescriptorQueryManager().addQuery("myQuery14", namedQuery5);

		//Named Query -- myQuery4
		ReadAllQuery namedQuery6 = new ReadAllQuery();
		namedQuery6.setName("myQuery4");
		namedQuery6.setCascadePolicy(1);
		namedQuery6.setQueryTimeout(1);
		namedQuery6.setShouldUseWrapperPolicy(true);
		namedQuery6.setShouldBindAllParameters(true);
		namedQuery6.setShouldCacheStatement(true);
		namedQuery6.setShouldMaintainCache(false);
		namedQuery6.setShouldPrepare(true);
		namedQuery6.setMaxRows(1);
		namedQuery6.setShouldRefreshIdentityMapResult(false);
		namedQuery6.setCacheUsage(0);
		namedQuery6.setLockMode((short)-1);
		namedQuery6.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery6.setDistinctState((short)2);
		namedQuery6.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(1));
		namedQuery6.addArgumentByTypeName("arg", java.lang.Integer.class.getName());
		namedQuery6.getQueryMechanism();

        namedQuery6.addJoinedAttribute("manager");
        namedQuery6.addAscendingOrdering("lastName");
        namedQuery6.addDescendingOrdering("firstName");
        namedQuery6.addBatchReadAttribute(namedQuery6.getExpressionBuilder().anyOf("phoneNumbers"));
        descriptor.getDescriptorQueryManager().addQuery("myQuery4", namedQuery6);

		//Named Query -- myQuery13
		ReadObjectQuery namedQuery7 = new ReadObjectQuery();
		namedQuery7.setName("myQuery13");
		namedQuery7.setShouldMaintainCache(true);
		namedQuery7.setCascadePolicy(1);
		namedQuery7.setShouldUseWrapperPolicy(true);
		namedQuery7.setShouldPrepare(true);
		namedQuery7.setMaxRows(0);
		namedQuery7.setShouldBindAllParameters(true);
		namedQuery7.setShouldRefreshIdentityMapResult(false);
		namedQuery7.setCacheUsage(2);
		namedQuery7.setLockMode((short)-1);
		namedQuery7.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery7.setDistinctState((short)0);
		namedQuery7.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		ExpressionBuilder expBuilder7 = namedQuery7.getExpressionBuilder();
		namedQuery7.setSelectionCriteria(expBuilder7.anyOf("phoneNumbers").get("areaCode").equal("123").and(expBuilder7.anyOf("phoneNumbers").get("number").like("848")));
		descriptor.getDescriptorQueryManager().addQuery("myQuery13", namedQuery7);

		//Named Query -- myQuery12
		ReadObjectQuery namedQuery8 = new ReadObjectQuery();
		namedQuery8.setName("myQuery12");
		namedQuery8.setShouldMaintainCache(true);
		namedQuery8.setCascadePolicy(1);
		namedQuery8.setShouldUseWrapperPolicy(true);
		namedQuery8.setShouldPrepare(true);
		namedQuery8.setMaxRows(0);
		namedQuery8.setShouldBindAllParameters(true);
		namedQuery8.setShouldRefreshIdentityMapResult(false);
		namedQuery8.setCacheUsage(2);
		namedQuery8.setLockMode((short)-1);
		namedQuery8.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery8.setDistinctState((short)0);
		namedQuery8.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		ExpressionBuilder expBuilder8 = namedQuery8.getExpressionBuilder();
		namedQuery8.setSelectionCriteria(expBuilder8.anyOf("phoneNumbers").get("areaCode").equal(""));
		descriptor.getDescriptorQueryManager().addQuery("myQuery12", namedQuery8);

		//Named Query -- myQuery3
		ReadObjectQuery namedQuery9 = new ReadObjectQuery();
		namedQuery9.setName("myQuery3");
		namedQuery9.setQueryTimeout(DescriptorQueryManager.NoTimeout);
		namedQuery9.setShouldMaintainCache(true);
		namedQuery9.setCascadePolicy(1);
		namedQuery9.setShouldUseWrapperPolicy(true);
		namedQuery9.setShouldBindAllParameters(true);
		namedQuery9.setShouldCacheStatement(false);
		namedQuery9.setShouldPrepare(true);
		namedQuery9.setQueryResultsCachePolicy(new QueryResultsCachePolicy());
		namedQuery9.setMaxRows(14);
		namedQuery9.setShouldRefreshIdentityMapResult(true);
		namedQuery9.setCacheUsage(5);
		namedQuery9.setLockMode((short)2);
		namedQuery9.setShouldRefreshRemoteIdentityMapResult(true);
		namedQuery9.setDistinctState((short)2);
		namedQuery9.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(3));
		ExpressionBuilder expBuilder9 = namedQuery9.getExpressionBuilder();
		namedQuery9.setSelectionCriteria(expBuilder9.get("firstName").greaterThanEqual(expBuilder9.getParameter("name")).and(expBuilder9.get("id").lessThanEqual(expBuilder9.getParameter("id"))));
		namedQuery9.addArgumentByTypeName("name", java.lang.String.class.getName());
		namedQuery9.addArgumentByTypeName("id", java.lang.Integer.class.getName());
		descriptor.getDescriptorQueryManager().addQuery("myQuery3", namedQuery9);

		//Named Query -- myQuery11
		ReadObjectQuery namedQuery10 = new ReadObjectQuery();
		namedQuery10.setName("myQuery11");
		namedQuery10.setCascadePolicy(1);
		namedQuery10.setShouldUseWrapperPolicy(true);
		namedQuery10.setShouldMaintainCache(false);
		namedQuery10.setShouldPrepare(true);
		namedQuery10.setMaxRows(0);
		namedQuery10.setShouldBindAllParameters(true);
		namedQuery10.setShouldRefreshIdentityMapResult(false);
		namedQuery10.setCacheUsage(2);
		namedQuery10.setLockMode((short)-1);
		namedQuery10.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery10.setDistinctState((short)0);
		namedQuery10.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		ExpressionBuilder expBuilder10 = namedQuery10.getExpressionBuilder();
		namedQuery10.setSelectionCriteria(expBuilder10.get("manager").notNull().and(expBuilder10.anyOf("phoneNumbers").isNull()));
		descriptor.getDescriptorQueryManager().addQuery("myQuery11", namedQuery10);

		//Named Query -- myQuery2
		ReadAllQuery namedQuery11 = new ReadAllQuery();
		namedQuery11.setSQLString("I am a bad SQL query");
		namedQuery11.setName("myQuery2");
		namedQuery11.setShouldMaintainCache(true);
		namedQuery11.setCascadePolicy(1);
		namedQuery11.setQueryTimeout(2);
		namedQuery11.setShouldUseWrapperPolicy(true);
		namedQuery11.setShouldCacheStatement(true);
		namedQuery11.setShouldPrepare(true);
		namedQuery11.setMaxRows(11);
		namedQuery11.setShouldBindAllParameters(true);
		namedQuery11.setShouldRefreshIdentityMapResult(true);
		namedQuery11.setCacheUsage(4);
		namedQuery11.setLockMode((short)1);
		namedQuery11.setShouldRefreshRemoteIdentityMapResult(true);
		namedQuery11.setDistinctState((short)1);
		namedQuery11.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		namedQuery11.addArgumentByTypeName("name", java.lang.String.class.getName());
		namedQuery11.addArgumentByTypeName("age", java.lang.Integer.class.getName());

        descriptor.getDescriptorQueryManager().addQuery("myQuery2", namedQuery11);

		//Named Query -- myQuery10
		ReadObjectQuery namedQuery12 = new ReadObjectQuery();
		namedQuery12.setName("myQuery10");
		namedQuery12.setShouldMaintainCache(true);
		namedQuery12.setCascadePolicy(1);
		namedQuery12.setShouldUseWrapperPolicy(true);
		namedQuery12.setShouldPrepare(true);
		namedQuery12.setMaxRows(0);
		namedQuery12.setShouldBindAllParameters(true);
		namedQuery12.setShouldRefreshIdentityMapResult(false);
		namedQuery12.setCacheUsage(2);
		namedQuery12.setLockMode((short)-1);
		namedQuery12.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery12.setDistinctState((short)0);
		namedQuery12.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		ExpressionBuilder expBuilder12 = namedQuery12.getExpressionBuilder();
		Expression firstExpression12 = expBuilder12.get("lastName");
		expBuilder12.derivedExpressions = null;
		namedQuery12.setSelectionCriteria(firstExpression12.lessThan("Moore").and(expBuilder12.getAllowingNull("manager").anyOf("phoneNumbers").getAllowingNull("owner").get("firstName").equal("Roger").or(expBuilder12.get("lastName").equal("")).not()).not());

        descriptor.getDescriptorQueryManager().addQuery("myQuery10", namedQuery12);

		//Named Query -- myQuery1
		ReadObjectQuery namedQuery13 = new ReadObjectQuery();
		namedQuery13.setName("myQuery1");
		namedQuery13.setShouldMaintainCache(true);
		namedQuery13.setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
		namedQuery13.getQueryMechanism();
		namedQuery13.setShouldBindAllParameters(true);
		descriptor.getDescriptorQueryManager().addQuery("myQuery1", namedQuery13);


        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);//TODO why is our default different than the runtime's default???
        reportQuery.setShouldBindAllParameters(true);
        ExpressionBuilder reportQueryExpBuilder = reportQuery.getExpressionBuilder();
        reportQuery.addAverage("managerName", reportQuery.getExpressionBuilder().get("manager").get("firstName"));
        reportQueryExpBuilder.derivedExpressions = null;
        reportQuery.addCount("managerName", reportQuery.getExpressionBuilder().get("manager").get("firstName"));
        reportQueryExpBuilder.derivedExpressions = null;
        reportQuery.addItem("managerName", reportQuery.getExpressionBuilder().get("manager").get("firstName").distinct());
        reportQueryExpBuilder.derivedExpressions = null;
        reportQuery.addMaximum("managerName", reportQuery.getExpressionBuilder().get("manager").get("firstName"));
        reportQueryExpBuilder.derivedExpressions = null;
        reportQuery.addMinimum("managerName", reportQuery.getExpressionBuilder().get("manager").get("firstName"));
        reportQueryExpBuilder.derivedExpressions = null;
        reportQuery.addSum("managerName", reportQuery.getExpressionBuilder().get("manager").get("firstName"));
        reportQueryExpBuilder.derivedExpressions = null;
        reportQuery.addVariance("managerName", reportQuery.getExpressionBuilder().get("manager").get("firstName"));
        reportQueryExpBuilder.derivedExpressions = null;
        reportQuery.addItem("managerName", reportQuery.getExpressionBuilder().get("manager").get("firstName").getFunction("customFunction"));
        descriptor.getDescriptorQueryManager().addQuery("reportQuery", reportQuery);


        ReportQuery reportQuery2 = new ReportQuery();
        reportQuery2.setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        reportQuery2.setShouldBindAllParameters(true);
        reportQuery2.setShouldReturnSingleAttribute(true);
        reportQuery2.setShouldRetrievePrimaryKeys(true);
        reportQuery2.addAscendingOrdering("firstName");
        reportQuery2.addGrouping("lastName");
        descriptor.getDescriptorQueryManager().addQuery("reportQuery2", reportQuery2);

        ReportQuery reportQuery3 = new ReportQuery();
        reportQuery3.setShouldCacheStatement(true);
        reportQuery3.setShouldBindAllParameters(true);
        reportQuery3.setShouldPrepare(false);
        reportQuery3.setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        reportQuery3.setShouldReturnSingleResult(true);
        reportQuery3.setShouldRetrieveFirstPrimaryKey(true);
        descriptor.getDescriptorQueryManager().addQuery("reportQuery3", reportQuery3);

        ReportQuery reportQuery4 = new ReportQuery();
        reportQuery4.setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        reportQuery4.setShouldBindAllParameters(true);
        reportQuery4.setShouldReturnSingleValue(true);
        reportQuery4.setShouldRetrieveFirstPrimaryKey(true);
        descriptor.getDescriptorQueryManager().addQuery("reportQuery4", reportQuery4);


        // Event manager.

		// Mappings.
		DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setFieldName("EMPLOYEE.F_NAME");
		descriptor.addMapping(firstNameMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setFieldName("EMPLOYEE.EMP_ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
		lastNameMapping.setAttributeName("lastName");
		lastNameMapping.setFieldName("EMPLOYEE.L_NAME");
		descriptor.addMapping(lastNameMapping);

		AggregateObjectMapping periodMapping = new AggregateObjectMapping();
		periodMapping.setAttributeName("period");
		periodMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.query.EmploymentPeriod.class.getName());
		periodMapping.setIsNullAllowed(false);
		periodMapping.addFieldNameTranslation("EMPLOYEE.START_DATE", "startDate->DIRECT");
		periodMapping.addFieldNameTranslation("EMPLOYEE.END_DATE", "endDate->DIRECT");
		descriptor.addMapping(periodMapping);

		OneToOneMapping managerMapping = new OneToOneMapping();
		managerMapping.setAttributeName("manager");
		managerMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.query.Employee.class.getName());
		managerMapping.useBasicIndirection();
		managerMapping.addForeignKeyFieldName("EMPLOYEE.MANAGER_ID", "EMPLOYEE.EMP_ID");
		descriptor.addMapping(managerMapping);

		OneToManyMapping phoneNumbersMapping = new OneToManyMapping();
		phoneNumbersMapping.setAttributeName("phoneNumbers");
		phoneNumbersMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.query.PhoneNumber.class.getName());
		phoneNumbersMapping.useBasicIndirection();
        phoneNumbersMapping.useListClassName("java.util.Vector");
		phoneNumbersMapping.privateOwnedRelationship();
		phoneNumbersMapping.addTargetForeignKeyFieldName("PHONE.EMP_ID", "EMPLOYEE.EMP_ID");
		descriptor.addMapping(phoneNumbersMapping);

		return descriptor;
	}

	public RelationalDescriptor buildEmploymentPeriodDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.query.EmploymentPeriod.class.getName());

		// Descriptor properties.
		descriptor.setAlias("EmploymentPeriod");

		// Query manager.
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping endDateMapping = new DirectToFieldMapping();
		endDateMapping.setAttributeName("endDate");
		endDateMapping.setFieldName("endDate->DIRECT");
		descriptor.addMapping(endDateMapping);

		DirectToFieldMapping startDateMapping = new DirectToFieldMapping();
		startDateMapping.setAttributeName("startDate");
		startDateMapping.setFieldName("startDate->DIRECT");
		descriptor.addMapping(startDateMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPhoneNumberDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.query.PhoneNumber.class.getName());
		descriptor.addTableName("PHONE");
		descriptor.addPrimaryKeyFieldName("PHONE.EMP_ID");
		descriptor.addPrimaryKeyFieldName("PHONE.TYPE");

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIsIsolated(false);
		descriptor.setIdentityMapSize(100);

		// Locking Policy
		TimestampLockingPolicy lockingPolicy = new TimestampLockingPolicy();
		lockingPolicy.setWriteLockField(new DatabaseField("PHONE.P_NUMBER"));
		descriptor.setOptimisticLockingPolicy(lockingPolicy);

		descriptor.setAlias("foo");

		// Query manager.
		descriptor.getDescriptorQueryManager().checkCacheForDoesExist();
		descriptor.getDescriptorQueryManager().setReadObjectSQLString("Read an object");
		descriptor.getDescriptorQueryManager().getReadObjectQuery().setShouldBindAllParameters(true);
		descriptor.getDescriptorQueryManager().setReadAllSQLString("Here's how to Read all");
		descriptor.getDescriptorQueryManager().getReadAllQuery().setShouldBindAllParameters(true);
		descriptor.getDescriptorQueryManager().setInsertSQLString("Inserting stuff");
		descriptor.getDescriptorQueryManager().getInsertQuery().setShouldBindAllParameters(true);
		descriptor.getDescriptorQueryManager().setUpdateSQLString("Updating sql");
		descriptor.getDescriptorQueryManager().getUpdateQuery().setShouldBindAllParameters(true);
		descriptor.getDescriptorQueryManager().setDeleteSQLString("Deleting stuff");
		descriptor.getDescriptorQueryManager().getDeleteQuery().setShouldBindAllParameters(true);

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping areaCodeMapping = new DirectToFieldMapping();
		areaCodeMapping.setAttributeName("areaCode");
		areaCodeMapping.setFieldName("PHONE.AREA_CODE");
		descriptor.addMapping(areaCodeMapping);

		DirectToFieldMapping numberMapping = new DirectToFieldMapping();
		numberMapping.setAttributeName("number");
		numberMapping.setFieldName("PHONE.P_NUMBER");
		numberMapping.readOnly();
		descriptor.addMapping(numberMapping);

		DirectToFieldMapping typeMapping = new DirectToFieldMapping();
		typeMapping.setAttributeName("type");
		typeMapping.setFieldName("PHONE.TYPE");
		descriptor.addMapping(typeMapping);

		OneToOneMapping ownerMapping = new OneToOneMapping();
		ownerMapping.setAttributeName("owner");
		ownerMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.query.Employee.class.getName());
		ownerMapping.useBasicIndirection();
		ownerMapping.addForeignKeyFieldName("PHONE.EMP_ID", "EMPLOYEE.EMP_ID");
		descriptor.addMapping(ownerMapping);

		return descriptor;
	}


	public Project getRuntimeProject() {
		return this.runtimeProject;
	}
}

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


import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;


/**
 * @author kamoore
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ComplexAggregateRuntimeProject {

	private Project runtimeProject;
	public ComplexAggregateRuntimeProject() {
		super();
		this.runtimeProject = new Project();
		this.runtimeProject.setName("ComplexAggregate");
		applyLogin();

		this.runtimeProject.addDescriptor(buildAddressDescriptor());
		this.runtimeProject.addDescriptor(buildAddressDescriptionDescriptor());
		this.runtimeProject.addDescriptor(buildClientDescriptor());
		this.runtimeProject.addDescriptor(buildComputerDescriptor());
		this.runtimeProject.addDescriptor(buildEmployeeDescriptor());
		this.runtimeProject.addDescriptor(buildEvaluationClientDescriptor());
		this.runtimeProject.addDescriptor(buildLanguageDescriptor());
		this.runtimeProject.addDescriptor(buildPeriodDescriptor());
		this.runtimeProject.addDescriptor(buildPeriodDescriptionDescriptor());
		this.runtimeProject.addDescriptor(buildProjectDescriptionDescriptor());
		this.runtimeProject.addDescriptor(buildResponsibilityDescriptor());
	}

	public void applyLogin() {
		DatabaseLogin login = new DatabaseLogin();
		login.usePlatform(new org.eclipse.persistence.platform.database.MySQLPlatform());
		login.setDriverClassName(TestDatabases.mySQLDriverClassName());
		login.setConnectionString(TestDatabases.mySQLServerURL());
		login.setUserName(TestDatabases.userName());
		login.setPassword(TestDatabases.password());

		// Configuration properties.
		//login.setUsesNativeSequencing(false);
		((TableSequence)login.getDefaultSequence()).setTableName("SEQUENCE");
		((TableSequence)login.getDefaultSequence()).setNameFieldName("SEQ_NAME");
		((TableSequence)login.getDefaultSequence()).setCounterFieldName("SEQ_COUNT");
		((TableSequence)login.getDefaultSequence()).setPreallocationSize(50);
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

	public RelationalDescriptor buildAddressDescriptionDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.AddressDescription.class.getName());

		// Descriptor properties.
		descriptor.setAlias("AddressDescription");

		// Query manager.

		//Named Queries

		// Query keys.
		descriptor.addDirectQueryKey("id" , "QUERY_KEY id");

		// Mappings.
		AggregateObjectMapping periodDescriptionMapping = new AggregateObjectMapping();
		periodDescriptionMapping.setAttributeName("periodDescription");
		periodDescriptionMapping.setGetMethodName("getPeriodDescription");
		periodDescriptionMapping.setSetMethodName("setPeriodDescription");
		periodDescriptionMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.PeriodDescription.class.getName());
		periodDescriptionMapping.setIsNullAllowed(true);
		periodDescriptionMapping.addFieldNameTranslation("periodDescription_period_endDate->DIRECT", "period_endDate->DIRECT");
		periodDescriptionMapping.addFieldNameTranslation("periodDescription_period_startDate->DIRECT", "period_startDate->DIRECT");
		descriptor.addMapping(periodDescriptionMapping);

		OneToOneMapping addressMapping = new OneToOneMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Address.class.getName());
		addressMapping.useBasicIndirection();
		addressMapping.privateOwnedRelationship();
		addressMapping.addForeignKeyFieldName("address->EM_ADD_IN_REFERENCE_AGG_EMP_AGG_ADD", "AGG_ADD.ID");
		descriptor.addMapping(addressMapping);

		return descriptor;
	}

	public RelationalDescriptor buildAddressDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Address.class.getName());
		descriptor.addTableName("AGG_ADD");
		descriptor.addPrimaryKeyFieldName("AGG_ADD.ID");

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(100);
		descriptor.setSequenceNumberFieldName("AGG_ADD.ID");
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setAlias("Address");
		descriptor.setIsIsolated(false);

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeNonExistenceForDoesExist();
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping addressMapping = new DirectToFieldMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setFieldName("AGG_ADD.ADDRESS");
		descriptor.addMapping(addressMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setFieldName("AGG_ADD.ID");
		descriptor.addMapping(idMapping);

		return descriptor;
	}

	public RelationalDescriptor buildClientDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Client.class.getName());
		descriptor.addTableName("AGG_CLNT");
		descriptor.addPrimaryKeyFieldName("AGG_CLNT.CL_ID");

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setClassIndicatorFieldName("AGG_CLNT.TYPE");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Client.class.getName(), "Client");
		descriptor.getDescriptorInheritancePolicy().addClassNameIndicator(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.EvaluationClient.class.getName(), "Eval");
		descriptor.getDescriptorInheritancePolicy().dontReadSubclassesOnQueries();

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(100);
		descriptor.setSequenceNumberFieldName("AGG_CLNT.CL_ID");
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setReadOnly();
		descriptor.setAlias("Client");
		descriptor.setIsIsolated(false);

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeNonExistenceForDoesExist();
		descriptor.getDescriptorQueryManager().setReadObjectSQLString("read the object");
		descriptor.getDescriptorQueryManager().getReadObjectQuery().setShouldBindAllParameters(true);
		descriptor.getDescriptorQueryManager().setReadAllSQLString("read everything");
		descriptor.getDescriptorQueryManager().getReadAllQuery().setShouldBindAllParameters(true);
		descriptor.getDescriptorQueryManager().setInsertSQLString("an insert");
		descriptor.getDescriptorQueryManager().getInsertQuery().setShouldBindAllParameters(true);
		descriptor.getDescriptorQueryManager().setUpdateSQLString("an update");
		descriptor.getDescriptorQueryManager().getUpdateQuery().setShouldBindAllParameters(true);
		descriptor.getDescriptorQueryManager().setDeleteSQLString("an update");
		descriptor.getDescriptorQueryManager().getDeleteQuery().setShouldBindAllParameters(true);

		//Named Queries
		//Named Query -- query3
		ReadObjectQuery namedQuery0 = new ReadObjectQuery();
		namedQuery0.setEJBQLString("3");
		namedQuery0.setName("query3");
		namedQuery0.setCascadePolicy(1);
		namedQuery0.setQueryTimeout(-1);
		namedQuery0.setShouldUseWrapperPolicy(true);
		namedQuery0.setShouldBindAllParameters(true);
		namedQuery0.setShouldCacheStatement(true);
		namedQuery0.setShouldMaintainCache(false);
		namedQuery0.setShouldPrepare(true);
		namedQuery0.setMaxRows(0);
		namedQuery0.setShouldRefreshIdentityMapResult(false);
		namedQuery0.setCacheUsage(1);
		namedQuery0.setLockMode((short)-1);
		namedQuery0.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery0.setDistinctState((short)0);
		namedQuery0.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		descriptor.getDescriptorQueryManager().addQuery("query3", namedQuery0);

		//Named Query -- query2
		ReadAllQuery namedQuery1 = new ReadAllQuery();
		namedQuery1.setEJBQLString("2");
		namedQuery1.setName("query2");
		namedQuery1.setCascadePolicy(1);
		namedQuery1.setQueryTimeout(-1);
		namedQuery1.setShouldUseWrapperPolicy(true);
		namedQuery1.setShouldBindAllParameters(true);
		namedQuery1.setShouldCacheStatement(true);
		namedQuery1.setShouldMaintainCache(true);
		namedQuery1.setShouldPrepare(true);
		namedQuery1.setMaxRows(0);
		namedQuery1.setShouldRefreshIdentityMapResult(true);
		namedQuery1.setCacheUsage(0);
		namedQuery1.setLockMode((short)1);
		namedQuery1.setShouldRefreshRemoteIdentityMapResult(true);
		namedQuery1.setDistinctState((short)0);
		namedQuery1.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		namedQuery1.addArgumentByTypeName("arg", org.eclipse.persistence.sessions.DatabaseLogin.class.getName());
		namedQuery1.addArgumentByTypeName("arg2", java.lang.String.class.getName());
		descriptor.getDescriptorQueryManager().addQuery("query2", namedQuery1);

		//Named Query -- query1
		ReadObjectQuery namedQuery2 = new ReadObjectQuery();
		namedQuery2.setEJBQLString("foo");
		namedQuery2.setName("query1");
		namedQuery2.setCascadePolicy(1);
		namedQuery2.setQueryTimeout(-1);
		namedQuery2.setShouldUseWrapperPolicy(true);
		namedQuery2.setShouldBindAllParameters(false);
		namedQuery2.setShouldCacheStatement(false);
		namedQuery2.setShouldMaintainCache(true);
		namedQuery2.setShouldPrepare(true);
		namedQuery2.setMaxRows(0);
		namedQuery2.setShouldRefreshIdentityMapResult(false);
		namedQuery2.setCacheUsage(ObjectLevelReadQuery.CheckCacheByPrimaryKey);
		namedQuery2.setLockMode((short)-1);
		namedQuery2.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery2.setDistinctState((short)0);
		namedQuery2.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		namedQuery2.addArgumentByTypeName("arg", java.lang.String.class.getName());
		descriptor.getDescriptorQueryManager().addQuery("query1", namedQuery2);

		//Named Query -- query6
		ReadObjectQuery namedQuery3 = new ReadObjectQuery();
		namedQuery3.setSQLString("blah qwerty");
		namedQuery3.setName("query6");
		namedQuery3.setCascadePolicy(1);
		namedQuery3.setQueryTimeout(-1);
		namedQuery3.setShouldUseWrapperPolicy(true);
		namedQuery3.setShouldBindAllParameters(false);
		namedQuery3.setShouldCacheStatement(true);
		namedQuery3.setShouldMaintainCache(false);
		namedQuery3.setShouldPrepare(true);
		namedQuery3.setMaxRows(0);
		namedQuery3.setShouldRefreshIdentityMapResult(true);
		namedQuery3.setCacheUsage(5);
		namedQuery3.setLockMode((short)2);
		namedQuery3.setShouldRefreshRemoteIdentityMapResult(true);
		namedQuery3.setDistinctState((short)0);
		namedQuery3.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		descriptor.getDescriptorQueryManager().addQuery("query6", namedQuery3);

		//Named Query -- query5
		ReadAllQuery namedQuery4 = new ReadAllQuery();
		namedQuery4.setEJBQLString("hi");
		namedQuery4.setName("query5");
		namedQuery4.setCascadePolicy(1);
		namedQuery4.setQueryTimeout(-1);
		namedQuery4.setShouldUseWrapperPolicy(true);
		namedQuery4.setShouldBindAllParameters(true);
		namedQuery4.setShouldCacheStatement(false);
		namedQuery4.setShouldMaintainCache(true);
		namedQuery4.setShouldPrepare(true);
		namedQuery4.setMaxRows(0);
		namedQuery4.setShouldRefreshIdentityMapResult(false);
		namedQuery4.setCacheUsage(4);
		namedQuery4.setLockMode((short)-1);
		namedQuery4.setShouldRefreshRemoteIdentityMapResult(false);
		namedQuery4.setDistinctState((short)0);
		namedQuery4.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		descriptor.getDescriptorQueryManager().addQuery("query5", namedQuery4);

		//Named Query -- query4
		ReadAllQuery namedQuery5 = new ReadAllQuery();
		namedQuery5.setSQLString("yo");
		namedQuery5.setName("query4");
		namedQuery5.setCascadePolicy(1);
		namedQuery5.setQueryTimeout(-1);
		namedQuery5.setShouldUseWrapperPolicy(true);
		namedQuery5.setShouldBindAllParameters(true);
		namedQuery5.setShouldCacheStatement(false);
		namedQuery5.setShouldMaintainCache(true);
		namedQuery5.setShouldPrepare(true);
		namedQuery5.setMaxRows(0);
		namedQuery5.setShouldRefreshIdentityMapResult(true);
		namedQuery5.setCacheUsage(3);
		namedQuery5.setLockMode((short)1);
		namedQuery5.setShouldRefreshRemoteIdentityMapResult(true);
		namedQuery5.setDistinctState((short)0);
		namedQuery5.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(0));
		descriptor.getDescriptorQueryManager().addQuery("query4", namedQuery5);


		// Event manager.

		// Mappings.
		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setFieldName("AGG_CLNT.CL_ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping nameMapping = new DirectToFieldMapping();
		nameMapping.setAttributeName("name");
		nameMapping.setFieldName("AGG_CLNT.CL_NAME");
		descriptor.addMapping(nameMapping);

		AggregateObjectMapping addressDescriptionMapping = new AggregateObjectMapping();
		addressDescriptionMapping.setAttributeName("addressDescription");
		addressDescriptionMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.AddressDescription.class.getName());
		addressDescriptionMapping.setIsNullAllowed(false);
		addressDescriptionMapping.addFieldNameTranslation("AGG_CLNT.CL_EDATE", "periodDescription_period_endDate->DIRECT");
		addressDescriptionMapping.addFieldNameTranslation("AGG_CLNT.CL_SDATE", "periodDescription_period_startDate->DIRECT");
		addressDescriptionMapping.addFieldNameTranslation("AGG_CLNT.CL_ADD", "address->EM_ADD_IN_REFERENCE_AGG_EMP_AGG_ADD");
		addressDescriptionMapping.addFieldNameTranslation("AGG_CLNT.CL_ID", "QUERY_KEY id");
		descriptor.addMapping(addressDescriptionMapping);

		return descriptor;
	}

	public RelationalDescriptor buildComputerDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Computer.class.getName());
		descriptor.addTableName("AGG_COM");
		descriptor.addPrimaryKeyFieldName("AGG_COM.ID");

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(100);
		descriptor.setSequenceNumberFieldName("AGG_COM.ID");
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setAlias("Computer");
		descriptor.setIsIsolated(false);

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeNonExistenceForDoesExist();
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping descriptionMapping = new DirectToFieldMapping();
		descriptionMapping.setAttributeName("description");
		descriptionMapping.setFieldName("AGG_COM.DESCRIP");
		descriptor.addMapping(descriptionMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setFieldName("AGG_COM.ID");
		descriptor.addMapping(idMapping);

		return descriptor;
	}

	public RelationalDescriptor buildEmployeeDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Employee.class.getName());
		descriptor.addTableName("AGG_EMP");
		descriptor.addPrimaryKeyFieldName("AGG_EMP.EM_ADD");
		descriptor.addPrimaryKeyFieldName("AGG_EMP.EM_ID");

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(100);
		descriptor.setSequenceNumberFieldName("AGG_EMP.EM_ID");
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setAlias("Employee");
		descriptor.setIsIsolated(false);

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeNonExistenceForDoesExist();
		//Named Queries

		// Event manager.

		// Query keys.
		descriptor.addDirectQueryKey("id", "AGG_EMP.EM_ID");

		// Mappings.
		DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setFieldName("AGG_EMP.EM_FNAME");
		descriptor.addMapping(firstNameMapping);

		DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
		lastNameMapping.setAttributeName("lastName");
		lastNameMapping.setFieldName("AGG_EMP.EM_LNAME");
		descriptor.addMapping(lastNameMapping);

		AggregateObjectMapping addressDescriptionMapping = new AggregateObjectMapping();
		addressDescriptionMapping.setAttributeName("addressDescription");
		addressDescriptionMapping.setGetMethodName("getAddressDescription");
		addressDescriptionMapping.setSetMethodName("setAddressDescription");
		addressDescriptionMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.AddressDescription.class.getName());
		addressDescriptionMapping.setIsNullAllowed(false);
		addressDescriptionMapping.addFieldNameTranslation("AGG_EMP.EM_EDATE", "periodDescription_period_endDate->DIRECT");
		addressDescriptionMapping.addFieldNameTranslation("AGG_EMP.EM_SDATE", "periodDescription_period_startDate->DIRECT");
		addressDescriptionMapping.addFieldNameTranslation("AGG_EMP.EM_ADD", "address->EM_ADD_IN_REFERENCE_AGG_EMP_AGG_ADD");
		addressDescriptionMapping.addFieldNameTranslation("AGG_EMP.EM_ID", "QUERY_KEY id");
		descriptor.addMapping(addressDescriptionMapping);

		AggregateObjectMapping projectDescriptionMapping = new AggregateObjectMapping();
		projectDescriptionMapping.setAttributeName("projectDescription");
		projectDescriptionMapping.setGetMethodName("getProjectDescription");
		projectDescriptionMapping.setSetMethodName("setProjectDescription");
		projectDescriptionMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.ProjectDescription.class.getName());
		projectDescriptionMapping.setIsNullAllowed(false);
		projectDescriptionMapping.addFieldNameTranslation("AGG_EMP.COMP_ID", "computer->COMP_ID_IN_REFERENCE_AGG_EMP_AGG_COM");
		projectDescriptionMapping.addFieldNameTranslation("AGG_EMP.EM_PDESC", "description->DIRECT");
		projectDescriptionMapping.addFieldNameTranslation("AGG_EMP.EM_ID", "id->DIRECT");
		projectDescriptionMapping.addFieldNameTranslation("AGG_EMP.EM_ID", "languages->EM_ID_IN_REFERENCE_EMP_LAN_AGG_EMP");
		projectDescriptionMapping.addFieldNameTranslation("AGG_EMP.EM_ID", "responsibilities->EM_ID_IN_REFERENCE_AGG_RES_AGG_EMP");
		descriptor.addMapping(projectDescriptionMapping);

		return descriptor;
	}

	public RelationalDescriptor buildEvaluationClientDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.EvaluationClient.class.getName());
		descriptor.addTableName("AGG_ECNT");
		descriptor.addPrimaryKeyFieldName("AGG_ECNT.CL_ID");
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		descriptor.getDescriptorQueryManager().getDoesExistQuery().setExistencePolicy(DoesExistQuery.AssumeNonExistence);

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Client.class.getName());

		// Descriptor properties.
		descriptor.setAlias("EvaluationClient");
		descriptor.setIsIsolated(false);

		// Query manager.

		//Named Queries

		// Event manager.

		// Mappings.
		AggregateObjectMapping evaluationPeriodMapping = new AggregateObjectMapping();
		evaluationPeriodMapping.setAttributeName("evaluationPeriod");
		evaluationPeriodMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Period.class.getName());
		evaluationPeriodMapping.setIsNullAllowed(false);
		evaluationPeriodMapping.addFieldNameTranslation("AGG_ECNT.EV_SDATE", "startDate->DIRECT");
		evaluationPeriodMapping.addFieldNameTranslation("AGG_ECNT.EV_EDATE", "endDate->DIRECT");
		descriptor.addMapping(evaluationPeriodMapping);

		return descriptor;
	}

	public RelationalDescriptor buildLanguageDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Language.class.getName());
		descriptor.addTableName("AGG_LAN");
		descriptor.addPrimaryKeyFieldName("AGG_LAN.ID");

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(100);
		descriptor.setIsIsolated(false);
		descriptor.setSequenceNumberFieldName("AGG_LAN.ID");
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setAlias("Language");

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeNonExistenceForDoesExist();
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setFieldName("AGG_LAN.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping languageMapping = new DirectToFieldMapping();
		languageMapping.setAttributeName("language");
		languageMapping.setFieldName("AGG_LAN.LANGUAGE");
		descriptor.addMapping(languageMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPeriodDescriptionDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.PeriodDescription.class.getName());

		// Descriptor properties.
		descriptor.setAlias("PeriodDescription");

		// Query manager.

		//Named Queries

		// Event manager.

		// Mappings.
		AggregateObjectMapping periodMapping = new AggregateObjectMapping();
		periodMapping.setAttributeName("period");
		periodMapping.setGetMethodName("getPeriod");
		periodMapping.setSetMethodName("setPeriod");
		periodMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Period.class.getName());
		periodMapping.setIsNullAllowed(false);
		periodMapping.addFieldNameTranslation("period_endDate->DIRECT", "endDate->DIRECT");
		periodMapping.addFieldNameTranslation("period_startDate->DIRECT", "startDate->DIRECT");
		descriptor.addMapping(periodMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPeriodDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Period.class.getName());

		// Descriptor properties.
		descriptor.setAlias("Period");

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

	public RelationalDescriptor buildProjectDescriptionDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.ProjectDescription.class.getName());

		// Descriptor properties.
		descriptor.setAlias("ProjectDescription");

		// Query manager.

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping descriptionMapping = new DirectToFieldMapping();
		descriptionMapping.setAttributeName("description");
		descriptionMapping.setFieldName("description->DIRECT");
		descriptor.addMapping(descriptionMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setFieldName("id->DIRECT");
		descriptor.addMapping(idMapping);

		OneToOneMapping computerMapping = new OneToOneMapping();
		computerMapping.setAttributeName("computer");
		computerMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Computer.class.getName());
		computerMapping.useBasicIndirection();
		computerMapping.privateOwnedRelationship();
		computerMapping.addForeignKeyFieldName("computer->COMP_ID_IN_REFERENCE_AGG_EMP_AGG_COM", "AGG_COM.ID");
		descriptor.addMapping(computerMapping);

		OneToManyMapping responsibilitiesMapping = new OneToManyMapping();
		responsibilitiesMapping.setAttributeName("responsibilities");
		responsibilitiesMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Responsibility.class.getName());
		responsibilitiesMapping.useBasicIndirection();
        responsibilitiesMapping.useListClassName("java.util.Vector");
		responsibilitiesMapping.privateOwnedRelationship();
		responsibilitiesMapping.addTargetForeignKeyFieldName("AGG_RES.EMP_ID", "responsibilities->EM_ID_IN_REFERENCE_AGG_RES_AGG_EMP");
		descriptor.addMapping(responsibilitiesMapping);

		ManyToManyMapping languagesMapping = new ManyToManyMapping();
		languagesMapping.setAttributeName("languages");
		languagesMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Language.class.getName());
		languagesMapping.useBasicIndirection();
        languagesMapping.useListClassName("java.util.Vector");

		languagesMapping.setRelationTableName("EMP_LAN");
		languagesMapping.addSourceRelationKeyFieldName("EMP_LAN.EMP_ID", "languages->EM_ID_IN_REFERENCE_EMP_LAN_AGG_EMP");
		languagesMapping.addTargetRelationKeyFieldName("EMP_LAN.LAN_ID", "AGG_LAN.ID");
		descriptor.addMapping(languagesMapping);

		return descriptor;
	}

	public RelationalDescriptor buildResponsibilityDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Responsibility.class.getName());
		descriptor.addTableName("AGG_RES");
		descriptor.addPrimaryKeyFieldName("AGG_RES.ID");

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(100);
		descriptor.setSequenceNumberFieldName("AGG_RES.ID");
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setAlias("Responsibility");
		descriptor.setIsIsolated(false);

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeNonExistenceForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setFieldName("AGG_RES.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping responsibilityMapping = new DirectToFieldMapping();
		responsibilityMapping.setAttributeName("responsibility");
		responsibilityMapping.setFieldName("AGG_RES.DUTY");
		descriptor.addMapping(responsibilityMapping);

		OneToOneMapping employeeMapping = new OneToOneMapping();
		employeeMapping.setAttributeName("employee");
		employeeMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Employee.class.getName());
		employeeMapping.useBasicIndirection();
		employeeMapping.addForeignKeyFieldName("AGG_RES.EMP_ID", "AGG_EMP.EM_ID");
		descriptor.addMapping(employeeMapping);

		return descriptor;
	}


	public Project getRuntimeProject() {
		return this.runtimeProject;
	}
}

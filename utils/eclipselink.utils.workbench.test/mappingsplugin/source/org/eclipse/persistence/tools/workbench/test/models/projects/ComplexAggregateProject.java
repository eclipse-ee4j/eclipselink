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

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStringQueryFormat;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;



public class ComplexAggregateProject extends RelationalTestProject{

	public ComplexAggregateProject() {
		super();
	}
	
	public static MWRelationalProject emptyProject() {
		MWRelationalProject project = new MWRelationalProject("ComplexAggregate", spiManager(), mySqlPlatform());

		// project defaults policy
		MWProjectDefaultsPolicy pdp = project.getDefaultsPolicy();
		pdp.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_CHECK_DATABASE);
		pdp.getCachingPolicy().setCacheSize(405);
		pdp.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		pdp.setMethodAccessing(false);

		return project;
	}

	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	public MWTableDescriptor getAddressDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Address");
	}
	
	public MWAggregateDescriptor getAddressDescriptionDescriptor() {
		return (MWAggregateDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.AddressDescription");
	}
	
	public MWTableDescriptor getClientDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Client");
	}
	
	public MWTableDescriptor getComputerDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Computer");
	}
	
	protected Integer getDefaultQueryTimeout() {
		return MWQuery.QUERY_TIMEOUT_UNDEFINED;
	}
	
	protected String getDefaultQueryLockMode() {
		return MWQuery.DEFAULT_LOCK_MODE;
	}
	
	public MWTableDescriptor getEmployeeDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Employee");
	}
	
	public MWTableDescriptor getEvaluationClientDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.EvaluationClient");
	}
	
	public MWTableDescriptor getLanguageDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Language");
	}
	
	public MWAggregateDescriptor getPeriodDescriptor() {
		return (MWAggregateDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Period");
	}
	
	public MWAggregateDescriptor getPeriodDescriptionDescriptor() {
		return (MWAggregateDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.PeriodDescription");
	}
	
	public MWAggregateDescriptor getProjectDescriptionDescriptor() {
		return (MWAggregateDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.ProjectDescription");
	}
	
	public MWTableDescriptor getResponsibilityDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Responsibility");
	}
	
	protected void initializeAddressDescriptionDescriptor() {
		MWAggregateDescriptor addressDescriptionDescriptor = getAddressDescriptionDescriptor();
		addressDescriptionDescriptor.addQueryKey("id", null);

		//one-to-one mapping
		MWOneToOneMapping addressMapping = addressDescriptionDescriptor.addOneToOneMapping(addressDescriptionDescriptor.getMWClass().attributeNamed("address"));
		addressMapping.setPrivateOwned(true);
		addressMapping.setReferenceDescriptor(getAddressDescriptor());
		addressMapping.setReference(tableNamed("AGG_EMP").referenceNamed("AGG_EMP_AGG_ADD"));

		//aggregateMapping	
		MWAggregateMapping periodDescriptionMapping = addressDescriptionDescriptor.addAggregateMapping(addressDescriptionDescriptor.getMWClass().attributeNamed("periodDescription"));
		periodDescriptionMapping.setAllowsNull(true);
		periodDescriptionMapping.setUsesMethodAccessing(true);
		periodDescriptionMapping.setReferenceDescriptor(getPeriodDescriptionDescriptor());
	}	

	protected void initializeAddressDescriptor() {
		MWTableDescriptor descriptor = getAddressDescriptor();
		MWTable table = tableNamed("AGG_ADD");
		descriptor.setPrimaryTable(table);
		
		//sequencing policy
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));

		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(100);

		// Direct to field mappings
		addDirectMapping(descriptor, "id", table, "ID");	
		addDirectMapping(descriptor, "address", table, "ADDRESS");	
	}

	
	public void initializeAggAddTable() {
		MWTable table = database().addTable("AGG_ADD");
		addField(table, "ADDRESS", "varchar", 30);
		addPrimaryKeyField(table, "ID", "decimal", 15);
	}
	
	public void initializeAggClntTable() {
		MWTable table = database().addTable("AGG_CLNT");
		addField(table, "CL_ADD", "decimal", 15);
		addField(table, "CL_EDATE", "date");
		addPrimaryKeyField(table, "CL_ID", "decimal", 15);
		addField(table, "CL_NAME", "varchar", 20);
		addField(table, "CL_SDATE", "date");
		addField(table, "TYPE", "varchar", 20);
	}
	
	public void initializeAggComTable() {
		MWTable table = database().addTable("AGG_COM");
		addField(table, "DESCRIP", "varchar", 30);
		addPrimaryKeyField(table, "ID", "decimal",15);
	}
	
	public void initializeAggEcntTable() {
		MWTable table = database().addTable("AGG_ECNT");
		addPrimaryKeyField(table, "CL_ID", "decimal", 15);
		addField(table, "EV_EDATE", "date");
		addField(table, "EV_SDATE", "date");	
	}
	
	public void initializeAggEmpTable() {
		MWTable table = database().addTable("AGG_EMP");
		addField(table, "COMP_ID", "decimal",15);
		addPrimaryKeyField(table, "EM_ADD", "varchar",20);
		addField(table, "EM_EDATE", "date");
		addField(table, "EM_FNAME", "varchar",20);
		addPrimaryKeyField(table, "EM_ID", "decimal", 15);
		addField(table, "EM_LNAME", "varchar",20);
		addField(table, "EM_PDESC", "varchar",100);
		addField(table, "EM_SDATE", "date");
	
	}
	
	public void initializeAggLanTable() {
		MWTable table = database().addTable("AGG_LAN");
		addPrimaryKeyField(table, "ID", "decimal", 15);
		addField(table, "LANGUAGE", "varchar", 30);
	}
	
	public void initializeAggResTable() {
		MWTable table = database().addTable("AGG_RES");
		addField(table, "DUTY", "varchar", 30);
		addField(table, "EMP_ID", "decimal", 15);
		addPrimaryKeyField(table, "ID", "decimal", 15);
	}
	
	protected void initializeClientDescriptor() {
		MWTableDescriptor descriptor = getClientDescriptor();
		MWTable table = tableNamed("AGG_CLNT");
		descriptor.setPrimaryTable(table);
		descriptor.getTransactionalPolicy().setReadOnly(true);
		
		//queries
		MWAbstractRelationalReadQuery query = (MWAbstractRelationalReadQuery) descriptor.getQueryManager().addReadObjectQuery("query1");
        query.setQueryFormatType(MWRelationalQuery.EJBQL_FORMAT);
       ((MWStringQueryFormat) query.getQueryFormat()).setQueryString("foo");
        query.setBindAllParameters(TriStateBoolean.FALSE);
        query.setCacheStatement(TriStateBoolean.FALSE);
		query.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_PRIMARY_KEY);
		query.addParameter(typeFor(java.lang.String.class));
		query.setQueryTimeout(getDefaultQueryTimeout());
		query.setLocking(getDefaultQueryLockMode());



		query = (MWAbstractRelationalReadQuery) descriptor.getQueryManager().addReadAllQuery("query2");
        query.setQueryFormatType(MWRelationalQuery.EJBQL_FORMAT);
    	query.addParameter(typeFor(org.eclipse.persistence.sessions.DatabaseLogin.class));
		query.addParameter(typeFor(java.lang.String.class));
		query.setCacheStatement(new TriStateBoolean(true));
		query.setBindAllParameters(new TriStateBoolean(true));
		query.setMaintainCache(true);
		query.setRefreshIdentityMapResult(true);
		query.setCacheUsage(MWRelationalReadQuery.DO_NOT_CHECK_CACHE);
		query.setLocking(MWQuery.LOCK);
		((MWStringQueryFormat) query.getQueryFormat()).setQueryString("2");
		query.setQueryTimeout(getDefaultQueryTimeout());


		query = (MWAbstractRelationalReadQuery)descriptor.getQueryManager().addReadObjectQuery("query3");
        query.setQueryFormatType(MWRelationalQuery.EJBQL_FORMAT);
  		query.setCacheStatement(new TriStateBoolean(true));
		query.setBindAllParameters(new TriStateBoolean(true));
		query.setMaintainCache(false);
		query.setRefreshIdentityMapResult(false);
		query.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_BY_EXACT_PRIMARY_KEY);
		((MWStringQueryFormat) query.getQueryFormat()).setQueryString("3");
		query.setQueryTimeout(getDefaultQueryTimeout());
		query.setLocking(getDefaultQueryLockMode());



		query = (MWAbstractRelationalReadQuery)descriptor.getQueryManager().addReadAllQuery("query4");
        query.setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
    	query.setCacheStatement(new TriStateBoolean(false));
		query.setBindAllParameters(new TriStateBoolean(true));
		query.setMaintainCache(true);
		query.setRefreshIdentityMapResult(true);
		query.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_THEN_DATABASE);
		query.setLocking(MWQuery.LOCK);
		((MWStringQueryFormat) query.getQueryFormat()).setQueryString("yo");
		query.setQueryTimeout(getDefaultQueryTimeout());


		query = (MWAbstractRelationalReadQuery)descriptor.getQueryManager().addReadAllQuery("query5");
        query.setQueryFormatType(MWRelationalQuery.EJBQL_FORMAT);
  		query.setCacheStatement(new TriStateBoolean(false));
		query.setBindAllParameters(new TriStateBoolean(true));
		query.setMaintainCache(true);
		query.setRefreshIdentityMapResult(false);
		query.setCacheUsage(MWRelationalReadQuery.CHECK_CACHE_ONLY);
        ((MWStringQueryFormat) query.getQueryFormat()).setQueryString("hi");
		query.setQueryTimeout(getDefaultQueryTimeout());
		query.setLocking(getDefaultQueryLockMode());

		
		query = (MWAbstractRelationalReadQuery)descriptor.getQueryManager().addReadObjectQuery("query6");
        query.setQueryFormatType(MWRelationalQuery.SQL_FORMAT);
   		query.setCacheStatement(new TriStateBoolean(true));
		query.setBindAllParameters(new TriStateBoolean(false));
		query.setMaintainCache(false);
		query.setRefreshIdentityMapResult(true);
		query.setCacheUsage(MWRelationalReadQuery.CONFORM_RESULTS_IN_UNIT_OF_WORK);
		query.setLocking(MWQuery.LOCK_NOWAIT);
	     ((MWStringQueryFormat) query.getQueryFormat()).setQueryString("blah qwerty");
		query.setQueryTimeout(getDefaultQueryTimeout());
		
		//custom sql
		descriptor.getRelationalQueryManager().setInsertSQLString("an insert");
		descriptor.getRelationalQueryManager().setUpdateSQLString("an update");
		descriptor.getRelationalQueryManager().setDeleteSQLString("an update");
		descriptor.getRelationalQueryManager().setReadObjectSQLString("read the object");
		descriptor.getRelationalQueryManager().setReadAllSQLString("read everything");
		
		//sequencing policy
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("CL_ID"));
	
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(100);

		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		MWRelationalClassIndicatorFieldPolicy indicatorPolicy = (MWRelationalClassIndicatorFieldPolicy)inheritancePolicy.getClassIndicatorPolicy();
		inheritancePolicy.setReadSubclassesOnQuery(false);
		indicatorPolicy.setField(table.columnNamed("TYPE"));
		indicatorPolicy.setIndicatorType(new MWTypeDeclaration(indicatorPolicy, typeFor(java.lang.String.class)));
		indicatorPolicy.getClassIndicatorValueForDescriptor(getClientDescriptor()).setInclude(true);
		indicatorPolicy.getClassIndicatorValueForDescriptor(getClientDescriptor()).setIndicatorValue("Client");
		indicatorPolicy.getClassIndicatorValueForDescriptor(getEvaluationClientDescriptor()).setInclude(true);
		indicatorPolicy.getClassIndicatorValueForDescriptor(getEvaluationClientDescriptor()).setIndicatorValue("Eval");
	
		// Direct to field mappings
		addDirectMapping(descriptor, "name", table, "CL_NAME");	
		addDirectMapping(descriptor, "id", table, "CL_ID");	
		
		//aggregateMapping	
		MWAggregateMapping addressDescriptionMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("addressDescription"));
		addressDescriptionMapping.setReferenceDescriptor(getAddressDescriptionDescriptor());
			
		Iterator fieldAssociations = CollectionTools.sort(addressDescriptionMapping.pathsToFields()).iterator();
 
		String[] fieldNames = new String[] {"CL_ADD", "CL_EDATE", "CL_SDATE", "CL_ID"};

		for(int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			association.setColumn(table.columnNamed(fieldNames[i]));
		}

	}
	
	protected void initializeComputerDescriptor() {
		MWTableDescriptor descriptor = getComputerDescriptor();
		MWTable table = tableNamed("AGG_COM");
		descriptor.setPrimaryTable(table);
		
		//sequencing policy
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
	
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(100);

		// Direct to field mappings
		addDirectMapping(descriptor, "description", table, "DESCRIP");	
		addDirectMapping(descriptor, "id", table, "ID");	
	}
	
	@Override
	protected void initializeDatabase() {
		super.initializeDatabase();

		this.initializeAggAddTable();
		this.initializeAggClntTable();
		this.initializeAggComTable();
		this.initializeAggEcntTable();
		this.initializeAggEmpTable();
		this.initializeAggLanTable();
		this.initializeAggResTable();
		this.initializeEmpLanTable();
		this.initializeSequenceTable();

		this.initializeTableReferences();
	}
	
	@Override
	protected void initializeDescriptors() {
		super.initializeDescriptors();

		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Address");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Client");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Computer");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Employee");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.EvaluationClient");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Language");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Responsibility");

		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.AddressDescription");
		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.Period");
		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.PeriodDescription");
		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.complexaggregate.ProjectDescription");
			
		
		initializeAddressDescriptor();
		initializePeriodDescriptor();
		initializePeriodDescriptionDescriptor();
		initializeAddressDescriptionDescriptor();

		initializeProjectDescriptionDescriptor();
		initializeEvaluationClientDescriptor();
		initializeClientDescriptor();
		initializeComputerDescriptor();
		initializeEmployeeDescriptor();
		initializeLanguageDescriptor();
		initializeResponsibilityDescriptor();
	
	}
	
	public void initializeEmpLanTable() {
		MWTable table = database().addTable("EMP_LAN");
		addPrimaryKeyField(table, "EMP_ID", "integer");
		addPrimaryKeyField(table, "LAN_ID", "integer");
	}

	protected void initializeEmployeeDescriptor() {
		MWTableDescriptor descriptor = getEmployeeDescriptor();
		MWTable table = tableNamed("AGG_EMP");
		descriptor.setPrimaryTable(table);
		descriptor.addQueryKey("id", table.columnNamed("EM_ID"));
			
		//sequencing policy
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("EM_ID"));
	
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(100);
		
		// Direct to field mappings
		addDirectMapping(descriptor, "firstName", table, "EM_FNAME");	
		addDirectMapping(descriptor, "lastName", table, "EM_LNAME");		

		//aggregate mappings
		MWAggregateMapping addressDescriptionMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("addressDescription"));
		addressDescriptionMapping.setReferenceDescriptor(getAddressDescriptionDescriptor());
		addressDescriptionMapping.setUsesMethodAccessing(true);
 
		Iterator fieldAssociations = CollectionTools.sort(addressDescriptionMapping.pathsToFields()).iterator();
		String [] fieldNames = new String[] {"EM_ADD", "EM_EDATE", "EM_SDATE", "EM_ID"};
		for(int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			association.setColumn(table.columnNamed(fieldNames[i]));
		}
		
		
		MWAggregateMapping projectDescriptionMapping =  descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("projectDescription"));
		projectDescriptionMapping.setReferenceDescriptor(getProjectDescriptionDescriptor());
		projectDescriptionMapping.setUsesMethodAccessing(true);
			
		fieldAssociations =  CollectionTools.sort(projectDescriptionMapping.pathsToFields()).iterator();
		fieldNames = new String[] {"COMP_ID", "EM_PDESC", "EM_ID", "EM_ID", "EM_ID"};
		for(int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			association.setColumn(table.columnNamed(fieldNames[i]));
		}		
	}

	protected void initializeEvaluationClientDescriptor() {
		MWTableDescriptor descriptor = getEvaluationClientDescriptor();
		MWTable table = tableNamed("AGG_ECNT");
		descriptor.setPrimaryTable(table);
		
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE);
		//inheritance policy
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy)descriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getClientDescriptor());
		
		MWAggregateMapping evaluationPeriodMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("evaluationPeriod"));
		evaluationPeriodMapping.setReferenceDescriptor(getPeriodDescriptor());
			
		Iterator pathsToFields = CollectionTools.sort(evaluationPeriodMapping.pathsToFields()).iterator();
  
		String[] fieldNames = new String[] {"EV_EDATE", "EV_SDATE"};
		for(int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) pathsToFields.next();
			association.setColumn(table.columnNamed(fieldNames[i]));
		}		
	}
	
	protected void initializeLanguageDescriptor() {
		MWTableDescriptor descriptor = getLanguageDescriptor();
		MWTable table = tableNamed("AGG_LAN");
		descriptor.setPrimaryTable(table);
		
		//sequencing policy
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
	
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(100);
		
		// Direct to field mappings
		addDirectMapping(descriptor, "id", table, "ID");	
		addDirectMapping(descriptor, "language", table, "LANGUAGE");	
	}

	public void initializePeriodDescriptor() {
		MWAggregateDescriptor periodDescriptor = getPeriodDescriptor();
		
		addDirectMapping(periodDescriptor, "endDate");
		addDirectMapping(periodDescriptor, "startDate");	
	}
	
	public void initializePeriodDescriptionDescriptor() {
		MWAggregateDescriptor periodDescriptionDescriptor = getPeriodDescriptionDescriptor();
		
		MWAggregateMapping mapping = periodDescriptionDescriptor.addAggregateMapping(periodDescriptionDescriptor.getMWClass().attributeNamed("period"));
		mapping.setUsesMethodAccessing(true);
		mapping.setReferenceDescriptor(getPeriodDescriptor());
	}

	public void initializeProjectDescriptionDescriptor() {
		MWAggregateDescriptor projectDescriptionDescriptor = getProjectDescriptionDescriptor();

		//direct to field mapping
		addDirectMapping(projectDescriptionDescriptor, "description");
		addDirectMapping(projectDescriptionDescriptor, "id");
		
		//one-to-one mapping
		MWOneToOneMapping computerMapping = projectDescriptionDescriptor.addOneToOneMapping(projectDescriptionDescriptor.getMWClass().attributeNamed("computer"));
		computerMapping.setPrivateOwned(true);
		computerMapping.setReferenceDescriptor(getComputerDescriptor());
		computerMapping.setReference(tableNamed("AGG_EMP").referenceNamed("AGG_EMP_AGG_COM"));

		//many-to-many mapping
		MWManyToManyMapping languagesMapping = projectDescriptionDescriptor.addManyToManyMapping(projectDescriptionDescriptor.getMWClass().attributeNamed("languages"));
		languagesMapping.setReferenceDescriptor(getLanguageDescriptor());
		MWTable relationTable = tableNamed("EMP_LAN");
		languagesMapping.setRelationTable(relationTable);
		languagesMapping.setSourceReference(relationTable.referenceNamed("EMP_LAN_AGG_EMP"));
		languagesMapping.setTargetReference(relationTable.referenceNamed("EMP_LAN_AGG_LAN"));

		//one-to-many mapping
		MWOneToManyMapping responsibilitiesMapping = projectDescriptionDescriptor.addOneToManyMapping(projectDescriptionDescriptor.getMWClass().attributeNamed("responsibilities"));
		responsibilitiesMapping.setReferenceDescriptor(getResponsibilityDescriptor());
		responsibilitiesMapping.setReference(tableNamed("AGG_RES").referenceNamed("AGG_RES_AGG_EMP"));
		responsibilitiesMapping.setPrivateOwned(true);
	}
	
	protected void initializeResponsibilityDescriptor() {
		MWTableDescriptor descriptor = getResponsibilityDescriptor();
		MWTable table = tableNamed("AGG_RES");
		descriptor.setPrimaryTable(table);
		
		//sequencing policy
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
	
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE);
		descriptor.getCachingPolicy().setCacheSize(100);
		
		//direct to field mappings
		addDirectMapping(descriptor, "id", table, "ID");	
		addDirectMapping(descriptor, "responsibility", table, "DUTY");

//		//one-to-one mapping
		MWOneToOneMapping computerMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("employee"));
		computerMapping.setReferenceDescriptor(getEmployeeDescriptor());
		computerMapping.setReference(table.referenceNamed("AGG_RES_AGG_EMP"));
	}

	public void initializeTableReferences() {
	
		//create references
			
		MWTable aggResTable = tableNamed("AGG_RES");
		MWTable aggEmpTable = tableNamed("AGG_EMP");
		MWTable aggLanTable = tableNamed("AGG_LAN");
		MWTable empLanTable = tableNamed("EMP_LAN");
		MWTable aggAddTable = tableNamed("AGG_ADD");
		MWTable aggComTable = tableNamed("AGG_COM");
			
		addReferenceOnDB("AGG_RES_AGG_EMP", aggResTable, aggEmpTable, "EMP_ID", "EM_ID");
		addReferenceOnDB("EMP_LAN_AGG_EMP", empLanTable, aggEmpTable, "EMP_ID", "EM_ID");
		addReferenceOnDB("EMP_LAN_AGG_LAN", empLanTable, aggLanTable, "LAN_ID", "ID");
		addReferenceOnDB("AGG_EMP_AGG_ADD", aggEmpTable, aggAddTable, "EM_ADD", "ID");
		addReferenceOnDB("AGG_EMP_AGG_COM", aggEmpTable, aggComTable, "COMP_ID", "ID");			
	}
	
}

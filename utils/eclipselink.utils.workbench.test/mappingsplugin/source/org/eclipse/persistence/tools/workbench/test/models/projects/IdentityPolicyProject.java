/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCacheExpiry;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;


/**
 * The purpose of this project is to provide a test case for new
 * 10.1.3 Identity Policy Features.
 */
public class IdentityPolicyProject extends RelationalTestProject
{

	public IdentityPolicyProject()
	{
		super();
	}
	
	@Override
	protected MWProject buildEmptyProject() {
		return new MWRelationalProject("Identity", spiManager(), oraclePlatform());
	}
	
	@Override
	protected void initializeDatabase()
	{
		super.initializeDatabase();
		initializeSequenceTable();
		initializeEmployeeTable();	
		initializeSalaryTable();
	}

	@Override
	protected void initializeDescriptors()
	{
		super.initializeDescriptors();
		this.addDescriptorForTypeNamed("test.oracle.models.employee.Employee");

		initializeEmployeeDescriptor();
	}
	
	public MWTableDescriptor getEmployeeDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("test.oracle.models.employee.Employee");		
	}
	
	public void initializeEmployeeDescriptor() {
		MWTableDescriptor employeeDescriptor = getEmployeeDescriptor();
		
		employeeDescriptor.getRefreshCachePolicy().setAlwaysRefreshCache(true);
		employeeDescriptor.getRefreshCachePolicy().setDisableCacheHits(true);
		
		MWTable employeeTable = getProject().getDatabase().tableNamed("EMPLOYEE");
		employeeDescriptor.setPrimaryTable(employeeTable);

		//multi-table policy
		employeeDescriptor.addMultiTableInfoPolicy();

		MWTable salaryTable = getProject().getDatabase().tableNamed("SALARY");
		employeeDescriptor.addAssociatedTable(salaryTable);
	
		//initialize policies
		
		employeeDescriptor.setUsesSequencing(true);
		employeeDescriptor.setSequenceNumberName("EMP_SEQ");
		employeeDescriptor.setSequenceNumberTable(employeeTable);
		employeeDescriptor.setSequenceNumberColumn(employeeTable.columnNamed("EMP_ID"));
	
		employeeDescriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        employeeDescriptor.getCachingPolicy().setUseProjectDefaultCacheExpiry(false);
		employeeDescriptor.getCachingPolicy().getCacheExpiry().setExpiryType(MWCacheExpiry.CACHE_EXPIRY_TIME_TO_LIVE_EXPIRY);
		employeeDescriptor.getCachingPolicy().getCacheExpiry().setTimeToLiveExpiry(new Long(10000));
		
	}
	
	public void initializeEmployeeTable() {	
		MWTable employeeTable = database().addTable("EMPLOYEE");
		
		addField(employeeTable,"ADDR_ID", "NUMBER");
		addPrimaryKeyField(employeeTable,"EMP_ID", "NUMBER");
		addField(employeeTable,"END_DATE", "DATE");
		addField(employeeTable,"END_TIME", "DATE");
		addField(employeeTable,"F_NAME", "VARCHAR2", 20);
		addField(employeeTable,"GENDER", "VARCHAR2", 20);
		addField(employeeTable,"L_NAME", "VARCHAR2", 20);
		addField(employeeTable,"MANAGER_ID", "NUMBER");
		addField(employeeTable,"START_DATE", "DATE");
		addField(employeeTable,"START_TIME", "DATE");
		addField(employeeTable,"VERSION", "NUMBER");		
	}
	
	public void initializeSalaryTable() {
		MWTable salaryTable = database().addTable("SALARY");
		
		addPrimaryKeyField(salaryTable,"EMP_ID", "NUMBER");
		addField(salaryTable,"SALARY", "NUMBER");
	}
}

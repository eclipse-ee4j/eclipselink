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
 *     Oracle - initial API and implementation from Oracle TopLink
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
		return new MWRelationalProject("Identity", spiManager(), mySqlPlatform());
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
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Employee");

		initializeEmployeeDescriptor();
	}
	
	public MWTableDescriptor getEmployeeDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.employee.Employee");		
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
		
		addField(employeeTable,"ADDR_ID", "integer");
		addPrimaryKeyField(employeeTable,"EMP_ID", "integer");
		addField(employeeTable,"END_DATE", "date");
		addField(employeeTable,"END_TIME", "date");
		addField(employeeTable,"F_NAME", "varchar", 20);
		addField(employeeTable,"GENDER", "varchar", 20);
		addField(employeeTable,"L_NAME", "varchar", 20);
		addField(employeeTable,"MANAGER_ID", "integer");
		addField(employeeTable,"START_DATE", "date");
		addField(employeeTable,"START_TIME", "date");
		addField(employeeTable,"VERSION", "integer");		
	}
	
	public void initializeSalaryTable() {
		MWTable salaryTable = database().addTable("SALARY");
		
		addPrimaryKeyField(salaryTable,"EMP_ID", "integer");
		addField(salaryTable,"SALARY", "integer");
	}
}

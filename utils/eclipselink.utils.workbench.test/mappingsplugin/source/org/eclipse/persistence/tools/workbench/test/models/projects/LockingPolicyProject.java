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

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;


/**
 * The purpose of this project is to provide a test case for new
 * 10.1.3 Locking Policy Features.
 */
public class LockingPolicyProject extends RelationalTestProject
{

	public LockingPolicyProject()
	{
		super();
	}
	
	@Override
	protected MWProject buildEmptyProject() {
		return new MWRelationalProject("Locking", spiManager(), mySqlPlatform());
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
			
		
		addDirectMapping(employeeDescriptor, "id", employeeTable, "EMP_ID");
		addDirectMapping(employeeDescriptor, "firstName", employeeTable, "F_NAME");
		addDirectMapping(employeeDescriptor, "lastName", employeeTable, "L_NAME");
		addDirectMapping(employeeDescriptor, "salary", salaryTable, "SALARY");
		
		MWDirectToFieldMapping genderMapping = (MWDirectToFieldMapping) addDirectMapping(employeeDescriptor, "gender").asMWObjectTypeMapping();
		genderMapping.setColumn(employeeTable.columnNamed("GENDER"));
		genderMapping.setUsesMethodAccessing(false);
		((MWObjectTypeConverter) genderMapping.getConverter()).setDataType(new MWTypeDeclaration(genderMapping.getConverter(), typeFor(java.lang.String.class)));
		((MWObjectTypeConverter) genderMapping.getConverter()).setAttributeType(new MWTypeDeclaration(genderMapping.getConverter(), typeFor(java.lang.String.class)));
		try
		{
			((MWObjectTypeConverter) genderMapping.getConverter()).addValuePair("M", "Male");
			((MWObjectTypeConverter) genderMapping.getConverter()).addValuePair("F", "Female");
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		MWTableDescriptorLockingPolicy lockingPolicy = (MWTableDescriptorLockingPolicy)employeeDescriptor.getLockingPolicy();
		lockingPolicy.setLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_LOCKING);
		lockingPolicy.setOptimisticLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_LOCKING_TYPE);
		lockingPolicy.setOptimisticColumnsLockingType(MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_SELECTED_COLUMNS);
		lockingPolicy.addColumnLockColumn(employeeTable.columnNamed("L_NAME"));
		lockingPolicy.addColumnLockColumn(salaryTable.columnNamed("SALARY"));
		lockingPolicy.addColumnLockColumn(employeeTable.columnNamed("GENDER"));
		
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

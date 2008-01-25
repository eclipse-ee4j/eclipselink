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

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;

/**
 * Created to test returning policy features
 */
public class ReturningPolicyProject extends RelationalTestProject{

	public ReturningPolicyProject()
	{
		super();
	}
	
	@Override
	protected MWProject buildEmptyProject() {
		return new MWRelationalProject("Returning", spiManager(), oraclePlatform());
	}
	
	@Override
	public void initializeDatabase()
	{
		super.initializeDatabase();
		initializeEmployeeTable();	
		initializeSalaryTable();
        this.initializeResponsTable();
        
        MWTable employeeTable = this.tableNamed("EMPLOYEE");
        MWTable responsTable = this.tableNamed("RESPONS");
            
        this.addReferenceOnDB("EMPLOYEE_EMPLOYEE", employeeTable, employeeTable, "MANAGER_ID", "EMP_ID");      
        this.addReferenceOnDB("RESPONS_EMPLOYEE", responsTable, employeeTable, "EMP_ID", "EMP_ID");
    }

	@Override
	public void initializeDescriptors()
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
        MWClass employeeClass = employeeDescriptor.getMWClass();

		employeeDescriptor.getRefreshCachePolicy().setAlwaysRefreshCache(true);
		employeeDescriptor.getRefreshCachePolicy().setDisableCacheHits(true);
		
		MWTable employeeTable = getProject().getDatabase().tableNamed("EMPLOYEE");
		employeeDescriptor.setPrimaryTable(employeeTable);

		//multi-table policy
		employeeDescriptor.addMultiTableInfoPolicy();

		MWTable salaryTable = getProject().getDatabase().tableNamed("SALARY");
		employeeDescriptor.addAssociatedTable(salaryTable);
	
        addDirectMapping(employeeDescriptor, "id", employeeTable, "EMP_ID");
        addDirectMapping(employeeDescriptor, "firstName", employeeTable, "F_NAME");


        //transformation mappings
        MWRelationalTransformationMapping normalHoursMapping = (MWRelationalTransformationMapping)
            employeeDescriptor.addTransformationMapping(employeeClass.attributeNamedFromAll("normalHours"));
        normalHoursMapping.setAttributeTransformer(methodNamed(employeeClass, "buildNormalHours")); 
            
 //       try {
            normalHoursMapping.addFieldTransformerAssociation(employeeTable.columnNamed("START_TIME"), 
                                                              methodNamed(employeeClass, "getStartTime"));
            normalHoursMapping.addFieldTransformerAssociation(employeeTable.columnNamed("END_TIME"), 
                                                              methodNamed(employeeClass, "getEndTime"));
 //       }
 //       catch (DuplicateFieldException dfe) {
 //           /*** should not happen ***/
 //           throw new RuntimeException(dfe);
 //       }
                    

        
        //initialize policies	
		employeeDescriptor.addReturningPolicy();
		MWRelationalReturningPolicy returningPolicy = (MWRelationalReturningPolicy) employeeDescriptor.getReturningPolicy();
		
		MWColumn field = getProject().getDatabase().columnNamed("EMPLOYEE.EMP_ID");
		returningPolicy.addInsertFieldReadOnlyFlag(field);
		
		field = getProject().getDatabase().columnNamed("EMPLOYEE.F_NAME");
		returningPolicy.addInsertFieldReadOnlyFlag(field).setReturnOnly(true);
		
        field = getProject().getDatabase().columnNamed("SALARY.EMP_ID");
        returningPolicy.addUpdateField(field);
        
        
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
    
    public void initializeResponsTable() {
        MWTable responsTable = database().addTable("RESPONS");
            
        addPrimaryKeyField(responsTable,"DESCRIP", "VARCHAR2", 200);
        addPrimaryKeyField(responsTable,"EMP_ID", "NUMBER");
    }

}

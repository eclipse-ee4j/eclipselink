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
import org.eclipse.persistence.descriptors.ReturningPolicy;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.transformers.MethodBasedAttributeTransformer;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;

/**
  */
public class ReturningPolicyRuntimeProject {
	
	private Project runtimeProject;
	
	public ReturningPolicyRuntimeProject() {
		super();
		this.initialize();
	}
	
	private void initialize() {
		this.runtimeProject = new Project();
		this.runtimeProject.setName("Returning");
		this.applyLogin();
		this.initializeDescriptors();
	}
	
	private void initializeDescriptors() {
		this.runtimeProject.addDescriptor(this.buildEmployeeDescriptor());

	}
	
	private RelationalDescriptor buildEmployeeDescriptor() 
	{
		RelationalDescriptor descriptor = new RelationalDescriptor();

		descriptor.setShouldAlwaysRefreshCache(true);
		descriptor.setShouldDisableCacheHits(true);

		descriptor.getDescriptorQueryManager().checkCacheForDoesExist();
		
		descriptor.setJavaClassName("org.eclipse.persistence.tools.workbench.test.models.employee.Employee");
		descriptor.setTableName("EMPLOYEE");
		descriptor.addTableName("SALARY");
		descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");
		
		descriptor.setReturningPolicy(new ReturningPolicy());
		descriptor.getReturningPolicy().addFieldForInsert("EMPLOYEE.EMP_ID");
		descriptor.getReturningPolicy().addFieldForInsertReturnOnly("EMPLOYEE.F_NAME");
        descriptor.getReturningPolicy().addFieldForUpdate("SALARY.EMP_ID");
		
        descriptor.addDirectMapping("firstName", "EMPLOYEE.F_NAME");
        descriptor.addDirectMapping("id", "EMPLOYEE.EMP_ID");
    
        TransformationMapping normalHoursMapping = new TransformationMapping();
        normalHoursMapping.setAttributeName("normalHours");
        normalHoursMapping.setAttributeTransformer(new MethodBasedAttributeTransformer("buildNormalHours"));
        normalHoursMapping.addFieldTransformation("START_TIME", "getStartTime");
        normalHoursMapping.addFieldTransformation("END_TIME", "getEndTime");
        descriptor.addMapping(normalHoursMapping);
     
        return descriptor;	
	}
	
	public void applyLogin() {
		DatabaseLogin login = new DatabaseLogin();
		login.usePlatform(new org.eclipse.persistence.platform.database.OraclePlatform());
		login.setDriverClassName(TestDatabases.oracleDriverClassName());
		login.setConnectionString(TestDatabases.oracleServerURL());
		login.setUserName(TestDatabases.userName());
		login.setPassword(TestDatabases.password());
		
		// Configuration properties.
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
	
	public Project getRuntimeProject() {
		return this.runtimeProject;
	}
}

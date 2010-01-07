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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;


public class LockingPolicyRuntimeProject
{
	private Project runtimeProject;

	public LockingPolicyRuntimeProject()
	{
		super();
		this.initialize();
	}

	private void initialize()
	{
		this.runtimeProject = new Project();
		this.runtimeProject.setName("Locking");
		applyLogin();
		this.initializeDescriptors();
	}

	private void initializeDescriptors()
	{
		this.runtimeProject.addDescriptor(this.buildEmployeeDescriptor());
	}

	private RelationalDescriptor buildEmployeeDescriptor()
	{
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName("org.eclipse.persistence.tools.workbench.test.models.employee.Employee");
		descriptor.setTableName("EMPLOYEE");
		descriptor.addTableName("SALARY");
		descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

		//force initialization
		descriptor.getQueryManager();

		descriptor.setShouldAlwaysRefreshCache(true);
		descriptor.setShouldDisableCacheHits(true);

		descriptor.setSequenceNumberFieldName("EMPLOYEE.EMP_ID");
		descriptor.setSequenceNumberName("EMP_SEQ");
		
		descriptor.setIsIsolated(false);
		descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.SoftCacheWeakIdentityMap.class);
		descriptor.setIdentityMapSize(100);

	    SelectedFieldsLockingPolicy lockingPolicy = new SelectedFieldsLockingPolicy();
	    lockingPolicy.addLockFieldName("EMPLOYEE.L_NAME");
	    lockingPolicy.addLockFieldName("SALARY.SALARY");
	    lockingPolicy.addLockFieldName("EMPLOYEE.GENDER");
	    descriptor.setOptimisticLockingPolicy(lockingPolicy);

		// --- mappings ---

		DirectToFieldMapping empId = new DirectToFieldMapping();
		empId.setFieldName("EMPLOYEE.EMP_ID");
		empId.setAttributeName("id");
		descriptor.addMapping(empId);

		DirectToFieldMapping firstName = new DirectToFieldMapping();
		firstName.setFieldName("EMPLOYEE.F_NAME");
		firstName.setAttributeName("firstName");
		descriptor.addMapping(firstName);

		DirectToFieldMapping lastName = new DirectToFieldMapping();
		lastName.setFieldName("EMPLOYEE.L_NAME");
		lastName.setAttributeName("lastName");
		descriptor.addMapping(lastName);

		DirectToFieldMapping salary = new DirectToFieldMapping();
		salary.setFieldName("SALARY.SALARY");
		salary.setAttributeName("salary");
		descriptor.addMapping(salary);

		DirectToFieldMapping gender = new DirectToFieldMapping();
		gender.setFieldName("EMPLOYEE.GENDER");
		gender.setAttributeName("gender");

		ObjectTypeConverter genderConverter = new ObjectTypeConverter(gender);
		genderConverter.addConversionValue("M","Male");
		genderConverter.addConversionValue("F", "Female");
		gender.setConverter(genderConverter);
		descriptor.addMapping(gender);

		return descriptor;

	}

	public void applyLogin()
	{
		DatabaseLogin login = new DatabaseLogin();
		login.usePlatform(new org.eclipse.persistence.platform.database.MySQLPlatform());
		login.setDriverClassName(TestDatabases.mySQLDriverClassName());
		login.setConnectionString(TestDatabases.mySQLServerURL());
		login.setUserName(TestDatabases.userName());
		login.setPassword(TestDatabases.password());

		// Configuration properties.
		((TableSequence) login.getDefaultSequence()).setPreallocationSize(50);
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

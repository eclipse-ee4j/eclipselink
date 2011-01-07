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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;


public class IdentityPolicyRuntimeProject
{
	private Project runtimeProject;

	public IdentityPolicyRuntimeProject() {
		super();
		this.initialize();
	}

	private void initialize() {
		this.runtimeProject = new Project();
		this.runtimeProject.setName("Identity");
		applyLogin();
		this.initializeDescriptors();
	}

	private void initializeDescriptors() {
		this.runtimeProject.addDescriptor(this.buildEmployeeDescriptor());

	}

	private RelationalDescriptor buildEmployeeDescriptor()
	{
		RelationalDescriptor descriptor = new RelationalDescriptor();

		// necessary call to lazily initialize default value of NoExpiryCacheInvalidationPolicy
		descriptor.getCacheInvalidationPolicy();

		descriptor.setJavaClassName("org.eclipse.persistence.tools.workbench.test.models.employee.Employee");
		descriptor.setTableName("EMPLOYEE");
		descriptor.addTableName("SALARY");
		descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

		descriptor.setShouldAlwaysRefreshCache(true);
		descriptor.setShouldDisableCacheHits(true);

		descriptor.setSequenceNumberFieldName("EMPLOYEE.EMP_ID");
		descriptor.setSequenceNumberName("EMP_SEQ");

		descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
		descriptor.setIdentityMapSize(100);
		descriptor.setIsIsolated(false);
		descriptor.getDescriptorQueryManager().checkCacheForDoesExist();
		descriptor.setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000));

		return descriptor;

	}

	public void applyLogin() {
		DatabaseLogin login = new DatabaseLogin();
		login.usePlatform(new org.eclipse.persistence.platform.database.MySQLPlatform());
		login.setDriverClassName(TestDatabases.mySQLDriverClassName());
		login.setConnectionString(TestDatabases.mySQLServerURL());
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

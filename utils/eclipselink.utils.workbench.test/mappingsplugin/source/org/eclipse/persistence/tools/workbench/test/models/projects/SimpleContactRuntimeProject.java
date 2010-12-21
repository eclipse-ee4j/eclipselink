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
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;



public class SimpleContactRuntimeProject {

	private Project runtimeProject;
	public SimpleContactRuntimeProject() {
		this.runtimeProject = new Project();
		this.runtimeProject.setName("SimpleContact");
		applyLogin();

		this.runtimeProject.addDescriptor(buildContactDescriptor());
		this.runtimeProject.addDescriptor(buildEmailAddressDescriptor());
		this.runtimeProject.addDescriptor(buildPersonImplDescriptor());
		this.runtimeProject.addDescriptor(buildPhoneNumberDescriptor());
	}

	public void applyLogin() {
		DatabaseLogin login = new DatabaseLogin();
		login.usePlatform(new org.eclipse.persistence.platform.database.MySQLPlatform());
		login.setDriverClassName(TestDatabases.mySQLDriverClassName());
		login.setConnectionString(TestDatabases.mySQLServerURL());
		login.setUserName(TestDatabases.userName());
		login.setPassword(TestDatabases.password());

		// Configuration properties.
		((TableSequence) login.getDefaultSequence()).setTableName("SEQUENCE");
		((TableSequence) login.getDefaultSequence()).setNameFieldName("SEQ_NAME");
		((TableSequence) login.getDefaultSequence()).setCounterFieldName("SEQ_COUNT");
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

	public RelationalDescriptor buildContactDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsForInterface();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.contact.Contact.class.getName());

		// Interface properties.

		// Descriptor properties.
		descriptor.setAlias("Contact");

		// Query manager.
		//Named Queries

		// Event manager.

		// Query keys.
		descriptor.addAbstractQueryKey("id");

		return descriptor;
	}

	public RelationalDescriptor buildEmailAddressDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.contact.EmailAddress.class.getName());
		descriptor.addTableName("EMAILADDRESS");
		descriptor.addPrimaryKeyFieldName("EMAILADDRESS.ID");

		// Interface properties.
		descriptor.getInterfacePolicy().addParentInterfaceName(org.eclipse.persistence.tools.workbench.test.models.contact.Contact.class.getName());

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.useSoftCacheWeakIdentityMap();
		descriptor.setIdentityMapSize(100);
		descriptor.setAlias("EmailAddress");


		// Query manager.
		descriptor.getDescriptorQueryManager().checkCacheForDoesExist();
		descriptor.addDirectQueryKey("email", "EMAILADDRESS.ADDRESS");

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping addressMapping = new DirectToFieldMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setGetMethodName("getAddress");
		addressMapping.setSetMethodName("setAddress");
		addressMapping.setFieldName("EMAILADDRESS.ADDRESS");
		descriptor.addMapping(addressMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("EMAILADDRESS.ID");
		descriptor.addMapping(idMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPersonImplDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.contact.PersonImpl.class.getName());
		descriptor.addTableName("PERSON");
		descriptor.addPrimaryKeyFieldName("PERSON.ID");

		// Interface properties.
		descriptor.getInterfacePolicy().addParentInterfaceName(org.eclipse.persistence.tools.workbench.test.models.contact.Person.class.getName());

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.useSoftCacheWeakIdentityMap();
		descriptor.setIdentityMapSize(100);
		descriptor.setAlias("PersonImpl");

		// Query manager.
		descriptor.getDescriptorQueryManager().checkCacheForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.

		VariableOneToOneMapping contactMapping = new VariableOneToOneMapping();
		contactMapping.setAttributeName("contact");
		contactMapping.setGetMethodName("getContact");
		contactMapping.setSetMethodName("setContact");
		contactMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.contact.Contact.class.getName());
		contactMapping.setIndirectionPolicy(new ProxyIndirectionPolicy());
		contactMapping.addForeignQueryKeyName("PERSON.ID", "id");
		contactMapping.setTypeFieldName("PERSON.CONTACT_TYPE");
		contactMapping.addClassNameIndicator("org.eclipse.persistence.tools.workbench.test.models.contact.PhoneNumber", "P");
		contactMapping.addClassNameIndicator("org.eclipse.persistence.tools.workbench.test.models.contact.EmailAddress", "E");
		descriptor.addMapping(contactMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPhoneNumberDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.contact.PhoneNumber.class.getName());
		descriptor.addTableName("PHONENUMBER");
		descriptor.addPrimaryKeyFieldName("PHONENUMBER.ID");

		// Interface properties.
		descriptor.getInterfacePolicy().addParentInterfaceName(org.eclipse.persistence.tools.workbench.test.models.contact.Contact.class.getName());

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.useSoftCacheWeakIdentityMap();
		descriptor.setIdentityMapSize(100);
		descriptor.setAlias("PhoneNumber");

		// Query manager.
		descriptor.getDescriptorQueryManager().checkCacheForDoesExist();
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("PHONENUMBER.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping numberMapping = new DirectToFieldMapping();
		numberMapping.setAttributeName("number");
		numberMapping.setGetMethodName("getNumber");
		numberMapping.setSetMethodName("setNumber");
		numberMapping.setFieldName("PHONENUMBER.P_NUM");
		descriptor.addMapping(numberMapping);

		return descriptor;
	}


	public Project getRuntimeProject() {
		return this.runtimeProject;
	}
}

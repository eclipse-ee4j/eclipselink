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
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;


public class SimpleAggregateRuntimeProject {

	private Project runtimeProject;
	public SimpleAggregateRuntimeProject() {
		this.runtimeProject = new Project();
		this.runtimeProject.setName("SimpleAggregate");
		applyLogin();

		this.runtimeProject.addDescriptor(buildAddressDescriptor());
		this.runtimeProject.addDescriptor(buildAddressSubClassDescriptor());
		this.runtimeProject.addDescriptor(buildPersonDescriptor());
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

	public RelationalDescriptor buildAddressDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.Address.class.getName());

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setClassIndicatorFieldName("CLASS_INDICATOR_FIELD");
		descriptor.getDescriptorInheritancePolicy().useClassNameAsIndicator();
		descriptor.getDescriptorInheritancePolicy().setShouldReadSubclasses(true);

		// Descriptor properties.
		descriptor.setAlias("Address");

		// Query manager.
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping cityMapping = new DirectToFieldMapping();
		cityMapping.setAttributeName("city");
		cityMapping.setGetMethodName("getCity");
		cityMapping.setSetMethodName("setCity");
		cityMapping.setFieldName("city->DIRECT");
		descriptor.addMapping(cityMapping);

		DirectToFieldMapping stateMapping = new DirectToFieldMapping();
		stateMapping.setAttributeName("state");
		stateMapping.setGetMethodName("getState");
		stateMapping.setSetMethodName("setState");
		stateMapping.setFieldName("state->DIRECT");
		descriptor.addMapping(stateMapping);

		DirectToFieldMapping streetMapping = new DirectToFieldMapping();
		streetMapping.setAttributeName("street");
		streetMapping.setGetMethodName("getStreet");
		streetMapping.setSetMethodName("setStreet");
		streetMapping.setFieldName("street->DIRECT");
		descriptor.addMapping(streetMapping);

		DirectToFieldMapping zipMapping = new DirectToFieldMapping();
		zipMapping.setAttributeName("zip");
		zipMapping.setGetMethodName("getZip");
		zipMapping.setSetMethodName("setZip");
		zipMapping.setFieldName("zip->DIRECT");
		descriptor.addMapping(zipMapping);

		return descriptor;
	}

	public RelationalDescriptor buildAddressSubClassDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.AddressSubClass.class.getName());

		// Inheritance properties.
		descriptor.getDescriptorInheritancePolicy().setParentClassName(org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.Address.class.getName());
		descriptor.getDescriptorInheritancePolicy().dontReadSubclassesOnQueries();

		// Descriptor properties.
		descriptor.setAlias("AddressSubClass");

		// Query manager.
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping countryMapping = new DirectToFieldMapping();
		countryMapping.setAttributeName("country");
		countryMapping.setGetMethodName("getCountry");
		countryMapping.setSetMethodName("setCountry");
		countryMapping.setFieldName("country->DIRECT");
		descriptor.addMapping(countryMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPersonDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.Person.class.getName());
		descriptor.addTableName("PERSON");
		descriptor.addTableName("ADDRESS");
		descriptor.addPrimaryKeyFieldName("PERSON.ID");

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(100);
//		descriptor.useRemoteFullIdentityMap();
//		descriptor.setRemoteIdentityMapSize(100);
		descriptor.setAlias("Person");
		descriptor.setIsIsolated(false);

		// Query manager.
		descriptor.getDescriptorQueryManager().checkCacheForDoesExist();
		//Named Queries

		// Event manager.

		// Query keys.
		descriptor.addDirectQueryKey("email", "PERSON.ADDRESS");

		// Mappings.
		DirectToFieldMapping ageMapping = new DirectToFieldMapping();
		ageMapping.setAttributeName("age");
		ageMapping.setGetMethodName("getAge");
		ageMapping.setSetMethodName("setAge");
		ageMapping.setFieldName("PERSON.AGE");
		descriptor.addMapping(ageMapping);

		DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setGetMethodName("getFirstName");
		firstNameMapping.setSetMethodName("setFirstName");
		firstNameMapping.setFieldName("PERSON.F_NAME");
		descriptor.addMapping(firstNameMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("PERSON.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
		lastNameMapping.setAttributeName("lastName");
		lastNameMapping.setGetMethodName("getLastName");
		lastNameMapping.setSetMethodName("setLastName");
		lastNameMapping.setFieldName("PERSON.L_NAME");
		descriptor.addMapping(lastNameMapping);

		DirectToFieldMapping genderMapping = new DirectToFieldMapping();
		ObjectTypeConverter converter = new ObjectTypeConverter(genderMapping);
		genderMapping.setConverter(converter);
		genderMapping.setAttributeName("gender");
		genderMapping.setGetMethodName("getGender");
		genderMapping.setSetMethodName("setGender");
		genderMapping.setFieldName("PERSON.GENDER");
		converter.addConversionValue("F", "Female");
		converter.addConversionValue("M", "Male");
		descriptor.addMapping(genderMapping);

		AggregateObjectMapping addressMapping = new AggregateObjectMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setGetMethodName("getAddress");
		addressMapping.setSetMethodName("setAddress");
		addressMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.AddressSubClass.class.getName());
		addressMapping.setIsNullAllowed(false);
		addressMapping.addFieldNameTranslation("ADDRESS.CITY", "city->DIRECT");
		addressMapping.addFieldNameTranslation("ADDRESS.COUNTRY", "country->DIRECT");
		addressMapping.addFieldNameTranslation("ADDRESS.STATE", "state->DIRECT");
		addressMapping.addFieldNameTranslation("ADDRESS.P_CODE", "zip->DIRECT");
		addressMapping.addFieldNameTranslation("ADDRESS.STREET", "street->DIRECT");
		addressMapping.addFieldNameTranslation("ADDRESS.TYPE", "CLASS_INDICATOR_FIELD");
		descriptor.addMapping(addressMapping);

		return descriptor;
	}


	public Project getRuntimeProject() {
		return this.runtimeProject;
	}
}

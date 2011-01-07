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
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;



public class PhoneCompanyRuntimeProject {

	private Project runtimeProject;

	public PhoneCompanyRuntimeProject(boolean usesSharedAggregates) {
		super();

		this.runtimeProject = new Project();
		StringBuffer projectName = new StringBuffer("PhoneCompany");
		if (usesSharedAggregates) projectName.append(" (shared aggregates)");

		this.runtimeProject.setName(projectName.toString());
		applyLogin();

		this.runtimeProject.addDescriptor(buildCompanyDescriptor(usesSharedAggregates));
		this.runtimeProject.addDescriptor(buildContactDescriptor());
		this.runtimeProject.addDescriptor(buildEmailAddressDescriptor());
		this.runtimeProject.addDescriptor(buildHouseholdDescriptor(usesSharedAggregates));
		this.runtimeProject.addDescriptor(buildPersonDescriptor());
		this.runtimeProject.addDescriptor(buildPhoneNumberDescriptor());
		this.runtimeProject.addDescriptor(buildServiceDescriptor(usesSharedAggregates));
		this.runtimeProject.addDescriptor(buildServiceCallDescriptor());
		this.runtimeProject.addDescriptor(buildServiceableDescriptor());
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

	public RelationalDescriptor buildCompanyDescriptor(boolean usesSharedAggregates) {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Company.class.getName());
		descriptor.addTableName("COMPANY");
		descriptor.addPrimaryKeyFieldName("COMPANY.ID");

		// Interface properties.
		descriptor.getInterfacePolicy().addParentInterfaceName(org.eclipse.persistence.tools.workbench.test.models.phone.Serviceable.class.getName());

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(405);
		descriptor.setIsIsolated(false);
		descriptor.setAlias("Company");

		// Query manager.
		descriptor.getDescriptorQueryManager().checkDatabaseForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("COMPANY.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping nameMapping = new DirectToFieldMapping();
		nameMapping.setAttributeName("name");
		nameMapping.setGetMethodName("getName");
		nameMapping.setSetMethodName("setName");
		nameMapping.setFieldName("COMPANY.NAME");
		descriptor.addMapping(nameMapping);

		AggregateObjectMapping serviceMapping = new AggregateObjectMapping();
		serviceMapping.setAttributeName("service");
		serviceMapping.setGetMethodName("getService");
		serviceMapping.setSetMethodName("setService");
		serviceMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Service.class.getName());
		serviceMapping.setIsNullAllowed(false);

		serviceMapping.addFieldNameTranslation("COMPANY.ID", "primaryContactPerson->HEAD_ID_IN_REFERENCE_HOUSEHOLD_PERSON2");
		serviceMapping.addFieldNameTranslation("COMPANY.SERVICE_PLAN", "servicePlan->DIRECT");
		serviceMapping.addFieldNameTranslation("COMPANY.ADDRESS", "billingAddress->DIRECT");
		serviceMapping.addFieldNameTranslation("COMPANY.HAS_DSL", "rate->METHOD_TRANSFORMER hasDslService");
		serviceMapping.addFieldNameTranslation("COMPANY.ID", "serviceCalls->ID_IN_REFERENCE_SERVICE_CALL_HOUSEHOLD");
		if (!usesSharedAggregates) {
			serviceMapping.addFieldNameTranslation("COMPANY.ID", "users->ID_IN_REFERENCE_COMPANY_PERSON_COMPANY");
			serviceMapping.addFieldNameTranslation("COMPANY.ID", "lineAccounts->ID_IN_REFERENCE_LINE_ACCOUNT_COMPANY");
		}
		serviceMapping.addFieldNameTranslation("COMPANY.CONTACT_TYPE", "primaryContact->CLASS_INDICATOR_FIELD");
		serviceMapping.addFieldNameTranslation("COMPANY.CONTACT_ID", "primaryContact->QUERY_KEY id");
		serviceMapping.addFieldNameTranslation("COMPANY.HAS_LONG_DISTANCE", "rate->METHOD_TRANSFORMER hasLongDistanceService");
		serviceMapping.addFieldNameTranslation("COMPANY.HAS_LOCAL", "rate->METHOD_TRANSFORMER hasLocalService");
		descriptor.addMapping(serviceMapping);

		return descriptor;
	}

	public RelationalDescriptor buildContactDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsForInterface();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Contact.class.getName());

		// Interface properties.

		// Descriptor properties.
		descriptor.setAlias("Contact");
		descriptor.addAbstractQueryKey("id");

		// Query manager.
		//Named Queries

		// Event manager.

		return descriptor;
	}

	public RelationalDescriptor buildEmailAddressDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.phone.EmailAddress.class.getName());
		descriptor.addTableName("EMAIL");
		descriptor.addPrimaryKeyFieldName("EMAIL.ID");

		// Interface properties.
		descriptor.getInterfacePolicy().addParentInterfaceName(org.eclipse.persistence.tools.workbench.test.models.phone.Contact.class.getName());

		// Descriptor properties.
		descriptor.useWeakIdentityMap();
		descriptor.setIdentityMapSize(98);
		descriptor.setIsIsolated(false);
		descriptor.setSequenceNumberFieldName("EMAIL.ID");
		descriptor.setSequenceNumberName("org.eclipse.persistence.tools.workbench.test.models.phone.EmailAddress");
		descriptor.setAlias("EmailAddress");
		
		// Query manager.
		descriptor.getDescriptorQueryManager().assumeExistenceForDoesExist();
		//Named Queries

		// Event manager.

		// Query keys.
		descriptor.addDirectQueryKey("email", "EMAIL.ADDRESS");

		// Mappings.
		DirectToFieldMapping addressMapping = new DirectToFieldMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setGetMethodName("getAddress");
		addressMapping.setSetMethodName("setAddress");
		addressMapping.setFieldName("EMAIL.ADDRESS");
		descriptor.addMapping(addressMapping);

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("EMAIL.ID");
		descriptor.addMapping(idMapping);

		return descriptor;
	}

	public RelationalDescriptor buildHouseholdDescriptor(boolean usesSharedAggregates) {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Household.class.getName());
		descriptor.addTableName("HOUSEHOLD");
		descriptor.addPrimaryKeyFieldName("HOUSEHOLD.ID");

		// Interface properties.
		descriptor.getInterfacePolicy().addParentInterfaceName(org.eclipse.persistence.tools.workbench.test.models.phone.Serviceable.class.getName());

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(405);
		descriptor.setIsIsolated(false);
		descriptor.setAlias("Household");
		
		// Query manager.
		descriptor.getDescriptorQueryManager().checkDatabaseForDoesExist();
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("HOUSEHOLD.ID");
		descriptor.addMapping(idMapping);

		OneToOneMapping headOfHouseholdMapping = new OneToOneMapping();
		headOfHouseholdMapping.setAttributeName("headOfHousehold");
		headOfHouseholdMapping.setGetMethodName("getHeadOfHousehold");
		headOfHouseholdMapping.setSetMethodName("setHeadOfHousehold");
		headOfHouseholdMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Person.class.getName());
		headOfHouseholdMapping.dontUseIndirection();
		headOfHouseholdMapping.addForeignKeyFieldName("HOUSEHOLD.CONTACT_PERSON_ID", "PERSON.ID");
		descriptor.addMapping(headOfHouseholdMapping);

		if (usesSharedAggregates) {
			AggregateObjectMapping serviceMapping = new AggregateObjectMapping();
			serviceMapping.setAttributeName("service");
			serviceMapping.setGetMethodName("getService");
			serviceMapping.setSetMethodName("setService");
			serviceMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Service.class.getName());
			serviceMapping.setIsNullAllowed(false);
			serviceMapping.addFieldNameTranslation("HOUSEHOLD.ID", "primaryContactPerson->HEAD_ID_IN_REFERENCE_HOUSEHOLD_PERSON2");
			serviceMapping.addFieldNameTranslation("HOUSEHOLD.SERVICE_PLAN", "servicePlan->DIRECT");
			serviceMapping.addFieldNameTranslation("HOUSEHOLD.ADDRESS", "billingAddress->DIRECT");
			serviceMapping.addFieldNameTranslation("HOUSEHOLD.HAS_DSL", "rate->METHOD_TRANSFORMER hasDslService");
			serviceMapping.addFieldNameTranslation("HOUSEHOLD.ID", "serviceCalls->ID_IN_REFERENCE_SERVICE_CALL_HOUSEHOLD");
			serviceMapping.addFieldNameTranslation("HOUSEHOLD.CONTACT_TYPE", "primaryContact->CLASS_INDICATOR_FIELD");
			serviceMapping.addFieldNameTranslation("HOUSEHOLD.CONTACT_ID", "primaryContact->QUERY_KEY id");
			serviceMapping.addFieldNameTranslation("HOUSEHOLD.HAS_LONG_DISTANCE", "rate->METHOD_TRANSFORMER hasLongDistanceService");
			serviceMapping.addFieldNameTranslation("HOUSEHOLD.HAS_LOCAL", "rate->METHOD_TRANSFORMER hasLocalService");
			descriptor.addMapping(serviceMapping);
		}
		return descriptor;
	}

	public RelationalDescriptor buildPersonDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Person.class.getName());
		descriptor.addTableName("PERSON");
		descriptor.addPrimaryKeyFieldName("PERSON.ID");

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(405);
		descriptor.setIsIsolated(false);
		descriptor.setAlias("Person");

		// Query manager.
		descriptor.getDescriptorQueryManager().checkDatabaseForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
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

		DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setGetMethodName("getFirstName");
		firstNameMapping.setSetMethodName("setFirstName");
		firstNameMapping.setFieldName("PERSON.F_NAME");
		descriptor.addMapping(firstNameMapping);

		return descriptor;
	}

	public RelationalDescriptor buildPhoneNumberDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.phone.PhoneNumber.class.getName());
		descriptor.addTableName("PHONE_NUMBER");
		descriptor.addPrimaryKeyFieldName("PHONE_NUMBER.ID");

		// Interface properties.
		descriptor.getInterfacePolicy().addParentInterfaceName(org.eclipse.persistence.tools.workbench.test.models.phone.Contact.class.getName());

		// Descriptor properties.
		descriptor.useWeakIdentityMap();
		descriptor.setIdentityMapSize(98);
		descriptor.setIsIsolated(false);
		descriptor.setSequenceNumberFieldName("PHONE_NUMBER.ID");
		descriptor.setSequenceNumberName("org.eclipse.persistence.tools.workbench.test.models.phone.PhoneNumber");
		descriptor.setAlias("PhoneNumber");

		// Query manager.
		descriptor.getDescriptorQueryManager().assumeExistenceForDoesExist();
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("PHONE_NUMBER.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping numberMapping = new DirectToFieldMapping();
		numberMapping.setAttributeName("number");
		numberMapping.setGetMethodName("getNumber");
		numberMapping.setSetMethodName("setNumber");
		numberMapping.setFieldName("PHONE_NUMBER.PHONE_NUMBER");
		descriptor.addMapping(numberMapping);

		return descriptor;
	}

	public RelationalDescriptor buildServiceCallDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.phone.ServiceCall.class.getName());
		descriptor.addTableName("SERVICE_CALL");
		descriptor.addPrimaryKeyFieldName("SERVICE_CALL.ID");

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(405);
		descriptor.setIsIsolated(false);
		descriptor.setAlias("ServiceCall");

		// Query manager.
		descriptor.getDescriptorQueryManager().checkDatabaseForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setGetMethodName("getId");
		idMapping.setSetMethodName("setId");
		idMapping.setFieldName("SERVICE_CALL.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping timeMapping = new DirectToFieldMapping();
		timeMapping.setAttributeName("time");
		timeMapping.setGetMethodName("getTime");
		timeMapping.setSetMethodName("setTime");
		timeMapping.setFieldName("SERVICE_CALL.TIME_OF_CALL");
		descriptor.addMapping(timeMapping);

		VariableOneToOneMapping serviceUserMapping = new VariableOneToOneMapping();
		serviceUserMapping.setAttributeName("serviceUser");
		serviceUserMapping.setGetMethodName("getServiceUser");
		serviceUserMapping.setSetMethodName("setServiceUser");
		serviceUserMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Serviceable.class.getName());
		serviceUserMapping.setIndirectionPolicy(new ProxyIndirectionPolicy());
		serviceUserMapping.addForeignQueryKeyName("SERVICE_CALL.SERVICE_USER_ID", "id");
		serviceUserMapping.setTypeFieldName("SERVICE_CALL.SERVICE_USER_TYPE");
		serviceUserMapping.addClassNameIndicator("org.eclipse.persistence.tools.workbench.test.models.phone.Household", "H");
		serviceUserMapping.addClassNameIndicator("org.eclipse.persistence.tools.workbench.test.models.phone.Company", "C");
		descriptor.addMapping(serviceUserMapping);

		return descriptor;
	}

	public RelationalDescriptor buildServiceDescriptor(boolean usesSharedAggregates) {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Service.class.getName());

		// Descriptor properties.
		descriptor.setAlias("Service");

		// Query manager.
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping billingAddressMapping = new DirectToFieldMapping();
		billingAddressMapping.setAttributeName("billingAddress");
		billingAddressMapping.setFieldName("billingAddress->DIRECT");
		descriptor.addMapping(billingAddressMapping);

		DirectToFieldMapping servicePlanMapping = new DirectToFieldMapping();
		ObjectTypeConverter converter = new ObjectTypeConverter(servicePlanMapping);
		servicePlanMapping.setConverter(converter);
		servicePlanMapping.setAttributeName("servicePlan");
		servicePlanMapping.setFieldName("servicePlan->DIRECT");
		converter.addConversionValue(new java.lang.Integer(0), "Platinum");
		converter.addConversionValue(new java.lang.Integer(2), "Standard");
		converter.addConversionValue(new java.lang.Integer(1), "Gold");
		descriptor.addMapping(servicePlanMapping);

		TransformationMapping rateMapping = new TransformationMapping();
		rateMapping.setAttributeName("rate");
		rateMapping.setAttributeTransformation("calculateRate");
		rateMapping.addFieldTransformation("rate->METHOD_TRANSFORMER hasLocalService", "hasLocalService");
		rateMapping.addFieldTransformation("rate->METHOD_TRANSFORMER hasDslService", "hasDslService");
		rateMapping.addFieldTransformation("rate->METHOD_TRANSFORMER hasLongDistanceService", "hasLongDistanceService");
		descriptor.addMapping(rateMapping);

		if (!usesSharedAggregates) {
			DirectCollectionMapping lines_accountsMapping = new DirectCollectionMapping();
			lines_accountsMapping.setAttributeName("lineAccounts");
			lines_accountsMapping.dontUseIndirection();
			lines_accountsMapping.useBatchReading();
			lines_accountsMapping.useListClassName("java.util.ArrayList");
			lines_accountsMapping.setReferenceTableName("LINE_ACCOUNT");
			lines_accountsMapping.setDirectFieldName("LINE_ACCOUNT.LINE_ACCOUNT");
			lines_accountsMapping.addReferenceKeyFieldName("LINE_ACCOUNT.COMPANY_ID", "lineAccounts->ID_IN_REFERENCE_LINE_ACCOUNT_COMPANY");
			descriptor.addMapping(lines_accountsMapping);
		}

		VariableOneToOneMapping primaryContactMapping = new VariableOneToOneMapping();
		primaryContactMapping.setAttributeName("primaryContact");
		primaryContactMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Contact.class.getName());
		primaryContactMapping.setIndirectionPolicy(new ProxyIndirectionPolicy());
		primaryContactMapping.addForeignQueryKeyName("primaryContact->QUERY_KEY id", "id");
		primaryContactMapping.setTypeFieldName("primaryContact->CLASS_INDICATOR_FIELD");
		primaryContactMapping.addClassNameIndicator("org.eclipse.persistence.tools.workbench.test.models.phone.PhoneNumber", "P");
		primaryContactMapping.addClassNameIndicator("org.eclipse.persistence.tools.workbench.test.models.phone.EmailAddress", "E");
		descriptor.addMapping(primaryContactMapping);

		OneToOneMapping primaryContactPersonMapping = new OneToOneMapping();
		primaryContactPersonMapping.setAttributeName("primaryContactPerson");
		primaryContactPersonMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Person.class.getName());
		primaryContactPersonMapping.dontUseIndirection();
		primaryContactPersonMapping.addForeignKeyFieldName("primaryContactPerson->HEAD_ID_IN_REFERENCE_HOUSEHOLD_PERSON2", "PERSON.ID");
		descriptor.addMapping(primaryContactPersonMapping);

		OneToManyMapping serviceCallsMapping = new OneToManyMapping();
		serviceCallsMapping.setAttributeName("serviceCalls");
		serviceCallsMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.phone.ServiceCall.class.getName());
		serviceCallsMapping.privateOwnedRelationship();
		serviceCallsMapping.setUsesIndirection(false);
		serviceCallsMapping.useListClassName("java.util.ArrayList");
		serviceCallsMapping.addDescendingOrdering("id");
		serviceCallsMapping.addTargetForeignKeyFieldName("SERVICE_CALL.SERVICE_USER_ID", "serviceCalls->ID_IN_REFERENCE_SERVICE_CALL_HOUSEHOLD");
		descriptor.addMapping(serviceCallsMapping);

		if (!usesSharedAggregates) {
			ManyToManyMapping usersMapping = new ManyToManyMapping();
			usersMapping.setAttributeName("users");
			usersMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Person.class.getName());
			usersMapping.setUsesIndirection(false);
			usersMapping.useListClassName("java.util.ArrayList");
			usersMapping.setRelationTableName("COMPANY_PERSON");
			usersMapping.addSourceRelationKeyFieldName("COMPANY_PERSON.COMPANY_ID", "users->ID_IN_REFERENCE_COMPANY_PERSON_COMPANY");
			usersMapping.addTargetRelationKeyFieldName("COMPANY_PERSON.PERSON_ID", "PERSON.ID");
			descriptor.addMapping(usersMapping);
		}

		return descriptor;
	}

	public RelationalDescriptor buildServiceableDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsForInterface();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.phone.Serviceable.class.getName());

		// Interface properties.

		// Descriptor properties.
		descriptor.setAlias("Serviceable");
		descriptor.addAbstractQueryKey("id");

		// Query manager.
		//Named Queries

		// Event manager.

		return descriptor;
	}


	public Project getRuntimeProject() {
		return this.runtimeProject;
	}

}

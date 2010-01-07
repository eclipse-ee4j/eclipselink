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

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.test.models.phone.Service;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionOrdering;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWColumnQueryKeyPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

public class LegacyPhoneCompanyProject extends RelationalTestProject{
	private boolean sharedAggregates;


	/**
	 * You can create one of two phone company projects -
	 * withSharedAggregates - means that some of the mappings will
	 * be missing, but the Address aggregate is shared between Person and Company.
	 */
	public LegacyPhoneCompanyProject(boolean sharedAggregates) {
		super();
		this.sharedAggregates = sharedAggregates;
		this.initialize2();
	}

	public static MWRelationalProject emptyProject() {
		MWRelationalProject project = new MWRelationalProject("PhoneCompany", spiManager(), mySqlPlatform());

		// Defaults policy
		//added this for testing purposes , it was failing because of useMethodAccessing
		project.getDefaultsPolicy().getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_CHECK_DATABASE);
		project.getDefaultsPolicy().getCachingPolicy().setCacheSize(405);
		project.getDefaultsPolicy().getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		project.getDefaultsPolicy().setMethodAccessing(true);
	
		return project;
	}
	
	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	@Override
	protected void initializeDatabase() {
		super.initializeDatabase();
		this.initializeSequenceTable();
	}

	protected void initialize2() {
		if (this.sharedAggregates) {
			this.getProject().setName(this.getProject().getName() + " (shared aggregates)");
		}
		// we don't initialize anything during the first pass, because we need
		// the 'sharedAggregates' flag set
		this.initializeDatabase2();
		this.initializeDescriptors2();
	}

	public MWTableDescriptor getCompanyDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Company");
	}
	
	public MWInterfaceDescriptor getContactDescriptor() {
		return (MWInterfaceDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Contact");
	}
	
	public MWTableDescriptor getEmailAddressDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.EmailAddress");
	}
	
	public MWTableDescriptor getHouseholdDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Household");
	}
	
	public MWTableDescriptor getPersonDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Person");
	}
	
	public MWTableDescriptor getPhoneNumberDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.PhoneNumber");
	}
	
	public MWInterfaceDescriptor getServiceableDescriptor() {
		return (MWInterfaceDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Serviceable");
	}
	
	public MWTableDescriptor getServiceCallDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.ServiceCall");
	}
	
	public MWAggregateDescriptor getServiceDescriptor() {
		return (MWAggregateDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Service");
	}
	
	protected void initializeCompanyDescriptor() {
		MWTableDescriptor descriptor = getCompanyDescriptor();
		MWTable table = database().tableNamed("COMPANY");
		descriptor.setPrimaryTable(table);
	
		// Direct to field mappings
		addDirectMapping(descriptor, "id", table, "ID");
		addDirectMapping(descriptor, "name", table, "NAME");	
			
		// Aggregate mappings
		MWAggregateDescriptor serviceDescriptor = ((MWAggregateDescriptor) getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Service")).asMWAggregateDescriptor();
		MWAggregateMapping serviceMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("service"));
		serviceMapping.setReferenceDescriptor(serviceDescriptor);
		
		Iterator pathsToFields = CollectionTools.sort(serviceMapping.pathsToFields()).iterator();
	 	String[] fieldNames;
	 	if (this.sharedAggregates) {
			fieldNames = new String[] {"ADDRESS", "CONTACT_TYPE", "ID", "CONTACT_ID", "HAS_DSL", "HAS_LOCAL", "HAS_LONG_DISTANCE", "ID", "SERVICE_PLAN"};
	 	} else {
			fieldNames = new String[] {"ADDRESS", "ID", "CONTACT_TYPE", "ID", "CONTACT_ID", "HAS_DSL", "HAS_LOCAL", "HAS_LONG_DISTANCE", "ID", "SERVICE_PLAN", "ID"};
	 	}
		for(int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) pathsToFields.next();
			association.setColumn(table.columnNamed(fieldNames[i]));
		}
	
	}
	
	public void initializeCompanyPersonTable() {
		MWTable table = database().addTable("COMPANY_PERSON");
		addPrimaryKeyField(table, "COMPANY_ID", "integer");
		addPrimaryKeyField(table, "PERSON_ID", "integer");
	}
	
	protected void initializeCompanyTable()  {
		MWTable table = database().addTable("COMPANY");
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "NAME", "varchar", 50);
		addField(table, "ADDRESS", "varchar", 100);
		addField(table, "SERVICE_PLAN", "integer");
		addField(table, "CONTACT_PERSON_ID", "integer");
		addField(table, "CONTACT_ID", "integer");
		addField(table, "CONTACT_TYPE", "varchar", 20);
		
		// For rate transformation mapping
		addField(table, "HAS_LOCAL", "integer");
		addField(table, "HAS_LONG_DISTANCE", "integer");
		addField(table, "HAS_DSL", "integer");
	}
	
	public void initializeContactDescriptor() {
		MWInterfaceDescriptor descriptor = getContactDescriptor();
		
	 	descriptor.addImplementor(getEmailAddressDescriptor());
		descriptor.addImplementor(getPhoneNumberDescriptor());
	}

	protected void initializeDatabase2() {
		this.initializeCompanyTable();
		this.initializeHouseholdTable();
		this.initializePersonTable();
		this.initializeServiceCallTable();
		this.initializeCompanyPersonTable();
		this.initializePhoneNumberTable();
		this.initializeEmailTable();
		this.initializeLineAccountTable();
	 
		this.initializeTableReferences();
	}

	protected void initializeDescriptors2() {
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.PhoneNumber");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.EmailAddress");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Contact");
		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Service");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Person");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Company");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Household");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.Serviceable");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.phone.ServiceCall");
		
		initializePhoneNumberDescriptor();
		initializeEmailDescriptor();
		initializeContactDescriptor();
		initializeServiceCallDescriptor();
		initializeServiceDescriptor();
		initializePersonDescriptor();
		initializeCompanyDescriptor();
		initializeHouseholdDescriptor();
		initializeServiceableDescriptor();
	}

	public void initializeEmailDescriptor(){
		MWTableDescriptor descriptor = getEmailAddressDescriptor();
	
		// Table info
		MWTable table = database().tableNamed("EMAIL");	
		descriptor.setPrimaryTable(table);
		
		// Sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
		descriptor.setSequenceNumberName(descriptor.getName());
		
		// Caching policy
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK);
		descriptor.getCachingPolicy().setCacheSize(98);
		
		// Mappings
		
		//Direct to Field
		descriptor.addQueryKey("email",table.columnNamed("ADDRESS"));
		
		addDirectMapping(descriptor, "id", table, "ID");	
		addDirectMapping(descriptor, "address", table, "ADDRESS");
	}
	
	public void initializeEmailTable() {
		MWTable table = database().addTable("EMAIL");
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "ADDRESS", "varchar", 50);
	}
	
	protected void initializeHouseholdDescriptor() {
		MWTableDescriptor householdDescriptor = getHouseholdDescriptor();
		MWTableDescriptor personDescriptor = getPersonDescriptor();
		MWTable householdTable = database().tableNamed("HOUSEHOLD");
		
		householdDescriptor.setPrimaryTable(householdTable);
	
		// Direct to field mappings
		addDirectMapping(householdDescriptor, "id", householdTable, "ID");	
	
		// 1-1 - headOfHousehold
		MWOneToOneMapping headMapping = householdDescriptor.addOneToOneMapping(householdDescriptor.getMWClass().attributeNamed("headOfHousehold"));
		headMapping.setReferenceDescriptor(personDescriptor);
		headMapping.setReference(householdTable.referenceNamed("HOUSEHOLD_PERSON"));
		
		if (this.sharedAggregates) {
			
			// Aggregate mappings
			MWAggregateMapping serviceMapping = householdDescriptor.addAggregateMapping(householdDescriptor.getMWClass().attributeNamed("service"));
			serviceMapping.setReferenceDescriptor(getServiceDescriptor());
				
			Iterator pathsToFields = CollectionTools.sort(serviceMapping.pathsToFields()).iterator();
	  		String[] fieldNames = new String[] {"ADDRESS", "CONTACT_TYPE", "ID", "CONTACT_ID", "HAS_DSL", "HAS_LOCAL", "HAS_LONG_DISTANCE", "ID", "SERVICE_PLAN"};
	
			for(int i=0; i<fieldNames.length; i++) {
				MWAggregatePathToColumn association = (MWAggregatePathToColumn) pathsToFields.next();
				association.setColumn(householdTable.columnNamed(fieldNames[i]));
			}
	
		}
	}
	
	protected void initializeHouseholdTable()  {
		MWTable table = database().addTable("HOUSEHOLD");
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "HEAD_ID", "integer");
		addField(table, "ADDRESS", "varchar", 100);
		addField(table, "SERVICE_PLAN", "integer");
		addField(table, "CONTACT_PERSON_ID", "integer");
		addField(table, "CONTACT_ID", "integer");
		addField(table, "CONTACT_TYPE", "varchar", 20);
		
		// For rate transformation mapping
		addField(table, "HAS_LOCAL", "integer");
		addField(table, "HAS_LONG_DISTANCE", "integer");
		addField(table, "HAS_DSL", "integer");
	}

	public void initializeLineAccountTable() {
		MWTable table = database().addTable("LINE_ACCOUNT");
		addPrimaryKeyField(table, "COMPANY_ID", "integer");
		addPrimaryKeyField(table, "LINE_ACCOUNT", "varchar", 50);
	}

	protected void initializePersonDescriptor()  {
		MWTableDescriptor descriptor = getPersonDescriptor();
	
		// Table info
		MWTable table = database().tableNamed("PERSON");
		descriptor.setPrimaryTable(table);
	
	
		// Mappings
		
		// Direct to field mappings
		addDirectMapping(descriptor, "id", table, "ID");	
		addDirectMapping(descriptor, "firstName", table, "F_NAME");	
		addDirectMapping(descriptor, "lastName", table, "L_NAME");	
	}

	protected void initializePersonTable()  {
		MWTable table = database().addTable("PERSON");
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "F_NAME", "varchar", 50);
		addField(table, "L_NAME", "varchar", 50);
	}

	public void initializePhoneNumberDescriptor(){
		MWTableDescriptor descriptor = getPhoneNumberDescriptor();
	
		// Table info
		MWTable table = database().tableNamed("PHONE_NUMBER");	
		descriptor.setPrimaryTable(table);
		
		// Sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ID"));
		descriptor.setSequenceNumberName(descriptor.getName());
		
		// Caching policy
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_ASSUME_EXISTENCE);
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK);
		descriptor.getCachingPolicy().setCacheSize(98);
	
		// Mappings
		
		//Direct to Field
		addDirectMapping(descriptor, "id", table, "ID");	
		addDirectMapping(descriptor, "number", table, "PHONE_NUMBER");
	}
	
	protected void initializePhoneNumberTable()  {
		MWTable table = database().addTable("PHONE_NUMBER");
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "PHONE_NUMBER", "varchar", 50);
	}
	
	public void initializeServiceableDescriptor(){
		MWInterfaceDescriptor descriptor = getServiceableDescriptor();
	
		descriptor.addImplementor(getHouseholdDescriptor());
		descriptor.addImplementor(getCompanyDescriptor());
	}

	protected MWTableDescriptor initializeServiceCallDescriptor()  {
	 	MWInterfaceDescriptor serviceableDescriptor = getServiceableDescriptor();
	 	MWTable serviceCallTable = database().tableNamed("SERVICE_CALL");
		
		MWTableDescriptor descriptor = getServiceCallDescriptor();
		descriptor.setPrimaryTable(serviceCallTable);
	
		// Mappings
		
		// Direct to field mappings
		addDirectMapping(descriptor, "id", serviceCallTable, "ID");	
		addDirectMapping(descriptor, "time", serviceCallTable, "TIME_OF_CALL");	
	
		// Variable 1-1 -  serviceUser (back pointer for 1-M from Service)
	  	MWVariableOneToOneMapping serviceableMapping = descriptor.addVariableOneToOneMapping(descriptor.getMWClass().attributeNamed("serviceUser"));
	  	serviceableMapping.setReferenceDescriptor(serviceableDescriptor);
	  	serviceableMapping.setUseNoIndirection();

		MWColumnQueryKeyPair association = serviceableMapping.addColumnQueryKeyPair((MWColumn)serviceCallTable.columns().next(), "id");
		association.setColumn(serviceCallTable.columnNamed("SERVICE_USER_ID"));
		association.setQueryKeyName("id");
		
		MWRelationalClassIndicatorFieldPolicy indicatorPolicy = serviceableMapping.getClassIndicatorPolicy();
		indicatorPolicy.setField(serviceCallTable.columnNamed("SERVICE_USER_TYPE"));
		indicatorPolicy.setIndicatorType(new MWTypeDeclaration(indicatorPolicy, typeFor(java.lang.String.class)));
	
		indicatorPolicy.getClassIndicatorValueForDescriptor(getHouseholdDescriptor()).setInclude(true);
		indicatorPolicy.getClassIndicatorValueForDescriptor(getHouseholdDescriptor()).setIndicatorValue("H");
	
		indicatorPolicy.getClassIndicatorValueForDescriptor(getCompanyDescriptor()).setInclude(true);
		indicatorPolicy.getClassIndicatorValueForDescriptor(getCompanyDescriptor()).setIndicatorValue("C");
		
		return descriptor;
	}

	protected void initializeServiceCallTable() {
		MWTable table = database().addTable("SERVICE_CALL");
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "SERVICE_USER_ID", "integer"); // back pointer to Serviceable (owner of service aggregate mapping)
		addField(table, "SERVICE_USER_TYPE", "varchar", 50); // class indicator field for above pointer
		addField(table, "TIME_OF_CALL", "date");
	}
	
	protected MWAggregateDescriptor initializeServiceDescriptor()  {
		MWInterfaceDescriptor contactDescriptor = getContactDescriptor();
		MWTableDescriptor serviceCallDescriptor = getServiceCallDescriptor();
		MWTableDescriptor personDescriptor = getPersonDescriptor();
		MWTable relationTable = tableNamed("COMPANY_PERSON");
		MWTable householdTable = tableNamed("HOUSEHOLD");
		MWTable serviceCallTable = tableNamed("SERVICE_CALL");
		MWTable lineAccountTable = tableNamed("LINE_ACCOUNT");
		
		MWAggregateDescriptor descriptor = getServiceDescriptor();
	
		// Mappings
		
		// Direct to field mappings
		MWDirectToFieldMapping billingAddressMapping = addDirectMapping(descriptor, "billingAddress");
		billingAddressMapping.setUsesMethodAccessing(false);
	
		// Object Type - servicePlan
		MWDirectToFieldMapping servicePlanMapping = (MWDirectToFieldMapping) addDirectMapping(descriptor, "servicePlan").asMWObjectTypeMapping();
		servicePlanMapping.setUsesMethodAccessing(false);
		((MWObjectTypeConverter) servicePlanMapping.getConverter()).setDataType(new MWTypeDeclaration(servicePlanMapping.getConverter(), typeFor(java.lang.Integer.class)));
		((MWObjectTypeConverter) servicePlanMapping.getConverter()).setAttributeType(new MWTypeDeclaration(servicePlanMapping.getConverter(), typeFor(java.lang.String.class)));
		
		try {
			for (int i = 0; i < Service.SERVICE_PLANS.length; i++)
			((MWObjectTypeConverter) servicePlanMapping.getConverter()).addValuePair(new Integer(i), Service.SERVICE_PLANS[i]);
		}
		catch (MWObjectTypeConverter.ConversionValueException cve) { /*** shouldn't happen ***/}
		
		
		//1-1 - primaryContactPerson
		MWOneToOneMapping contactPersonMapping = addDirectMapping(descriptor, "primaryContactPerson").asMWOneToOneMapping();
		contactPersonMapping.setUsesMethodAccessing(false);
		contactPersonMapping.setReferenceDescriptor(personDescriptor);
		contactPersonMapping.setReference(householdTable.referenceNamed("HOUSEHOLD_PERSON2"));
		
		//Variable 1-1 - primaryContact
		MWVariableOneToOneMapping contactMapping = descriptor.addVariableOneToOneMapping(descriptor.getMWClass().attributeNamed("primaryContact"));
		contactMapping.setReferenceDescriptor(contactDescriptor);
		contactMapping.setUsesMethodAccessing(false);
		contactMapping.addColumnQueryKeyPair(null, "id");
		contactMapping.setUseNoIndirection();

		MWRelationalClassIndicatorFieldPolicy indicatorPolicy = contactMapping.getClassIndicatorPolicy();
		indicatorPolicy.setIndicatorType(new MWTypeDeclaration(indicatorPolicy, typeFor(java.lang.String.class)));
	
		indicatorPolicy.getClassIndicatorValueForDescriptor(getEmailAddressDescriptor()).setInclude(true);
		indicatorPolicy.getClassIndicatorValueForDescriptor(getEmailAddressDescriptor()).setIndicatorValue("E");
		
		indicatorPolicy.getClassIndicatorValueForDescriptor(getPhoneNumberDescriptor()).setInclude(true);
		indicatorPolicy.getClassIndicatorValueForDescriptor(getPhoneNumberDescriptor()).setIndicatorValue("P");
		
		
		// 1-M - serviceCalls
		MWOneToManyMapping serviceCallsMapping = addDirectMapping(descriptor, "serviceCalls").asMWOneToManyMapping();
		serviceCallsMapping.setUsesMethodAccessing(false);
		serviceCallsMapping.setPrivateOwned(true);
		serviceCallsMapping.setReference(serviceCallTable.referenceNamed("SERVICE_CALL_HOUSEHOLD"));
		serviceCallsMapping.setReferenceDescriptor(serviceCallDescriptor);
		MWCollectionOrdering ordering = serviceCallsMapping.addOrdering(serviceCallDescriptor.queryKeyNamed("id"));
        ordering.setAscending(false);
		serviceCallsMapping.setUseNoIndirection();
		
		// Transformation - rate
		MWClass serviceClass = descriptor.getMWClass();
		MWRelationalTransformationMapping rateMapping = (MWRelationalTransformationMapping) descriptor.addTransformationMapping(descriptor.getMWClass().attributeNamed("rate"));
		rateMapping.setUsesMethodAccessing(false);
		rateMapping.setAttributeTransformer(methodNamed(serviceClass, "calculateRate"));
		rateMapping.addFieldTransformerAssociation(null, methodNamed(serviceClass, "hasLocalService"));
		rateMapping.addFieldTransformerAssociation(null, methodNamed(serviceClass, "hasLongDistanceService"));
		rateMapping.addFieldTransformerAssociation(null, methodNamed(serviceClass, "hasDslService"));
		
		
		if (! this.sharedAggregates) { 
			// There is no way to do a M-M or DC with shared aggregates...
			// M-M providers 
			MWManyToManyMapping usersMapping = addDirectMapping(descriptor, "users").asMWManyToManyMapping();
			usersMapping.setUsesMethodAccessing(false);
			usersMapping.setRelationTable(relationTable);
			usersMapping.setPrivateOwned(false);
			usersMapping.setSourceReference(relationTable.referenceNamed("COMPANY_PERSON_COMPANY"));
			usersMapping.setTargetReference(relationTable.referenceNamed("COMPANY_PERSON_PERSON"));
			usersMapping.setReferenceDescriptor(personDescriptor);
			usersMapping.setUseNoIndirection();
			
			// DC - lines_accounts
			MWRelationalDirectCollectionMapping lines_accountsMapping = 
				(MWRelationalDirectCollectionMapping) addDirectMapping(descriptor, "lineAccounts").asMWDirectCollectionMapping();
			lines_accountsMapping.setUsesMethodAccessing(false);
			lines_accountsMapping.setTargetTable(lineAccountTable);
			lines_accountsMapping.setReference(lineAccountTable.referenceNamed("LINE_ACCOUNT_COMPANY"));
			lines_accountsMapping.setDirectValueColumn(lineAccountTable.columnNamed("LINE_ACCOUNT"));
			lines_accountsMapping.setUsesBatchReading(true);
			lines_accountsMapping.setUseNoIndirection();
		}
		
		return descriptor;
	}

	public void initializeTableReferences() {
		MWTable relationTable = getProject().getDatabase().tableNamed("COMPANY_PERSON");
		MWTable personTable = getProject().getDatabase().tableNamed("PERSON");
		MWTable householdTable = getProject().getDatabase().tableNamed("HOUSEHOLD");
		MWTable companyTable = getProject().getDatabase().tableNamed("COMPANY");
		MWTable lineAccountTable = getProject().getDatabase().tableNamed("LINE_ACCOUNT");
		MWTable serviceCallTable = getProject().getDatabase().tableNamed("SERVICE_CALL");
		 
		// COMPANY_PERSON to PERSON
		addReference("COMPANY_PERSON_PERSON", relationTable, personTable, "PERSON_ID", "ID");
	
		// COMPANY_PERSON to COMPANY
		addReference("COMPANY_PERSON_COMPANY", relationTable, companyTable, "COMPANY_ID", "ID");
	
		// HOUSEHOLD TO PERSON (headOfHousehold)
		addReference("HOUSEHOLD_PERSON2", householdTable, personTable, "HEAD_ID", "ID");
	
		// HOUSEHOLD TO PERSON (contact)
		addReference("HOUSEHOLD_PERSON", householdTable, personTable, "CONTACT_PERSON_ID", "ID");
	
		// COMPANY TO PERSON
		addReference("COMPANY_PERSON", companyTable, personTable, "CONTACT_PERSON_ID", "ID");
		
		// LINE_ACCOUNT TO COMPANY
		addReference("LINE_ACCOUNT_COMPANY", lineAccountTable, companyTable, "COMPANY_ID", "ID");
	
		// SERVICE_CALL TO HOUSEHOLD
		addReference("SERVICE_CALL_HOUSEHOLD", serviceCallTable, householdTable, "SERVICE_USER_ID", "ID");
		// SERVICE_CALL TO COMPANY
		addReference("SERVICE_CALL_COMPANY", serviceCallTable, companyTable, "SERVICE_USER_ID", "ID");
	}

}

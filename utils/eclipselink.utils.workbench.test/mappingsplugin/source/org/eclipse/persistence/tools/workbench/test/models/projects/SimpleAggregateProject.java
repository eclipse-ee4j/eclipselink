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

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



public class SimpleAggregateProject extends RelationalTestProject {

	public static MWRelationalProject emptyProject() {
		MWRelationalProject project = new MWRelationalProject("SimpleAggregate", spiManager(), mySqlPlatform());
	
		// defaults policy
		project.getDefaultsPolicy().getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_CHECK_CACHE);
		project.getDefaultsPolicy().getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE);
		return project;
	}

	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	public MWAggregateDescriptor getAddressDescriptor() {
		return (MWAggregateDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.Address");
	}

	public MWAggregateDescriptor getAddressSubClassDescriptor() {
		return (MWAggregateDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.AddressSubClass");
	}

	public MWTableDescriptor getPersonDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.Person");
	}

	public void initializeAddressDescriptor()
	{
		MWAggregateDescriptor descriptor = getAddressDescriptor();

		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy) descriptor.getInheritancePolicy();
		((MWRelationalClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy()).setClassNameIsIndicator(true);
		
		//direct to field mappings
		MWDirectToFieldMapping cityMapping = addDirectMapping(descriptor, "city");
		cityMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping stateMapping = addDirectMapping(descriptor, "state");
		stateMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping streetMapping = addDirectMapping(descriptor, "street");
		streetMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping zipMapping = addDirectMapping(descriptor, "zip");
		zipMapping.setUsesMethodAccessing(true);
	}

	public void initializeAddressSubClassDescriptor()
	{
		MWAggregateDescriptor descriptor = getAddressSubClassDescriptor();
		
		//direct to field mappings
		MWDirectToFieldMapping countryMapping = addDirectMapping(descriptor, "country");
		countryMapping.setUsesMethodAccessing(true);
		
		descriptor.addInheritancePolicy();
		MWRelationalDescriptorInheritancePolicy inheritancePolicy = (MWRelationalDescriptorInheritancePolicy) descriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getAddressDescriptor());
		inheritancePolicy.setReadSubclassesOnQuery(false);
		
	}

	public void initializeAddressTable() {
		MWTable table = database().addTable("ADDRESS");
		
		addField(table, "CITY", "varchar", 20);
		addField(table, "COUNTRY", "varchar", 20);

		addPrimaryKeyField(table, "ID", "decimal", 20);
		addField(table, "P_CODE", "varchar", 20);
		addField(table, "STATE", "varchar", 20);
		addField(table, "STREET", "varchar", 20);
		addField(table, "TYPE", "varchar", 20);

	}

	@Override
	protected void initializeDatabase() {
		super.initializeDatabase();
		this.initializeSequenceTable();
		this.initializeAddressTable();
		this.initializePersonTable();	
	}

	@Override
	protected void initializeDescriptors() {
		super.initializeDescriptors();
		
		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.Address");
		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.AddressSubClass");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.simpleaggregate.Person");
		
		this.initializeAddressDescriptor();
		this.initializeAddressSubClassDescriptor();
		this.initializePersonDescriptor();
	}

	public void initializePersonDescriptor(){
		MWTableDescriptor descriptor = getPersonDescriptor();
	
		MWTable table = database().tableNamed("PERSON");	
		descriptor.setPrimaryTable(table);
	
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        descriptor.getCachingPolicy().setCacheSize(100);

		//multi-table policy
		descriptor.addMultiTableInfoPolicy();

		MWTable addressTable = tableNamed("ADDRESS");
		descriptor.addAssociatedTable(addressTable);

		// Mappings
		
		//Direct to Field
		descriptor.addQueryKey("email", table.columnNamed("ADDRESS"));
	
		MWDirectToFieldMapping ageMapping = addDirectMapping(descriptor, "age", table, "AGE");
		ageMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping firstNameMapping = addDirectMapping(descriptor, "firstName", table, "F_NAME");
		firstNameMapping.setUsesMethodAccessing(true);

		MWDirectToFieldMapping idMapping = addDirectMapping(descriptor, "id", table, "ID");
		idMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping lastNameMapping = addDirectMapping(descriptor, "lastName", table, "L_NAME");
		lastNameMapping.setUsesMethodAccessing(true);

		//object type mappings
		MWDirectToFieldMapping genderMapping = addDirectMapping(descriptor, "gender", table, "GENDER");
		MWObjectTypeConverter converter = genderMapping.setObjectTypeConverter();
		genderMapping.setUsesMethodAccessing(true);
		
		//TODO need to test with attribute and data types of something other than String
		try {
			converter.addValuePair("F", "Female");
			converter.addValuePair("M", "Male");
		}
		catch (MWObjectTypeConverter.ConversionValueException cve) 
		{ 
			/*** shouldn't happen ***/
		} 


		//aggregate mappings
		MWAggregateMapping addressMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("address"));
		addressMapping.setUsesMethodAccessing(true);
		addressMapping.setReferenceDescriptor(getAddressSubClassDescriptor());
		
		Iterator fieldAssociations = CollectionTools.sort(addressMapping.pathsToFields()).iterator();
		String[] fieldNames = new String[] {"CITY", "STATE", "STREET", "P_CODE", "TYPE", "COUNTRY"};
		for(int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			association.setColumn(addressTable.columnNamed(fieldNames[i]));
		}
	
	}

	public void initializePersonTable() {
		MWTable table = database().addTable("PERSON");
		
		addField(table, "ADDRESS", "varchar", 50);
		addField(table, "AGE", "decimal", 10);
		addField(table, "F_NAME", "varchar", 20);
		addField(table, "GENDER", "varchar", 20);
		addPrimaryKeyField(table, "ID", "decimal", 15);

		addField(table, "L_NAME", "varchar", 20);

	}

}

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

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInterfaceAliasPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;


public class LegacySimpleContactProject extends RelationalTestProject {

	public LegacySimpleContactProject() {
		super();
	}
	
	public static MWRelationalProject emptyProject() {
		MWRelationalProject project = new MWRelationalProject("SimpleContact", spiManager(), mySqlPlatform());
	
		// defaults policy
		project.getDefaultsPolicy().getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_CHECK_CACHE);
		project.getDefaultsPolicy().getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE);
		
		return project;
	}
	
	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	public MWClass getContactClass() {
		return typeNamed(getPackageName() + ".Contact");
	}
	
	public MWInterfaceDescriptor getContactDescriptor() {
		return (MWInterfaceDescriptor)  getProject().descriptorForTypeNamed(getPackageName() + ".Contact");
	}
	
	public MWClass getPersonClass() {
		return typeNamed(getPackageName() + ".Person");
	}
	
	public MWClass getPersonImplClass() {
		return typeNamed(getPackageName() + ".PersonImpl");
	}
	
	public MWTableDescriptor getEmailAddressDescriptor() {
			return (MWTableDescriptor)  getProject().descriptorForTypeNamed(getPackageName() + ".EmailAddress");
	}
	
	public MWTableDescriptor getPersonImplDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed(getPackageName() + ".PersonImpl");
	}
	
	public MWTableDescriptor getPhoneNumberDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed(getPackageName() + ".PhoneNumber");
	}
	
	public String getPackageName() {
		return "org.eclipse.persistence.tools.workbench.test.models.contact";
	}
	
	public MWTable getPersonTable() {
		return getProject().getDatabase().tableNamed("PERSON");
	}
	
	public void initializeContactDescriptor(){
		MWInterfaceDescriptor contactDescriptor = (MWInterfaceDescriptor) getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.contact.Contact");
		
		contactDescriptor.addImplementor(getEmailAddressDescriptor());
		contactDescriptor.addImplementor(getPhoneNumberDescriptor());
	}
	
	@Override
	protected void initializeDatabase() {
		super.initializeDatabase();
		this.initializeSequenceTable();
		this.initializeEmailAddressTable();
		this.initializePersonTable();
		this.initializePhoneNumberTable();	
	}
	
	@Override
	protected void initializeDescriptors() {
		super.initializeDescriptors();

		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.contact.Contact");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.contact.EmailAddress");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.contact.PersonImpl");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.contact.PhoneNumber");
		
		this.initializeEmailDescriptor();
		this.initializePhoneNumberDescriptor();
		this.initializeContactDescriptor();
		this.initializePersonDescriptor();
	}

	public void initializeEmailDescriptor(){
		MWTableDescriptor descriptor = getEmailAddressDescriptor();
	
		MWTable table = database().tableNamed("EMAILADDRESS");	
		descriptor.setPrimaryTable(table);
	
		// Mappings
		
		//Direct to Field
		descriptor.addQueryKey("email", table.columnNamed("ADDRESS"));
	
		MWDirectToFieldMapping idMapping = addDirectMapping(descriptor, "id", table, "ID");
		idMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping addressMapping = addDirectMapping(descriptor, "address", table, "ADDRESS");
		addressMapping.setUsesMethodAccessing(true);
	}

	public void initializeEmailAddressTable() {
		MWTable table = database().addTable("EMAILADDRESS");
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "ADDRESS", "varchar", 20).setAllowsNull(false);
	}

	public MWTableDescriptor initializePersonDescriptor(){
		MWTableDescriptor descriptor = getPersonImplDescriptor();
		
		MWTable personTable = database().tableNamed("PERSON");	
		descriptor.setPrimaryTable(personTable);
		
		// Single-implementor (or interfaceAlias) policy
		
		descriptor.addInterfaceAliasPolicy();
		MWClass interfaceAlias = refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.contact.Person");
		((MWDescriptorInterfaceAliasPolicy) descriptor.getInterfaceAliasPolicy()).setInterfaceAlias(interfaceAlias);
		
		// Mappings
				
		//Variable One to One
		descriptor.addDirectMapping(descriptor.getMWClass().attributeNamed("contact"));
		MWVariableOneToOneMapping contactMapping = descriptor.mappingNamed("contact").asMWVariableOneToOneMapping();
		contactMapping.setReferenceDescriptor(getContactDescriptor());
		contactMapping.setUsesMethodAccessing(true);
		contactMapping.setUseNoIndirection();
	       
		contactMapping.addColumnQueryKeyPair(personTable.columnNamed("ID"), "id");
	
		contactMapping.getClassIndicatorPolicy().setField(personTable.columnNamed("CONTACT_TYPE"));
		contactMapping.getClassIndicatorPolicy().setIndicatorType(new MWTypeDeclaration(contactMapping.getClassIndicatorPolicy(), typeFor(java.lang.String.class)));

		contactMapping.getClassIndicatorPolicy().getClassIndicatorValueForDescriptor(getEmailAddressDescriptor()).setInclude(true);
		contactMapping.getClassIndicatorPolicy().getClassIndicatorValueForDescriptor(getEmailAddressDescriptor()).setIndicatorValue("E");


		contactMapping.getClassIndicatorPolicy().getClassIndicatorValueForDescriptor(getPhoneNumberDescriptor()).setInclude(true);
		contactMapping.getClassIndicatorPolicy().getClassIndicatorValueForDescriptor(getPhoneNumberDescriptor()).setIndicatorValue("P");
	
        return descriptor;
	}
    
	public void initializePersonTable() {
		MWTable table = database().addTable("PERSON");
		addPrimaryKeyField(table, "ID", "decimal", 15);
		addField(table, "CONTACT_ID", "integer");
		addField(table, "CONTACT_TYPE", "varchar", 20);
	}

	public void initializePhoneNumberDescriptor(){
	  	MWTableDescriptor descriptor = getPhoneNumberDescriptor();
		MWTable phoneTable = database().tableNamed("PHONENUMBER");	
		descriptor.setPrimaryTable(phoneTable);	
				
		// Mappings
	
		MWDirectToFieldMapping idMapping = addDirectMapping(descriptor, "id", phoneTable, "ID");
		idMapping.setUsesMethodAccessing(true);
		
		MWDirectToFieldMapping numberMapping = addDirectMapping(descriptor, "number", phoneTable, "P_NUM");
		numberMapping.setUsesMethodAccessing(true);
	}

	public void initializePhoneNumberTable() {
		MWTable table = database().addTable("PHONENUMBER");
		addPrimaryKeyField(table, "ID", "decimal", 20);
		addField(table, "P_NUM", "varchar", 20);
	}
	
}

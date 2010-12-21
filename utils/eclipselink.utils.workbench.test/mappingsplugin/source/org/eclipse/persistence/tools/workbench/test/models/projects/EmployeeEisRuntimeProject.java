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

import java.util.HashMap;
import java.util.Iterator;
import javax.xml.namespace.QName;

import org.eclipse.persistence.tools.workbench.test.models.eis.employee.Address;
import org.eclipse.persistence.tools.workbench.test.models.eis.employee.Dependent;
import org.eclipse.persistence.tools.workbench.test.models.eis.employee.Employee;
import org.eclipse.persistence.tools.workbench.test.models.eis.employee.LargeProject;
import org.eclipse.persistence.tools.workbench.test.models.eis.employee.NormalHoursTransformer;
import org.eclipse.persistence.tools.workbench.test.models.eis.employee.PhoneNumber;
import org.eclipse.persistence.tools.workbench.test.models.eis.employee.Project;

import org.eclipse.persistence.eis.EISConnectionSpec;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.eis.adapters.aq.AQPlatform;
import org.eclipse.persistence.eis.adapters.jms.JMSPlatform;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.eis.mappings.EISCompositeCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeDirectCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeObjectMapping;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.eis.mappings.EISOneToManyMapping;
import org.eclipse.persistence.eis.mappings.EISOneToOneMapping;
import org.eclipse.persistence.eis.mappings.EISTransformationMapping;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.platform.database.AttunityPlatform;

public final class EmployeeEisRuntimeProject 
{
	private org.eclipse.persistence.sessions.Project runtimeProject;
	public EmployeeEisRuntimeProject() {
		
		super();
		this.initialize();
	}
	
	private void initialize() {
		this.runtimeProject = new org.eclipse.persistence.sessions.Project();
		this.runtimeProject.setName("Employee-EIS");
		EISLogin login = new EISLogin(new JMSPlatform());
		login.setConnectionSpec(new EISConnectionSpec());
		login.setConnectionFactoryURL("www.imguessingatthis.com");
		this.runtimeProject.setLogin(login);
		this.initializeDescriptors();
	}
	
	private void initializeDescriptors() {
		this.runtimeProject.addDescriptor(this.buildAddressDescriptor());
		this.runtimeProject.addDescriptor(this.buildDependentDescriptor());
		this.runtimeProject.addDescriptor(this.buildEmployeeDescriptor());
		this.runtimeProject.addDescriptor(this.buildPhoneNumberDescriptor());
		this.runtimeProject.addDescriptor(this.buildProjectDescriptor());
		this.runtimeProject.addDescriptor(this.buildLargeProjectDescriptor());
		
		// Set the namespaces on all descriptors.
        
        for (Iterator descriptors = this.runtimeProject.getOrderedDescriptors().iterator(); descriptors.hasNext(); ) {
            ((EISDescriptor) descriptors.next()).setNamespaceResolver(new NamespaceResolver());
        }
	}
	
	private EISDescriptor buildAddressDescriptor() {
		EISDescriptor addressDescriptor = new EISDescriptor();
		addressDescriptor.setJavaClassName(Address.class.getName());
		addressDescriptor.setDataTypeName("address");
		addressDescriptor.descriptorIsAggregate();
		
		EISDirectMapping street1Mapping = new EISDirectMapping();
		street1Mapping.setAttributeName("street1");
		street1Mapping.setXPath("street[1]/text()");
		addressDescriptor.addMapping(street1Mapping);
		
		EISDirectMapping street2Mapping = new EISDirectMapping();
		street2Mapping.setAttributeName("street2");
		street2Mapping.setXPath("street[2]/text()");
		addressDescriptor.addMapping(street2Mapping);
		
		EISDirectMapping cityMapping = new EISDirectMapping();
		cityMapping.setAttributeName("city");
		cityMapping.setXPath("city/text()");
		addressDescriptor.addMapping(cityMapping);
		
		EISDirectMapping provinceMapping = new EISDirectMapping();
		provinceMapping.setAttributeName("province");
		provinceMapping.setXPath("province/text()");
		addressDescriptor.addMapping(provinceMapping);
		
		EISDirectMapping postalCodeMapping = new EISDirectMapping();
		postalCodeMapping.setAttributeName("postalCode");
		postalCodeMapping.setXPath("postal-code/text()");
		addressDescriptor.addMapping(postalCodeMapping);
		
		return addressDescriptor;
	}
	
	private EISDescriptor buildDependentDescriptor() {
		EISDescriptor dependentDescriptor = new EISDescriptor();
		dependentDescriptor.setJavaClassName(Dependent.class.getName());
		dependentDescriptor.setDataTypeName("dependent");
		dependentDescriptor.descriptorIsAggregate();
		
		EISDirectMapping firstNameMapping = new EISDirectMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setXPath("@first-name");
		dependentDescriptor.addMapping(firstNameMapping);
		
		EISDirectMapping lastNameMapping = new EISDirectMapping();
		lastNameMapping.setAttributeName("lastName");
		lastNameMapping.setXPath("@last-name");
		dependentDescriptor.addMapping(lastNameMapping);
		
		EISCompositeObjectMapping addressMapping = new EISCompositeObjectMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setXPath("address");
		addressMapping.setReferenceClassName(Address.class.getName());
		dependentDescriptor.addMapping(addressMapping);
		
		EISCompositeObjectMapping phoneNumberMapping = new EISCompositeObjectMapping();
		phoneNumberMapping.setAttributeName("phoneNumber");
		phoneNumberMapping.setXPath("phone-no");
		phoneNumberMapping.setReferenceClassName(PhoneNumber.class.getName());
		dependentDescriptor.addMapping(phoneNumberMapping);
		
		return dependentDescriptor;
	}
	
	private EISDescriptor buildEmployeeDescriptor() {
		EISDescriptor employeeDescriptor = new EISDescriptor();
		employeeDescriptor.setJavaClassName(Employee.class.getName());
		employeeDescriptor.setDataTypeName("employee");
		
		employeeDescriptor.getInterfacePolicy().addParentInterfaceName("org.eclipse.persistence.tools.workbench.test.models.contact.Person");

		employeeDescriptor.setShouldAlwaysConformResultsInUnitOfWork(true);
		employeeDescriptor.setShouldAlwaysRefreshCache(true);
		employeeDescriptor.setShouldDisableCacheHits(true);
		employeeDescriptor.setIsIsolated(false);
		employeeDescriptor.useSoftCacheWeakIdentityMap();
		employeeDescriptor.setIdentityMapSize(100);
				
		XMLInteraction readObjectCall = new XMLInteraction();
		readObjectCall.setFunctionName("readEmployeeById");
		readObjectCall.addArgument("id", "@id");
		employeeDescriptor.getDescriptorQueryManager().setReadObjectCall(readObjectCall);
		
		employeeDescriptor.addPrimaryKeyField(new XMLField("@id"));
		
		EISDirectMapping idMapping = new EISDirectMapping();
		idMapping.setAttributeName("id");
		idMapping.setXPath("@id");
		employeeDescriptor.addMapping(idMapping);
		
		EISDirectMapping firstNameMapping = new EISDirectMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setXPath("personal-information/@first-name");
		employeeDescriptor.addMapping(firstNameMapping);
		
		EISDirectMapping lastNameMapping = new EISDirectMapping();
		lastNameMapping.setAttributeName("lastName");
		lastNameMapping.setXPath("personal-information/@last-name");
		employeeDescriptor.addMapping(lastNameMapping);
		
		EISDirectMapping genderMapping = new EISDirectMapping();
		genderMapping.setAttributeName("gender");
		genderMapping.setXPath("personal-information/@gender");
		ObjectTypeConverter genderConverter = new ObjectTypeConverter(genderMapping);
		genderConverter.addConversionValue("F", "female");
		genderConverter.addConversionValue("M", "male");
		genderConverter.setDefaultAttributeValue("male");
		genderMapping.setConverter(genderConverter);
		employeeDescriptor.addMapping(genderMapping);
		
		EISTransformationMapping normalHoursMapping = new EISTransformationMapping();
		normalHoursMapping.setAttributeName("normalHours");
		normalHoursMapping.setAttributeTransformerClassName(NormalHoursTransformer.class.getName());
		//TODO proxy indirection, when runtime supports it
//        normalHoursMapping.setIndirectionPolicy(new ProxyIndirectionPolicy());
		XMLField startTimeField = new XMLField("working-hours/start-time/text()");
		startTimeField.setSchemaType(new QName(XMLConstants.SCHEMA_URL, XMLConstants.DATE_TIME));
		normalHoursMapping.addFieldTransformerClassName(startTimeField, NormalHoursTransformer.class.getName());
		XMLField endTimeField = new XMLField("working-hours/end-time/text()");
		endTimeField.setSchemaType(new QName(XMLConstants.SCHEMA_URL, XMLConstants.DATE_TIME));
		normalHoursMapping.addFieldTransformerClassName(endTimeField, NormalHoursTransformer.class.getName());
		employeeDescriptor.addMapping(normalHoursMapping);
		
		EISCompositeObjectMapping addressMapping = new EISCompositeObjectMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setXPath("contact-information/address");
		addressMapping.setReferenceClassName(Address.class.getName());
		employeeDescriptor.addMapping(addressMapping);
		
		EISCompositeCollectionMapping phoneNumbersMapping = new EISCompositeCollectionMapping();
		phoneNumbersMapping.setAttributeName("phoneNumbers");
		phoneNumbersMapping.setXPath("contact-information/phone");
		phoneNumbersMapping.setReferenceClassName(PhoneNumber.class.getName());
		MapContainerPolicy containerPolicy = new MapContainerPolicy(HashMap.class.getName());
		containerPolicy.setKeyName("getType");
		phoneNumbersMapping.setContainerPolicy(containerPolicy);
		employeeDescriptor.addMapping(phoneNumbersMapping);
		
		EISCompositeCollectionMapping dependentsMapping = new EISCompositeCollectionMapping();
		dependentsMapping.setAttributeName("dependents");
		dependentsMapping.setXPath("dependent-information/dependent");
		dependentsMapping.setReferenceClassName(Dependent.class.getName());
        dependentsMapping.useCollectionClassName("java.util.Vector");
		employeeDescriptor.addMapping(dependentsMapping);
		
		EISCompositeDirectCollectionMapping responsibilitiesMapping = new EISCompositeDirectCollectionMapping();
		responsibilitiesMapping.setAttributeName("responsibilities");
		responsibilitiesMapping.setXPath("responsibility/text()");
        responsibilitiesMapping.setContainerPolicy(new CollectionContainerPolicy("java.util.Vector"));
		employeeDescriptor.addMapping(responsibilitiesMapping);
		
		EISOneToOneMapping managerMapping = new EISOneToOneMapping();
		managerMapping.setAttributeName("manager");
		managerMapping.setReferenceClassName(Employee.class.getName());
		//TODO proxy indirection, when runtime supports it
//        managerMapping.setIndirectionPolicy(new ProxyIndirectionPolicy());
		managerMapping.addForeignKeyField(new XMLField("manager/@first-name"), new XMLField("personal-information/@first-name"));
		managerMapping.addForeignKeyField(new XMLField("manager/@last-name"), new XMLField("personal-information/@last-name"));
		managerMapping.setUsesIndirection(true);
		XMLInteraction selectManagerCall = new XMLInteraction();
		selectManagerCall.setFunctionName("selectManager");
		managerMapping.setSelectionCall(selectManagerCall);
		employeeDescriptor.addMapping(managerMapping);
		
		EISOneToManyMapping projectsMapping = new EISOneToManyMapping();
		projectsMapping.setAttributeName("projects");
		projectsMapping.setReferenceClassName(Project.class.getName());
		projectsMapping.addForeignKeyField(new XMLField("projects/project-id/text()"), new XMLField("id/text()"));
        projectsMapping.useListClassName(ClassConstants.IndirectList_Class.getName());
		projectsMapping.setIndirectionPolicy(new TransparentIndirectionPolicy());
		XMLInteraction selectProjectsCall = new XMLInteraction();
		selectProjectsCall.setFunctionName("selectProjects");
		projectsMapping.setSelectionCall(selectProjectsCall);
		XMLInteraction deleteAllCall = new XMLInteraction();
		deleteAllCall.setFunctionName("deleteAllProjects");
		projectsMapping.setDeleteAllCall(deleteAllCall);
		projectsMapping.setIsPrivateOwned(true);
		employeeDescriptor.addMapping(projectsMapping);
		
		return employeeDescriptor;
	}
	
	private EISDescriptor buildPhoneNumberDescriptor() {
		EISDescriptor phoneNumberDescriptor = new EISDescriptor();
		phoneNumberDescriptor.setJavaClassName(PhoneNumber.class.getName());
		phoneNumberDescriptor.setDataTypeName("phone-type");
		phoneNumberDescriptor.descriptorIsAggregate();
		
		EISDirectMapping typeMapping = new EISDirectMapping();
		typeMapping.setAttributeName("type");
		typeMapping.setXPath("@type");
		phoneNumberDescriptor.addMapping(typeMapping);
		
		EISDirectMapping areaCodeMapping = new EISDirectMapping();
		areaCodeMapping.setAttributeName("areaCode");
		XMLField areaCodeField = new XMLField("area-code/text()");
		areaCodeField.setIsTypedTextField(true);
		areaCodeMapping.setField(areaCodeField);
		phoneNumberDescriptor.addMapping(areaCodeMapping);
		
		EISDirectMapping numberMapping = new EISDirectMapping();
		numberMapping.setAttributeName("number");
		XMLField numberField = new XMLField("number/text()");
		numberField.setIsTypedTextField(true);
		numberMapping.setField(numberField);
		phoneNumberDescriptor.addMapping(numberMapping);
		
		return phoneNumberDescriptor;
	}
	
	private EISDescriptor buildProjectDescriptor() {
		EISDescriptor projectDescriptor = new EISDescriptor();
		projectDescriptor.setJavaClassName(Project.class.getName());
		projectDescriptor.setDataTypeName("project");
		
		projectDescriptor.setShouldAlwaysConformResultsInUnitOfWork(true);
		projectDescriptor.setShouldAlwaysRefreshCache(true);
		projectDescriptor.setShouldDisableCacheHits(true);
		projectDescriptor.setIsIsolated(false);
		projectDescriptor.useSoftCacheWeakIdentityMap();
		projectDescriptor.setIdentityMapSize(100);
		
		projectDescriptor.getDescriptorInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
		projectDescriptor.getDescriptorInheritancePolicy().addClassNameIndicator(Project.class.getName(), "project");
		projectDescriptor.getDescriptorInheritancePolicy().addClassNameIndicator(LargeProject.class.getName(), "large-project");
		
		//force initialization
		projectDescriptor.getQueryManager();
		
		projectDescriptor.addPrimaryKeyField(new XMLField("id/text()"));
		
		EISDirectMapping idMapping = new EISDirectMapping();
		idMapping.setAttributeName("id");
		idMapping.setXPath("id/text()");
		projectDescriptor.addMapping(idMapping);
		
		EISDirectMapping nameMapping = new EISDirectMapping();
		nameMapping.setAttributeName("name");
		nameMapping.setXPath("name/text()");
		projectDescriptor.addMapping(nameMapping);
		
		EISDirectMapping descriptionMapping = new EISDirectMapping();
		descriptionMapping.setAttributeName("description");
		descriptionMapping.setXPath("description/text()");
		projectDescriptor.addMapping(descriptionMapping);
		
		EISDirectMapping versionMapping = new EISDirectMapping();
		versionMapping.setAttributeName("version");
		versionMapping.setXPath("version/text()");
		projectDescriptor.addMapping(versionMapping);
		
		EISDirectMapping endDateMapping = new EISDirectMapping();
		endDateMapping.setAttributeName("endDate");
		XMLField endDateField = new XMLField("end-date/text()");
		endDateField.setSchemaType(new QName(XMLConstants.SCHEMA_URL, XMLConstants.DATE));
		endDateMapping.setField(endDateField);
		projectDescriptor.addMapping(endDateMapping);
		
		EISOneToOneMapping teamLeaderMapping = new EISOneToOneMapping();
		teamLeaderMapping.setAttributeName("teamLeader");
		teamLeaderMapping.setReferenceClassName(Employee.class.getName());
		teamLeaderMapping.addForeignKeyField(new XMLField("team-leader/@employee-id"), new XMLField("@id"));
		teamLeaderMapping.setUsesIndirection(true);
		projectDescriptor.addMapping(teamLeaderMapping);
		
		EISOneToManyMapping teamMembersMapping = new EISOneToManyMapping();
		teamMembersMapping.setAttributeName("teamMembers");
		teamMembersMapping.setReferenceClassName(Employee.class.getName());
		teamMembersMapping.setForeignKeyGroupingElement("team-members");
		teamMembersMapping.addForeignKeyField(new XMLField("employee-id/text()"), new XMLField("@id"));
		teamMembersMapping.useListClassName(ClassConstants.IndirectList_Class.getName());
		teamMembersMapping.setIndirectionPolicy(new TransparentIndirectionPolicy());
		XMLInteraction selectTeamMembersCall = new XMLInteraction();
		selectTeamMembersCall.setFunctionName("selectTeamMembers");
		teamMembersMapping.setSelectionCall(selectTeamMembersCall);
		projectDescriptor.addMapping(teamMembersMapping);
		
		return projectDescriptor;
	}
	
	private EISDescriptor buildLargeProjectDescriptor() {
		EISDescriptor projectDescriptor = new EISDescriptor();
		projectDescriptor.setJavaClassName(LargeProject.class.getName());
		projectDescriptor.setDataTypeName("project");

		projectDescriptor.setShouldAlwaysConformResultsInUnitOfWork(true);
		projectDescriptor.setShouldAlwaysRefreshCache(true);
		projectDescriptor.setShouldDisableCacheHits(true);
		projectDescriptor.setIsIsolated(false);
		
		projectDescriptor.getDescriptorInheritancePolicy().setParentClassName(Project.class.getName());

		//force initialization
		projectDescriptor.getQueryManager();
		
		EISDirectMapping budgetMapping = new EISDirectMapping();
		budgetMapping.setAttributeName("budget");
		XMLUnionField budgetField = new XMLUnionField("budget/text()");
		budgetField.addSchemaType(new QName(XMLConstants.SCHEMA_URL, XMLConstants.FLOAT));
		budgetField.addSchemaType(new QName(XMLConstants.SCHEMA_URL, XMLConstants.INTEGER));
		budgetMapping.setField(budgetField);
		projectDescriptor.addMapping(budgetMapping);
		
		EISDirectMapping milestoneMapping = new EISDirectMapping();
		milestoneMapping.setAttributeName("milestoneVersion");
		XMLField milestoneField = new XMLField("milestone/text()");
		milestoneField.setSchemaType(new QName(XMLConstants.SCHEMA_URL, XMLConstants.DATE));
		milestoneMapping.setField(milestoneField);
		projectDescriptor.addMapping(milestoneMapping);

		return projectDescriptor;
	}
	
	
	public org.eclipse.persistence.sessions.Project getRuntimeProject() {
		return this.runtimeProject;
	}
}

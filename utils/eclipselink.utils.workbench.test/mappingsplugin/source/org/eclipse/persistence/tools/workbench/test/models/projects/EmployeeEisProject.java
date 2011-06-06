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

import org.eclipse.persistence.tools.workbench.test.models.eis.employee.NormalHoursTransformer;

import org.eclipse.persistence.eis.EISConnectionSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInterfaceAliasPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWCompositeEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlPrimaryKeyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter.ConversionValueException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisInteraction;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;


public class EmployeeEisProject 
	extends XmlTestProject 
{
	// **************** static methods ****************************************
	
	public static void main(String[] arg) {
		new EmployeeEisProject();
	}
	
	
	// **************** Constructors ******************************************
	
	public EmployeeEisProject() {
		super();
	}
	
	
	// **************** Initialization ****************************************
	
	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	public static MWEisProject emptyProject() {
		MWEisProject project = new MWEisProject("Employee-EIS", MWEisLoginSpec.JMS_ADAPTER_NAME, spiManager());
		project.getEisLoginSpec().setConnectionFactoryURL("www.imguessingatthis.com");
		project.getEisLoginSpec().setConnectionSpecClass(project.typeFor(EISConnectionSpec.class));
		return project;
	} 
	
	@Override
	protected void initializeSchemas() {
		super.initializeSchemas();
		this.addSchema("eis-employee.xsd", "/schema/eis-employee.xsd");
		this.addSchema("eis-project.xsd", "/schema/eis-project.xsd");
	}
	
	@Override
	protected void initializeDescriptors() {
		super.initializeDescriptors();

		//this is not exactly how it's done in the MultipleClassChooserDialog
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.eis.employee.Address");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.eis.employee.Dependent");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.eis.employee.PhoneNumber");

		this.addRootEisDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.eis.employee.Employee");
		this.addRootEisDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.eis.employee.Project");
		this.addRootEisDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.eis.employee.LargeProject");
		
		initializeAddressDescriptor();
		initializeDependentDescriptor();	
		initializePhoneNumberDescriptor();
		initializeEmployeeDescriptor();
		initializeProjectDescriptor();
		initializeLargeProjectDescriptor();
		initializeReferenceMappings();
        
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.AFTER_LOAD_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.COPY_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.INSTANTIATION_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.INHERITANCE_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWTransactionalProjectDefaultsPolicy.EVENTS_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWTransactionalProjectDefaultsPolicy.RETURNING_POLICY);
	}

	public void initializeLargeProjectDescriptor() {	
		MWRootEisDescriptor largeProjectDescriptor = getLargeProjectDescriptor();	
		MWClass largeProjectClass = largeProjectDescriptor.getMWClass();
		MWComplexTypeDefinition largeProjectDef = this.getProjectSchema().complexType("large-project-type");
		MWElementDeclaration projectElement = this.getProjectSchema().element("project");
		
		largeProjectDescriptor.setSchemaContext(largeProjectDef);
		largeProjectDescriptor.setDefaultRootElement(projectElement);
								
		largeProjectDescriptor.addInheritancePolicy();
		MWEisDescriptorInheritancePolicy inheritancePolicy = (MWEisDescriptorInheritancePolicy)largeProjectDescriptor.getInheritancePolicy();
		inheritancePolicy.setParentDescriptor(getProjectDescriptor());			
		
		MWClassIndicatorFieldPolicy classIndicatorPolicy = 
			(MWClassIndicatorFieldPolicy) this.getProjectDescriptor().getInheritancePolicy().getClassIndicatorPolicy();
		MWClassIndicatorValue classIndicatorValue = classIndicatorPolicy.getClassIndicatorValueForDescriptor(largeProjectDescriptor);
		classIndicatorValue.setInclude(true);
		classIndicatorValue.setIndicatorValue("large-project");
		
		MWEisTransactionalPolicy transactionalPolicy = (MWEisTransactionalPolicy) largeProjectDescriptor.getTransactionalPolicy();
		transactionalPolicy.setConformResultsInUnitOfWork(true);
		transactionalPolicy.getRefreshCachePolicy().setDisableCacheHits(true);
		transactionalPolicy.getRefreshCachePolicy().setAlwaysRefreshCache(true);
			
		MWXmlDirectMapping budgetMapping = (MWXmlDirectMapping)largeProjectDescriptor.addDirectMapping(largeProjectClass.attributeNamed("budget"));
		budgetMapping.getXmlField().setXpath("budget/text()");

		MWXmlDirectMapping milestoneVersionMapping = (MWXmlDirectMapping)largeProjectDescriptor.addDirectMapping(largeProjectClass.attributeNamed("milestoneVersion"));
		milestoneVersionMapping.getXmlField().setXpath("milestone/text()");
		
	}
		
	public void initializeAddressDescriptor() {		
		MWCompositeEisDescriptor addressDescriptor = this.getAddressDescriptor();
		MWClass addressClass = addressDescriptor.getMWClass();
		MWElementDeclaration addressElement = this.getEmployeeSchema().element("address");
		
		addressDescriptor.setSchemaContext(addressElement);
		
		MWXmlDirectMapping street1Mapping = 
			(MWXmlDirectMapping) addressDescriptor.addDirectMapping(addressClass.attributeNamed("street1"));
		street1Mapping.getXmlField().setXpath("street[1]/text()");
		
		MWXmlDirectMapping street2Mapping = 
			(MWXmlDirectMapping) addressDescriptor.addDirectMapping(addressClass.attributeNamed("street2"));
		street2Mapping.getXmlField().setXpath("street[2]/text()");
		
		MWXmlDirectMapping cityMapping = 
			(MWXmlDirectMapping) addressDescriptor.addDirectMapping(addressClass.attributeNamed("city"));
		cityMapping.getXmlField().setXpath("city/text()");
		
		MWXmlDirectMapping provinceMapping = 
			(MWXmlDirectMapping) addressDescriptor.addDirectMapping(addressClass.attributeNamed("province"));
		provinceMapping.getXmlField().setXpath("province/text()");

		MWXmlDirectMapping postalCodeMapping = 
			(MWXmlDirectMapping) addressDescriptor.addDirectMapping(addressClass.attributeNamed("postalCode"));
		postalCodeMapping.getXmlField().setXpath("postal-code/text()");
	}
	
	public void initializeDependentDescriptor() {		
		MWCompositeEisDescriptor dependentDescriptor = getDependentDescriptor();
		MWClass dependentClass = dependentDescriptor.getMWClass();
		MWElementDeclaration dependentElement = 
			this.getEmployeeSchema().element("employee").nestedElement("", "dependent-information").nestedElement("", "dependent");
		
		dependentDescriptor.setSchemaContext(dependentElement);
		
		MWXmlDirectMapping firstNameMapping = 
			(MWXmlDirectMapping) dependentDescriptor.addDirectMapping(dependentClass.attributeNamed("firstName"));
		firstNameMapping.getXmlField().setXpath("@first-name");
		
		MWXmlDirectMapping lastNameMapping = 
			(MWXmlDirectMapping) dependentDescriptor.addDirectMapping(dependentClass.attributeNamed("lastName"));
		lastNameMapping.getXmlField().setXpath("@last-name");
		
		MWCompositeObjectMapping addressMapping = 
			dependentDescriptor.addCompositeObjectMapping(dependentClass.attributeNamed("address"));
		addressMapping.getXmlField().setXpath("address");
		addressMapping.setReferenceDescriptor(this.getAddressDescriptor());
		
		MWCompositeObjectMapping phoneNumberMapping = 
			dependentDescriptor.addCompositeObjectMapping(dependentClass.attributeNamed("phoneNumber"));
		phoneNumberMapping.getXmlField().setXpath("phone-no");
		phoneNumberMapping.setReferenceDescriptor(this.getPhoneNumberDescriptor());
	}

	public void initializeEmployeeDescriptor() {
		MWRootEisDescriptor employeeDescriptor = getEmployeeDescriptor();
		
		employeeDescriptor.addInterfaceAliasPolicy();
		MWClass interfaceAlias = typeNamed("org.eclipse.persistence.tools.workbench.test.models.contact.Person");
		((MWDescriptorInterfaceAliasPolicy) employeeDescriptor.getInterfaceAliasPolicy()).setInterfaceAlias(interfaceAlias);

		MWClass employeeClass = employeeDescriptor.getMWClass();
		MWElementDeclaration employeeElement = this.getEmployeeSchema().element("employee");
		
		employeeDescriptor.setSchemaContext(employeeElement);
		
		MWEisTransactionalPolicy transactionalPolicy = (MWEisTransactionalPolicy) employeeDescriptor.getTransactionalPolicy();
		transactionalPolicy.setConformResultsInUnitOfWork(true);
		transactionalPolicy.getRefreshCachePolicy().setDisableCacheHits(true);
		transactionalPolicy.getRefreshCachePolicy().setAlwaysRefreshCache(true);
		
		MWXmlPrimaryKeyPolicy primaryKeyPolicy = transactionalPolicy.getPrimaryKeyPolicy();
		primaryKeyPolicy.addPrimaryKey("@id");
		
		MWEisQueryManager queryManager = (MWEisQueryManager) employeeDescriptor.getQueryManager();
		MWEisInteraction readObjectInteraction = queryManager.getReadObjectInteraction();
		readObjectInteraction.setFunctionName("readEmployeeById");
		readObjectInteraction.addInputArgument("id", "@id");
		
		MWXmlDirectMapping idMapping = 
			(MWXmlDirectMapping) employeeDescriptor.addDirectMapping(employeeClass.attributeNamed("id"));
		idMapping.getXmlField().setXpath("@id");
		
		MWXmlDirectMapping firstNameMapping = 
			(MWXmlDirectMapping) employeeDescriptor.addDirectMapping(employeeClass.attributeNamed("firstName"));
		firstNameMapping.getXmlField().setXpath("personal-information/@first-name");
		
		MWXmlDirectMapping lastNameMapping = 
			(MWXmlDirectMapping) employeeDescriptor.addDirectMapping(employeeClass.attributeNamed("lastName"));
		lastNameMapping.getXmlField().setXpath("personal-information/@last-name");
		
		MWXmlDirectMapping genderMapping = 
			(MWXmlDirectMapping) employeeDescriptor.addDirectMapping(employeeClass.attributeNamed("gender"));
		genderMapping.getXmlField().setXpath("personal-information/@gender");
		MWObjectTypeConverter genderConverter = genderMapping.setObjectTypeConverter();
		genderConverter.setAttributeType(new MWTypeDeclaration(genderConverter, genderMapping.typeNamed("java.lang.String")));
		try {
			genderConverter.addValuePair("F", "female");
			genderConverter.addValuePair("M", "male");
		}
		catch (ConversionValueException cve) {/* shouldn't happen here */}
		genderConverter.setDefaultAttributeValue("male");
		
		MWXmlTransformationMapping normalHoursMapping = 
			(MWXmlTransformationMapping) employeeDescriptor.addTransformationMapping(employeeClass.attributeNamed("normalHours"));
		normalHoursMapping.setAttributeTransformer(normalHoursMapping.typeFor(NormalHoursTransformer.class));
		normalHoursMapping.addFieldTransformerAssociation("working-hours/start-time/text()", normalHoursMapping.typeFor(NormalHoursTransformer.class));
		normalHoursMapping.addFieldTransformerAssociation("working-hours/end-time/text()", normalHoursMapping.typeFor(NormalHoursTransformer.class));
		//TODO proxy indirection, when runtime supports it
//        normalHoursMapping.setUseProxyIndirection();

        
		MWXmlDirectCollectionMapping responsibilitiesMapping = 
			(MWXmlDirectCollectionMapping) employeeDescriptor.addDirectCollectionMapping(employeeClass.attributeNamed("responsibilities"));
		responsibilitiesMapping.getXmlField().setXpath("responsibility/text()");
		
		MWCompositeObjectMapping addressMapping = 
			employeeDescriptor.addCompositeObjectMapping(employeeClass.attributeNamed("address"));
		addressMapping.getXmlField().setXpath("contact-information/address");
		addressMapping.setReferenceDescriptor(this.getAddressDescriptor());
		
		MWCompositeCollectionMapping phoneNumbersMapping =
			employeeDescriptor.addCompositeCollectionMapping(employeeClass.attributeNamed("phoneNumbers"));
		phoneNumbersMapping.setReferenceDescriptor(this.getPhoneNumberDescriptor());
		phoneNumbersMapping.getXmlField().setXpath("contact-information/phone");
		((MWMapContainerPolicy) phoneNumbersMapping.getContainerPolicy()).setKeyMethod(this.getPhoneNumberDescriptor().getMWClass().methodWithSignature("getType()"));
		
		MWCompositeCollectionMapping dependentsMapping =
			employeeDescriptor.addCompositeCollectionMapping(employeeClass.attributeNamed("dependents"));
		dependentsMapping.setReferenceDescriptor(this.getDependentDescriptor());
		dependentsMapping.getXmlField().setXpath("dependent-information/dependent");
		
		MWEisOneToOneMapping managerMapping =
			employeeDescriptor.addEisOneToOneMapping(employeeClass.attributeNamed("manager"));
		managerMapping.setReferenceDescriptor(this.getEmployeeDescriptor());
		managerMapping.addFieldPair("manager/@first-name", "personal-information/@first-name");
		managerMapping.addFieldPair("manager/@last-name", "personal-information/@last-name");
		managerMapping.setUseValueHolderIndirection();
		managerMapping.setUseDescriptorReadObjectInteraction(false);
		managerMapping.getSelectionInteraction().setFunctionName("selectManager");
		//TODO proxy indirection, when runtime supports it
//        managerMapping.setUseProxyIndirection();
	}
	
	public void initializePhoneNumberDescriptor() {
		MWCompositeEisDescriptor phoneNumberDescriptor = getPhoneNumberDescriptor();
		MWClass phoneNumberClass = phoneNumberDescriptor.getMWClass();
		MWComplexTypeDefinition phoneNumberType = this.getEmployeeSchema().complexType("phone-type");
		
		phoneNumberDescriptor.setSchemaContext(phoneNumberType);
		
		MWXmlDirectMapping typeMapping = 
			(MWXmlDirectMapping) phoneNumberDescriptor.addDirectMapping(phoneNumberClass.attributeNamed("type"));
		typeMapping.getXmlField().setXpath("@type");
		
		MWXmlDirectMapping areaCodeMapping = 
			(MWXmlDirectMapping) phoneNumberDescriptor.addDirectMapping(phoneNumberClass.attributeNamed("areaCode"));
		areaCodeMapping.getXmlField().setXpath("area-code/text()");
		areaCodeMapping.getXmlField().setTyped(true);
		
		MWXmlDirectMapping numberMapping = 
			(MWXmlDirectMapping) phoneNumberDescriptor.addDirectMapping(phoneNumberClass.attributeNamed("number"));
		numberMapping.getXmlField().setXpath("number/text()");
		numberMapping.getXmlField().setTyped(true);
	}
	
	public void initializeProjectDescriptor() {
		MWRootEisDescriptor projectDescriptor = this.getProjectDescriptor();
		MWClass projectClass = projectDescriptor.getMWClass();
		MWComplexTypeDefinition projectComplexType = this.getProjectSchema().complexType("project-type");
		MWElementDeclaration projectElement = this.getProjectSchema().element("project");
		
		projectDescriptor.addInheritancePolicy();
		MWEisDescriptorInheritancePolicy inheritancePolicy = (MWEisDescriptorInheritancePolicy) projectDescriptor.getInheritancePolicy();
		inheritancePolicy.setIsRoot(true);
	
		MWXmlClassIndicatorFieldPolicy classIndicatorPolicy = (MWXmlClassIndicatorFieldPolicy) inheritancePolicy.getClassIndicatorPolicy();
		classIndicatorPolicy.setUseXSIType(true);
		MWClassIndicatorValue classIndicatorValue = classIndicatorPolicy.getClassIndicatorValueForDescriptor(projectDescriptor);
		classIndicatorValue.setInclude(true);
		classIndicatorValue.setIndicatorValue("project");
		
		projectDescriptor.setSchemaContext(projectComplexType);
		projectDescriptor.setDefaultRootElement(projectElement);
				
		MWEisTransactionalPolicy transactionalPolicy = (MWEisTransactionalPolicy) projectDescriptor.getTransactionalPolicy();
		transactionalPolicy.setConformResultsInUnitOfWork(true);
		transactionalPolicy.getRefreshCachePolicy().setDisableCacheHits(true);
		transactionalPolicy.getRefreshCachePolicy().setAlwaysRefreshCache(true);
		
		MWXmlPrimaryKeyPolicy primaryKeyPolicy = transactionalPolicy.getPrimaryKeyPolicy();
			primaryKeyPolicy.addPrimaryKey("id/text()");
		
		MWXmlDirectMapping idMapping = 
			(MWXmlDirectMapping) projectDescriptor.addDirectMapping(projectClass.attributeNamed("id"));
		idMapping.getXmlField().setXpath("id/text()");
		
		MWXmlDirectMapping nameMapping = 
			(MWXmlDirectMapping) projectDescriptor.addDirectMapping(projectClass.attributeNamed("name"));
		nameMapping.getXmlField().setXpath("name/text()");
		
		MWXmlDirectMapping descriptionMapping = 
			(MWXmlDirectMapping) projectDescriptor.addDirectMapping(projectClass.attributeNamed("description"));
		descriptionMapping.getXmlField().setXpath("description/text()");
		
		MWXmlDirectMapping versionMapping = 
			(MWXmlDirectMapping) projectDescriptor.addDirectMapping(projectClass.attributeNamed("version"));
		versionMapping.getXmlField().setXpath("version/text()");
		
		MWXmlDirectMapping endDateMapping = 
			(MWXmlDirectMapping) projectDescriptor.addDirectMapping(projectClass.attributeNamed("endDate"));
		endDateMapping.getXmlField().setXpath("end-date/text()");
	}
	
	/** These must be done after descriptors are generally set up */
	public void initializeReferenceMappings() {
		MWRootEisDescriptor employeeDescriptor = getEmployeeDescriptor();
		MWClass employeeClass = employeeDescriptor.getMWClass();
		MWRootEisDescriptor projectDescriptor = this.getProjectDescriptor();
		MWClass projectClass = projectDescriptor.getMWClass();
		
		MWEisOneToManyMapping projectsMapping =
			employeeDescriptor.addEisOneToManyMapping(employeeClass.attributeNamed("projects"));
		projectsMapping.setReferenceDescriptor(this.getProjectDescriptor());
		projectsMapping.addFieldPair("projects/project-id/text()", "id/text()");
		projectsMapping.setUseTransparentIndirection();
		projectsMapping.getSelectionInteraction().setFunctionName("selectProjects");
		projectsMapping.getDeleteAllInteraction().setFunctionName("deleteAllProjects");
		projectsMapping.setPrivateOwned(true);
		
		MWEisOneToOneMapping teamLeaderMapping =
			projectDescriptor.addEisOneToOneMapping(projectClass.attributeNamed("teamLeader"));
		teamLeaderMapping.setReferenceDescriptor(this.getEmployeeDescriptor());
		teamLeaderMapping.addFieldPair("team-leader/@employee-id", "@id");
		teamLeaderMapping.setUseValueHolderIndirection();
		teamLeaderMapping.setUseDescriptorReadObjectInteraction(true);
		
		MWEisOneToManyMapping teamMembersMapping =
			projectDescriptor.addEisOneToManyMapping(projectClass.attributeNamed("teamMembers"));
		teamMembersMapping.setReferenceDescriptor(this.getEmployeeDescriptor());
		teamMembersMapping.setForeignKeysOnSource();
		teamMembersMapping.getForeignKeyGroupingElement().setXpath("team-members");
		teamMembersMapping.addFieldPair("team-members/employee-id/text()", "@id");
		teamMembersMapping.setUseTransparentIndirection();
		teamMembersMapping.getSelectionInteraction().setFunctionName("selectTeamMembers");
	}
	
	public MWCompositeEisDescriptor getAddressDescriptor() {
		return (MWCompositeEisDescriptor) xmlDescriptorWithShortName("Address");
	}

	public MWRootEisDescriptor getLargeProjectDescriptor() {
		return (MWRootEisDescriptor) xmlDescriptorWithShortName("LargeProject");
	}
	
	public MWCompositeEisDescriptor getDependentDescriptor() {
		return (MWCompositeEisDescriptor) xmlDescriptorWithShortName("Dependent");
	}
	
	public MWRootEisDescriptor getEmployeeDescriptor() {
		return (MWRootEisDescriptor) xmlDescriptorWithShortName("Employee");
	}
	
	public MWCompositeEisDescriptor getPhoneNumberDescriptor() {
		return (MWCompositeEisDescriptor) xmlDescriptorWithShortName("PhoneNumber");
	}
	
	public MWRootEisDescriptor getProjectDescriptor() {
		return (MWRootEisDescriptor) xmlDescriptorWithShortName("Project");
	}
	
	public MWXmlSchema getEmployeeSchema() {
		return this.getProject().getSchemaRepository().getSchema("eis-employee.xsd");
	}
	
	public MWXmlSchema getProjectSchema() {
		return this.getProject().getSchemaRepository().getSchema("eis-project.xsd");
	}
}

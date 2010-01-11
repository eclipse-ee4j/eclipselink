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

import java.util.ArrayList;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.models.xml.employee.NormalHoursTransformer;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter.ConversionValueException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;


public class LegacyEmployeeOXProject 
	extends XmlTestProject 
{
	// **************** static methods ****************************************
	
	public static void main(String[] arg) {
		new LegacyEmployeeOXProject();
	}
	
	
	// **************** Constructors ******************************************
	
	public LegacyEmployeeOXProject() {
		super();
	}
	
	
	// **************** Initialization ****************************************
	
	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}

	public static MWOXProject emptyProject() {
		return new MWOXProject("Employee-OX", MappingsModelTestTools.buildSPIManager());
	} 
	
	@Override
	public void initializeSchemas() {
		super.initializeSchemas();
		this.addSchema("employee.xsd", "/schema/employee.xsd");
	}
	
	@Override
	public void initializeDescriptors() {
		super.initializeDescriptors();

		//this is not exactly how it's done in the MultipleClassChooserDialog
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.xml.employee.Address");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.xml.employee.Employee");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.xml.employee.PhoneNumber");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.xml.employee.Dependent");
		
		initializeAddressDescriptor();
		initializePhoneNumberDescriptor();
		initializeEmployeeDescriptor();
		initializeDependentDescriptor();	

        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.AFTER_LOAD_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.COPY_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.INSTANTIATION_POLICY);
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWProjectDefaultsPolicy.INHERITANCE_POLICY);
    }
	
	public void initializeAddressDescriptor() {		
		MWOXDescriptor addressDescriptor = this.getAddressDescriptor();
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
		MWOXDescriptor dependentDescriptor = this.getDependentDescriptor();
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
		MWOXDescriptor employeeDescriptor = this.getEmployeeDescriptor();
		MWClass employeeClass = employeeDescriptor.getMWClass();
		MWElementDeclaration employeeElement = this.getEmployeeSchema().element("employee");
		
		employeeDescriptor.setSchemaContext(employeeElement);
		
		employeeDescriptor.setRootDescriptor(true);
		// root element should be set already
		
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
//		normalHoursMapping.setUseProxyIndirection();
        
		MWXmlDirectCollectionMapping responsibilitiesMapping = 
			(MWXmlDirectCollectionMapping) employeeDescriptor.addDirectCollectionMapping(employeeClass.attributeNamed("responsibilities"));
		responsibilitiesMapping.getXmlField().setXpath("responsibility/text()");
        responsibilitiesMapping.getContainerPolicy().getDefaultingContainerClass().setContainerClass(responsibilitiesMapping.typeFor(ArrayList.class));
		
		MWCompositeObjectMapping addressMapping = 
			employeeDescriptor.addCompositeObjectMapping(employeeClass.attributeNamed("address"));
		addressMapping.getXmlField().setXpath("contact-information/address");
		addressMapping.setReferenceDescriptor(this.getAddressDescriptor());
		
		MWCompositeCollectionMapping phoneNumbersMapping =
			employeeDescriptor.addCompositeCollectionMapping(employeeClass.attributeNamed("phoneNumbers"));
		phoneNumbersMapping.setReferenceDescriptor(this.getPhoneNumberDescriptor());
		phoneNumbersMapping.getXmlField().setXpath("contact-information/phone");
		phoneNumbersMapping.setMapContainerPolicy().setKeyMethod(this.getPhoneNumberDescriptor().getMWClass().methodWithSignature("getType()"));
		
		MWCompositeCollectionMapping dependentsMapping =
			employeeDescriptor.addCompositeCollectionMapping(employeeClass.attributeNamed("dependents"));
		dependentsMapping.setReferenceDescriptor(this.getDependentDescriptor());
		dependentsMapping.getXmlField().setXpath("dependent-information/dependent");
		dependentsMapping.setCollectionContainerPolicy();
	}
	
	public void initializePhoneNumberDescriptor() {
		MWOXDescriptor phoneNumberDescriptor = this.getPhoneNumberDescriptor();
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
	
	public MWOXDescriptor getAddressDescriptor() {
		return (MWOXDescriptor) xmlDescriptorWithShortName("Address");
	}
	
	public MWOXDescriptor getEmployeeDescriptor() {
		return (MWOXDescriptor) xmlDescriptorWithShortName("Employee");
	}
	
	public MWOXDescriptor getPhoneNumberDescriptor() {
		return (MWOXDescriptor) xmlDescriptorWithShortName("PhoneNumber");
	}
	
	public MWOXDescriptor getDependentDescriptor() {
		return (MWOXDescriptor) xmlDescriptorWithShortName("Dependent");
	}
	
	public MWXmlSchema getEmployeeSchema() {
		return this.getProject().getSchemaRepository().getSchema("employee.xsd");
	}
}

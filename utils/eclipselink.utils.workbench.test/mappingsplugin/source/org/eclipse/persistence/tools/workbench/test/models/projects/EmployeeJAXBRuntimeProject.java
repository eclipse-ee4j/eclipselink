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

import java.net.URISyntaxException;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.eclipse.persistence.tools.workbench.test.models.jaxb.employee.Address;
import org.eclipse.persistence.tools.workbench.test.models.jaxb.employee.Dependent;
import org.eclipse.persistence.tools.workbench.test.models.jaxb.employee.Employee;
import org.eclipse.persistence.tools.workbench.test.models.jaxb.employee.NormalHoursTransformer;
import org.eclipse.persistence.tools.workbench.test.models.jaxb.employee.PhoneNumber;

import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ListContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaFileReference;
import org.eclipse.persistence.platform.xml.XMLSchemaReference;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.AnyAttributeMappingNode;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public class EmployeeJAXBRuntimeProject
{
	
	private Project runtimeProject;
	
	public EmployeeJAXBRuntimeProject() {
		super();
		this.initialize();
	}
	
	private void initialize() {
		this.runtimeProject = new Project();
		this.runtimeProject.setName("Employee-OX");
		this.runtimeProject.setLogin(new XMLLogin());
		this.initializeDescriptors();
	}
	
	private void initializeDescriptors() {
		this.runtimeProject.addDescriptor(this.buildAddressDescriptor());
		this.runtimeProject.addDescriptor(this.buildDependentDescriptor());
		this.runtimeProject.addDescriptor(this.buildEmployeeDescriptor());
		this.runtimeProject.addDescriptor(this.buildPhoneNumberDescriptor());
	}
	
	private String buildSchemaFile() {
		try {
			return FileTools.buildFile(this.getClass().getResource("/schema/employee.xsd")).getPath();
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private XMLDescriptor buildAddressDescriptor() {
		XMLDescriptor addressDescriptor = new XMLDescriptor();
		addressDescriptor.setJavaClassName(Address.class.getName());
		addressDescriptor.setSchemaReference(new XMLSchemaFileReference(buildSchemaFile()));
		addressDescriptor.setNamespaceResolver(new NamespaceResolver());
		((XMLSchemaFileReference) addressDescriptor.getSchemaReference()).setSchemaContext("/address");
		((XMLSchemaFileReference) addressDescriptor.getSchemaReference()).setType(XMLSchemaReference.ELEMENT);
		
		addressDescriptor.descriptorIsAggregate();
		
		XMLDirectMapping street1Mapping = new XMLDirectMapping();
		street1Mapping.setAttributeName("street1");
		street1Mapping.setXPath("street[1]/text()");
		addressDescriptor.addMapping(street1Mapping);
		
		XMLDirectMapping street2Mapping = new XMLDirectMapping();
		street2Mapping.setAttributeName("street2");
		street2Mapping.setXPath("street[2]/text()");
		addressDescriptor.addMapping(street2Mapping);
		
		XMLDirectMapping cityMapping = new XMLDirectMapping();
		cityMapping.setAttributeName("city");
		cityMapping.setXPath("city/text()");
		addressDescriptor.addMapping(cityMapping);
		
		XMLDirectMapping provinceMapping = new XMLDirectMapping();
		provinceMapping.setAttributeName("province");
		provinceMapping.setXPath("province/text()");
		addressDescriptor.addMapping(provinceMapping);
		
		XMLDirectMapping postalCodeMapping = new XMLDirectMapping();
		postalCodeMapping.setAttributeName("postalCode");
		postalCodeMapping.setXPath("postal-code/text()");
		addressDescriptor.addMapping(postalCodeMapping);
		
		return addressDescriptor;
	}
	
	private XMLDescriptor buildDependentDescriptor() {
		XMLDescriptor dependentDescriptor = new XMLDescriptor();
		dependentDescriptor.setJavaClassName(Dependent.class.getName());
		dependentDescriptor.setSchemaReference(new XMLSchemaFileReference(buildSchemaFile()));
		dependentDescriptor.setNamespaceResolver(new NamespaceResolver());
		((XMLSchemaFileReference) dependentDescriptor.getSchemaReference()).setSchemaContext("/employee/dependent-information/dependent");
		((XMLSchemaFileReference) dependentDescriptor.getSchemaReference()).setType(XMLSchemaReference.ELEMENT);
		dependentDescriptor.descriptorIsAggregate();
		
		XMLDirectMapping firstNameMapping = new XMLDirectMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setXPath("@first-name");
		dependentDescriptor.addMapping(firstNameMapping);
		
		XMLDirectMapping lastNameMapping = new XMLDirectMapping();
		lastNameMapping.setAttributeName("lastName");
		lastNameMapping.setXPath("@last-name");
		dependentDescriptor.addMapping(lastNameMapping);
		
		XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setXPath("address");
		addressMapping.setReferenceClassName(Address.class.getName());
		dependentDescriptor.addMapping(addressMapping);
		
		XMLCompositeObjectMapping phoneNumberMapping = new XMLCompositeObjectMapping();
		phoneNumberMapping.setAttributeName("phoneNumber");
		phoneNumberMapping.setXPath("phone-no");
		phoneNumberMapping.setReferenceClassName(PhoneNumber.class.getName());
		dependentDescriptor.addMapping(phoneNumberMapping);
		
		return dependentDescriptor;
	}
	
	private XMLDescriptor buildEmployeeDescriptor() {
		XMLDescriptor employeeDescriptor = new XMLDescriptor();
		employeeDescriptor.setJavaClassName(Employee.class.getName());
		employeeDescriptor.setSchemaReference(new XMLSchemaFileReference(buildSchemaFile()));
		employeeDescriptor.setNamespaceResolver(new NamespaceResolver());
		((XMLSchemaFileReference) employeeDescriptor.getSchemaReference()).setSchemaContext("/employee");
		((XMLSchemaFileReference) employeeDescriptor.getSchemaReference()).setType(XMLSchemaReference.ELEMENT);
		employeeDescriptor.setDefaultRootElement("employee");
		
		XMLFragmentMapping firstNameMapping = new XMLFragmentMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setXPath("personal-information/@first-name");
		employeeDescriptor.addMapping(firstNameMapping);
		
		XMLFragmentCollectionMapping lastNameMapping = new XMLFragmentCollectionMapping();
		lastNameMapping.setAttributeName("lastName");
		lastNameMapping.setXPath("personal-information/@last-name");
		lastNameMapping.getContainerPolicy().getContainerClassName();
		employeeDescriptor.addMapping(lastNameMapping);
		
		XMLDirectMapping genderMapping = new XMLDirectMapping();
		genderMapping.setAttributeName("gender");
		genderMapping.setXPath("personal-information/@gender");
		ObjectTypeConverter genderConverter = new ObjectTypeConverter(genderMapping);
		genderConverter.addConversionValue("F", "female");
		genderConverter.addConversionValue("M", "male");
		genderConverter.setDefaultAttributeValue("male");
		genderMapping.setConverter(genderConverter);
		employeeDescriptor.addMapping(genderMapping);
		
		XMLTransformationMapping normalHoursMapping = new XMLTransformationMapping();
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
		
		XMLObjectReferenceMapping addressMapping = new XMLObjectReferenceMapping();
		addressMapping.setAttributeName("address");
		addressMapping.addSourceToTargetKeyFieldAssociation("contact-information/address/@employee-id", "@employee-id");
		addressMapping.setReferenceClassName(Address.class.getName());
		employeeDescriptor.addMapping(addressMapping);
		
		XMLCollectionReferenceMapping phoneNumbersMapping = new XMLCollectionReferenceMapping();
		phoneNumbersMapping.setAttributeName("phoneNumbers");
		phoneNumbersMapping.addSourceToTargetKeyFieldAssociation("contact-information/phone/@type", "@type");
		phoneNumbersMapping.setReferenceClassName(PhoneNumber.class.getName());
		phoneNumbersMapping.setContainerPolicy(new ListContainerPolicy("java.util.Vector"));
		employeeDescriptor.addMapping(phoneNumbersMapping);
		
		XMLCompositeCollectionMapping dependentsMapping = new XMLCompositeCollectionMapping();
		dependentsMapping.setAttributeName("dependents");
		dependentsMapping.setXPath("dependent-information/dependent");
		dependentsMapping.setReferenceClassName(Dependent.class.getName());
        dependentsMapping.useCollectionClassName("java.util.Vector");
		employeeDescriptor.addMapping(dependentsMapping);
		
		XMLAnyAttributeMapping responsibilitiesMapping = new XMLAnyAttributeMapping();
		responsibilitiesMapping.setAttributeName("responsibilities");
		responsibilitiesMapping.setXPath("responsibility/text()");
		responsibilitiesMapping.useMapClassName("java.util.HashMap");
 		employeeDescriptor.addMapping(responsibilitiesMapping);
		
		return employeeDescriptor;
	}
	
	private XMLDescriptor buildPhoneNumberDescriptor() {
		XMLDescriptor phoneNumberDescriptor = new XMLDescriptor();
		phoneNumberDescriptor.setJavaClassName(PhoneNumber.class.getName());
		phoneNumberDescriptor.setSchemaReference(new XMLSchemaFileReference(buildSchemaFile()));
		phoneNumberDescriptor.setNamespaceResolver(new NamespaceResolver());
		((XMLSchemaFileReference) phoneNumberDescriptor.getSchemaReference()).setSchemaContext("/phone-type");
		phoneNumberDescriptor.descriptorIsAggregate();
		
		XMLDirectMapping typeMapping = new XMLDirectMapping();
		typeMapping.setAttributeName("type");
		typeMapping.setXPath("@type");
		phoneNumberDescriptor.addMapping(typeMapping);
		
		XMLDirectMapping areaCodeMapping = new XMLDirectMapping();
		areaCodeMapping.setAttributeName("areaCode");
		XMLField areaCodeField = new XMLField("area-code/text()");
		areaCodeField.setIsTypedTextField(true);
		areaCodeMapping.setField(areaCodeField);
		phoneNumberDescriptor.addMapping(areaCodeMapping);
		
		XMLDirectMapping numberMapping = new XMLDirectMapping();
		numberMapping.setAttributeName("number");
		XMLField numberField = new XMLField("number/text()");
		numberField.setIsTypedTextField(true);
		numberMapping.setField(numberField);
		phoneNumberDescriptor.addMapping(numberMapping);
		
		return phoneNumberDescriptor;
	}
	
	
	public Project getRuntimeProject() {
		return this.runtimeProject;
	}
}

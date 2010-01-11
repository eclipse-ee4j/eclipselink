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
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

import javax.xml.namespace.QName;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class TypeProject extends Project {
    private NamespaceResolver namespaceResolver;

    public TypeProject() {
        super();
        namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaceResolver.put("oxm", "http://www.example.com/toplink-oxm");
        addDescriptor(getAddressDescriptor());
        addDescriptor(getCdnAddressDescriptor());
        addDescriptor(getContactMethodDescriptor());
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getCustomerDescriptor());
        addDescriptor(getDependantDescriptor());
    }
    
    public XMLDescriptor getContactMethodDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ContactMethod.class);
        descriptor.setDefaultRootElement("contact-method");
        descriptor.setDefaultRootElementType(new QName("http://www.example.com/toplink-oxm", "contact-method-type"));
        descriptor.setNamespaceResolver(namespaceResolver);
        
        XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/oxm:contact-method-type");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        descriptor.setSchemaReference(ref);
        
        XMLField classIndicatorField = new XMLField("@xsi:type");
        descriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);
        descriptor.getInheritancePolicy().addClassIndicator(ContactMethod.class, "oxm:contact-method-type");
        descriptor.getInheritancePolicy().addClassIndicator(Address.class, "address-type");
        descriptor.getInheritancePolicy().addClassIndicator(CanadianAddress.class, "canadian-address-type");
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("id/text()");
        descriptor.addMapping(idMapping);

        return descriptor;
    }
    
    public XMLDescriptor getAddressDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(Address.class);
        descriptor.setDefaultRootElement("contact-method");
        descriptor.setDefaultRootElementType(new QName("http://www.example.com/toplink-oxm", "contact-method-type"));
		descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.getInheritancePolicy().setParentClass(ContactMethod.class);

        XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/address-type");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        descriptor.setSchemaReference(ref);
        
		XMLDirectMapping streetMapping = new XMLDirectMapping();
		streetMapping.setAttributeName("street");
		streetMapping.setXPath("street/text()");
		descriptor.addMapping(streetMapping);
		return descriptor;
    }

    public XMLDescriptor getCdnAddressDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(CanadianAddress.class);
        descriptor.setDefaultRootElement("contact-method");
        descriptor.setDefaultRootElementType(new QName("http://www.example.com/toplink-oxm", "contact-method-type"));
		descriptor.setNamespaceResolver(namespaceResolver);
		descriptor.getInheritancePolicy().setParentClass(Address.class);

        XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/canadian-address-type");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        descriptor.setSchemaReference(ref);
        
		XMLDirectMapping postalCodeMapping = new XMLDirectMapping();
		postalCodeMapping.setAttributeName("postalCode");
		postalCodeMapping.setXPath("postal-code/text()");
		descriptor.addMapping(postalCodeMapping);

		return descriptor;
    }

    public XMLDescriptor getCustomerDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(Customer.class);
		descriptor.setDefaultRootElement("customer");
		descriptor.setNamespaceResolver(namespaceResolver);
        
		XMLCompositeObjectMapping contactMapping = new XMLCompositeObjectMapping();
		contactMapping.setAttributeName("contact");
		contactMapping.setXPath("contact-method");
		contactMapping.setReferenceClass(ContactMethod.class);

        XMLField xmlFld = (XMLField) contactMapping.getField();
        xmlFld.setLeafElementType(new QName("http://www.example.com/toplink-oxm", "contact-method-type"));
		
        descriptor.addMapping(contactMapping);

        return descriptor;
    }

    public XMLDescriptor getEmployeeDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(Employee.class);
		descriptor.setDefaultRootElement("employee");
		descriptor.setNamespaceResolver(namespaceResolver);

		XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
		addressMapping.setAttributeName("address");
		addressMapping.setXPath("address");
		addressMapping.setReferenceClass(Address.class);
        
        XMLField xmlFld = (XMLField) addressMapping.getField();
        xmlFld.setLeafElementType(new QName("address-type"));

        descriptor.addMapping(addressMapping);

        return descriptor;
    }

    public XMLDescriptor getDependantDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Dependant.class);
        descriptor.setDefaultRootElement("dependant");
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setXPath("address");
        addressMapping.setReferenceClass(Address.class);
        
        XMLField xmlFld = (XMLField) addressMapping.getField();
        xmlFld.setLeafElementType(new QName("canadian-address-type"));

        descriptor.addMapping(addressMapping);

        return descriptor;
    }
}

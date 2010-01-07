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
package org.eclipse.persistence.testing.oxm.inheritance.typetests.any.collection;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.oxm.inheritance.typetests.Address;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.CanadianAddress;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.ContactMethod;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class AnyCollectionTypeProject extends Project {
    private NamespaceResolver namespaceResolver;

    public AnyCollectionTypeProject() {
        super();
        namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        addDescriptor(getAddressDescriptor());
        addDescriptor(getCdnAddressDescriptor());
        addDescriptor(getContactMethodDescriptor());
        addDescriptor(getCustomerDescriptor());
    }
    
    public XMLDescriptor getContactMethodDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ContactMethod.class);
        descriptor.setDefaultRootElement("contact-method");
        descriptor.setDefaultRootElementType(new QName("contact-method-type"));
        descriptor.setNamespaceResolver(namespaceResolver);
        
        XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/contact-method-type");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        descriptor.setSchemaReference(ref);
        
        XMLField classIndicatorField = new XMLField("@xsi:type");
        descriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);
        descriptor.getInheritancePolicy().addClassIndicator(ContactMethod.class, "contact-method-type");
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
        descriptor.setDefaultRootElementType(new QName("contact-method-type"));
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
        descriptor.setDefaultRootElementType(new QName("contact-method-type"));
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
		
        XMLAnyCollectionMapping contactMapping = new XMLAnyCollectionMapping();
		contactMapping.setAttributeName("contacts");
        descriptor.addMapping(contactMapping);
        return descriptor;
    }
}

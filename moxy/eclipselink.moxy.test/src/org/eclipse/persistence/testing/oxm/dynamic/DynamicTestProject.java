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
 *     rbarkhouse - 2009-11-16 14:08:13 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.dynamic;

import java.util.ArrayList;

import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse.Address;
import org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse.Employee;
import org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse.Root;

public class DynamicTestProject extends Project {

    public DynamicTestProject() {
        super();
        addEmployeeDescriptor();
        addAddressDescriptor();
        addPhoneNumberDescriptor();
        addEmailDescriptor();
        addSecurityCredentialDescriptor();
        addRootDescriptor();
    }

    private void addEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.eclipse.persistence.testing.oxm.dynamic.XEmployee");

        XMLDirectMapping name = new XMLDirectMapping();
        name.setAttributeName("name");
        name.setXPath("name/text()");
        descriptor.addMapping(name);
        
        XMLCompositeObjectMapping addr = new XMLCompositeObjectMapping();
        addr.setAttributeName("address");
        addr.setXPath("address");
        addr.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XAddress");
        descriptor.addMapping(addr);

        XMLCompositeCollectionMapping phone = new XMLCompositeCollectionMapping();
        phone.setAttributeName("phoneNumbers");
        phone.setXPath("phone-numbers/number");
        phone.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XPhoneNumber");
        phone.useCollectionClass(ArrayList.class);
        phone.setContainerPolicy(ContainerPolicy.buildPolicyFor(ArrayList.class));
        descriptor.addMapping(phone);

        XMLCollectionReferenceMapping emails = new XMLCollectionReferenceMapping();
        emails.setAttributeName("emails");
        emails.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XEmail");
        emails.addSourceToTargetKeyFieldAssociation("email-id/text()", "@id");
        descriptor.addMapping(emails);
        
        XMLObjectReferenceMapping secCred = new XMLObjectReferenceMapping();
        secCred.setAttributeName("securityCredential");
        secCred.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XSecurityCredential");
        secCred.addSourceToTargetKeyFieldAssociation("sec-cred-id/text()", "@id");
        descriptor.addMapping(secCred);
        
        this.addDescriptor(descriptor);
    }

    private void addAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.eclipse.persistence.testing.oxm.dynamic.XAddress");

        XMLDirectMapping street = new XMLDirectMapping();
        street.setAttributeName("street");
        street.setXPath("street/text()");
        descriptor.addMapping(street);

        XMLInverseReferenceMapping owningEmployee = new XMLInverseReferenceMapping();
        owningEmployee.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XEmployee");
        owningEmployee.setMappedBy("address");
        owningEmployee.setAttributeName("owningEmployee");
        owningEmployee.setSetMethodName("setOwningEmployee");
        owningEmployee.setGetMethodName("getOwningEmployee");
        descriptor.addMapping(owningEmployee);
        
        this.addDescriptor(descriptor);
    }

    private void addPhoneNumberDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.eclipse.persistence.testing.oxm.dynamic.XPhoneNumber");

        XMLDirectMapping num = new XMLDirectMapping();
        num.setAttributeName("number");
        num.setXPath("text()");
        descriptor.addMapping(num);

        XMLInverseReferenceMapping owningEmployee = new XMLInverseReferenceMapping();
        owningEmployee.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XEmployee");
        owningEmployee.setMappedBy("phoneNumbers");
        owningEmployee.setAttributeName("owningEmployee");
        owningEmployee.setSetMethodName("setOwningEmployee");
        owningEmployee.setGetMethodName("getOwningEmployee");
        descriptor.addMapping(owningEmployee);
        
        this.addDescriptor(descriptor);
    }

    private void addSecurityCredentialDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.eclipse.persistence.testing.oxm.dynamic.XSecurityCredential");
        descriptor.addPrimaryKeyFieldName("@id");

        XMLDirectMapping id = new XMLDirectMapping();
        id.setAttributeName("id");
        id.setXPath("@id");
        descriptor.addMapping(id);
        
        XMLDirectMapping zone = new XMLDirectMapping();
        zone.setAttributeName("zone");
        zone.setXPath("zone/text()");
        descriptor.addMapping(zone);

        XMLDirectMapping keyNumber = new XMLDirectMapping();
        keyNumber.setAttributeName("keyNumber");
        keyNumber.setXPath("key-number/text()");
        descriptor.addMapping(keyNumber);
        
        XMLInverseReferenceMapping owningEmployee = new XMLInverseReferenceMapping();
        owningEmployee.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XEmployee");
        owningEmployee.setMappedBy("securityCredential");
        owningEmployee.setAttributeName("owningEmployee");
        owningEmployee.setSetMethodName("setOwningEmployee");
        owningEmployee.setGetMethodName("getOwningEmployee");
        descriptor.addMapping(owningEmployee);
        
        this.addDescriptor(descriptor);
    }
    
    private void addEmailDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.eclipse.persistence.testing.oxm.dynamic.XEmail");
        descriptor.addPrimaryKeyFieldName("@id");
        
        XMLDirectMapping id = new XMLDirectMapping();
        id.setAttributeName("id");
        id.setXPath("@id");
        descriptor.addMapping(id);
        
        XMLDirectMapping username = new XMLDirectMapping();
        username.setAttributeName("username");
        username.setXPath("username/text()");
        descriptor.addMapping(username);

        XMLDirectMapping domain = new XMLDirectMapping();
        domain.setAttributeName("domain");
        domain.setXPath("domain/text()");
        descriptor.addMapping(domain);

        XMLInverseReferenceMapping owningEmployee = new XMLInverseReferenceMapping();
        owningEmployee.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XEmployee");
        owningEmployee.setMappedBy("emails");
        owningEmployee.setAttributeName("owningEmployee");
        owningEmployee.setSetMethodName("setOwningEmployee");
        owningEmployee.setGetMethodName("getOwningEmployee");
        descriptor.addMapping(owningEmployee);        
        
        this.addDescriptor(descriptor);
    }
    
    private void addRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.eclipse.persistence.testing.oxm.dynamic.XRoot");
        descriptor.setDefaultRootElement("root");

        XMLCompositeObjectMapping employee = new XMLCompositeObjectMapping();
        employee.setAttributeName("employee");
        employee.setXPath("employee");
        employee.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XEmployee");
        descriptor.addMapping(employee);

        XMLCompositeCollectionMapping projects = new XMLCompositeCollectionMapping();
        projects.setAttributeName("emails");
        projects.setXPath("email");
        projects.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XEmail");
        descriptor.addMapping(projects);

        XMLCompositeObjectMapping secCred = new XMLCompositeObjectMapping();
        secCred.setAttributeName("securityCredential");
        secCred.setXPath("security-credential");
        secCred.setReferenceClassName("org.eclipse.persistence.testing.oxm.dynamic.XSecurityCredential");
        descriptor.addMapping(secCred);
        
        this.addDescriptor(descriptor);
    }
    
}
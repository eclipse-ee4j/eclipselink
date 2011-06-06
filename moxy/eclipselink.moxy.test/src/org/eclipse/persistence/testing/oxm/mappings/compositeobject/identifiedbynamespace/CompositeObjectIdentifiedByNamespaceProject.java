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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbynamespace;

import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.MailingAddress;

public class CompositeObjectIdentifiedByNamespaceProject extends Project {

	private final static String EMAIL_PREFIX = "email";
	private final static String EMAIL_NAMESPACE = "www.example.com/some-dir/email.xsd";
	private final static String MAILING_PREFIX = "mailing";
	private final static String MAILING_NAMESPACE = "www.example.com/some-dir/mailing.xsd";

  private NamespaceResolver namespaceResolver;

  public CompositeObjectIdentifiedByNamespaceProject() {
    namespaceResolver = new NamespaceResolver();
    namespaceResolver.put(EMAIL_PREFIX, EMAIL_NAMESPACE);
    namespaceResolver.put(MAILING_PREFIX, MAILING_NAMESPACE);
  
    addDescriptor(getEmployeeDescriptor());
    addDescriptor(getEmailAddressDescriptor());
    addDescriptor(getMailingAddressDescriptor());
  }

  private XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(Employee.class);
    descriptor.setNamespaceResolver(namespaceResolver);
    descriptor.setDefaultRootElement("employee");

    XMLDirectMapping idMapping = new XMLDirectMapping();
    idMapping.setAttributeName("id");
    idMapping.setXPath("id/text()");
    descriptor.addMapping(idMapping);   

    XMLCompositeObjectMapping emailMapping = new XMLCompositeObjectMapping();
    emailMapping.setAttributeName("emailAddress");
    emailMapping.setXPath("email:address");
    emailMapping.setGetMethodName("getEmailAddress");
    emailMapping.setSetMethodName("setEmailAddress");
    emailMapping.setReferenceClass(EmailAddress.class);
    descriptor.addMapping(emailMapping);

    XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
    addressMapping.setAttributeName("mailingAddress");
    addressMapping.setXPath("mailing:address");
    addressMapping.setGetMethodName("getMailingAddress");
    addressMapping.setSetMethodName("setMailingAddress");
    addressMapping.setReferenceClass(MailingAddress.class);
    descriptor.addMapping(addressMapping);

    return descriptor;
  }

  private XMLDescriptor getEmailAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(EmailAddress.class);
    descriptor.setNamespaceResolver(namespaceResolver);
    
    XMLDirectMapping userIDMapping = new XMLDirectMapping();
    userIDMapping.setAttributeName("userID");
    userIDMapping.setXPath("email:user-id/text()");
    descriptor.addMapping(userIDMapping);   

    XMLDirectMapping domainMapping = new XMLDirectMapping();
    domainMapping.setAttributeName("domain");
    domainMapping.setXPath("email:domain/text()");
    descriptor.addMapping(domainMapping);   

    XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();
    schemaRef.setSchemaContext("/email:addressType");
    schemaRef.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
    descriptor.setSchemaReference(schemaRef);    

    return descriptor;
  }  

  private XMLDescriptor getMailingAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(MailingAddress.class);
    descriptor.setNamespaceResolver(namespaceResolver);
        
    XMLDirectMapping streetMapping = new XMLDirectMapping();
    streetMapping.setAttributeName("street");
    streetMapping.setXPath("mailing:street/text()");
    descriptor.addMapping(streetMapping);   

    XMLDirectMapping cityMapping = new XMLDirectMapping();
    cityMapping.setAttributeName("city");
    cityMapping.setXPath("mailing:city/text()");
    descriptor.addMapping(cityMapping);   

    XMLDirectMapping provinceMapping = new XMLDirectMapping();
    provinceMapping.setAttributeName("province");
    provinceMapping.setXPath("mailing:province/text()");
    descriptor.addMapping(provinceMapping);   

    XMLDirectMapping postalCodeMapping = new XMLDirectMapping();
    postalCodeMapping.setAttributeName("postalCode");
    postalCodeMapping.setXPath("mailing:postal-code/text()");
    descriptor.addMapping(postalCodeMapping);   
    
    XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();
    schemaRef.setSchemaContext("/mailing:addressType");
    schemaRef.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
    descriptor.setSchemaReference(schemaRef); 

    return descriptor;
  }    
  
}

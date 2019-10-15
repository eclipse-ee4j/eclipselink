/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withgroupingelement;

import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.MailingAddress;

public class CompositeCollectionWithGroupingElementIdentifiedByNamespaceProject extends Project {

  private final static String EMAIL_PREFIX = "email";
  private final static String EMAIL_NAMESPACE = "www.example.com/some-dir/email.xsd";
  private final static String MAILING_PREFIX = "mailing";
  private final static String MAILING_NAMESPACE = "www.example.com/some-dir/mailing.xsd";

  private NamespaceResolver namespaceResolver;

  public CompositeCollectionWithGroupingElementIdentifiedByNamespaceProject() {
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
    descriptor.setDefaultRootElement("employee");
        descriptor.setNamespaceResolver(namespaceResolver);

    XMLCompositeCollectionMapping emailMapping = new XMLCompositeCollectionMapping();
    emailMapping.setAttributeName("emailAddresses");
    emailMapping.useCollectionClass(java.util.Vector.class);
    emailMapping.setReferenceClass(EmailAddress.class);
    emailMapping.setXPath(EMAIL_PREFIX + ":addresses/" + EMAIL_PREFIX + ":address");
    descriptor.addMapping(emailMapping);

    XMLCompositeCollectionMapping mailingMapping = new XMLCompositeCollectionMapping();
    mailingMapping.setAttributeName("mailingAddresses");
    mailingMapping.useCollectionClass(java.util.Vector.class);
    mailingMapping.setReferenceClass(MailingAddress.class);
    mailingMapping.setXPath(MAILING_PREFIX + ":addresses/" + MAILING_PREFIX + ":address");
    descriptor.addMapping(mailingMapping);

    return descriptor;
  }

  private XMLDescriptor getEmailAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(EmailAddress.class);
        descriptor.setNamespaceResolver(namespaceResolver);

    XMLDirectMapping userIDMapping = new XMLDirectMapping();
    userIDMapping.setAttributeName("userID");
    userIDMapping.setXPath(EMAIL_PREFIX + ":user-id/text()");
    descriptor.addMapping(userIDMapping);

    XMLDirectMapping domainMapping = new XMLDirectMapping();
    domainMapping.setAttributeName("domain");
    domainMapping.setXPath(EMAIL_PREFIX + ":domain/text()");
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
    streetMapping.setXPath(MAILING_PREFIX + ":street/text()");
    descriptor.addMapping(streetMapping);

    XMLDirectMapping cityMapping = new XMLDirectMapping();
    cityMapping.setAttributeName("city");
    cityMapping.setXPath(MAILING_PREFIX + ":city/text()");
    descriptor.addMapping(cityMapping);

    XMLDirectMapping provinceMapping = new XMLDirectMapping();
    provinceMapping.setAttributeName("province");
    provinceMapping.setXPath(MAILING_PREFIX + ":province/text()");
    descriptor.addMapping(provinceMapping);

    XMLDirectMapping postalCodeMapping = new XMLDirectMapping();
    postalCodeMapping.setAttributeName("postalCode");
    postalCodeMapping.setXPath(MAILING_PREFIX + ":postal-code/text()");
    descriptor.addMapping(postalCodeMapping);

       XMLSchemaClassPathReference schemaRef = new XMLSchemaClassPathReference();
    schemaRef.setSchemaContext("/mailing:addressType");
    schemaRef.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
    descriptor.setSchemaReference(schemaRef);


    return descriptor;
  }

}

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
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import java.net.URL;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.sessions.Project;

public class XMLContextTestProject extends Project {
    public XMLContextTestProject() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getEmailAddressDescriptor());
    }

    protected XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        // descriptor.setDefaultRootElement("employee");
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setXPath("id/text()");
        idMapping.setAttributeName("id");
        descriptor.addMapping(idMapping);

        XMLCompositeObjectMapping emailMapping = new XMLCompositeObjectMapping();
        emailMapping.setAttributeName("emailAddress");
        emailMapping.setXPath("info/email-address");
        emailMapping.setGetMethodName("getEmailAddress");
        emailMapping.setSetMethodName("setEmailAddress");
        emailMapping.setReferenceClass(EmailAddress.class);
        descriptor.addMapping(emailMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaURLReference.COMPLEX_TYPE);
        schemaRef.setSchemaContext("employee-root");
        descriptor.setSchemaReference(schemaRef);

        /*
                NamespaceResolver try_test = new NamespaceResolver();
                try_test.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);
                descriptor.setNamespaceResolver(try_test);*/
        return descriptor;
    }

    private XMLDescriptor getEmailAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EmailAddress.class);

        XMLDirectMapping userIDMapping = new XMLDirectMapping();
        userIDMapping.setAttributeName("userID");
        userIDMapping.setXPath("user-id/text()");
        descriptor.addMapping(userIDMapping);

        XMLDirectMapping domainMapping = new XMLDirectMapping();
        domainMapping.setAttributeName("domain");
        domainMapping.setXPath("domain/text()");
        descriptor.addMapping(domainMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaURLReference.SIMPLE_TYPE);
        schemaRef.setSchemaContext("emailaddress");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }
}

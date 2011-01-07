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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.mappingxpathcollision;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmployeeWithUserID;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmailAddress;

public class MappingXpathCollisionProject extends Project {
    public MappingXpathCollisionProject() {
        addDescriptor(buildEmployeeDescriptor());
        addDescriptor(buildEmailAddressDescriptor());
    }

    private XMLDescriptor buildEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EmployeeWithUserID.class);
        descriptor.setDefaultRootElement("emp");

        XMLDirectMapping userIDMapping = new XMLDirectMapping();
        userIDMapping.setAttributeName("userID");
        userIDMapping.setXPath("@userID");
        descriptor.addMapping(userIDMapping);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);

        /*XMLCompositeCollectionMapping emailMapping= new XMLCompositeCollectionMapping();
        emailMapping.setReferenceClass(EmailAddress.class);
        emailMapping.setAttributeName("emailAddresses");
        emailMapping.setXPath("emailAddress");
        descriptor.addMapping(emailMapping);*/
        return descriptor;
    }

    private XMLDescriptor buildEmailAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EmailAddress.class);
        descriptor.setDefaultRootElement("email");

        XMLDirectMapping userIDMapping = new XMLDirectMapping();
        userIDMapping.setAttributeName("userID");
        //userIDMapping.setGetMethodName("getUserID");
        //userIDMapping.setSetMethodName("setName");
        userIDMapping.setXPath("@userID");
        descriptor.addMapping(userIDMapping);

        XMLDirectMapping domainMapping = new XMLDirectMapping();
        domainMapping.setAttributeName("domain");
        domainMapping.setXPath("domain/text()");
        descriptor.addMapping(domainMapping);
        return descriptor;
    }
}

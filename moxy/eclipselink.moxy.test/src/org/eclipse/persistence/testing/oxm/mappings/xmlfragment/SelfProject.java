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
*     bdoughan - April 6/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.xmlfragment;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;
import org.eclipse.persistence.sessions.Project;

public class SelfProject extends Project {

    public SelfProject() {
        this.addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("bar:employee");

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("bar", "urn:example");
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("bar:first-name/text()");
        firstNameMapping.setIsReadOnly(true);
        descriptor.addMapping(firstNameMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath("bar:last-name/text()");
        lastNameMapping.setIsReadOnly(true);
        descriptor.addMapping(lastNameMapping);

        XMLFragmentMapping nodeMapping = new XMLFragmentMapping();
        nodeMapping.setAttributeName("xmlNode");
        nodeMapping.setXPath(".");
        descriptor.addMapping(nodeMapping);
        
        return descriptor;
    }

}
/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.typetranslator.childelement;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {
    public EmployeeProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
        this.addDescriptor(getPhoneDescriptor());
    }

    XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
        namespaceResolver.put(XMLConstants.SCHEMA_PREFIX, XMLConstants.SCHEMA_URL);
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("name");
        mapping.setXPath("name/text()");
        xmlDescriptor.addMapping(mapping);

        XMLCompositeObjectMapping cmapping = new XMLCompositeObjectMapping();
        cmapping.setAttributeName("phone");
        cmapping.setReferenceClass(Phone.class);
        cmapping.setXPath("phone-no");
        xmlDescriptor.addMapping(cmapping);

        return xmlDescriptor;
    }

    XMLDescriptor getPhoneDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Phone.class);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
        namespaceResolver.put(XMLConstants.SCHEMA_PREFIX, XMLConstants.SCHEMA_URL);
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping mapping = new XMLDirectMapping();
        XMLField tef = new XMLField();
        tef.setIsTypedTextField(true);
        tef.setXPath("text()");
        mapping.setField(tef);
        mapping.setAttributeName("number");
        xmlDescriptor.addMapping(mapping);

        return xmlDescriptor;
    }
}

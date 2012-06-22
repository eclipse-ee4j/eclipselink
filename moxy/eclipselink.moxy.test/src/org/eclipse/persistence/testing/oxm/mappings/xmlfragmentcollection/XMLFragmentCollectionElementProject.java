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
package org.eclipse.persistence.testing.oxm.mappings.xmlfragmentcollection;

import java.util.ArrayList;

import org.w3c.dom.Node;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.sessions.Project;

/**
 * XMLFragmentCollectionMapping test project
 */
public class XMLFragmentCollectionElementProject extends Project {
    
	private NamespaceResolver namespaceResolver;
	
    public XMLFragmentCollectionElementProject(NamespaceResolver nsresolver) {
        namespaceResolver = nsresolver;
    	addEmployeeDescriptor();
    }

    public void addEmployeeDescriptor() {
        XMLDescriptor desc = new XMLDescriptor();
        desc.setJavaClass(Employee.class);
        desc.setDefaultRootElement("employee");
        desc.setNamespaceResolver(namespaceResolver);
        
        // setup first-name mapping
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("firstName");
        mapping.setXPath("first-name/text()");
        desc.addMapping(mapping);

        // setup last-name mapping
        mapping = new XMLDirectMapping();
        mapping.setAttributeName("lastName");
        mapping.setXPath("last-name/text()");
        desc.addMapping(mapping);
        
        // setup xml-node mapping
        XMLFragmentCollectionMapping mapping2 = new XMLFragmentCollectionMapping();
        mapping2.setAttributeName("xmlnodes");
        mapping2.setXPath("xml-node");
        mapping2.useCollectionClass(ArrayList.class);
        desc.addMapping(mapping2);
        
        // add the descriptor to the project
        this.addDescriptor(desc);
    }
    
}

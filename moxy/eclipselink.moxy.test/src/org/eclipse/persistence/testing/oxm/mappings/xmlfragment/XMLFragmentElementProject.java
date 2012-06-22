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
package org.eclipse.persistence.testing.oxm.mappings.xmlfragment;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.sessions.Project;

/**
 *  @version $Header: XMLFragmentElementProject.java 02-nov-2006.10:57:28 gyorke Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class XMLFragmentElementProject extends Project {
    public XMLFragmentElementProject() {
        addEmployeeDescriptor();
        XMLLogin login = new XMLLogin();
        login.setPlatform(new DOMPlatform());
        //this.setLogin(login);
    }
    
    public void addEmployeeDescriptor() {
        XMLDescriptor desc = new XMLDescriptor();
        desc.setJavaClass(Employee.class);
        desc.setDefaultRootElement("employee");
        
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("firstName");
        mapping.setXPath("first-name/text()");
        desc.addMapping(mapping);

        mapping = new XMLDirectMapping();
        mapping.setAttributeName("lastName");
        mapping.setXPath("last-name/text()");
        desc.addMapping(mapping);

        XMLFragmentMapping mapping2 = new XMLFragmentMapping();
        mapping2.setAttributeName("xmlNode");
        mapping2.setXPath("xml-node");
        desc.addMapping(mapping2);
        
        //desc.setShouldPreserveDocument(true);
    
        this.addDescriptor(desc);
    
    }
    

}

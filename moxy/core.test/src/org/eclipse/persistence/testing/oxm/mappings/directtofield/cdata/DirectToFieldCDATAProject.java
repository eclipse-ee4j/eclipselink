/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directtofield.cdata;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public class DirectToFieldCDATAProject extends Project {
    public DirectToFieldCDATAProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("firstName");
        mapping.setXPath("first-name");
        xmlDescriptor.addMapping(mapping);
        
        mapping = new XMLDirectMapping();
        mapping.setAttributeName("lastName");
        mapping.setXPath("last-name");
        xmlDescriptor.addMapping(mapping);
        
        mapping = new XMLDirectMapping();
        mapping.setAttributeName("data");
        mapping.setXPath("data");
        mapping.setIsCDATA(true);
        xmlDescriptor.addMapping(mapping);
        
        
        return xmlDescriptor;
    }
}
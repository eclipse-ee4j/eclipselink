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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlelement;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.Employee;

public class DefaultNullValueElementProject extends Project {
    public final static int CONTROL_ID = -1;
    public final static String CONTROL_FIRSTNAME = "";
	
    public DefaultNullValueElementProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("id/text()");
        idMapping.setNullValue(new Integer(CONTROL_ID));
        xmlDescriptor.addMapping(idMapping);

        XMLDirectMapping numericNoNullValueMapping = new XMLDirectMapping();
        numericNoNullValueMapping.setAttributeName("numericNoNullValue");
        numericNoNullValueMapping.setXPath("numeric-no-null-value/text()");
        //numericNoNullValueMapping.setNullValue(new Integer(CONTROL_ID));
        xmlDescriptor.addMapping(numericNoNullValueMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("first-name/text()");
        firstNameMapping.setNullValue(CONTROL_FIRSTNAME);
        xmlDescriptor.addMapping(firstNameMapping);

        return xmlDescriptor;
    }
}

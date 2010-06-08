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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlattribute;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.Employee;

public class DefaultNullValueAttributeProject extends Project {
    public final static int CONTROL_ID = -1;
    public final static String CONTROL_FIRSTNAME = "";
	
    public DefaultNullValueAttributeProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.getAttributeName();        
        idMapping.setXPath("@id");
        idMapping.setNullValue(new Integer(CONTROL_ID));
        xmlDescriptor.addMapping(idMapping);

        //XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        //firstNameMapping.setAttributeName("firstName");
        //firstNameMapping.getAttributeName();
        //firstNameMapping.setXPath("@first-name");
        //firstNameMapping.setNullValue(CONTROL_FIRSTNAME);
        // Set the mapping in the same manner as MW does in AbstractNamedSchemaComponent
        ((XMLDirectMapping)xmlDescriptor.addDirectMapping("firstName", "@first-name")).setNullValue(CONTROL_FIRSTNAME);
        //xmlDescriptor.addMapping(firstNameMapping);

        return xmlDescriptor;
    }
}

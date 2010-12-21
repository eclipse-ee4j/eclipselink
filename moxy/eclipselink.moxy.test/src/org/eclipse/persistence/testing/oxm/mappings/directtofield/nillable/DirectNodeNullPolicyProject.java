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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class DirectNodeNullPolicyProject extends Project {

    /**
     * Construct a project with a descriptor setup for all fields that do not use a default NodeNullPolicy
     * @param aMapping
     * @param fieldsAsElements
     */
    public DirectNodeNullPolicyProject(boolean fieldsAsElements) {
        XMLDescriptor aDescriptor = getEmployeeDescriptor(fieldsAsElements);
        addDescriptor(aDescriptor);
    }

    /**
     * Set only the mappings that do not have a default NodeNullPolicy
     * @return
     */
    private XMLDescriptor getEmployeeDescriptor(boolean fieldsAsElements) {
        // if all fields are attributes the use XPath format @id otherwise use id/text()
        String xPathPrepend;

        // if all fields are attributes the use XPath format @id otherwise use id/text()
        String xPathAppend;
        if (fieldsAsElements) {
            xPathPrepend = "";
            xPathAppend = "/text()";
        } else {
            xPathPrepend = "@";
            xPathAppend = "";
        }

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setGetMethodName("getId");
        idMapping.setSetMethodName("setId");
        idMapping.setXPath(xPathPrepend + "id" + xPathAppend);
        descriptor.addMapping(idMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setGetMethodName("getFirstName");
        firstNameMapping.setSetMethodName("setFirstName");
        firstNameMapping.setXPath(xPathPrepend + "first-name" + xPathAppend);
        descriptor.addMapping(firstNameMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setGetMethodName("getLastName");
        lastNameMapping.setSetMethodName("setLastName");
        lastNameMapping.setXPath(xPathPrepend + "last-name" + xPathAppend);
        descriptor.addMapping(lastNameMapping);

        return descriptor;
    }
}

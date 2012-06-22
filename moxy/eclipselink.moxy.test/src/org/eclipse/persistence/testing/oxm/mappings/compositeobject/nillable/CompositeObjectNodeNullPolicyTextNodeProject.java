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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class CompositeObjectNodeNullPolicyTextNodeProject extends Project {

    /**
     * Construct a project with a descriptor setup for all fields that do not use a default NodeNullPolicy
     * The firstname field of the composite object "manager" is a text() field
     * @param aMapping
     * @param fieldsAsElements
     */
    public CompositeObjectNodeNullPolicyTextNodeProject(boolean fieldsAsElements) {
        XMLDescriptor aTeamDescriptor = getTeamDescriptor(fieldsAsElements);
        XMLDescriptor anEmployeeDescriptor = getEmployeeDescriptor(fieldsAsElements);
        addDescriptor(aTeamDescriptor);
        addDescriptor(anEmployeeDescriptor);
    }

    private XMLDescriptor getTeamDescriptor(boolean fieldsAsElements) {
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
        descriptor.setJavaClass(Team2.class);
        descriptor.setDefaultRootElement("team");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath(xPathPrepend + "id" + xPathAppend);
        descriptor.addMapping(idMapping);

        XMLCompositeObjectMapping managerMapping = new XMLCompositeObjectMapping();
        managerMapping.setAttributeName("manager");
        managerMapping.setXPath("manager");
        managerMapping.setGetMethodName("getManager");
        managerMapping.setSetMethodName("setManager");
        managerMapping.setReferenceClass(Employee2.class);
        descriptor.addMapping(managerMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("name");
        lastNameMapping.setXPath(xPathPrepend + "name" + xPathAppend);
        descriptor.addMapping(lastNameMapping);
        
        return descriptor;
    }
    
    /**
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
        descriptor.setJavaClass(Employee2.class);
        descriptor.setDefaultRootElement("manager");

/*        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath(xPathPrepend + "id" + xPathAppend);
        descriptor.addMapping(idMapping);
*/
        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("text()");//xPathPrepend + "first-name" + xPathAppend);
        descriptor.addMapping(firstNameMapping);
/*
        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath(xPathPrepend + "last-name" + xPathAppend);
        descriptor.addMapping(lastNameMapping);
*/        
        return descriptor;
    }
}

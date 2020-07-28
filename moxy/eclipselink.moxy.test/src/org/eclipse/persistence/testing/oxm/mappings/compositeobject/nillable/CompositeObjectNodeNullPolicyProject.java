/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class CompositeObjectNodeNullPolicyProject extends Project {

    /**
     * Construct a project with a descriptor setup for all fields that do not use a default NodeNullPolicy
     * @param aMapping
     * @param fieldsAsElements
     */
    public CompositeObjectNodeNullPolicyProject(boolean fieldsAsElements) {
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
        descriptor.setJavaClass(Team.class);
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
        managerMapping.setReferenceClass(Employee.class);
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
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("manager");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath(xPathPrepend + "id" + xPathAppend);
        descriptor.addMapping(idMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath(xPathPrepend + "first-name" + xPathAppend);
        descriptor.addMapping(firstNameMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath(xPathPrepend + "last-name" + xPathAppend);
        descriptor.addMapping(lastNameMapping);

        return descriptor;
    }
}

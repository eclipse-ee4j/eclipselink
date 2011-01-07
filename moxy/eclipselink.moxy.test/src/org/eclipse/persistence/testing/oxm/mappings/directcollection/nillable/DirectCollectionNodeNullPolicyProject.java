/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class DirectCollectionNodeNullPolicyProject extends Project {

    /**
     * Construct a project with a descriptor setup for all fields that do not use a default NodeNullPolicy
     * @param aMapping
     * @param fieldsAsElements
     */
    public DirectCollectionNodeNullPolicyProject(boolean fieldsAsElements) {
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
        idMapping.setXPath(xPathPrepend + "id" + xPathAppend);
        descriptor.addMapping(idMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath(xPathPrepend + "first-name" + xPathAppend);
        descriptor.addMapping(firstNameMapping);

        XMLCompositeDirectCollectionMapping taskMapping = new XMLCompositeDirectCollectionMapping();
        taskMapping.setAttributeName("tasks");
        if (fieldsAsElements) {
            taskMapping.setXPath(xPathPrepend + "task" + xPathAppend);
        } else {
            taskMapping.setXPath("tasks/" + xPathPrepend + "task" + xPathAppend);
        }
        descriptor.addMapping(taskMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath(xPathPrepend + "last-name" + xPathAppend);
        descriptor.addMapping(lastNameMapping);

        NamespaceResolver nsr = new NamespaceResolver();
        nsr.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
        descriptor.setNamespaceResolver(nsr);

        return descriptor;
    }

}
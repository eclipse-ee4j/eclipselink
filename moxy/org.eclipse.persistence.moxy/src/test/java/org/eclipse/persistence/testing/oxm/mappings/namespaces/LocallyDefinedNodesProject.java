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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class LocallyDefinedNodesProject extends Project  {

    public LocallyDefinedNodesProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("ns:employee");

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns", "http://www.example.com/EMPLOYEE");
        xmlDescriptor.setNamespaceResolver(nsResolver);

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("ns:personal-info/@id");
        xmlDescriptor.addMapping(idMapping);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("ns:personal-info/name/text()");
        xmlDescriptor.addMapping(nameMapping);

        return xmlDescriptor;
    }

}

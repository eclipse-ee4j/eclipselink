/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlmarshaller.setschemas;

import java.net.URL;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {

    private static final String XML_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/setschemas/schema.xsd";

    public EmployeeProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
    xmlDescriptor.setJavaClass(org.eclipse.persistence.testing.oxm.mappings.transformation.Employee.class);
    xmlDescriptor.setDefaultRootElement("ns:root");

        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
        URL url = ClassLoader.getSystemResource(XML_SCHEMA_RESOURCE);
        schemaReference.setURL(url);
        xmlDescriptor.setSchemaReference(schemaReference);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns", "ElemDecl/maxOccurs");
        nsResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmlDescriptor.setNamespaceResolver(nsResolver);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
    nameMapping.setAttributeName("name");
    nameMapping.setXPath("ns:Local");
    xmlDescriptor.addMapping(nameMapping);

        return xmlDescriptor;
    }

}

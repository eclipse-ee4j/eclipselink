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
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import org.eclipse.persistence.testing.oxm.xmlroot.Person;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class XMLRootComplexProject extends Project {
    private NamespaceResolver namespaceResolver;

    public XMLRootComplexProject() {
        namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        addDescriptor(getPersonDescriptor());
    }

    private XMLDescriptor getPersonDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Person.class);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);

        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/oxm:person");
        schemaReference.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        descriptor.setSchemaReference(schemaReference);

        namespaceResolver.put("oxm", "test");
        descriptor.setNamespaceResolver(namespaceResolver);

        return descriptor;
    }
}

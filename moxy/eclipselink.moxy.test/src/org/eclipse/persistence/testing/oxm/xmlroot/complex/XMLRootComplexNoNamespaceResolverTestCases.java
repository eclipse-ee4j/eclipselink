/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;

public class XMLRootComplexNoNamespaceResolverTestCases extends XMLRootComplexTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/employee-no-nr.xml";
    private final static String NS0_CONTROL_ELEMENT_NAME = "ns0:employee";
    private final static String NS0_CONTROL_ELEMENT_NAME_NO_PREFIX = "employee";

    public XMLRootComplexNoNamespaceResolverTestCases(String name) throws Exception {
        super(name);
        XMLRootComplexProject project = new XMLRootComplexProject();
        ((XMLDescriptor)project.getDescriptor(Person.class)).getSchemaReference().setSchemaContext("/person");
        ((XMLDescriptor)project.getDescriptor(Person.class)).setNamespaceResolver(null);
        setProject(project);
    }

    public Object getReadControlObject() {
        Person peep = new Person();
        peep.setName(CONTROL_PERSON_NAME);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(NS0_CONTROL_ELEMENT_NAME);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(peep);
        return xmlRoot;
    }

    public Object getWriteControlObject() {
        Person peep = new Person();
        peep.setName(CONTROL_PERSON_NAME);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(NS0_CONTROL_ELEMENT_NAME_NO_PREFIX);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(peep);
        return xmlRoot;
    }

    public String getXMLResource() {
        return XML_RESOURCE;
    }
}

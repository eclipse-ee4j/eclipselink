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

public class XMLRootComplexNullUriTestCases extends XMLRootComplexTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/employee-null-uri.xml";
    private final static String CONTROL_PERSON_NAME = "Joe Smith";
    private final static String CONTROL_ELEMENT_NAME = "employee";
    private final static String CONTROL_NAMESPACE_URI = null;

    public XMLRootComplexNullUriTestCases(String name) throws Exception {
        super(name);
        XMLRootComplexProject project= new XMLRootComplexProject();
        ((XMLDescriptor)project.getDescriptor(Person.class)).getSchemaReference().setSchemaContext("/person");
        ((XMLDescriptor)project.getDescriptor(Person.class)).setNamespaceResolver(null);
        setProject(project);
    }

    public Object getControlObject() {
        Person peep = new Person();
        peep.setName(CONTROL_PERSON_NAME);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(peep);
        return xmlRoot;
    }

    public Object getWriteControlObject() {
        return getControlObject();
    }

    public String getXMLResource() {
        return XML_RESOURCE;
    }
}

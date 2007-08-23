/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;

public class XMLRootComplexNoPrefixTestCases extends XMLRootComplexTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/employee-no-prefix.xml";
    private final static String CONTROL_ELEMENT_NAME_NO_PREFIX = "employee";

    public XMLRootComplexNoPrefixTestCases(String name) throws Exception {
        super(name);
    }

    public Object getReadControlObject() {
        Person peep = new Person();
        peep.setName(CONTROL_PERSON_NAME);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(peep);
        return xmlRoot;
    }

    public Object getWriteControlObject() {
        Person peep = new Person();
        peep.setName(CONTROL_PERSON_NAME);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME_NO_PREFIX);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(peep);
        return xmlRoot;
    }

    public String getXMLResource() {
        return XML_RESOURCE;
    }
}
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
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import java.io.InputStream;

import junit.textui.TestRunner;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;
import org.w3c.dom.Document;

public class XMLRootComplexDifferentPrefixTestCases extends XMLRootComplexTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/employee-diff-prefix.xml";
    protected final static String CONTROL_PERSON_NAME = "Joe Smith";
    protected final static String CONTROL_ELEMENT_NAME = "myns:employee";
    protected final static String CONTROL_NAMESPACE_URI = "test";

    public XMLRootComplexDifferentPrefixTestCases(String name) throws Exception {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexDifferentPrefixTestCases" };
        TestRunner.main(arguments);
    }

    public Project getTopLinkProject() {
        Project p = super.getTopLinkProject();
        ((XMLDescriptor)p.getDescriptor(Person.class)).getNamespaceResolver().put("myns", "test");

        return p;
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

    public Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xmlroot/complex/employee-diff-prefix-write.xml");
        Document writeDocument = parser.parse(inputStream);
        removeEmptyTextNodes(controlDocument);
        inputStream.close();
        return writeDocument;
    }
}

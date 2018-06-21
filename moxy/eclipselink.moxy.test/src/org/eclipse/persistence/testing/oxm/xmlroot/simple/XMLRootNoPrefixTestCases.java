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
package org.eclipse.persistence.testing.oxm.xmlroot.simple;

import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLRoot;
import org.w3c.dom.Document;

public class XMLRootNoPrefixTestCases extends XMLRootSimpleTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/simple/employee-no-prefix.xml";
    private final static String CONTROL_ELEMENT_NAME = "ns0:employee-name";
    private final static String CONTROL_ELEMENT_NAME_NO_PREFIX = "employee-name";

    public XMLRootNoPrefixTestCases(String name) throws Exception {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootNoPrefixTestCases" };
        TestRunner.main(arguments);
    }

    public Object getReadControlObject() {
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(CONTROL_OBJECT);
        return xmlRoot;
    }

    public Object getWriteControlObject() {
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME_NO_PREFIX);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(CONTROL_OBJECT);
        return xmlRoot;
    }

    public String getXMLResource() {
        return XML_RESOURCE;
    }

    public Document getWriteControlDocument() {
        return getControlDocument();
    }
}

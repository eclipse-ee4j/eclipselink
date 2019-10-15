/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.3
package org.eclipse.persistence.testing.jaxb.binder.nscollison;

import java.io.File;
import java.io.StringReader;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class NamespaceCollisionTestCases extends TestCase {
    private XMLParser parser;

    public NamespaceCollisionTestCases() {
        XMLPlatform platform = XMLPlatformFactory.getInstance().getXMLPlatform();
        parser = platform.newXMLParser();
    }

    public void testNamespaceCollision() throws Exception {
        String xml = "<ns0:employee xmlns:ns1=\"mynamespace3\" xmlns:ns2=\"mynamespace2\" xmlns:ns0=\"mynamespace1\"><ns2:address><street>123 Fake Street</street></ns2:address><firstName>Matt</firstName><id>123</id></ns0:employee>";
        String controlSource = "org/eclipse/persistence/testing/jaxb/binder/nscollision/employee.xml";
        Document controlDocument = parser.parse(Thread.currentThread().getContextClassLoader().getResource(controlSource));

        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{Employee.class}, null);

        Binder binder = ctx.createBinder();

        JAXBElement elem = binder.unmarshal(parser.parse(new StringReader(xml)), Employee.class);
        Employee emp = (Employee)elem.getValue();
        emp.address.city = "Toronto";

        binder.updateXML(emp.address);

        JAXBXMLComparer comparer = new JAXBXMLComparer();
        assertTrue("Marshalled document does not match the control document.", comparer.isNodeEqual(controlDocument, ((Node)binder.getXMLNode(emp)).getOwnerDocument()));
    }
}

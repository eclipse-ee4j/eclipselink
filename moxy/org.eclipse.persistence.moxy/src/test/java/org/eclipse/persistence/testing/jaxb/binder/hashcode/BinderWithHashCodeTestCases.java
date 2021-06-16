/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.binder.hashcode;

import java.io.File;
import java.io.StringReader;

import jakarta.xml.bind.Binder;
import jakarta.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class BinderWithHashCodeTestCases extends TestCase {
    private XMLParser parser;

    public BinderWithHashCodeTestCases() {
        XMLPlatform platform = XMLPlatformFactory.getInstance().getXMLPlatform();
        parser = platform.newXMLParser();
    }

    public void testAbsentNode() throws Exception {
        String xml = "<employee><!-- Comment 1 --><name>Matt</name><age>32</age><!-- Comment 2 --><address>Kanata</address></employee>";
        String controlSource = "org/eclipse/persistence/testing/jaxb/binder/nullpolicy/absentnode.xml";
        Document controlDocument = parser.parse(Thread.currentThread().getContextClassLoader().getResource(controlSource));

        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{Employee.class}, null);

        Binder binder = ctx.createBinder();

        Employee emp = (Employee)binder.unmarshal(parser.parse(new StringReader(xml)));

        emp.setName(null);

        binder.updateXML(emp);

        JAXBXMLComparer comparer = new JAXBXMLComparer();
        assertTrue("Marshalled document does not match the control document.", comparer.isNodeEqual(controlDocument, ((Node)binder.getXMLNode(emp)).getOwnerDocument()));
    }
}

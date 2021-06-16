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
//     Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.binder.nullpolicy;

import java.io.File;
import java.io.StringReader;

import jakarta.xml.bind.Binder;
import jakarta.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class BinderWithNullPolicyCompositeTestCases extends TestCase {

    private XMLParser parser;

    public BinderWithNullPolicyCompositeTestCases() {
        XMLPlatform platform = XMLPlatformFactory.getInstance().getXMLPlatform();
        parser = platform.newXMLParser();
    }

    public void testEmptyNode() throws Exception {
        String xml = "<employee><!-- Comment 1 --><name>Matt</name><!-- Comment 2 --><address><street>123 Fake Street</street><city>Kanata</city></address><phone>123-4567</phone><phone>234-5678</phone></employee>";
        String controlSource = "org/eclipse/persistence/testing/jaxb/binder/nullpolicy/emptynodecomposite.xml";
        Document controlDocument = parser.parse(Thread.currentThread().getContextClassLoader().getResource(controlSource));

        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{EmployeeCompositeA.class}, null);

        Binder binder = ctx.createBinder();

        EmployeeCompositeA emp = (EmployeeCompositeA)binder.unmarshal(parser.parse(new StringReader(xml)));

        emp.address = null;

        emp.phone.add(1, null);

        binder.updateXML(emp);

        XMLTransformer transformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        transformer.transform(controlDocument, System.out);

        transformer.transform((Node)binder.getXMLNode(emp), System.out);

        JAXBXMLComparer comparer = new JAXBXMLComparer();
        assertTrue("Marshalled document does not match the control document.", comparer.isNodeEqual(controlDocument, ((Node)binder.getXMLNode(emp)).getOwnerDocument()));
    }
}

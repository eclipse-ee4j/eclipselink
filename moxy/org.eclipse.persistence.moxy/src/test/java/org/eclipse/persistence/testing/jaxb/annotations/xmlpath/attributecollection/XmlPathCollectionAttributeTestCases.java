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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.attributecollection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XmlPathCollectionAttributeTestCases extends JAXBTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/listattributes.xml";
    private static final String SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/listattributes.xsd";

    public XmlPathCollectionAttributeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setTypes(new Class[] { TestClass.class });
    }

    @Override
    protected Object getControlObject() {
        ArrayList<String> items = new ArrayList<String>();
        items.add("aaa");
        items.add("bbb");
        items.add("ccc");

        TestClass tc = new TestClass();
        tc.setItemList(items);
        tc.setAttributeList(items);
        tc.setElementList(items);

        return tc;
    }

    public void testUnmarshalFromNode() throws Exception {
        if (isUnmarshalTest()) {
            InputStream instream = ClassLoader
                    .getSystemResourceAsStream(resourceName);
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(instream);

            Object testObject = (TestClass) jaxbUnmarshaller.unmarshal(doc);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testUnmarshalFromNodeNSAware() throws Exception {
        if (isUnmarshalTest()) {
            InputStream instream = ClassLoader
                    .getSystemResourceAsStream(resourceName);
            Node node = parser.parse(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(node);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>(1);
        InputStream is = getClass().getClassLoader().getResourceAsStream(SCHEMA_RESOURCE);
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }

}

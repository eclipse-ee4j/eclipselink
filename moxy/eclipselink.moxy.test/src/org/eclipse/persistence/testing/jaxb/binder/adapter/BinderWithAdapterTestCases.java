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
// dmccann - June 4/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.binder.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import junit.framework.TestCase;

/**
 * Tests JAXBBinder marshal/unmarshal with an XmlJavaTypeAdapter.
 *
 */
public class BinderWithAdapterTestCases  extends TestCase {
    static XPath xpath;
    Binder<Node> binder;;
    DocumentBuilderFactory documentBuilderFactory;

    public BinderWithAdapterTestCases(String name) throws Exception {
        super(name);
        try {
            binder = JAXBContextFactory.createContext(new Class[] { Element.class }, null).createBinder();
        } catch (JAXBException x) {
            fail("Binder creation failed: " + x.getMessage());
        }
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        xpath = XPathFactory.newInstance().newXPath();
    }

    public void testUnmarshal() {
        try {
            String src = "org/eclipse/persistence/testing/jaxb/binder/adapter/element.xml";
            Document doc = documentBuilderFactory.newDocumentBuilder().parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(src));

            JAXBElement<Element> result = binder.unmarshal(doc, Element.class);

            Element returnedElt = result.getValue();
            Element controlElt = getControlObject();

            assertTrue("Unarshal operation failed:  expected element [" + controlElt + "] but was [" + returnedElt + "]", returnedElt.equals(controlElt));
        } catch (Exception ex) {
            fail("Unmarshal failed: " + ex.getMessage());
        }
    }

    public void testMarshal() {
        Element elt = new Element();
        elt.key = 0;
        elt.value = new TreeMap<Integer, String>();
        elt.value.put(101, "KTM");
        elt.value.put(69, "CBR");

        InputStream srcFileInputStream = null;

        try {
            String src = "org/eclipse/persistence/testing/jaxb/binder/adapter/element.xml";

            try {
                srcFileInputStream = new FileInputStream(new File(src));
            } catch (FileNotFoundException fnfe) {
                srcFileInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(src);
                if (null == srcFileInputStream) {
                    throw fnfe;
                }
            }

            Document controlDoc = documentBuilderFactory.newDocumentBuilder().parse(srcFileInputStream);

            Document doc = documentBuilderFactory.newDocumentBuilder().newDocument();
            binder.marshal(elt, doc);

            // for debugging - do not remove
            /*
            Transformer tx = TransformerFactory.newInstance().newTransformer();
            tx.transform(new DOMSource(doc), new StreamResult(System.out));
            System.out.println();
            Transformer tx1 = TransformerFactory.newInstance().newTransformer();
            tx1.transform(new DOMSource(controlDoc), new StreamResult(System.out));
            */

            JAXBXMLComparer comparer = new JAXBXMLComparer();
            assertTrue("Marshalled document does not match the control document.", comparer.isNodeEqual(controlDoc, doc));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (null != srcFileInputStream) {
                try {
                    srcFileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Used for unmarshal test verification
     */
    Element getControlObject() {
        Element elt = new Element();
        elt.key = 0;

        TreeMap<Integer, String> tMap = new TreeMap<Integer, String>();
        tMap.put(101, "KTM");
        tMap.put(69, "CBR");
        elt.value = tMap;
        return elt;
    }
}

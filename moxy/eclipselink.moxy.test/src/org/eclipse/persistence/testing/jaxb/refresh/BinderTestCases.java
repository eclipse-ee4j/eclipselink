/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.refresh;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import junit.framework.TestCase;

public class BinderTestCases extends TestCase {

    private static final String XML_METADATA = "org/eclipse/persistence/testing/jaxb/refresh/metadata.xml";
    private static final String XML_RESOURCE_BEFORE = "org/eclipse/persistence/testing/jaxb/refresh/before.xml";
    private static final String XML_RESOURCE_AFTER = "org/eclipse/persistence/testing/jaxb/refresh/after.xml";

    public BinderTestCases(String name) {
        super(name);
    }

    public void testRefresh() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputStream metadataStream = classLoader.getResourceAsStream(XML_METADATA);
        Document metadataDocument = db.parse(metadataStream);
        metadataStream.close();

        Map<String, Object> props = new HashMap<String, Object>(1);
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataDocument);
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {Root.class}, props);
        Binder<Node> binder = jc.createBinder();

        Root controlRoot = new Root();
        controlRoot.setName("R");

        InputStream xmlBeforeStream = classLoader.getResourceAsStream(XML_RESOURCE_BEFORE);
        Document xmlBeforeDocument = db.parse(xmlBeforeStream);
        xmlBeforeStream.close();

        Root rootBefore = (Root) binder.unmarshal(xmlBeforeDocument);
        assertEquals(controlRoot, rootBefore);

        Element xmlElementElement = (Element) metadataDocument.getElementsByTagNameNS("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-element").item(0);
        xmlElementElement.setAttribute("name", "after-name");
        JAXBHelper.getJAXBContext(jc).refreshMetadata();

        Root rootAfter = (Root) binder.unmarshal(xmlBeforeDocument);
        assertEquals(controlRoot, rootAfter);

        InputStream xmlAfterStream = classLoader.getResourceAsStream(XML_RESOURCE_AFTER);
        Document xmlAfterDocument = db.parse(xmlAfterStream);
        xmlAfterStream.close();

        Binder<Node> newBinder = jc.createBinder();
        Root rootNewBinder = (Root) newBinder.unmarshal(xmlAfterDocument);
        assertEquals(controlRoot, rootNewBinder);
    }

}
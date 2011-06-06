/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

public class RefreshTestCases extends TestCase {

    private static final String XML_METADATA = "org/eclipse/persistence/testing/jaxb/refresh/metadata.xml";
    private static final String XML_RESOURCE_BEFORE = "org/eclipse/persistence/testing/jaxb/refresh/before.xml";
    private static final String XML_RESOURCE_AFTER = "org/eclipse/persistence/testing/jaxb/refresh/after.xml";

    public RefreshTestCases(String name) {
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
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        Root controlRoot = new Root();
        controlRoot.setName("R");

        InputStream xmlBeforeStream = classLoader.getResourceAsStream(XML_RESOURCE_BEFORE);
        Root rootBefore = (Root) unmarshaller.unmarshal(xmlBeforeStream);
        assertEquals(controlRoot, rootBefore);

        Element xmlElementElement = (Element) metadataDocument.getElementsByTagNameNS("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-element").item(0);
        xmlElementElement.setAttribute("name", "after-name");
        JAXBHelper.getJAXBContext(jc).refeshMetadata();

        InputStream xmlAfterStream = classLoader.getResourceAsStream(XML_RESOURCE_AFTER);
        Root rootAfter = (Root) unmarshaller.unmarshal(xmlAfterStream);
        assertEquals(controlRoot, rootAfter);
    }

}
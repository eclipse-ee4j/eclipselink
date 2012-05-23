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
 *     Rick Barkhouse - 2.3.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.namespace;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.oxm.XMLTestCase;
import org.w3c.dom.Document;

/**
 * <p>Tests general XML Namespace operations when using EclipseLink XML Bindings.</p>
 */
public class NamespaceTestCases extends XMLTestCase {

    private static final String BINDINGS1 = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/namespace/xml-bindings.xml";
    private static final String INSTANCE1 = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/namespace/instance.xml";
    private static final String BINDINGS2 = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/namespace/bindings-rootelement.xml";
    private static final String INSTANCE2 = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/namespace/instance2.xml";

    public NamespaceTestCases(String name) throws Exception {
        super(name);
    }

    public String getName() {
        return "ExternalizedMetadata - Namespace: " + super.getName();
    }

    private Object getControlObject() {
        Customer c = new Customer();
        c.id = 6;
        c.name = "Bob";
        c.account = "32847";

        return c;
    }

    /**
     * <p>Tests that basic namespace information is being interpreted at all three possible levels,
     * when bootstrapped from XML Bindings:</p>
     *
     * <ul>
     *  <li>Package-level (defined in &lt;xml-schema&gt;)</li>
     *  <li>Type-level (defined in &lt;xml-type&gt;)</li>
     *  <li>Property-level (defined in &lt;xml-element&gt;)</li>
     * </ul>
     */
    public void testBasicNamespaces() throws Exception {
        InputStream bindings = ClassLoader.getSystemResourceAsStream(BINDINGS1);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindings);

        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{ Customer.class }, properties);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();

        // Marshal control doc to Document
        Marshaller m = ctx.createMarshaller();
        Document marshalDoc = parser.newDocument();
        m.marshal(getControlObject(), marshalDoc);

        // Parse instance doc to Document
        InputStream iStream = ClassLoader.getSystemResourceAsStream(INSTANCE1);
        Document unmarshalDoc = parser.parse(iStream);

        // Compare
        assertXMLIdentical(unmarshalDoc, marshalDoc);
    }

    /**
     * Test that root elements that are in a non-default namespace are prefixed correctly.
     */
    public void testNonDefaultRootElement() throws Exception {
        InputStream bindings = ClassLoader.getSystemResourceAsStream(BINDINGS2);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindings);

        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{ Customer.class }, properties);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();

        Marshaller m = ctx.createMarshaller();
        Document marshalDoc = parser.newDocument();
        m.marshal(getControlObject(), marshalDoc);

        InputStream iStream = ClassLoader.getSystemResourceAsStream(INSTANCE2);
        Document unmarshalDoc = parser.parse(iStream);

        assertXMLIdentical(unmarshalDoc, marshalDoc);
    }

}
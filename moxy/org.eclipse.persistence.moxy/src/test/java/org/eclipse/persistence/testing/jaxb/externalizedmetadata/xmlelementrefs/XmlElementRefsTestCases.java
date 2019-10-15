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
// dmccann - December 04/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs.collectiontype.Root;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;

/**
 * Tests XmlElementRefs via eclipselink-oxm.xml
 */
public class XmlElementRefsTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementrefs/";

    /**
     * This is the preferred (and only) constructor.
     */
    public XmlElementRefsTestCases(String name) {
        super(name);
    }

    /**
     * Tests @XmlElementRefs schema generation via eclipselink-oxm.xml.
     *
     * Positive test.
     */
    public void testXmlElementRefsSchemaGen() throws URISyntaxException {
        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
        generateSchemaWithFileName(new Class[] { Foos.class, ObjectFactory.class }, CONTEXT_PATH, PATH + "eclipselink-oxm.xml", 2, outputResolver);
        // validate schema
        URI controlSchema = Thread.currentThread().getContextClassLoader().getResource(PATH + "schema.xsd").toURI();
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE).toString(), new File(controlSchema));
    }

    /**
     * Tests @XmlElementRefs via eclipselink-oxm.xml.
     *
     * Positive test.
     * @throws JAXBException
     */
    public void testXmlElementRefs() throws JAXBException {
        // load XML metadata
        generateSchema(new Class[] { Foos.class, ObjectFactory.class }, CONTEXT_PATH, PATH, 2);

        // load instance doc
        String src = PATH + "foos.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // unmarshal
        Object obj = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            obj = unmarshaller.unmarshal(iDocStream);
            assertFalse("Unmarshalled object is null.", obj == null);
            assertTrue("Unmarshalled object was expected to be [Foos] but was [" + obj.getClass().getName() + "]", obj instanceof Foos);
            assertNotNull("Unmarshalled object [Foos] has no 'items' set", ((Foos) obj).items);
            assertNotNull("Unmarshalled object [Foos] has no 'stuff' set", ((Foos) obj).stuff);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }

        // load write instance doc
        src = PATH + "foos-write.xml";
        iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        // marshal
        Marshaller marshaller = jaxbContext.createMarshaller();
        try {
            marshaller.marshal(obj, testDoc);
            OXTestCase.removeEmptyTextNodes(testDoc);
            //marshaller.marshal(obj, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
            assertTrue("Method accessor was not called as expected.", ((Foos) obj).accessedViaMethod);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Verify that the container type is set correctly for Array types.
     *
     * Positive test.
     */
    public void testCollectionType() {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new File(Thread.currentThread().getContextClassLoader().getResource(PATH + "collectiontype/oxm.xml").toURI()));
            JAXBContextFactory.createContext(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs.collectiontype.ObjectFactory.class, Root.class }, properties);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception was thrown while attempting to create the JAXBContext: " + e.getMessage());
        }
    }
}

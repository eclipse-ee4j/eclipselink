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
 * dmccann - December 04/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs.collectiontype.Root;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;

/**
 * Tests XmlElementRefs via eclipselink-oxm.xml
 *
 */
public class XmlElementRefsTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementrefs/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlElementRefsTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests @XmlElementRefs schema generation via eclipselink-oxm.xml.  
     * 
     * Positive test.
     */
    public void testXmlElementRefsSchemaGen() {
        MySchemaOutputResolver outputResolver = generateSchema(CONTEXT_PATH, PATH, 2);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlElementRefs via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlElementRefs() {
        // load XML metadata
    	MySchemaOutputResolver outputResolver = generateSchema(new Class[] { Foos.class, ObjectFactory.class }, CONTEXT_PATH, PATH, 2);
          
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
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new File(PATH + "collectiontype/oxm.xml"));
            javax.xml.bind.JAXBContext jCtx = JAXBContextFactory.createContext(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs.collectiontype.ObjectFactory.class, Root.class }, properties);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception was thrown while attempting to create the JAXBContext: " + e.getMessage());
        }
    }
}

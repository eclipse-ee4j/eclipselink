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
 * dmccann - December 03/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlElementRef via eclipselink-oxm.xml
 *
 */
public class XmlElementRefTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementref/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlElementRefTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests @XmlElementRef schema generation via eclipselink-oxm.xml.  
     * 
     * Positive test.
     */
    public void testXmlElementRefSchemaGen() {
        MySchemaOutputResolver outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlElementRef schema generation via eclipselink-oxm.xml.  Here, an
     * xml-element-wrapper is also used.
     * 
     * Positive test.
     */
    public void testXmlElementRefWithWrapperSchemaGen() {
        String metadataFile = PATH + "eclipselink-oxm-wrapper.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(new Class[] { Foos.class, Bar.class }, CONTEXT_PATH, metadataFile, 1);
        
        // validate schema
        String controlSchema = PATH + "schema_wrapper.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlElementRef via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testXmlElementRef() {
        // load XML metadata
    	
    	Class[] classesToProcess = new Class[] { Foo.class };
        MySchemaOutputResolver outputResolver = generateSchema(classesToProcess, CONTEXT_PATH , PATH, 1);
    	        
        // load instance doc
        String src = PATH + "foo.xml";
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
            assertTrue("Unmarshalled object was expected to be [Foo] but was [" + obj.getClass().getName() + "]", obj instanceof Foo);
            Foo f = (Foo) obj;
            assertNull("Item is non-null after unmarshal but is set write-only.", f.item);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
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
        // set item manually, as it is write-only and was not set during unmarshal
        Bar b = new Bar();
        b.id = 66;
        Foo f = (Foo) obj;
        f.item = b;
        try {
            //marshaller.marshal(obj, System.out);
            marshaller.marshal(obj, testDoc);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
            assertTrue("Method accessor was not called as expected.", ((Foo) obj).accessedViaMethod);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Tests @XmlElementRef via eclipselink-oxm.xml.  Here an xml-element-wrapper
     * is also used.
     * 
     * Positive test.
     */
    public void testXmlElementRefWithWrapper() {
        String metadataFile = PATH + "eclipselink-oxm-wrapper.xml";
        MySchemaOutputResolver outputResolver = generateSchemaWithFileName(new Class[] { Foos.class, Bar.class }, CONTEXT_PATH, metadataFile, 1);

        // make sure container-type was processed correctly
        XMLDescriptor xDesc = jaxbContext.getXMLContext().getDescriptor(new QName("foos"));
        assertNotNull("No descriptor was generated for Foos.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("items");
        assertNotNull("No mapping exists on Foos for attribute [items].", mapping);
        assertTrue("Expected an XMLChoiceCollectionMapping for attribute [items], but was [" + mapping.toString() +"].", mapping instanceof XMLChoiceCollectionMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLChoiceCollectionMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLChoiceCollectionMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
        
        // load instance doc
        String src = PATH + "foo-wrapper.xml";
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
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
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
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
}

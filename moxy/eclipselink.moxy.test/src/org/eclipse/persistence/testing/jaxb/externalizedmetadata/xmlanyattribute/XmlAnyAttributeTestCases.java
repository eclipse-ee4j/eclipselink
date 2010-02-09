/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - October 26/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyattribute;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests XmlAnyElement via eclipselink-oxm.xml
 *
 */
public class XmlAnyAttributeTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyattribute";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyattribute/";
    public static final String RETURN_STRING = "Giggity";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAnyAttributeTestCases(String name) {
        super(name);
        outputResolver = new MySchemaOutputResolver();
    }

    private void doSchemaGeneration() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            // validate schema
            String controlSchema = PATH + "schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
            shouldGenerateSchema = false;
        }
    }
    
    /**
     * Tests @XmlAnyAttribute override via eclipselink-oxm.xml.  This test will also
     * verify that an @XmlAnyAttribute in code can be overridden with 
     * xml-any-element in XML, while a second property can be set as an 
     * xml-any-attribute in XML.  Two xml-any-attributes would cause an
     * exception if the overrides don't work properly.   
     * 
     * Positive test.
     */
    public void testXmlAnyAttribute() {
        doSchemaGeneration();
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests @XmlAnyElement via eclipselink-oxm.xml.  Here, a number of
     * overrides are performed. An unmarshal operation is performed, 
     * then a marshal operation is performed to ensure the Any is 
     * processed correctly.
     * 
     * Positive test.
     *
    public void testXmlAnyElementUnmarshalThenMarshal() {
        doSchemaGeneration();

        // test unmarshal
        Employee emp = null;
        Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
        try {
            String src = PATH + "employee.xml";
            emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }

        assertTrue("The Employee did not umnmarshal properly: expected 'a' to be [1] but was [" + emp.a + "]", emp.a == 1);
        assertTrue("The Employee did not umnmarshal properly: expected 'b' to be [\"3\"] but was [\"" + emp.b + "\"]", emp.b.equals("3"));
        assertNotNull("The Employee did not umnmarshal properly: 'stuff' is null.", emp.stuff);
        assertTrue("The Employee did not umnmarshal properly: expected size of 'stuff' to be [1] but was [" + emp.stuff.size() + "]", emp.stuff.size() == 1);
        assertNotNull("The Employee did not umnmarshal properly: 'stuff.0' is null.", emp.stuff.get(0));
        assertTrue("The Employee did not umnmarshal properly: expected 'stuff.0' to be instance of [Element] but was [" + emp.stuff.get(0) + "]", emp.stuff.get(0) instanceof Element);
        
        Document testDoc = parser.newDocument();
        
        // test marshal
        Marshaller marshaller = getJAXBContext().createMarshaller();
        try {
            marshaller.marshal(emp, testDoc);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred marshalling the Employee.");
        }

        String src = PATH + "employee.xml";
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        assertTrue("The Employee did not marshal correctly - document comparison failed: ", compareDocuments(ctrlDoc, testDoc));
    }
        
    /**
     * Tests lax set to true.  There is a sub-employee element which should be
     * unmarshalled to an Employee, since lax is true and the Employee is 
     * known, i.e. can be eagerly marshalled to.
     * 
     * Positive test.
     *
    public void testLaxTrue() {
        String metadataFile = PATH + "eclipselink-oxm-lax.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Employee.class }, properties);
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("JAXBContext creation failed");
        }

        // test unmarshal
        Employee emp = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            String src = PATH + "employee-with-employee.xml";
            emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }

        assertNotNull("The Employee did not umnmarshal properly: 'stuff.0' is null.", emp.stuff.get(0));
        assertTrue("The Employee did not umnmarshal properly: expected 'stuff.0' to be an [Employee] but was [" + emp.stuff.get(0) + "]", emp.stuff.get(0) instanceof Employee);
    }

    /**
     * Tests lax set to false.  There is a sub-employee element which should be
     * unmarshalled to an Element, since lax is false.
     * 
     * Positive test.
     *
    public void testLaxFalse() {
        String metadataFile = PATH + "eclipselink-oxm.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Employee.class }, properties);
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("JAXBContext creation failed");
        }

        // test unmarshal
        Employee emp = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            String src = PATH + "employee-with-employee.xml";
            emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }

        assertNotNull("The Employee did not umnmarshal properly: 'stuff.0' is null.", emp.stuff.get(0));
        assertTrue("The Employee did not umnmarshal properly: expected 'stuff.0' to be an [Element] but was [" + emp.stuff.get(0) + "]", emp.stuff.get(0) instanceof Element);
    }

    public void xtestXmlAdapter() {
        try {
            javax.xml.bind.JAXBContext jCtx = javax.xml.bind.JAXBContext.newInstance(new Class[] {Employee.class});
            //JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Employee.class }, null);
            Employee emp = null;
            Unmarshaller unmarshaller = jCtx.createUnmarshaller();
            try {
                String src = PATH + "employee.xml";
                emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
                assertNotNull("The Employee object is null after unmarshal.", emp);
            } catch (Exception e) {
                e.printStackTrace();
                fail("An unexpected exception occurred unmarshalling the document.");
            }
            
            // test marshal
            Marshaller marshaller = jCtx.createMarshaller();
            try {
                marshaller.marshal(emp, System.out);
            } catch (JAXBException e) {
                e.printStackTrace();
                fail("An unexpected exception occurred marshalling the Employee.");
            }
             
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }*/
}

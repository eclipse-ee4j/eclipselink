/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.util.HashMap;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlAnyElement via eclipselink-oxm.xml
 *
 */
public class XmlAnyAttributeTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyattribute";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyattribute/";
    public static final String RETURN_STRING = "Giggity";
    JAXBContext jCtx;
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAnyAttributeTestCases(String name) {
        super(name);
    }
    
    /**
     * The JAXBContext to be used for these tests is created here.
     * 
     */
    public void setUp() throws Exception {
        super.setUp();
        try {
            jCtx = createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "eclipselink-oxm.xml");
        } catch (JAXBException e) {
            //e.printStackTrace();
            fail("JAXBContext creation failed unexpectedly.");
        }
    }

    /**
     * Tests schema generation for @XmlAnyAttribute override via eclipselink-oxm.xml.
     * This test will also verify that an @XmlAnyAttribute in code can be overridden
     * with xml-element in XML, while a second property can be set as an
     * xml-any-attribute in XML.  Two xml-any-attributes would cause an exception if 
     * the overrides don't work properly.   
     * 
     * Positive test.
     */
    public void testXmlAnyAttributeSchemaGen() {
        // generate schema
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver(); 
        jCtx.generateSchema(outputResolver);
        
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests @XmlAnyAttribute via eclipselink-oxm.xml.  Here, a number of
     * overrides are performed. An unmarshal operation is performed, 
     * then a marshal operation is performed to ensure the Any is 
     * processed correctly.
     * 
     * Positive test.
     */
    public void testXmlAnyAttributeUnmarshalThenMarshal() {
        // setup control Employee
        Employee ctrlEmp = new Employee();
        ctrlEmp.a = 1;
        ctrlEmp.b = "3";
        HashMap<QName, Object> someStuff = new HashMap<QName, Object>();
        someStuff.put(new QName("www.example.com", "stuff"), "blah");
        ctrlEmp.stuff = someStuff;

        // test unmarshal
        Employee emp = null;
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            String src = PATH + "employee.xml";
            emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
            assertTrue("The unmarshalled Employee does not match the control Employee", ctrlEmp.equals(emp));
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }
        
        String src = PATH + "employee.xml";
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        Document testDoc = parser.newDocument();
        
        // test marshal
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            marshaller.marshal(ctrlEmp, testDoc);
            assertTrue("The Employee did not marshal correctly - document comparison failed: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred marshalling the Employee.");
        }
    }

    /**
     * Test setting the map class via container-type attribute.
     * 
     * Positive test.
     */
    public void testContainerType() {
        XMLDescriptor xDesc = jCtx.getXMLContext().getDescriptor(new QName("employee"));
        assertNotNull("No descriptor was generated for Employee.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("stuff");
        assertNotNull("No mapping exists on Employee for attribute [stuff].", mapping);
        assertTrue("Expected an XMLAnyAttributeMapping for attribute [stuff], but was [" + mapping.toString() +"].", mapping instanceof XMLAnyAttributeMapping);
        assertTrue("Expected map class [java.util.LinkedHashMap] but was ["+((XMLAnyAttributeMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLAnyAttributeMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedHashMap"));
    }
}

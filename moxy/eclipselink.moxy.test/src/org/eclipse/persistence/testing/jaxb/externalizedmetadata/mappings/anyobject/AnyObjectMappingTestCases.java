/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - March 24/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyobject;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.MyDomAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests XmlAnyObjectMapping via eclipselink-oxm.xml
 *
 */
public class AnyObjectMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyobject";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyobject/";
    
    private static final String STUFF = "Some Stuff";
    private static final String STUFF_NS = "http://www.example.com/stuff";
    private static final String OTHER = "ns0:other";
    private static final String OTHER_NS = "http://www.example.com/other";

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public AnyObjectMappingTestCases(String name) {
        super(name);
    }

    /**
     * Create the control Employee.
     */
    private Employee getControlObject() {
        Employee ctrlEmp = new Employee();

        Element elt = null;
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            elt = doc.createElementNS(OTHER_NS, OTHER);
            elt.appendChild(doc.createTextNode(STUFF));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        
        ctrlEmp.stuff = elt;
        return ctrlEmp;
    }

    /**
     * 
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tests schema generation and instance document validation for XmlAnyObjectMapping
     * via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testSchemaGenAndValidation() {
        // generate employee & stuff schemas
        MySchemaOutputResolver resolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "employee-oxm.xml", 2);
        // validate employee schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "employee.xsd"));
        // validate stuff schema
        compareSchemas(resolver.schemaFiles.get(STUFF_NS), new File(PATH + "stuff.xsd"));
        // validate employee.xml
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);

        // generate read only employee schema
        resolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "read-only-employee-oxm.xml", 1);
        // validate read only employee schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "read-only-employee.xsd"));
        // validate read-only-employee.xml
        src = PATH + "read-only-employee.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (read-only-employee.xml) failed unxepectedly: " + result, result == null);
        
        // THIS TEST CAN BE ENABLED WHEN BUG 313568 IS RESOLVED
        // validate marshal-read-only-employee.xml
        src = PATH + "marshal-read-only-employee.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (marshal-read-only-employee.xml) failed unxepectedly: " + result, result == null);

        // generate write only employee schema
        resolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "write-only-employee-oxm.xml", 1);
        // validate write only employee schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "write-only-employee.xsd"));
        // validate write-only-employee.xml
        src = PATH + "write-only-employee.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (write-only-employee.xml) failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests XmlAnyObjectMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  
     * 
     * Positive test.
     */
    public void testAnyObjectUnmarshal() {
        // load instance doc
        String src = PATH + "employee.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "employee-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }
        
        // unmarshal
        Employee ctrlEmp = getControlObject();
        
        Employee empObj = null;
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            empObj = (Employee) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Accessor method was not called as expected", empObj.wasSetCalled);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
    
    /**
     * Tests XmlAnyObjectMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.  
     * 
     * Positive test.
     */
    public void testAnyObjectMarshal() {
        // setup control document
        String src = PATH + "employee.xml";
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "employee-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }
        
        // test marshal
        Employee ctrlEmp = getControlObject();
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            marshaller.marshal(ctrlEmp, testDoc);
            //marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Accessor method was not called as expected", ctrlEmp.wasGetCalled);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
    
    /**
     * Tests XmlAnyObjectMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  In this case, the 'any'
     * is marked read-only.
     * 
     * Positive test.
     */
    public void testAnyObjectReadOnlyUnmarshal() {
        // load instance doc
        String src = PATH + "read-only-employee.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "read-only-employee-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }
        
        // unmarshal
        Employee ctrlEmp = getControlObject();
        Employee eObj = null;
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            eObj = (Employee) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", eObj);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(eObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Tests XmlAnyObjectMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.    In this case, the 'any'
     * is marked read-only, and should not be written out.
     * 
     * Positive test.
     */
    public void testAnyObjectReadOnlyMarshal() {
        // setup control document
        String src = PATH + "marshal-read-only-employee.xml";
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "read-only-employee-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }
        
        // test marshal
        Employee ctrlEmp = getControlObject();
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            marshaller.marshal(ctrlEmp, testDoc);
            //marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
    
    /**
     * Tests XmlAnyObjectMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  In this case, the 'any'
     * is marked write-only, and should not be read in.
     * 
     * Positive test.
     */
    public void testAnyObjectWriteOnlyUnmarshal() {
        // load instance doc
        String src = PATH + "write-only-employee.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "write-only-employee-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }

        // unmarshal
        Employee ctrlEmp = getControlObject();
        ctrlEmp.stuff = null;
        Employee eObj = null;
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            eObj = (Employee) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", eObj);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(eObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
    
    /**
     * Tests XmlAnyObjectMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.    In this case, the 'any'
     * is marked write-only.
     * 
     * Positive test.
     */
    public void testAnyObjectWriteOnlyMarshal() {
        // setup control document
        String src = PATH + "write-only-employee.xml";
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "write-only-employee-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }
        
        // test marshal
        Employee ctrlEmp = getControlObject();
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            marshaller.marshal(ctrlEmp, testDoc);
            //marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
}
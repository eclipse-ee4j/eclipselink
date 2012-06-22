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
 * dmccann - March 25/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlAnyAttributeMapping via eclipselink-oxm.xml
 *
 */
public class AnyAttributeMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anyattribute/";
    
    private static final String FNAME = "Joe";
    private static final String LNAME = "Oracle";
    private static final String FIRST_NAME = "first-name";
    private static final String LAST_NAME = "last-name";
    private static final String STUFF_NS = "http://www.example.com/stuff";
    private static final String OTHER_NS = "http://www.example.com/other";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public AnyAttributeMappingTestCases(String name) {
        super(name);
    }

    /**
     * Create the control Employee.
     */
    private Employee getControlObject() {
        Employee ctrlEmp = new Employee();
        HashMap stuff = new HashMap();
        QName qname = new QName(OTHER_NS, FIRST_NAME);
        stuff.put(qname, FNAME);
        qname = new QName(OTHER_NS, LAST_NAME);
        stuff.put(qname, LNAME);
        ctrlEmp.stuff = stuff;
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
     * Tests schema generation for XmlAnyAttributeMapping via eclipselink-oxm.xml.
     * Generated schemas and instance documents are validated as well.
     * 
     * Positive test.
     */
    public void testSchemaGenAndValidation() {
        // generate employee & stuff schemas
        MyStreamSchemaOutputResolver resolver = new MyStreamSchemaOutputResolver();
        generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "employee-oxm.xml", 2, resolver);
        // validate employee schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE).toString(), new File(PATH + "employee.xsd"));
        // validate stuff schema
        compareSchemas(resolver.schemaFiles.get(STUFF_NS).toString(), new File(PATH + "stuff.xsd"));
        // validate employee.xml
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
        
        // generate read only employee schema
        resolver = new MyStreamSchemaOutputResolver();
        generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "read-only-employee-oxm.xml", 1, resolver);
        // validate read only employee schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE).toString(), new File(PATH + "read-only-employee.xsd"));
        // validate read-only-employee.xml
        src = PATH + "read-only-employee.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (read-only-employee.xml) failed unxepectedly: " + result, result == null);

        // THIS TEST CAN BE ENABLED WHEN BUG 313568 IS RESOLVED
        // validate marshal-read-only-employee.xml
        src = PATH + "marshal-read-only-employee.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (marshal-read-only-employee.xml) failed unxepectedly: " + result, result == null);
        // NOTE THAT marshal-read-only-employee.xml NEEDS TO BE CHANGED AS WELL - PLEASE SEE THAT FILE FOR INFO

        // generate write only employee schema
        resolver = new MyStreamSchemaOutputResolver();
        generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "write-only-employee-oxm.xml", 1, resolver);
        // validate write only employee schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE).toString(), new File(PATH + "write-only-employee.xsd"));
        // validate write-only-employee.xml
        src = PATH + "write-only-employee.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (write-only-employee.xml) failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests XmlAnyAttributeMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  
     * 
     * Positive test.
     */
    public void testAnyAttributeUnmarshal() {
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
     * Tests XmlAnyAttributeMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.  
     * 
     * Positive test.
     */
    public void testAnyAttributeMarshal() {
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
     * Tests XmlAnyAttributeMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  In this case, the 'any'
     * is marked read-only.
     * 
     * Positive test.
     */
    public void testAnyAttributeReadOnlyUnmarshal() {
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
     * Tests XmlAnyAttributeMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.    In this case, the 'any'
     * is marked read-only, and should not be written out.
     * 
     * Positive test.
     */
    public void testAnyAttributeReadOnlyMarshal() {
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
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
    
    /**
     * Tests XmlAnyAttributeMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  In this case, the 'any'
     * is marked write-only, and should not be read in.
     * 
     * Positive test.
     */
    public void testAnyAttributeWriteOnlyUnmarshal() {
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
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
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
    public void testAnyAttributeWriteOnlyMarshal() {
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
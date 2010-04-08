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
 * dmccann - March 25/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anycollection;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlAnyCollectionMapping via eclipselink-oxm.xml
 *
 */
public class AnyCollectionMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anycollection";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anycollection/";
    
    private MySchemaOutputResolver employeeResolver;

    private static final String STUFF = "Some Stuff";
    private static final String STUFF_NS = "http://www.example.com/stuff";
    private static final int ID = 101;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public AnyCollectionMappingTestCases(String name) {
        super(name);
    }

    /**
     * Create the control Employee.
     */
    private Employee getControlObject() {
        List<Object> stuff = new ArrayList<Object>();
        stuff.add(STUFF);
        
        Employee ctrlEmp = new Employee();
        ctrlEmp.stuff = stuff;
        return ctrlEmp;
    }
    
    /**
     * This method's primary purpose id to generate schema(s). Validation of
     * generated schemas will occur in the testXXXGen method(s) below. Note that
     * the JAXBContext is created from this call and is required for
     * marshal/unmarshal, etc. tests.
     * 
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        employeeResolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "employee-oxm.xml", 2);
    }

    /**
     * Tests schema generation for XmlAnyCollectionMapping via eclipselink-oxm.xml.
     * Utilizes xml-attribute and xml-element. xml-value is tested separately
     * below.
     * 
     * Positive test.
     */
    public void testAnyCollectionSchemaGen() {
        // validate employee schema
        compareSchemas(employeeResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "employee.xsd"));
        // validate stuff schema
        compareSchemas(employeeResolver.schemaFiles.get(STUFF_NS), new File(PATH + "stuff.xsd"));
    }

    /**
     * Tests XmlAnyCollectionMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  
     * 
     * Positive test.
     */
    public void testAnyCollectionUnmarshal() {
        // load instance doc
        String src = PATH + "employee.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // unmarshal
        Employee ctrlEmp = getControlObject();
        Employee empObj = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
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
     * Tests XmlAnyCollectionMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.  
     * 
     * Positive test.
     */
    public void testAnyCollectionMarshal() {
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

        // test marshal
        Employee ctrlEmp = getControlObject();
        Marshaller marshaller = jaxbContext.createMarshaller();
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
     * Tests XmlAnyCollectionMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  In this case, the 'any'
     * is marked read-only.
     * 
     * Positive test.
     */
    public void testAnyCollectionReadOnlyUnmarshal() {
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
     * Tests XmlAnyCollectionMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.    In this case, the 'any'
     * is marked read-only, and should not be written out.
     * 
     * Positive test.
     */
    public void testAnyCollectionReadOnlyMarshal() {
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
    public void testAnyCollectionWriteOnlyUnmarshal() {
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
    public void testAnyCollectionWriteOnlyMarshal() {
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
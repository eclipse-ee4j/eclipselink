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
 * dmccann - March 24/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.collectionreference;

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
 * Tests XmlCollectionReferenceMapping via eclipselink-oxm.xml
 *
 */
public class CollectionReferenceMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.collectionreference";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/collectionreference/";
    
    private MySchemaOutputResolver employeeResolver;

    private static final String ADD_ID1 = "100";
    private static final String ADD_ID2 = "101";
    private static final String ADD_ID3 = "102";

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public CollectionReferenceMappingTestCases(String name) {
        super(name);
    }

    /**
     * Create the control Root.
     */
    private Root getControlObject() {
        Root root = new Root();
        List<Employee> emps = new ArrayList<Employee>();
        List<Address> adds = new ArrayList<Address>();
        List<Address> workAddresses = new ArrayList<Address>();

        Address wAddress1 = new Address();
        wAddress1.id = ADD_ID1;
        adds.add(wAddress1);
        workAddresses.add(wAddress1);
        
        Address wAddress2 = new Address();
        wAddress2.id = ADD_ID2;
        adds.add(wAddress2);
        workAddresses.add(wAddress2);
        
        Address wAddress3 = new Address();
        wAddress3.id = ADD_ID3;
        adds.add(wAddress3);
        
        Employee ctrlEmp = new Employee();
        ctrlEmp.workAddresses = workAddresses;
        emps.add(ctrlEmp);

        root.addresses = adds;
        root.employees = emps;
        
        return root;
    }
    
    /**
     * Create the control write-only Root.
     */
    private Root getWriteOnlyControlObject() {
        Root root = new Root();
        List<Employee> emps = new ArrayList<Employee>();
        List<Address> adds = new ArrayList<Address>();

        Address wAddress1 = new Address();
        wAddress1.id = ADD_ID1;
        adds.add(wAddress1);
        
        Address wAddress2 = new Address();
        wAddress2.id = ADD_ID2;
        adds.add(wAddress2);
        
        Address wAddress3 = new Address();
        wAddress3.id = ADD_ID3;
        adds.add(wAddress3);
        
        Employee ctrlEmp = new Employee();
        ctrlEmp.workAddresses = null;
        emps.add(ctrlEmp);

        root.addresses = adds;
        root.employees = emps;
        
        return root;
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
        employeeResolver = generateSchemaWithFileName(new Class[] { Root.class }, CONTEXT_PATH, PATH + "root-oxm.xml", 1);
    }

    /**
     * Tests schema generation for XmlCollectionReferenceMapping via eclipselink-oxm.xml.
     * Utilizes xml-attribute and xml-element. xml-value is tested separately
     * below.
     * 
     * Positive test.
     */
    public void testCollectionReferenceSchemaGen() {
        // validate employee schema
        compareSchemas(employeeResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "root.xsd"));
    }

    /**
     * Tests XmlObjectCollectionMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  
     * 
     * Positive test.
     */
    public void testCollectionReferenceUnmarshal() {
        // load instance doc
        String src = PATH + "root.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // unmarshal
        Root ctrlObj = getControlObject();
        Root root = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            root = (Root) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", root);
            assertTrue("Unmarshal failed:  Root objects are not equal", ctrlObj.equals(root));
            assertTrue("Accessor method was not called as expected", root.employees.get(0).wasSetCalled);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
    
    /**
     * Tests XmlCollectionReferenceMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.  
     * 
     * Positive test.
     */
    public void testCollectionReferenceMarshal() {
        // setup control document
        String src = PATH + "root.xml";
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        // test marshal
        Root ctrlObj = getControlObject();
        Marshaller marshaller = jaxbContext.createMarshaller();
        try {
            marshaller.marshal(ctrlObj, testDoc);
            //marshaller.marshal(ctrlObj, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
            assertTrue("Accessor method was not called as expected", ctrlObj.employees.get(0).wasGetCalled);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }

    /**
     * Tests XmlObjectCollectionMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed. Here the source is set as 
     * read-only. 
     * 
     * Positive test.
     */
    public void testCollectionReferenceReadOnlyUnmarshal() {
        // load instance doc
        String src = PATH + "root.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Root.class }, CONTEXT_PATH, PATH + "read-only-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }

        // unmarshal
        Root ctrlObj = getControlObject();
        Root root = null;
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            root = (Root) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", root);
            assertTrue("Unmarshal failed:  Root objects are not equal", ctrlObj.equals(root));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
    
    /**
     * Tests XmlCollectionReferenceMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.  Here the source is set as 
     * read-only, so the addresses should not be written out.
     * 
     * Positive test.
     *
     * NOTE:  this test can be enabled when bug# 308390 is fixed
     * 
     */
    public void testCollectionReferenceReadOnlyMarshal() {
        // setup control document
        String src = PATH + "marshal-read-only.xml";
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
            jCtx = createContext(new Class[] { Root.class }, CONTEXT_PATH, PATH + "read-only-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }

        // test marshal
        Root ctrlObj = getControlObject();
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            marshaller.marshal(ctrlObj, testDoc);
            //marshaller.marshal(ctrlObj, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }

    /**
     * Tests XmlObjectCollectionMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed. Here the source is set as 
     * write-only, so the addresses should not be read in. 
     * 
     * Positive test.
     *
     * NOTE:  this test can be enabled when bug# 308471 is fixed
     */
    public void testCollectionReferenceWriteOnlyUnmarshal() {
        // load instance doc
        String src = PATH + "root.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Root.class }, CONTEXT_PATH, PATH + "write-only-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }

        // unmarshal
        Root ctrlObj = getWriteOnlyControlObject();
        Root root = null;
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            root = (Root) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", root);
            assertTrue("Unmarshal failed:  Root objects are not equal", ctrlObj.equals(root));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
    
    /**
     * Tests XmlCollectionReferenceMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.  Here the source is set as 
     * write-only.
     * 
     * Positive test.
     */
    public void testCollectionReferenceWriteOnlyMarshal() {
        // setup control document
        String src = PATH + "root.xml";
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
            jCtx = createContext(new Class[] { Root.class }, CONTEXT_PATH, PATH + "write-only-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }

        // test marshal
        Root ctrlObj = getControlObject();
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            marshaller.marshal(ctrlObj, testDoc);
            //marshaller.marshal(ctrlObj, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
}
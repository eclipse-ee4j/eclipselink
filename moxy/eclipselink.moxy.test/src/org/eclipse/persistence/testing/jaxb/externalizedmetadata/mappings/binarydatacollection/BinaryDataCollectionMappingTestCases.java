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
 * dmccann - March 31/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.binarydatacollection;

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
 * Tests XmlBinaryDataCollectionMappings via eclipselink-oxm.xml
 * 
 */
public class BinaryDataCollectionMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.binarydatacollection";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/binarydatacollection/";
    
    private static final byte[] BYTES0123 = new byte[] { 0, 1, 2, 3 };
    private static final byte[] BYTES1234 = new byte[] { 1, 2, 3, 4 };
    private static final byte[] BYTES2345 = new byte[] { 2, 3, 4, 5 };

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public BinaryDataCollectionMappingTestCases(String name) {
        super(name);
    }
    
    /**
     * 
     */
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * Return the control MyData.
     * 
     * @return
     */
    public MyData getControlObject() {
        // setup control object
        List<byte[]> bytesList = new ArrayList<byte[]>();
        bytesList.add(BYTES0123);
        bytesList.add(BYTES1234);
        List<byte[]> roBytesList = new ArrayList<byte[]>();
        roBytesList.add(BYTES2345);
        roBytesList.add(BYTES0123);
        List<byte[]> woBytesList = new ArrayList<byte[]>();
        woBytesList.add(BYTES1234);
        woBytesList.add(BYTES2345);
        MyData ctrlData = new MyData();
        ctrlData.bytes = bytesList;
        ctrlData.readOnlyBytes = roBytesList;
        ctrlData.writeOnlyBytes = woBytesList;
        return ctrlData;
    }
    
    /**
     * Verify schema generation was correct.
     * 
     */
    public void testSchemaGenAndValidation() {
        // generate schema
        MySchemaOutputResolver resolver = generateSchemaWithFileName(new Class[] { MyData.class }, CONTEXT_PATH, PATH + "mydata-oxm.xml", 1);
        // validate the schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "mydata.xsd"));
        // validate mydata.xml
        String src = PATH + "mydata.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (mydata.xml) failed unxepectedly: " + result, result == null);
        
        // validate write-mydata.xml
        src = PATH + "write-mydata.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (write-mydata.xml) failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests XmlBinaryDataCollectionMapping configuration via eclipselink-oxm.xml. 
     * Here an unmarshal operation is performed. Utilizes xml-attribute and 
     * xml-element.
     * 
     * Positive test.
     */
    public void testBinaryDataCollectionMappingUnmarshal() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(PATH + "mydata.xml");
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + PATH + "mydata.xml" + "]");
        }

        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { MyData.class }, CONTEXT_PATH, PATH + "mydata-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }

        MyData ctrlData = getControlObject();
        // writeOnlyBytes will not be read in
        ctrlData.writeOnlyBytes = null;
        try {
            Unmarshaller unmarshaller = jCtx.createUnmarshaller();
            MyData myObj = (MyData) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", myObj);
            assertTrue("Accessor method was not called as expected", myObj.wasSetCalled);
            assertTrue("Unmarshal failed:  MyData objects are not equal", ctrlData.equals(myObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Tests XmlBinaryDataCollectionMapping configuration via eclipselink-oxm.xml. Here a
     * marshal operation is performed. Utilizes xml-attribute and xml-element
     * 
     * Positive test.
     */
    public void testBinaryDataCollectionMappingMarshal() {
        // load instance doc
        String src = PATH + "write-mydata.xml";
        // setup control document
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
            jCtx = createContext(new Class[] { MyData.class }, CONTEXT_PATH, PATH + "mydata-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }

        try {
            MyData ctrlData = getControlObject();
            Marshaller marshaller = jCtx.createMarshaller();
            marshaller.marshal(ctrlData, testDoc);
            //marshaller.marshal(ctrlData, System.out);
            assertTrue("Accessor method was not called as expected", ctrlData.wasGetCalled);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
}
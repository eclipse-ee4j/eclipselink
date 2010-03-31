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

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases; 
import org.w3c.dom.Document;

/**
 * Tests XmlBinaryDataCollectionMappings via eclipselink-oxm.xml
 * 
 */
public class BinaryDataCollectionMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.binarydatacollection";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/binarydatacollection/";
    
    private MySchemaOutputResolver resolver;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public BinaryDataCollectionMappingTestCases(String name) {
        super(name);
    }
    
    /**
     * This method's primary purpose id to generate schema(s). Validation of
     * generated schemas will occur in the testXXXGen method(s) below. Note that
     * the JAXBContext is created from this call and is required for
     * marshal/unmarshal, etc. tests.
     * 
     */
    public void setUp() throws Exception {
        super.setUp();
        resolver = generateSchemaWithFileName(new Class[] { MyData.class }, CONTEXT_PATH, PATH + "mydata-oxm.xml", 1);
    }


    /**
     * Return the control MyData.
     * 
     * @return
     */
    public MyData getControlObject() {
        // setup control object
        MyData ctrlData = new MyData();
        byte[] bytes1 = new byte[] { 0, 1, 2, 3 };
        byte[] bytes2 = new byte[] { 1, 2, 3, 4 };
        List<byte[]> bytesList = new ArrayList<byte[]>();
        bytesList.add(bytes1);
        bytesList.add(bytes2);
        ctrlData.bytes = bytesList;
        return ctrlData;
    }
    
    /**
     * Verify schema generation was correct.
     * 
     */
    public void testBinaryDataCollectionSchemaGen() {
        // validate the schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "mydata.xsd"));
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
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            MyData myObj = (MyData) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", myObj);
            assertTrue("Unmarshal failed:  MyData objects are not equal", getControlObject().equals(myObj));
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
        String src = PATH + "mydata.xml";
        // setup control document
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(getControlObject(), testDoc);
            //marshaller.marshal(getControlObject(), System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
}
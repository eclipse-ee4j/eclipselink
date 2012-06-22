/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.externalizable;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDODataHelper;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;

import org.eclipse.persistence.testing.sdo.SDOXMLComparer;

public class SDOResolvableTest extends SDOResolvableTestCases {
    public final String SERIALIZATION_FILE_NAME = tempFileDir + "/serialization.bin";

    public SDOResolvableTest(String name) {
        super(name);
    }
    
    public void setUp() {
        try {
            xmlComparer = new SDOXMLComparer();
            aHelperContext = SDOHelperContext.getHelperContext();
            typeHelper = aHelperContext.getTypeHelper();
            xmlHelper = aHelperContext.getXMLHelper();
            xsdHelper = aHelperContext.getXSDHelper();
            equalityHelper = aHelperContext.getEqualityHelper();
            copyHelper = aHelperContext.getCopyHelper();
            dataFactory = aHelperContext.getDataFactory();
            // TODO: we should be using the DataHelper interface
            dataHelper = (SDODataHelper)aHelperContext.getDataHelper();

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            builderFactory.setIgnoringElementContentWhitespace(true);
            try {
                parser = builderFactory.newDocumentBuilder();
            } catch (Exception e) {
                fail("Could not create parser.");
                e.printStackTrace();
            }
            
            ((SDOTypeHelper) typeHelper).reset();
            ((SDOXMLHelper) xmlHelper).reset();
            ((SDOXSDHelper) xsdHelper).reset();
            // load in the schema
            String xsdString = getXSDString("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeep.xsd");

            // Define Types so that processing attributes completes
            List types = xsdHelper.define(xsdString);

            // first we set up root data object
            FileInputStream inStream = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderNSDeep.xml");

            XMLDocument document = xmlHelper.load(inStream);
            root = (DataObject)document.getRootObject();
            inStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            fail("SDOResolvableTestCases.setup() failed to load DataObject");
        }
    }

    /*    public SDOResolvableTest(String name, HelperContext aContext) {
            super(name, aContext);
        }
    */

    // UC44: SDOResolvable only uses getPath() for internal nodes
    // We need to verify root behavior returns [.]
    public void testGetPathForRootObject() {
        //log("SDOResolvable.testGetPathForRootObject() UC44");
        String aPath = ((SDODataObject)root)._getPath();
        assertNotNull(aPath);
        assertEquals(SDOConstants.SDO_XPATH_TO_ROOT, aPath);
    }

    // UC43: Write null DataObject
    // Serialization does not occur because anObject is not of type SDODataObject
    public void testWriteNullDataObject() {
        // invoke serialization
        DataObject anObject = null;

        // check that we received a DataObject
        assertNull(anObject);
        // log("SDOResolvable.testWriteNullDataObject() UC43");
        serialize(anObject, SERIALIZATION_FILE_NAME);
    }

    // UC10: Write root DataObject
    // UC30: Read root DataObject
    public void testWriteReadRootDataObject() {
        // invoke serialization
        //log("SDOResolvable.testWriteReadRootDataObject() UC10|30");
        // check that we received a DataObject        
        assertNotNull(root);

        serialize(root, SERIALIZATION_FILE_NAME);
        // deserialize the binary file representation    	
        DataObject aDeserializedDataObject = (DataObject)deserialize(SERIALIZATION_FILE_NAME);

        // the original and deserialized objects should be the same
        assertTrue(equalityHelper.equal(root, aDeserializedDataObject));
    }

    // UC6: Write (containment internal) DataObject
    // UC26: Read (containment internal) DataObject
    public void testWriteReadInternalDataObject() {
        // invoke serialization
        //log("SDOResolvable.testWriteReadInternalDataObject() UC6|26");
        DataObject anObject = root.getDataObject("shipTo");

        // check that we received a DataObject
        assertNotNull(anObject);

        serialize(anObject, SERIALIZATION_FILE_NAME);
        // deserialize the binary file representation
        DataObject aDeserializedDataObject = deserialize(SERIALIZATION_FILE_NAME);

        // the original and deserialized objects should be the same    	
        assertTrue(equalityHelper.equal(anObject, aDeserializedDataObject));
    }

    // UC3: Write (containment internal) indexed list DataObject
    // UC23: Read (containment internal) indexed list DataObject
    public void testWriteReadInternalListDataObject() {
        // invoke serialization
        //log("SDOResolvable.testWriteReadInternalListDataObject() UC3|23");
        DataObject anObject = root.getDataObject("items/item[2]");

        // check that we received a DataObject
        assertNotNull(anObject);

        serialize(anObject, SERIALIZATION_FILE_NAME);
        // deserialize the binary file representation
        DataObject aDeserializedDataObject = deserialize(SERIALIZATION_FILE_NAME);

        // the original and deserialized objects should be the same    	
        assertTrue(equalityHelper.equal(anObject, aDeserializedDataObject));
    }

    public void testWriteReadInternalMultiListDataObject() {
        // invoke serialization
        //log("SDOResolvable.testWriteReadInternalMultListDataObject() UC3|23");
        DataObject anObject = root.getDataObject("items/item[2]/levels2/level2[2]");

        // check that we received a DataObject
        assertNotNull(anObject);

        serialize(anObject, SERIALIZATION_FILE_NAME);
        // deserialize the binary file representation
        DataObject aDeserializedDataObject = deserialize(SERIALIZATION_FILE_NAME);

        // the original and deserialized objects should be the same
        assertTrue(equalityHelper.equal(anObject, aDeserializedDataObject));
    }

    // UC2: Write (containment internal)indexed list with changeSequence DataObject
    // UC22: Read (containment internal) indexed list with changeSequence DataObject

    /*    public void testWriteReadInternalListChangeSequenceDataObject() {
            // invoke serialization
            log("SDOResolvable.testWriteReadInternalListChangeSequenceDataObject() UC2|22");
            SDODataObject anObject = (SDODataObject)root;

            serialize(anObject, SERIALIZATION_FILE_NAME);
            // deserialize the binary file representation
            SDODataObject aDeserializedDataObject = (SDODataObject)deserialize(SERIALIZATION_FILE_NAME);

            // the original and deserialized objects should be the same
            assertTrue(equalityHelper.equal(anObject, aDeserializedDataObject));
        }*/
    public void testWriteListWrapper() {
        // invoke serialization
        //log("SDOResolvable.testWriteListWrapper()");
        DataObject itemsDO = root.getDataObject("items");
        assertNotNull(itemsDO);

        List items = itemsDO.getList("item");

        serializeList(items, SERIALIZATION_FILE_NAME + 2);
        // deserialize the binary file representation    	
        List aDeserializedDataObject = deserializeList(SERIALIZATION_FILE_NAME + 2);

        assertEquals(items.size(), aDeserializedDataObject.size());

        // the original and deserialized objects should be the same
        //assertTrue(equalityHelper.equal(anObject, aDeserializedDataObject));
    }

    public void testWriteList() {
        //log("SDOResolvable.testWriteList()");
        DataObject itemsDO = root.getDataObject("items");
        assertNotNull(itemsDO);

        List items = itemsDO.getList("item");
        ArrayList theList = new ArrayList(items);

        // invoke serialization      
        serializeList(theList, SERIALIZATION_FILE_NAME + 3);
        // deserialize the binary file representation    	
        List aDeserializedDataObject = deserializeList(SERIALIZATION_FILE_NAME + 3);

        assertEquals(items.size(), aDeserializedDataObject.size());

        // the original and deserialized objects should be the same
        //assertTrue(equalityHelper.equal(anObject, aDeserializedDataObject));
    }
}

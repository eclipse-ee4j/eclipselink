/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms
* of the Eclipse Public License v1.0 and Eclipse Distribution License v1.0
* which accompanies this distribution.
* 
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation
******************************************************************************/

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.SDOXMLDocument;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegator;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

public abstract class SDOXMLHelperDatatypeTestCase extends SDOTestCase {
    
	public SDOXMLHelperDatatypeTestCase(String name) {
        super(name);
    }

	// === METHODS TO OVERRIDE ==========================
	
    protected abstract Class getDatatypeJavaClass();
    
    protected abstract SDOType getValueType();    
    
    protected abstract String getSchemaNameForUserDefinedType();
    
    protected abstract String getSchemaNameForBuiltinType();    

    protected abstract String getControlFileName();

    protected abstract String getControlRootURI();

    protected abstract String getControlRootName();

	// ==================================================
    
    protected String getSchemaLocation() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/";
    }
    
    protected String getControlWriteFileName() {
        return getControlFileName();
    }

    protected void compareXML(String controlFileName, String testString) throws Exception {
        compareXML(controlFileName, testString, true);
    }

    protected void compareXML(String controlFileName, String testString, boolean compareNodes) throws Exception {
        String controlString = getControlString(controlFileName);
        log("Expected: " + controlString);
        log("Actual:   " + testString);

        StringReader reader = new StringReader(testString);
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        reader.close();

        if (compareNodes) {
            assertXMLIdentical(getDocument(controlFileName), testDocument);
        }
    }
    
    protected String getControlString(String fileName) {
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the control document.");
            return null;
        }
    }

    protected void verifyAfterLoad(XMLDocument document) {
    	assertNotNull(document);
        assertNotNull(((SDOXMLDocument) document).getObject());
    }    

	protected void defineTypesFromDataObjects() {
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
        
        DataObject newProperty = dataFactory.create(propertyType);
        newProperty.set("name", getControlRootName());
        newProperty.set("type", getValueType());
        
        typeHelper.defineOpenContentProperty(getControlRootURI(), newProperty);
	}    
    
    // === TEST METHODS =================================
    
    public void testRootObjectInstanceClassForUserDefinedType() throws Exception {
        xsdHelper.define(getSchema(getSchemaNameForUserDefinedType()));

        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, null);

        assertNull(document.getRootObject().getType().getInstanceClass());

        Class instanceClassFromDocument = document.getRootObject().get("value").getClass();
        assertEquals(getDatatypeJavaClass(), instanceClassFromDocument);
    }

    public void testRootObjectInstanceClassForBuiltinType() throws Exception {
        xsdHelper.define(getSchema(getSchemaNameForBuiltinType()));

        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, null);

        assertNull(document.getRootObject().getType().getInstanceClass());

        Class instanceClassFromDocument = document.getRootObject().get("value").getClass();
        assertEquals(getDatatypeJavaClass(), instanceClassFromDocument);
    }

    public void testLoadAndSaveForUserDefinedType() throws Exception {
    	xsdHelper.define(getSchema(getSchemaNameForUserDefinedType()));
    	
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, null);
        verifyAfterLoad(document);
        
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);
        ((SDOXMLHelper) xmlHelper).save(document, result, null);
        
        // Uncomment to print out document during test
        //((SDOXMLHelper) xmlHelper).save(document, System.out, null);
        
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());
    }
 
	public void testLoadAndSaveForBuiltinType() throws Exception {
    	xsdHelper.define(getSchema(getSchemaNameForBuiltinType()));
    	
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, null);
        verifyAfterLoad(document);
        
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);
        ((SDOXMLHelper) xmlHelper).save(document, result, null);
        
        // Uncomment to print out document during test
        //((SDOXMLHelper) xmlHelper).save(document, System.out, null);
        
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());
    }
	
    public void testLoadAndSaveTypesFromDataObject() throws Exception {
    	defineTypesFromDataObjects();
    	
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, null);
        verifyAfterLoad(document);
        
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);
        ((SDOXMLHelper) xmlHelper).save(document, result, null);
        
        // Uncomment to print out document during test
        //((SDOXMLHelper) xmlHelper).save(document, System.out, null);
        
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());    	
    }
    
}

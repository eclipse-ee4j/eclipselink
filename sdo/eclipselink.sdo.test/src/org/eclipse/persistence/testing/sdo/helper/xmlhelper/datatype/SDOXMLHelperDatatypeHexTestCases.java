/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.io.FileInputStream;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

import junit.textui.TestRunner;

import commonj.sdo.helper.XMLDocument;

public class SDOXMLHelperDatatypeHexTestCases extends SDOXMLHelperDatatypeTestCase {
    
	public SDOXMLHelperDatatypeHexTestCases(String name) {
        super(name);
    }
	
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeHexTestCases" };
        TestRunner.main(arguments);
    }

    protected Class getDatatypeJavaClass() {
    	return byte[].class;
    }
    
    protected SDOType getValueType() {
    	return SDOConstants.SDO_BYTES;
    }
    
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myHexBinary-1.xml");
    }

    protected String getControlRootURI() {
        return "myHexBinary-NS";
    }
    
    protected String getControlRootName() {
        return "myHexBinary";
    }
    
    protected String getSchemaNameForUserDefinedType() {
        return getSchemaLocation() + "myHexBinary.xsd";
    }

    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myHexBinary-builtin.xsd";
    }

    // === TEST METHODS =================================

    /**
     * Test to make sure that the when the XML doc is loaded, that the 
     * String in the document is interpreted properly as HexBinary.
     * 
     * i.e. if "48656C6C6F20576F726C6421" is converted to a byte[]
     * using hexBinary conversion, a String created with the resulting
     * byte[] should read "Hello World!" 
     */
    public void testBinaryConversionForUserDefinedType() throws Exception {
    	xsdHelper.define(getSchema(getSchemaNameForUserDefinedType()));
    	
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, null);
        
        byte[] bytesFromDocument = (byte[]) document.getRootObject().get("value");

        String controlString = "Hello World!";
        String testString = new String(bytesFromDocument);
        
        assertEquals(controlString, testString);
    }

    public void testBinaryConversionForBuiltinType() throws Exception {
    	xsdHelper.define(getSchema(getSchemaNameForBuiltinType()));
    	
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, null);
        
        byte[] bytesFromDocument = (byte[]) document.getRootObject().get("value");

        String controlString = "Hello World!";
        String testString = new String(bytesFromDocument);
        
        assertEquals(controlString, testString);
    }

}

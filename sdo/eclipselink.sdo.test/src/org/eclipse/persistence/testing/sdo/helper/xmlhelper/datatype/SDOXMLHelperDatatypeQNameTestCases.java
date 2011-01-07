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
*     bdoughan - May 25/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;

import commonj.sdo.helper.XMLDocument;

public class SDOXMLHelperDatatypeQNameTestCases extends SDOXMLHelperDatatypeTestCase {

    public SDOXMLHelperDatatypeQNameTestCases(String name) {
        super(name);
    }

    @Override
    protected Class getDatatypeJavaClass() {
        return String.class;
    }

    @Override
    protected SDOType getValueType() {
        return SDOConstants.SDO_URI;
    }

    @Override
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myQName-1.xml");
    }

    @Override
    protected String getControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myQName-1-write.xml");
    }

    @Override
    protected String getControlRootURI() {
        return "myQName-NS";
    }

    @Override
    protected String getControlRootName() {
        return "simple-root";
    }

    @Override
    protected String getSchemaNameForUserDefinedType() {
    	System.out.println(getSchemaLocation() + "myQName.xsd");
        return getSchemaLocation() + "myQName.xsd";
    }

    @Override
    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myQName-builtin.xsd";
    }

    public void testConversionForUserDefinedTypeSimpleRoot() throws Exception {
        xsdHelper.define(getSchema(getSchemaNameForUserDefinedType()));
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, null);

        String testString = document.getRootObject().getString("value");
        String controlString = "myQName-NS#Foo";
        assertEquals(controlString, testString);
    }

    public void testConversionForBuiltinTypeSimpleRoot() throws Exception {
        xsdHelper.define(getSchema(getSchemaNameForBuiltinType()));
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, null);

        String testString = document.getRootObject().getString("value");
        String controlString = "myQName-NS#Foo";
        assertEquals(controlString, testString);
    }
/*
    public void testConversionForUserDefinedTypeComplexRoot() throws Exception {
        xsdHelper.define(getSchema(getSchemaNameForUserDefinedType()));
        FileInputStream inputStream = new FileInputStream("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myQName-2.xml");
        XMLDocument document = xmlHelper.load(inputStream, null, null);

        String controlString = "myQName-NS#Foo";
        assertEquals(controlString, document.getRootObject().getString("child"));
        assertEquals(controlString, document.getRootObject().getString("attr"));
    }
*/
    public void testConversionForBuiltinTypeComplexRoot() throws Exception {
        xsdHelper.define(getSchema(getSchemaNameForBuiltinType()));
        FileInputStream inputStream = new FileInputStream("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myQName-2.xml");
        XMLDocument document = xmlHelper.load(inputStream, null, null);

        String controlString = "myQName-NS#Foo";
        assertEquals(controlString, document.getRootObject().getString("child"));
        assertEquals(controlString, document.getRootObject().getString("attr"));
    }

    public void testLoadAndSaveForBuiltinTypeComplexRoot() throws Exception {
        xsdHelper.define(getSchema(getSchemaNameForBuiltinType()));

        FileInputStream inputStream = new FileInputStream("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myQName-2.xml");
        XMLDocument document = xmlHelper.load(inputStream, null, null);
        verifyAfterLoad(document);
        
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);
        ((SDOXMLHelper) xmlHelper).save(document, result, null);

        // Uncomment to print out document during test
        //((SDOXMLHelper) xmlHelper).save(document, System.out, null);
        
        compareXML("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myQName-2.xml", result.getOutputStream().toString());
    }

    public void testLoadAndSaveForUserDefinedTypeComplexRoot() throws Exception {
        xsdHelper.define(getSchema(getSchemaNameForUserDefinedType()));

        FileInputStream inputStream = new FileInputStream("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myQName-2.xml");
        XMLDocument document = xmlHelper.load(inputStream, null, null);
        verifyAfterLoad(document);
        
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);
        ((SDOXMLHelper) xmlHelper).save(document, result, null);

        // Uncomment to print out document during test
        //((SDOXMLHelper) xmlHelper).save(document, System.out, null);
        
        compareXML("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myQName-2.xml", result.getOutputStream().toString());
    }

    @Override
    public void testLoadAndSaveTypesFromDataObject() throws Exception {
    }

}
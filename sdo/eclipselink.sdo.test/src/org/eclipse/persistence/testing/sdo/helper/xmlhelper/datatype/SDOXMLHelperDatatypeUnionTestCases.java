/*******************************************************************************
* Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.textui.TestRunner;

import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sdo.SDOXMLDocument;
import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.delegates.SDOXMLHelperDelegator;
import org.eclipse.persistence.sdo.helper.delegates.SDOXSDHelperDelegator;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import commonj.sdo.helper.XMLDocument;

public class SDOXMLHelperDatatypeUnionTestCases extends SDOTestCase {
    
	public SDOXMLHelperDatatypeUnionTestCases(String name) {
        super(name);
    }
	
	public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeUnionTestCases" };
        TestRunner.main(arguments);
    }

    public static void mainXXX(String[] args) {
        try {
            SDOXMLHelperDelegator sdoXMLHelper = (SDOXMLHelperDelegator) SDOXMLHelper.INSTANCE;
            SDODataFactory sdoDataFactory = (SDODataFactory) SDODataFactory.INSTANCE;
    
            SDOXSDHelperDelegator xsdHelper = (SDOXSDHelperDelegator) SDOXSDHelper.INSTANCE;

            File xsdFile = new File("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myUnion.xsd");
            //xsdFile = new File("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/SimpleTypeWithUnionType.xsd");            
    
            File xmlFile = new File("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myUnion-1.xml");            
            
            FileInputStream inStream = new FileInputStream(xsdFile);
            byte[] bytes = new byte[inStream.available()];
            inStream.read(bytes);
            List l = xsdHelper.define(new String(bytes));
            System.out.println(l.size());
            
            Object primitive = ((SDOXMLDocument) sdoXMLHelper.load(new FileInputStream(xmlFile))).getObject();
            
            System.out.println(primitive.getClass() + " " + primitive);

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    protected String getSchemaLocation() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/";
    }

    protected String getSchemaName() {
        return getSchemaLocation() + "myUnion.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myUnion-2.xml");
    }

    protected String getControlWriteFileName() {
        return getControlFileName();
    }

    protected String getNoSchemaControlFileName() {
        return "";
    }

    protected String getControlRootURI() {
        return "myUnion-NS";
    }

    protected String getControlRootName() {
        return "myUnion";
    }
    
    protected String getRootInterfaceName() {
        return "";
    }

    public void registerTypes() {
    }
    
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add("myUnion-NS");
        return packages;
    }    

    protected List defineTypes() {
        return xsdHelper.define(getSchema(getSchemaName()));
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
            fail("An error occurred loading the control document");
            return null;
        }
    }
    

    protected String getUnrelatedSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrder.xsd";
    }
    
    protected Object getOptions() {
        return null;
    }
    
    protected void verifyAfterLoad(XMLDocument document) {
    	assertNotNull(document);
        assertNotNull(((SDOXMLDocument) document).getObject());
    }    
    
    // === TEST METHODS =================================
    
    public void testLoadFromAndSaveAfterDefineMultipleSchemas() throws Exception {
    	defineTypes();
        xsdHelper.define(getSchema(getUnrelatedSchemaName()));
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, getOptions());// xsi:type will be written out
        verifyAfterLoad(document);
        
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);
        ((SDOXMLHelper) xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());
    }

    public void testLoadFromInputStreamWithURIAndOptionsSavePrimitiveToStreamResult() throws Exception {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document1 = null;

        //DOMSource source = null;
        defineTypes();
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        builder = factory.newDocumentBuilder();

        document1 = builder.parse(new File(getControlFileName()));
        document1.toString();
        //source = new DOMSource(document1);
        XMLDocument document = ((SDOXMLHelper) xmlHelper).load(new FileInputStream(getControlFileName()), null, getOptions());
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);
        ((SDOXMLHelper) xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());
    }

    public void testLoadFromDomSourceWithURIAndOptionsSavePrimitiveToStreamResult() throws Exception {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document1 = null;
        DOMSource source = null;

        defineTypes();

        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        builder = factory.newDocumentBuilder();

        document1 = builder.parse(new File(getControlFileName()));

        source = new DOMSource(document1);

        XMLDocument document = ((SDOXMLHelper) xmlHelper).load(source, null, getOptions());
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);

        ((SDOXMLHelper) xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());
    }

    public void testLoadFromSAXSourceWithURIAndOptionsSavePrimitiveToStreamResult() throws Exception {
        SAXSource source = null;

        defineTypes();
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        source = new SAXSource(new InputSource(inputStream));

        XMLDocument document = ((SDOXMLHelper) xmlHelper).load(source, null, getOptions());
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outstream);

        ((SDOXMLHelper) xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());
    }

    public void testLoadFromStreamSourceWithURIAndOptionsSavePrimitiveToStreamResult() throws Exception {
        StreamSource source = null;

        defineTypes();

        FileInputStream inputStream = new FileInputStream(getControlFileName());

        source = new StreamSource(inputStream);

        XMLDocument document = ((SDOXMLHelper) xmlHelper).load(source, null, getOptions());
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);

        ((SDOXMLHelper) xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());
    }
    
}
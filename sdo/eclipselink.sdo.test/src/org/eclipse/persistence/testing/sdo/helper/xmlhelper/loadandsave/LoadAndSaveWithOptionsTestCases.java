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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.extension.SDOUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public abstract class LoadAndSaveWithOptionsTestCases extends SDOXMLHelperTestCases {
    public LoadAndSaveWithOptionsTestCases(String name) {
        super(name);
    }

    protected String getNoSchemaControlFileName() {
        return getControlFileName();
    }

    protected String getNoSchemaControlWriteFileName() {
        return getNoSchemaControlFileName();
    }

    protected String getControlDataObjectFileName() {
        return getControlWriteFileName();
    }

    protected String getUnrelatedSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrder.xsd";
    }

    abstract protected String getSchemaName();

    abstract protected String getControlFileName();

    abstract protected void registerTypes();


    abstract protected String getControlRootName();
    
    protected String getControlWriteFileName() {
        return getControlFileName();
    }

    protected String getControlRootURI() {
        return NON_DEFAULT_URI;
    }

    public String getFullClassPackageName(Type aType) {
    	StringBuffer className = new StringBuffer(SDOUtil.getPackageNameFromURI(aType.getURI()));
        className.append(SDOConstants.JAVA_PACKAGE_NAME_SEPARATOR);
    	className.append(aType.getName().substring(0,1).toUpperCase());
    	className.append(aType.getName().substring(1));
        return className.toString();
    }

    public void verifyPackageNameGeneratedFromURI(String uri, String typeName) {
        Type aType = typeHelper.getType(uri, typeName);
        String className = ((SDOType)aType).getInstanceClassName();
        String mangledClassName = getFullClassPackageName(aType); 
        assertEquals(mangledClassName, className);
    }    

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
        return packages;
    }    
    
    public void testLoadFromInputStreamSaveDocumentToOutputStream() throws Exception {
        defineTypes();

        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, getOptions());
        verifyAfterLoad(document);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xmlHelper.save(document, outputStream, null);

        compareXML(getControlWriteFileName(), outputStream.toString());

    }

    //
    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
        registerTypes();
        FileInputStream inputStream = new FileInputStream(getNoSchemaControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, getOptions());
        verifyAfterLoad(document);
        StringWriter writer = new StringWriter();
        xmlHelper.save(document, writer, null);
        // Nodes will not be the same but XML output is
        compareXML(getNoSchemaControlWriteFileName(), writer.toString());//, false);
    }

    //
    public void testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToOutputStream() throws Exception {
        defineTypes();
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, getOptions());// xsi:type will be written out
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        xmlHelper.save(document.getRootObject(), getControlRootURI(), getControlRootName(), outstream);
        compareXML(getControlDataObjectFileName(), outstream.toString());

    }

    //
    // xpaths/doms null
    public void testLoadFromFileReaderWithURIAndOptionsStreamSaveDataObjectToWriter() throws Exception {
        defineTypes();
        FileReader reader = new FileReader(getControlFileName());
        XMLDocument document = xmlHelper.load(reader, null, getOptions());
        verifyAfterLoad(document);

        String s = xmlHelper.save(document.getRootObject(), getControlRootURI(), getControlRootName());
        compareXML(getControlDataObjectFileName(), s);
    }

    // okay except will set isDirty=true which will clear marshalledObject in xmlMarshaller
    public void testLoadFromAndSaveAfterDefineMultipleSchemas() throws Exception {
        defineTypes();
        xsdHelper.define(getSchema(getUnrelatedSchemaName()));
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, getOptions());// xsi:type will be written out
        verifyAfterLoad(document);
        String s = xmlHelper.save(document.getRootObject(), getControlRootURI(), getControlRootName());
        compareXML(getControlDataObjectFileName(), s);

    }

    public void testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
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
        XMLDocument document = ((SDOXMLHelper)xmlHelper).load(new FileInputStream(getControlFileName()), null, getOptions());
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);
        ((SDOXMLHelper)xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());

    }

    public void testLoadFromDomSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
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

        XMLDocument document = ((SDOXMLHelper)xmlHelper).load(source, null, getOptions());
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);

        ((SDOXMLHelper)xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());

    }

    public void testLoadFromSAXSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        SAXSource source = null;

        defineTypes();
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        source = new SAXSource(new InputSource(inputStream));

        XMLDocument document = ((SDOXMLHelper)xmlHelper).load(source, null, getOptions());
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outstream);

        ((SDOXMLHelper)xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());

    }

    public void testLoadFromStreamSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        StreamSource source = null;

        defineTypes();

        FileInputStream inputStream = new FileInputStream(getControlFileName());

        source = new StreamSource(inputStream);

        XMLDocument document = ((SDOXMLHelper)xmlHelper).load(source, null, getOptions());
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);

        ((SDOXMLHelper)xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());

    }

    protected void compareXML(String controlFileName, String testString) throws Exception {
        compareXML(controlFileName, testString, true);
    }

    protected void compareXML(String controlFileName, String testString, boolean compareNodes) throws Exception {
        String controlString = getControlString(controlFileName);
        log("Expected:" + controlString);
        log("Actual  :" + testString);

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

    protected void verifyAfterLoad(XMLDocument document) {
        assertNotNull(document);
        assertNotNull(document.getRootObject());
        assertNull(document.getRootObject().getContainer());
    }

    protected List defineTypes() {
        return xsdHelper.define(getSchema(getSchemaName()));
    }

    protected String getSchemaLocation() {
        return "";
    }

    protected Object getOptions() {
        return null;
    }
}

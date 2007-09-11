/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import com.sun.tools.javac.Main;
import commonj.sdo.helper.XMLDocument;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public abstract class LoadAndSaveTestCases extends SDOXMLHelperTestCases {
    public LoadAndSaveTestCases(String name) {
        super(name);
    }

    protected String getNoSchemaControlFileName() {
        return getControlFileName();
    }

    protected String getNoSchemaControlWriteFileName() {
        return getNoSchemaControlFileName();
    }

    protected String getControlDataObjectFileName() {
        // return getControlFileName();
        return getControlWriteFileName();
    }

    protected String getUnrelatedSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrder.xsd";
    }

    abstract protected String getSchemaName();

    abstract protected String getControlFileName();

    abstract protected void registerTypes();

    abstract protected String getControlRootURI();

    abstract protected String getControlRootName();

    abstract protected String getRootInterfaceName();

    protected String getControlWriteFileName() {
        return getControlFileName();
    }

    public void testLoadFromStringSaveDocumentToWriter() throws Exception {
        List types = defineTypes();

        FileInputStream inputStream = new FileInputStream(getControlFileName());
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        XMLDocument document = xmlHelper.load(new String(bytes));
        verifyAfterLoad(document);

        StringWriter writer = new StringWriter();
        xmlHelper.save(document, writer, null);
        compareXML(getControlWriteFileName(), writer.toString());

    }

    public void testLoadFromInputStreamSaveDocumentToOutputStream() throws Exception {
        List types = defineTypes();

        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream);
        verifyAfterLoad(document);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xmlHelper.save(document, outputStream, null);

        compareXML(getControlWriteFileName(), outputStream.toString());

    }

    //
    public void testNoSchemaLoadFromInputStreamSaveDataObjectToString() throws Exception {
        registerTypes();
        FileInputStream inputStream = new FileInputStream(getNoSchemaControlFileName());
        XMLDocument document = xmlHelper.load(inputStream);
        verifyAfterLoad(document);
        StringWriter writer = new StringWriter();
        xmlHelper.save(document, writer, null);
        // Nodes will not be the same but XML output is
        compareXML(getNoSchemaControlWriteFileName(), writer.toString());//, false);
    }

    //
    public void testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToOutputStream() throws Exception {
        List types = defineTypes();
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, null);// xsi:type will be written out
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        xmlHelper.save(document.getRootObject(), getControlRootURI(), getControlRootName(), outstream);
        compareXML(getControlDataObjectFileName(), outstream.toString());

    }

    //
    // xpaths/doms null
    public void testLoadFromFileReaderWithURIAndOptionsStreamSaveDataObjectToWriter() throws Exception {
        List types = defineTypes();
        FileReader reader = new FileReader(getControlFileName());
        XMLDocument document = xmlHelper.load(reader, null, null);
        verifyAfterLoad(document);
        String s = xmlHelper.save(document.getRootObject(), getControlRootURI(), getControlRootName());

        compareXML(getControlDataObjectFileName(), s);
    }

    // ok except will set isDirty=true which will clear marshalledObject in xmlMarshaller
    public void testLoadFromAndSaveAfterDefineMultipleSchemas() throws Exception {
        List types = defineTypes();
        xsdHelper.define(getSchema(getUnrelatedSchemaName()));
        FileReader reader = new FileReader(getControlFileName());
        XMLDocument document = xmlHelper.load(reader, null, null);// xsi:type will be written out
        verifyAfterLoad(document);
        String s = xmlHelper.save(document.getRootObject(), getControlRootURI(), getControlRootName());
        compareXML(getControlDataObjectFileName(), s);

    }

    public void testLoadFromInputStreamWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document1 = null;

        //DOMSource source = null;
        List types = defineTypes();
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        builder = factory.newDocumentBuilder();

        document1 = builder.parse(new File(getControlFileName()));
        document1.toString();
        //source = new DOMSource(document1);
        XMLDocument document = ((SDOXMLHelper)xmlHelper).load(new FileInputStream(getControlFileName()), null, null);
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

        List types = defineTypes();

        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        builder = factory.newDocumentBuilder();

        document1 = builder.parse(new File(getControlFileName()));

        source = new DOMSource(document1);

        XMLDocument document = ((SDOXMLHelper)xmlHelper).load(source, null, null);
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);

        ((SDOXMLHelper)xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());

    }

    public void testLoadFromSAXSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        SAXSource source = null;

        List types = defineTypes();
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        source = new SAXSource(new InputSource(inputStream));

        XMLDocument document = ((SDOXMLHelper)xmlHelper).load(source, null, null);
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outstream);

        ((SDOXMLHelper)xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());

    }

    public void testLoadFromStreamSourceWithURIAndOptionsSaveDataObjectToStreamResult() throws Exception {
        StreamSource source = null;

        List types = defineTypes();

        FileInputStream inputStream = new FileInputStream(getControlFileName());

        source = new StreamSource(inputStream);

        XMLDocument document = ((SDOXMLHelper)xmlHelper).load(source, null, null);
        verifyAfterLoad(document);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

        StreamResult result = new StreamResult(outstream);

        ((SDOXMLHelper)xmlHelper).save(document, result, null);
        compareXML(getControlWriteFileName(), result.getOutputStream().toString());

    }

    public void testClassGenerationLoadAndSave() throws Exception {
        // TODO: hardcoded path should be parameterized as an option to the test suite 
        String tmpDirName = tempFileDir + "/tmp/";
        File f = new File(tmpDirName);
        f.mkdir();
        f.deleteOnExit();

        generateClasses(tmpDirName);

        setUp();

        compileFiles(tmpDirName, getPackages());

        URL[] urls = new URL[1];
        urls[0] = f.toURL();
        URLClassLoader myURLLoader = new URLClassLoader(urls);
        String package1 = (String)getPackages().get(0);
        String className = package1 + "/" + getRootInterfaceName();
        className = className.replaceAll("/", ".");

        Class urlLoadedClass = myURLLoader.loadClass(className);

        ((SDOXMLHelper)xmlHelper).getLoader().setDelegateLoader(myURLLoader);
        Class loadedClass2 = ((SDOXMLHelper)xmlHelper).getLoader().loadClass(className);

        defineTypes();

        assertEquals(urlLoadedClass, loadedClass2);
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        XMLDocument document = xmlHelper.load(new String(bytes));
        Class loadedClass = document.getRootObject().getType().getInstanceClass();
        assertEquals(urlLoadedClass, loadedClass);

        verifyAfterLoad(document);

        StringWriter writer = new StringWriter();
        xmlHelper.save(document, writer, null);
        compareXML(getControlWriteFileName(), writer.toString());
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
        assertNull(document.getRootObject().getContainer());
    }

    protected List defineTypes() {
        return xsdHelper.define(getSchema(getSchemaName()));
    }

    protected String getSchemaLocation() {
        return "";
    }

    //first package should be the package that contains the class for the get root interface name class
    protected List getPackages() {
        List packages = new ArrayList();
        packages.add("defaultPackage");
        return packages;
    }

    protected void generateClasses(String tmpDirName) throws Exception {
        String xsdString = getSchema(getSchemaName());
        StringReader reader = new StringReader(xsdString);

        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);
        classGenerator.generate(reader, tmpDirName);
    }

    public void compileFiles(String dirName, List packages) throws Exception {
        // deleteDirsOnExit(new File(dirName));
        List allFilesInAllPackages = new ArrayList();

        for (int i = 0; i < packages.size(); i++) {
            String nextPackage = (String)packages.get(i);
            nextPackage = dirName + nextPackage;

            File f = new File(nextPackage);

            File[] filesInDir = f.listFiles();

            for (int j = 0; j < filesInDir.length; j++) {
                File nextFile = filesInDir[j];
                String fullName = nextFile.getAbsolutePath();
                nextFile.deleteOnExit();
                allFilesInAllPackages.add(fullName);

                String fullClassName = fullName.replace(".java", ".class");
                File nextClassFile = new File(fullClassName);
                nextClassFile.deleteOnExit();
            }
        }
        Object[] fileArray = allFilesInAllPackages.toArray();

        String[] args = new String[allFilesInAllPackages.size() + 2];
        args[0] = "-cp";
        args[1] = getClassPathForCompile();
        System.arraycopy(fileArray, 0, args, 2, allFilesInAllPackages.size());

        int returnVal = Main.compile(args);
        assertEquals(0, returnVal);
        //deleteDirsOnExit(new File(dirName));
    }

    public void tearDown() throws Exception {
        super.tearDown();
        // TODO: hardcoded path should be parameterized as an option to the test suite 
        String tmpDirName = tempFileDir + "/tmp/";
        emptyAndDeleteDirectory(new File(tmpDirName));
    }
}
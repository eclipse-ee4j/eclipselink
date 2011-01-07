/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.xmlfragment;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;

/**
 * Namespace qualified XMLFragmentMapping tests
 */
public class XMLFragmentNSAttributeTestCases extends OXTestCase {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/xmlfragment/employee_attribute_ns.xml";
    private final static String XML_RESOURCE_DIFF_PFX = "org/eclipse/persistence/testing/oxm/mappings/xmlfragment/employee_attribute_ns_different_prefix.xml";
    protected Document controlDocument;
    protected XMLMarshaller xmlMarshaller;
    protected XMLUnmarshaller xmlUnmarshaller;
    protected XMLContext xmlContext;
    public String resourceName;
    protected DocumentBuilder parser;

    public XMLFragmentNSAttributeTestCases(String name) throws Exception {
        super(name);
        setupParser();
    }

    protected void setupParser() {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            builderFactory.setIgnoringElementContentWhitespace(true);
            parser = builderFactory.newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred during setup");
        }
    }

    protected void setSession(String sessionName) {
        xmlContext = getXMLContext(sessionName);
        xmlMarshaller = xmlContext.createMarshaller();
        xmlMarshaller.setFormattedOutput(false);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
    }

    protected void setProject(Project project) {
        xmlContext = getXMLContext(project);
        xmlMarshaller = xmlContext.createMarshaller();
        xmlMarshaller.setFormattedOutput(false);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
    }

    protected Document getControlDocument() {
        return controlDocument;
    }

    protected Document getWriteControlDocument() throws Exception {
        return getControlDocument();
    }

    protected void setControlDocument(String xmlResource) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);
        resourceName = xmlResource;
        controlDocument = parser.parse(inputStream);
        removeEmptyTextNodes(controlDocument);
        inputStream.close();
    }

    public Object getReadControlObject() {
        return getControlObject();
    }

    public Object getWriteControlObject() {
        return getControlObject();
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.firstName = "Jane";
        employee.lastName = "Doe";
        return employee;
    }

    public Object getReadControlObject(String prefix, String uri) {
        return getControlObject(prefix, uri);
    }

    public Object getWriteControlObject(String prefix, String uri) {
        return getControlObject(prefix, uri);
    }

    protected Object getControlObject(String prefix, String uri) {
        Employee emp = (Employee) getControlObject();
        try {
            Document doc = parser.newDocument();
            Attr node = doc.createAttributeNS(uri, "attribute");
            node.setValue("attributevalue");
            node.setPrefix(prefix);
            emp.xmlNode = node;
        } catch(Exception ex) {}
        return emp;
    }

    // ------------------------- TESTS ------------------------- //
    /**
     * Test NodeRecord - prefixes and uris match
     */
    public void testObjectToXMLDocumentSamePrefixAndURI() throws Exception {
        NamespaceResolver nsresolver = new NamespaceResolver();
        nsresolver.put("ns1", "http://www.example.com/test-uri");
        setProject(new XMLFragmentNSAttributeProject(nsresolver));
        Document testDocument = xmlMarshaller.objectToXML(getWriteControlObject("ns1", "http://www.example.com/test-uri"));
        objectToXMLDocumentTest(testDocument, XML_RESOURCE, "testObjectToXMLDocumentSamePrefixAndURI");
    }

    /**
     * Test NodeRecord - prefixes don't match
     */
    public void testObjectToXMLDocumentDifferentPrefix() throws Exception {
        NamespaceResolver nsresolver = new NamespaceResolver();
        nsresolver.put("ns1", "http://www.example.com/test-uri");
        setProject(new XMLFragmentNSAttributeProject(nsresolver));
        Document testDocument = xmlMarshaller.objectToXML(getWriteControlObject("nsx", "http://www.example.com/test-uri"));
        objectToXMLDocumentTest(testDocument, XML_RESOURCE_DIFF_PFX, "testObjectToXMLDocumentDifferentPrefix");
    }
    
    /**
     * Test ContentHandlerRecord - prefixes and uris match
     */
    public void testObjectToContentHandlerSamePrefixAndURI() throws Exception {
        setControlDocument(XML_RESOURCE);
        NamespaceResolver nsresolver = new NamespaceResolver();
        nsresolver.put("ns1", "http://www.example.com/test-uri");
        setProject(new XMLFragmentNSAttributeProject(nsresolver));

        SAXDocumentBuilder builder = new SAXDocumentBuilder();
        xmlMarshaller.marshal(getWriteControlObject("ns1", "http://www.example.com/test-uri"), builder);

        Document controlDocument = getWriteControlDocument();
        Document testDocument = builder.getDocument();

        log("\n**testObjectToContentHandlerSamePrefixAndURI**");
        log("Expected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("\n");

        assertXMLIdentical(controlDocument, importNodeFix(testDocument));
    }

    /**
     * Test ContentHandlerRecord - prefixes don't match
     */
    public void testObjectToContentHandlerDifferentPrefix() throws Exception {
        setControlDocument(XML_RESOURCE_DIFF_PFX);
        NamespaceResolver nsresolver = new NamespaceResolver();
        nsresolver.put("ns1", "http://www.example.com/test-uri");
        setProject(new XMLFragmentNSAttributeProject(nsresolver));

        SAXDocumentBuilder builder = new SAXDocumentBuilder();
        xmlMarshaller.marshal(getWriteControlObject("nsx", "http://www.example.com/test-uri"), builder);

        Document controlDocument = getWriteControlDocument();
        Document testDocument = builder.getDocument();

        log("\n**testObjectToContentHandlerDifferentPrefix**");
        log("Expected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("\n");

        assertXMLIdentical(controlDocument, importNodeFix(testDocument));
    }

    /**
     * Test WriterRecord - prefixes and uris match
     */
    public void testObjectToXMLStringWriter() throws Exception {
        NamespaceResolver nsresolver = new NamespaceResolver();
        nsresolver.put("ns1", "http://www.example.com/test-uri");
        setProject(new XMLFragmentNSAttributeProject(nsresolver));

        StringWriter writer = new StringWriter();
        xmlMarshaller.marshal(getWriteControlObject("ns1", "http://www.example.com/test-uri"), writer);
        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        objectToXMLDocumentTest(testDocument, XML_RESOURCE, "testObjectToXMLStringWriter");
    }

    /**
     * Test WriterRecord - prefixes don't match
     */
    public void testObjectToXMLStringWriterDifferentPrefix() throws Exception {
        NamespaceResolver nsresolver = new NamespaceResolver();
        nsresolver.put("ns1", "http://www.example.com/test-uri");
        setProject(new XMLFragmentNSAttributeProject(nsresolver));

        StringWriter writer = new StringWriter();
        xmlMarshaller.marshal(getWriteControlObject("nsx", "http://www.example.com/test-uri"), writer);
        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        objectToXMLDocumentTest(testDocument, XML_RESOURCE_DIFF_PFX, "testObjectToXMLStringWriterDifferentPrefix");
    }

    /**
     * Test FormattedWriterRecord - prefixes and uris match
     */
    public void testObjectToFormattedXMLStringWriter() throws Exception {
        NamespaceResolver nsresolver = new NamespaceResolver();
        nsresolver.put("ns1", "http://www.example.com/test-uri");
        setProject(new XMLFragmentNSAttributeProject(nsresolver));

        StringWriter writer = new StringWriter();
        xmlMarshaller.setFormattedOutput(true);
        xmlMarshaller.marshal(getWriteControlObject("ns1", "http://www.example.com/test-uri"), writer);
        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Document xdoc = parser.parse(inputStream);
        inputStream.close();
        
        log("\n**testObjectToFormattedXMLStringWriter**");
        log("Expected:");
        log(xdoc);
        log("\nActual:");
        log(testDocument);
        log("\n");
        
        assertXMLIdentical(xdoc, importNodeFix(testDocument));
    }

    /**
     * Test FormattedWriterRecord - prefixes don't match
     */
    public void testObjectToFormattedXMLStringWriterDifferentPrefix() throws Exception {
        NamespaceResolver nsresolver = new NamespaceResolver();
        nsresolver.put("ns1", "http://www.example.com/test-uri");
        setProject(new XMLFragmentNSAttributeProject(nsresolver));

        StringWriter writer = new StringWriter();
        xmlMarshaller.setFormattedOutput(true);
        xmlMarshaller.marshal(getWriteControlObject("nsx", "http://www.example.com/test-uri"), writer);
        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_DIFF_PFX);
        Document xdoc = parser.parse(inputStream);
        inputStream.close();
        
        log("\n**testObjectToFormattedXMLStringWriterDifferentPrefix**");
        log("Expected:");
        log(xdoc);
        log("\nActual:");
        log(testDocument);
        log("\n");
        
        assertXMLIdentical(xdoc, importNodeFix(testDocument));
    }
    
    // ------------------ CONVENIENCE METHODS ------------------ //
    protected void objectToXMLDocumentTest(Document testDocument, String resource, String testCase) throws Exception {
        setControlDocument(resource);
        log("\n**"+testCase+"**");
        log("Expected:");
        log(getWriteControlDocument());
        log("\nActual:");
        log(testDocument);
        log("\n");
        
        assertXMLIdentical(getWriteControlDocument(), importNodeFix(testDocument));
    }
    
    protected void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        log(getReadControlObject().toString());
        log("Actual:");
        log(testObject.toString());
        log("\n");
        assertEquals(getReadControlObject(), testObject);
    }    
    
    protected Document importNodeFix(Document testDocument) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(testDocument), new StreamResult(writer));
            StringReader reader = new StringReader(writer.toString());
            testDocument = parser.parse(new InputSource(reader));
            writer.close();
            reader.close();
        } catch (Exception x) {}
        
        return testDocument;
    }
}

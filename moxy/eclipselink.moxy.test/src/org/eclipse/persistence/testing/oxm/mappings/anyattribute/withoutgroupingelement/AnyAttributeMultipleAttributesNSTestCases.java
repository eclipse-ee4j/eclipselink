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
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement;

import java.io.InputStream;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class AnyAttributeMultipleAttributesNSTestCases extends XMLMappingTestCases {
    public AnyAttributeMultipleAttributesNSTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyAttributeWithoutGroupingElementNSProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/withoutgroupingelement/multiple_attributes_ns.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        HashMap any = new HashMap();
        QName name = new QName("www.example.com/some-dir/some.xsd", "first-name");
        any.put(name, "Matt");
        name = new QName("http://www.w3.org/2000/xmlns/", "myns");
        any.put(name, "www.example.com/some-dir/some.xsd");
        name = new QName("www.example.com/some-dir/some.xsd", "last-name");
        any.put(name, "MacIvor");
        name = new QName(XMLConstants.XMLNS_URL, "ns0");
        any.put(name, XMLConstants.SCHEMA_INSTANCE_URL);
        name = new QName(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.NO_NS_SCHEMA_LOCATION);
        any.put(name, "someSchema.xsd");
        root.setAny(any);
        return root;
    }

    public Object getWriteControlObject() {
        Root root = new Root();
        HashMap any = new HashMap();
        QName name = new QName("www.example.com/some-dir/some.xsd", "first-name");
        any.put(name, "Matt");

        name = new QName("www.example.com/some-dir/some.xsd", "last-name");
        any.put(name, "MacIvor");

        name = new QName(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.NO_NS_SCHEMA_LOCATION);
        any.put(name, "someSchema.xsd");
        
        root.setAny(any);
        return root;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeMultipleAttributesNSTestCases" };
        junit.textui.TestRunner.main(arguments);
    }

    public void testLoadFromStreamSource() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        StreamSource theSource = new StreamSource(instream);
        Object testObject = xmlUnmarshaller.unmarshal(theSource);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testLoadFromSAXSource() throws Exception {
    System.out.println("testLoadFromSAXSource");
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        InputSource insource = new InputSource(instream);
        SAXSource theSource = new SAXSource(insource);
        Object testObject = xmlUnmarshaller.unmarshal(theSource);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testLoadFromSAXSourceWithReader() throws Exception {
        System.out.println("testLoadFromSAXSourceWithReader");
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        InputSource insource = new InputSource(instream);
        SAXSource theSource = new SAXSource(insource);
        
        SAXParserFactory factory = SAXParserFactory.newInstance();        
        factory.setNamespaceAware(true);
        XMLReader xmlReader = factory.newSAXParser().getXMLReader();
        xmlReader.setFeature("http://xml.org/sax/features/namespaces",true);
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes",true);
        
        theSource.setXMLReader(xmlReader);        
        Object testObject = xmlUnmarshaller.unmarshal(theSource);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testLoadFromDOMSource() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Node node = parser.parse(instream);
        DOMSource theSource = new DOMSource(node);

        Object testObject = xmlUnmarshaller.unmarshal(theSource);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromInputSource() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        InputSource insource = new InputSource(instream);
        Object testObject = xmlUnmarshaller.unmarshal(insource);
        instream.close();
        xmlToObjectTest(testObject);
    }
}

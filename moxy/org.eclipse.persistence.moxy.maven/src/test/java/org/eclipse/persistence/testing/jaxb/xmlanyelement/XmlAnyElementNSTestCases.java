/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlAnyElementNSTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employee_ns.xml";
    private final static String XML_CHILD_ELEMENTS = "org/eclipse/persistence/testing/jaxb/xmlanyelement/child_elements_all_ns.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employee_ns.json";

    public XmlAnyElementNSTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = Employee.class;
        classes[1] = Address.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");

        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("namespace1", "ns1");
        namespaces.put("namespace2", "ns2");
        jaxbMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    protected Object getJSONReadControlObject() {
        Employee emp = (Employee)getControlObject();
        Object objectRemoved = ((ArrayList)emp.elements).remove(emp.elements.size()-1);
        ((ArrayList)emp.elements).add(0, objectRemoved);
        //remove namespace declaration
        ((Element)((ArrayList)emp.elements).get(3)).removeAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "myns");
        return emp;
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.name = "John Doe";
        employee.homeAddress  = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";

        employee.elements = new ArrayList();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream(XML_CHILD_ELEMENTS));
            Element rootElem = doc.getDocumentElement();
            NodeList children = rootElem.getChildNodes();
            int i =0;
            for(i = 0; i < children.getLength(); i++) {
                if(children.item(i).getNodeType() == Element.ELEMENT_NODE) {
                    employee.elements.add(children.item(i));
                }
            }
            ((ArrayList)employee.elements).add(0,((ArrayList)employee.elements).get(employee.elements.size()-1));

        } catch(Exception ex) {}

        return employee;
    }

    public void testObjectToContentHandler() throws Exception {
    }
    public void testObjectToXMLDocument() throws Exception {
    }
    public void testXMLToObjectFromSAXSource() throws Exception {
        if(isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            InputSource source = new InputSource(instream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            try {
                factory.setFeature(XMLReader.REPORT_IGNORED_ELEMENT_CONTENT_WHITESPACE_FEATURE, true);
            } catch(org.xml.sax.SAXNotRecognizedException ex) {
                //  ignore if the parser doesn't recognize or support this feature
            } catch(org.xml.sax.SAXNotSupportedException ex) {
            } catch (ParserConfigurationException e) {
            }

            org.xml.sax.XMLReader reader = factory.newSAXParser().getXMLReader();
            SAXSource saxSource = new SAXSource(reader, source);

            Object testObject = jaxbUnmarshaller.unmarshal(saxSource);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

}

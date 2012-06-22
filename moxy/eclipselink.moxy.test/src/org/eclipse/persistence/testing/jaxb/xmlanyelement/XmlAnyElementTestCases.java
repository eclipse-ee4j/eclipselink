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
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlAnyElementTestCases extends JAXBTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employee.xml";
    private final static String XML_CHILD_ELEMENTS = "org/eclipse/persistence/testing/jaxb/xmlanyelement/child_elements_all.xml";

    public XmlAnyElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = Employee.class;
        classes[1] = Address.class;
        setClasses(classes);
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
            for(int i = 0; i < children.getLength(); i++) {
                if(children.item(i).getNodeType() == Element.ELEMENT_NODE) {
                    employee.elements.add(children.item(i));
                }
            }
        } catch(Exception ex) {}


        return employee;
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
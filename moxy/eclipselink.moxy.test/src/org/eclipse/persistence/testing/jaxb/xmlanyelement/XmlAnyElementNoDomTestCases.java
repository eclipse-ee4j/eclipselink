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
 *     Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlAnyElementNoDomTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employee_nodom.xml";    
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employee_nodom.json";

    public XmlAnyElementNoDomTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeLax.class;
        classes[1] = Address.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(JAXBMarshaller.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(JAXBUnmarshaller.JSON_ATTRIBUTE_PREFIX, "@");
    }

    public Object getReadControlObject() {
    	EmployeeLax employee = new EmployeeLax();
        employee.name = "John Doe";
        employee.homeAddress  = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";

        employee.elements = new ArrayList();
        employee.elements.add(employee.homeAddress);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         
		try {
			Document doc1 = dbf.newDocumentBuilder().newDocument();		
	        Element elem1 = doc1.createElement("jb1");
	        elem1.setTextContent("abc");
	        doc1.appendChild(elem1);
	        employee.elements.add(doc1.getDocumentElement());
	        
	        Document doc2 = dbf.newDocumentBuilder().newDocument();		
	        Element elem2 = doc2.createElement("jb2");
	        elem2.setTextContent("10");
	        doc2.appendChild(elem2);
	        employee.elements.add(doc2.getDocumentElement());
	       
	        Document doc3 = dbf.newDocumentBuilder().newDocument();		
	        Element elem3 = doc3.createElement("jb1");
	        elem3.setTextContent("def");
	        doc3.appendChild(elem3);
	        employee.elements.add(doc3.getDocumentElement());
	        
	        Document doc4 = dbf.newDocumentBuilder().newDocument();		
	        Element elem4 = doc4.createElement("jb1");
	        elem4.setTextContent("15");
	        doc4.appendChild(elem4);
	        employee.elements.add(doc4.getDocumentElement());
	        
		} catch (ParserConfigurationException e) {			
			e.printStackTrace();
			fail("An error occurred setup up the controlObject");
		}
        employee.elements.add(employee.homeAddress);
        return employee;
    }
    
    public Object getJSONReadControlObject() {
    	EmployeeLax employee = new EmployeeLax();
        employee.name = "John Doe";
        employee.homeAddress  = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";

        employee.elements = new ArrayList();
        employee.elements.add(employee.homeAddress);
        employee.elements.add(employee.homeAddress);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         
		try {
			Document doc1 = dbf.newDocumentBuilder().newDocument();		
	        Element elem1 = doc1.createElement("jb1");
	        elem1.setTextContent("abc");
	        doc1.appendChild(elem1);
	        employee.elements.add(doc1.getDocumentElement());
	        
	        Document doc3 = dbf.newDocumentBuilder().newDocument();		
	        Element elem3 = doc3.createElement("jb1");
	        elem3.setTextContent("def");
	        doc3.appendChild(elem3);
	        employee.elements.add(doc3.getDocumentElement());
	        
	        Document doc4 = dbf.newDocumentBuilder().newDocument();		
	        Element elem4 = doc4.createElement("jb1");
	        elem4.setTextContent("15");
	        doc4.appendChild(elem4);
	        employee.elements.add(doc4.getDocumentElement());
	        
	        Document doc2 = dbf.newDocumentBuilder().newDocument();		
	        Element elem2 = doc2.createElement("jb2");
	        elem2.setTextContent("10");
	        doc2.appendChild(elem2);
	        employee.elements.add(doc2.getDocumentElement());
		} catch (ParserConfigurationException e) {			
			e.printStackTrace();
			fail("An error occurred setup up the controlObject");
		}
        return employee;
    }
    
    public Object getControlObject() {
        EmployeeLax employee = new EmployeeLax();
        employee.name = "John Doe";
        employee.homeAddress  = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";

        employee.elements = new ArrayList();
        employee.elements.add(employee.homeAddress);
        employee.elements.add(new JAXBElement(new QName("jb1"), String.class,"abc") );
        employee.elements.add(new JAXBElement(new QName("jb2"), Integer.class,10) );       
        employee.elements.add(new JAXBElement(new QName("jb1"), String.class,"def") );
        employee.elements.add(new JAXBElement(new QName("jb1"), Integer.class,15) );
        employee.elements.add(employee.homeAddress);
        return employee;
    }
    
	/*
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
*/
}
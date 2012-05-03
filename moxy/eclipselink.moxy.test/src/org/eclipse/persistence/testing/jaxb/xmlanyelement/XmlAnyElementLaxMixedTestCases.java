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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlAnyElementLaxMixedTestCases extends JAXBWithJSONTestCases {
	   private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeLaxMixed.xml";
	   private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeLaxMixed.json";
	    
	    public XmlAnyElementLaxMixedTestCases(String name) throws Exception {
	        super(name);
	        setControlDocument(XML_RESOURCE);
	        setControlJSON(JSON_RESOURCE);
	        Class[] classes = new Class[2];
	        classes[0] = EmployeeLaxMixed.class;
	        classes[1] = Address.class;
	        setClasses(classes);
	        jaxbUnmarshaller.setProperty(JAXBUnmarshaller.JSON_ATTRIBUTE_PREFIX, "@");
	        jaxbMarshaller.setProperty(JAXBMarshaller.JSON_ATTRIBUTE_PREFIX, "@");
	    }

	    protected Object getControlObject() {
	    	EmployeeLaxMixed employee = new EmployeeLaxMixed();
	        
	        employee.name = "John Doe";
	        employee.homeAddress  = new Address();
	        employee.homeAddress.street = "123 Fake Street";
	        employee.homeAddress.city = "Ottawa";
	        employee.homeAddress.country = "Canada";

	        employee.elements = new ArrayList();
	        
	        JAXBElement jb1 = new JAXBElement(new QName("jb1"), String.class, "jb1String");
	        employee.elements.add(jb1);
	        
	        employee.elements.add("new string1");
	        employee.elements.add(10);
	        Address addr = new Address();
	        addr.street = "222 Fake Street";
	        addr.city = "Toronto";
	        addr.country = "Canada";
	        employee.elements.add(addr);
	        employee.elements.add("new string2");
	        Address addr2 = new Address();
	        addr2.street = "second address";
	        addr2.city = "Ottawa";
	        addr2.country = "Canada";
	        employee.elements.add(addr2);
	        
	        JAXBElement jb2 = new JAXBElement(new QName("jb2"), Integer.class, 15);
	        employee.elements.add(jb2);
	        
	        JAXBElement jb3 = new JAXBElement(new QName("jb1"), String.class, "jb3String");
	        employee.elements.add(jb3);
	        
	        JAXBElement jb4 = new JAXBElement(new QName("jb4"), Address.class, addr);
	        employee.elements.add(jb4);
	        return employee;
	    }
	    
	    public Object getReadControlObject() {
	    	EmployeeLaxMixed employee = new EmployeeLaxMixed();
	        
	        employee.name = "John Doe";
	        employee.homeAddress  = new Address();
	        employee.homeAddress.street = "123 Fake Street";
	        employee.homeAddress.city = "Ottawa";
	        employee.homeAddress.country = "Canada";

	        Address addr = new Address();
	        addr.street = "222 Fake Street";
	        addr.city = "Toronto";
	        addr.country = "Canada";
	        
	        employee.elements = new ArrayList();
	        try {     
	        	DocumentBuilder db;
			
				db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
		        Document doc = db.newDocument();
		        Element elem1 = doc.createElement("jb1");
		        elem1.setTextContent("jb1String");
		        
		        employee.elements.add(elem1);
		        
		        employee.elements.add("new string110");
		        
		        employee.elements.add(addr);
		        employee.elements.add("new string2");
		        Address addr2 = new Address();
		        addr2.street = "second address";
		        addr2.city = "Ottawa";
		        addr2.country = "Canada";
		        employee.elements.add(addr2);

		        Element elem2 = doc.createElement("jb2");
		        elem2.setTextContent("15");
		        employee.elements.add(elem2);

		        Element elem3 = doc.createElement("jb1");
		        elem3.setTextContent("jb3String");
		        employee.elements.add(elem3);
	        
	        } catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("An error occurred during setup");
			}

	        JAXBElement jb4 = new JAXBElement(new QName("jb4"), Address.class, addr);
	        employee.elements.add(jb4);
	        return employee;
	    }
	    
	    public Object getJSONReadControlObject() {
	    	EmployeeLaxMixed employee = new EmployeeLaxMixed();
	        
	        employee.name = "John Doe";
	        employee.homeAddress  = new Address();
	        employee.homeAddress.street = "123 Fake Street";
	        employee.homeAddress.city = "Ottawa";
	        employee.homeAddress.country = "Canada";

	        Address addr = new Address();
	        addr.street = "222 Fake Street";
	        addr.city = "Toronto";
	        addr.country = "Canada";
	        
	        employee.elements = new ArrayList();
	        try {     
	        	DocumentBuilder db;
			
				db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
		        Document doc = db.newDocument();
		        Element elem1 = doc.createElement("jb1");
		        elem1.setTextContent("jb1String");
		        employee.elements.add(elem1);
		        
		        Element elem3 = doc.createElement("jb1");
		        elem3.setTextContent("jb3String");
		        employee.elements.add(elem3);
		        
		        employee.elements.add(addr);
		        Address addr2 = new Address();
		        addr2.street = "second address";
		        addr2.city = "Ottawa";
		        addr2.country = "Canada";
		        employee.elements.add(addr2);

		        Element elem2 = doc.createElement("jb2");
		        elem2.setTextContent("15");
		        employee.elements.add(elem2);

		      
	        
	        } catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("An error occurred during setup");
			}

	        JAXBElement jb4 = new JAXBElement(new QName("jb4"), Address.class, addr);
	        employee.elements.add(jb4);
	        
	        employee.elements.add("new string1");
	        employee.elements.add("10");		        				      
	        employee.elements.add("new string2");
	        return employee;
	    }
	    
	    public void testObjectToXMLDocument() throws Exception {    	
	    }
}

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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlAnyElementArrayTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employee.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employee.json";
    private final static String XML_CHILD_ELEMENTS = "org/eclipse/persistence/testing/jaxb/xmlanyelement/child_elements_all.xml";

    public XmlAnyElementArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeArray.class;
        classes[1] = Address.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    protected Object getControlObject() {
    	EmployeeArray employee = new EmployeeArray();
        employee.name = "John Doe";
        employee.homeAddress  = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";

        List elements = getControlChildElements();
        employee.elements = elements.toArray();
      
        return employee;
    }
    
    private List getControlChildElements(){
    	 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         factory.setIgnoringElementContentWhitespace(true);
         try {
             DocumentBuilder builder = factory.newDocumentBuilder();
             Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream(XML_CHILD_ELEMENTS));
             Element rootElem = doc.getDocumentElement();
             NodeList children = rootElem.getChildNodes();

             List elements = new ArrayList();
             int j=0;
             for(int i = 0; i < children.getLength(); i++) {
                 if(children.item(i).getNodeType() == Element.ELEMENT_NODE) {
                 	elements.add(children.item(i));
                 }
             }
             elements.add(0,elements.get(elements.size()-1));
             return elements;
         } catch(Exception ex) {
        	 ex.printStackTrace();
        	 fail("An error occurred during setup");
        	 return null;
         }
    }
    
    
    protected Object getJSONReadControlObject() {
    	EmployeeArray employee = new EmployeeArray();
        employee.name = "John Doe";
        employee.homeAddress  = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";

        List elements = getControlChildElements();
    	Object objectRemoved = elements.remove(elements.size()-1);    	
    	elements.add(0, objectRemoved);
    	//remove namespace declaration
    	((Element)elements.get(3)).removeAttributeNS(XMLConstants.XMLNS_URL, "myns");

    	employee.elements = elements.toArray();
              
    
        
        return employee;
    	  
    }
    public void testSchemaGen() throws Exception{
		InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlanyelement/employee.xsd");		
    	List<InputStream> controlSchemas = new ArrayList<InputStream>();    	
    	controlSchemas.add(controlInputStream);		
		this.testSchemaGen(controlSchemas);
	}

}
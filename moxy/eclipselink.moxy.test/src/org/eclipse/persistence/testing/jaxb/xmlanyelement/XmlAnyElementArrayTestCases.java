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
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlAnyElementArrayTestCases extends JAXBTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employee.xml";
    private final static String XML_CHILD_ELEMENTS = "org/eclipse/persistence/testing/jaxb/xmlanyelement/child_elements_all.xml";

    public XmlAnyElementArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeArray.class;
        classes[1] = Address.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
    	EmployeeArray employee = new EmployeeArray();
        employee.name = "John Doe";
        employee.homeAddress  = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";

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
            employee.elements = elements.toArray();
        } catch(Exception ex) {}


        return employee;
    }
    
    public void testSchemaGen() throws Exception{
		InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlanyelement/employee.xsd");		
    	List<InputStream> controlSchemas = new ArrayList<InputStream>();    	
    	controlSchemas.add(controlInputStream);		
		this.testSchemaGen(controlSchemas);
	}

}
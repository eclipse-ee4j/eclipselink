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

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlAnyElementLaxSingleTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeSingle.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeSingle.json";

    public XmlAnyElementLaxSingleTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeSingle.class;
        classes[1] = Address.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }
    
    protected Object getControlObject() {
    	EmployeeSingle employee = new EmployeeSingle();
        employee.name = "John Doe";
        employee.homeAddress  = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";

        employee.element = new JAXBElement(new QName("newUri","mytag"), Object.class, null);
        return employee;
    }
    
    public Object getReadControlObject() {
    	EmployeeSingle employee = new EmployeeSingle();
        employee.name = "John Doe";
        employee.homeAddress  = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";        
        Element elem = parser.newDocument().createElementNS("newUri", "ns0:mytag");
        elem.setAttributeNS(XMLConstants.SCHEMA_INSTANCE_URL, "xsi:nil", "true");
        elem.setAttributeNS(XMLConstants.XMLNS_URL, "xmlns:xsi", XMLConstants.SCHEMA_INSTANCE_URL);
        elem.setAttributeNS(XMLConstants.XMLNS_URL, "xmlns:ns0", "newUri");
        employee.element = elem;
        
        
        return employee;
        
        
    }

    public Object getJSONReadControlObject() {
    	EmployeeSingle employee = new EmployeeSingle();
        employee.name = "John Doe";
        employee.homeAddress  = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";        
        Element elem = parser.newDocument().createElementNS(null, "mytag");
        employee.element = elem;
        
        return employee;
        
        
    }
   

}
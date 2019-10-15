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

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlAnyElementLaxTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employee.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employee.json";
    private final static String XML_CHILD_ELEMENTS = "org/eclipse/persistence/testing/jaxb/xmlanyelement/child_elements_unknown.xml";

    public XmlAnyElementLaxTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeLax.class;
        classes[1] = Address.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    protected Object getJSONReadControlObject() {
        EmployeeLax emp = (EmployeeLax)getControlObject();
        Object objectRemoved = ((ArrayList)emp.elements).remove(emp.elements.size()-1);
        ((ArrayList)emp.elements).add(0, objectRemoved);
        //remove namespace declaration
        ((Element)((ArrayList)emp.elements).get(3)).removeAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "myns");
        return emp;
    }

    protected Object getControlObject() {
        EmployeeLax employee = new EmployeeLax();
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
            Address addr = new Address();
            addr.street = "222 Fake Street";
            addr.city = "Toronto";
            addr.country = "Canada";
            ((ArrayList)employee.elements).add(0, addr);
            employee.elements.add(addr);

        } catch(Exception ex) {}

        return employee;
    }

}

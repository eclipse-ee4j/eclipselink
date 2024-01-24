/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.xml.bind.JAXBElement;

import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Element;

public class XmlAnyElementLaxSingleTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeSingle.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeSingle.json";

    public XmlAnyElementLaxSingleTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class<?>[] classes = new Class<?>[2];
        classes[0] = EmployeeSingle.class;
        classes[1] = Address.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    @Override
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

    @Override
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

    @Override
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

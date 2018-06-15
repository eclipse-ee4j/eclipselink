/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/24/2018-2.7.2 Radek Felcman
//       - 534812 - HIERARCHY_REQUEST_ERR marshalling a CDATA-containing XmlAnyElement to a Node result
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class XmlAnyElementLaxCDATATestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeLaxCDATA.xml";

    public XmlAnyElementLaxCDATATestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeLaxCDATA.class;
        classes[1] = Address.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeLaxCDATA employee = new EmployeeLaxCDATA();
        employee.name = "John Doe";
        employee.homeAddress = new Address();
        employee.homeAddress.street = "123 Fake Street";
        employee.homeAddress.city = "Ottawa";
        employee.homeAddress.country = "Canada";
        try {
            employee.element = parser.parse(new ByteArrayInputStream("<mytag><![CDATA[Hello world!<>'\"]]></mytag>".getBytes())).getDocumentElement();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employee;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

}

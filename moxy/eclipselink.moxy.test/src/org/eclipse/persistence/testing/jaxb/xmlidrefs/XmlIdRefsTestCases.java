/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - April 14/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidrefs;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlIdRefsTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidrefs/instance.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidrefs/instance.json";
    private static final String CONTROL_PHONE_ID_1 = "123";
    private static final String CONTROL_PHONE_ID_2 = "456";

    public XmlIdRefsTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[1];
        classes[0] = Root.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        Root root = new Root();
        root.phoneNumbers = new ArrayList();

        Employee employee = new Employee();
        employee.elementPhoneNumbers = new ArrayList();
        employee.attributePhoneNumbers = new ArrayList();
        root.employee = employee;

        PhoneNumber num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_1;
        employee.elementPhoneNumbers.add(num);
        employee.attributePhoneNumbers.add(num);
        root.phoneNumbers.add(num);

        num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_2;
        employee.elementPhoneNumbers.add(num);
        employee.attributePhoneNumbers.add(num);
        root.phoneNumbers.add(num);

        return root;
    }

}

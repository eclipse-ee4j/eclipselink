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
// mmacivor - June 05/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class EmployeeSingleTestCases  extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/employee-single.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/employee-single.json";

    public EmployeeSingleTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = TestObjectFactory.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeSingle employee = new EmployeeSingle();
        employee.intRoot = new JAXBElement(new QName("myns", "integer-root"), Integer.class, new Integer(21));
        return employee;
     }

}

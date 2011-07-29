/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - September 14/2009 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlJoinNodeTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE =  "org/eclipse/persistence/testing/jaxb/annotations/xmljoinnode/company.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmljoinnode/company.json";

    public XmlJoinNodeTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ Company.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }
    
    public Object getControlObject() {
        Address ottawa100 = new Address(100, "45 O'Connor St.", "400", "Ottawa", "K1P1A4");
        Address ottawa200 = new Address(200, "1 Anystreet Rd.", "9", "Ottawa", "K4P1A2");
        Address kanata100 = new Address(100, "99 Some St.", "1001", "Kanata", "K0A3m0");
        Employee emp101 = new Employee(101, ottawa100);
        Employee emp102 = new Employee(102, kanata100);
        ArrayList<Employee> empList = new ArrayList<Employee>();
        empList.add(emp101);
        empList.add(emp102);
        ArrayList<Address> addList = new ArrayList<Address>();
        addList.add(kanata100);
        addList.add(ottawa100);
        addList.add(ottawa200);
        return new Company(empList, addList);
    }
}

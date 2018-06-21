/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     mmacivor - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.json.JsonSchemaOutputResolver;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlPathTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/xmlpathannotation.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/xmlpathannotation.json";
    private static final String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/xmlpathschema.json";
    public XmlPathTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class, Employee.class, Address.class, PhoneNumber.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Object getJSONReadControlObject(){
        Root root = (Root)getControlObject();
        Employee emp = root.employees.get(0);
        emp.attributes = new HashMap<QName, String>();
        emp.attributes.put(new QName("attr1"), "value1");
        emp.attributes.put(new QName("attr2"), "value2");
        return root;
    }

    public Object getControlObject() {
        Employee emp = new Employee();
        emp.id = 101;
        emp.firstName = "Jane";
        emp.lastName = "Doe";
        emp.address = new Address();
        emp.address.street = "123 Fake Street";
        emp.address.city = "Ottawa";
        emp.address.id="102";

        emp.phones = new Vector<PhoneNumber>();

        PhoneNumber num1 = new PhoneNumber();
        num1.number = "123-4567";
        emp.phones.add(num1);

        PhoneNumber num2 = new PhoneNumber();
        num2.number = "234-5678";
        emp.phones.add(num2);

        emp.attributes = new HashMap<QName, String>();
        emp.attributes.put(new QName("attr1"), "value1");
        emp.attributes.put(new QName("http://myns.com/myns", "attr2"), "value2");

        Root root = new Root();
        root.employees = new Vector<Employee>();
        root.addresses = new Vector<Address>();

        root.employees.add(emp);
        root.addresses.add(emp.address);

        return root;
    }

    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = ClassLoader.getSystemResourceAsStream(JSON_SCHEMA_RESOURCE);
        super.generateJSONSchema(controlSchema);

    }





}

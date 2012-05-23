/*******************************************************************************
* Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath;

import java.util.HashMap;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlPathUnmappedTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/xmlpathannotation.xml";
    private static final String XML_RESOURCE_UNMAPPED = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/xmlpathannotation_unmapped.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/xmlpathannotation.json";
    private static final String JSON_RESOURCE_UNMAPPED = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/xmlpathannotation_unmapped.json";
    
    public XmlPathUnmappedTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class, Employee.class, Address.class, PhoneNumber.class});
        setControlDocument(XML_RESOURCE_UNMAPPED);
        setWriteControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE_UNMAPPED);
        setWriteControlJSON(JSON_RESOURCE);
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
    
    
}

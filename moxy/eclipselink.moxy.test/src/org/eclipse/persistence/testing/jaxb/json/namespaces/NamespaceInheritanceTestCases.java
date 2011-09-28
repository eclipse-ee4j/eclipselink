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
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.namespaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.PropertyException;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.inheritance.A;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class NamespaceInheritanceTestCases extends JSONMarshalUnmarshalTestCases{

	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/employee.json";
	private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/employee_different.json";

	public NamespaceInheritanceTestCases(String name) throws Exception {
		super(name);
		setControlJSON(JSON_RESOURCE);
		setWriteControlJSON(JSON_WRITE_RESOURCE);
		setClasses(new Class[]{Employee.class});
	}
	
	protected Object getControlObject() {
	    Employee emp = getEmployee();
		
		QName qname = new QName("", "person");
		JAXBElement jaxbElement = new JAXBElement(qname, Person.class, emp);

		return jaxbElement;
	}

	public Object getWriteControlObject() {
	    Employee emp = getEmployee();

		QName qname = new QName("", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Person.class, emp);

		return jaxbElement;
	}
	
	private Employee getEmployee() {
		Employee emp = new Employee();
		emp.salary = 10000;
		emp.setId(10);
		emp.setFirstName("Jill");
		emp.setLastName("MacDonald");
		
		List<String> middleNames = new ArrayList<String>();
		middleNames.add("Jane");
		middleNames.add("Janice");
		emp.setMiddleNames(middleNames);
		
		Address addr = new Address();
		addr.setStreet("The Street");
		addr.setCity("Ottawa");
		emp.setAddress(addr);
		return emp;
	}
	
	public void setUp(){
		super.setUp();
		
        Map<String, String> marshalNamespaceMap = new HashMap<String, String>();		
        marshalNamespaceMap.put("aaa", "namespace0");
        marshalNamespaceMap.put("bbb", "namespace1");
        marshalNamespaceMap.put("ccc", "namespace2");
        marshalNamespaceMap.put("ddd", "namespace3");
        marshalNamespaceMap.put("eee", XMLConstants.SCHEMA_INSTANCE_URL);
		
		Map<String, String> unmarshalNamespaceMap = new HashMap<String, String>();		
		unmarshalNamespaceMap.put("ns0", "namespace0");
		unmarshalNamespaceMap.put("ns1", "namespace1");
		unmarshalNamespaceMap.put("ns2", "namespace2");
		unmarshalNamespaceMap.put("ns3", "namespace3");
		unmarshalNamespaceMap.put("ns4", XMLConstants.SCHEMA_INSTANCE_URL);
				
		try{
		    jsonMarshaller.setProperty(JAXBContext.NAMESPACES, marshalNamespaceMap);
		    jsonUnmarshaller.setProperty(JAXBContext.NAMESPACES, unmarshalNamespaceMap);
		}catch(PropertyException e){
			e.printStackTrace();
			fail("An error occurred setting properties during setup.");
		}
	}
}

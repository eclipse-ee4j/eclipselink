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

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
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
	
	public void setUp() throws Exception{
		super.setUp();
		
        Map<String, String> marshalNamespaceMap = new HashMap<String, String>();	
        marshalNamespaceMap.put("namespace0", "aaa");
        marshalNamespaceMap.put("namespace1", "bbb");
        marshalNamespaceMap.put("namespace2", "ccc");
        marshalNamespaceMap.put("namespace3", "ddd");
        marshalNamespaceMap.put(XMLConstants.SCHEMA_INSTANCE_URL, "eee");
   
		Map<String, String> unmarshalNamespaceMap = new HashMap<String, String>();	
		unmarshalNamespaceMap.put("namespace0", "ns0");
		unmarshalNamespaceMap.put("namespace1", "ns1");
		unmarshalNamespaceMap.put("namespace2", "ns2");
		unmarshalNamespaceMap.put("namespace3", "ns3");
		unmarshalNamespaceMap.put(XMLConstants.SCHEMA_INSTANCE_URL, "ns4");
	
		
		try{
		    jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, marshalNamespaceMap);
		    jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, unmarshalNamespaceMap);
		}catch(PropertyException e){
			e.printStackTrace();
			fail("An error occurred setting properties during setup.");
		}
	}
}

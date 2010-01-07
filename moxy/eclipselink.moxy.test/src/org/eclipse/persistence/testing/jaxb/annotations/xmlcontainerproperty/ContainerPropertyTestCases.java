/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - January 09, 2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlcontainerproperty;

import java.util.Vector;

import org.eclipse.persistence.testing.jaxb.*;

public class ContainerPropertyTestCases extends JAXBTestCases {

	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlcontainerproperty/containeraccessor.xml";
	
	public ContainerPropertyTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[] {Employee.class, Address.class, PhoneNumber.class});
		setControlDocument(XML_RESOURCE);
	}
	
	public Employee getControlObject() {
		Employee emp = new Employee();
		emp.id = 10;
		emp.firstName = "Jane";
		emp.lastName = "Doe";
		emp.address = new Address();
		emp.address.street = "123 Fake Street";
		emp.address.city = "Ottawa";
		emp.address.owningEmployee = emp;
		emp.phoneNumbers = new Vector<PhoneNumber>();
		
		PhoneNumber num1 = new PhoneNumber();
		num1.number = "123-4567";
		num1.owningEmployee = emp;
		emp.phoneNumbers.add(num1);
		
		PhoneNumber num2 = new PhoneNumber();
		num2.number = "234-5678";
		num2.owningEmployee = emp;
		emp.phoneNumbers.add(num2);
		
		return emp;
	}
}

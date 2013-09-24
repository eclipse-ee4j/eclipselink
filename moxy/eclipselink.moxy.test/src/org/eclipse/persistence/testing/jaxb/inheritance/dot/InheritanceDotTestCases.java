/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - September 2013
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.inheritance.dot;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InheritanceDotTestCases extends JAXBWithJSONTestCases{
	  
	public InheritanceDotTestCases(String name) throws Exception {
	    super(name);
		setClasses(new Class[] { Person.class, Employee.class });
		setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/inheritanceDot.xml");
		setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/inheritanceDot.json");
	}
	    
	public Object getControlObject() {
	    Employee emp = new Employee();
	    emp.name = "Bob Smith";
	    emp.badgeNumber = "123";
	    return emp;
	}
		
}

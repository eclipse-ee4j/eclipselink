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
* mmacivor - June 05/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class EmployeeSingleTestCases  extends JAXBTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/employee-single.xml";

	public EmployeeSingleTestCases(String name) throws Exception {
		super(name);
        setControlDocument(XML_RESOURCE);        
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

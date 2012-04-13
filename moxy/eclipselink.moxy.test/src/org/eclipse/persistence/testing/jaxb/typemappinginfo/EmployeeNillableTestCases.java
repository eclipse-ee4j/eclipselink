/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith  November 2011 - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class EmployeeNillableTestCases extends EmployeeTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/employee_nil.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/employee_nil.json";
    
	public EmployeeNillableTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
	}
	
	protected Object getControlObject() {
		QName qname = new QName("someUri", "testTagName");
		JAXBElement jaxbElement = new JAXBElement(qname, Employee.class, null);
		return jaxbElement;
	}
	
	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}
}

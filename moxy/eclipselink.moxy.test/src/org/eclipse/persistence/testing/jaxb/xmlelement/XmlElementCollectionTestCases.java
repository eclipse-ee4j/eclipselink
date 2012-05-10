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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlelement;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementCollectionTestCases extends JAXBWithJSONTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_collection.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_collection.json";

    public XmlElementCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeCollection.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeCollection employee = new EmployeeCollection();
		ArrayList ids = new ArrayList();
		ids.add("123");
		ids.add("456");
		ids.add("789");
		employee.ids = ids;
        return employee;
    }
}

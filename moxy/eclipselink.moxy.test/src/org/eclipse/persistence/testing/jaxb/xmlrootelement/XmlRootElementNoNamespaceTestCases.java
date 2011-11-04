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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlrootelement;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlRootElementNoNamespaceTestCases extends JAXBWithJSONTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/employee_nonamespace.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/employee_nonamespace.json";
	private final static int CONTROL_ID = 10;

    public XmlRootElementNoNamespaceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeNoNamespace.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeNoNamespace employee = new EmployeeNoNamespace();
		employee.id = CONTROL_ID;
        return employee;
    }
}

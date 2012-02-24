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
 *     Denise Smith - September 3, 2009 Initial test
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.multiplepackage;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.EmployeeNamespace;

public class MultiplePackageTestCases extends JAXBWithJSONTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/multiplepackage/dept.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/multiplepackage/dept.json";
	private final static int CONTROL_ID = 10;

    public MultiplePackageTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeNamespace.class;
        classes[1] = Department.class;
        
        jaxbContext = JAXBContextFactory.createContext(classes, null);
        xmlContext =((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext(); 
        setProject(xmlContext.getSession(0).getProject());
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    protected Object getControlObject() {
    	Department dept = new Department();
        EmployeeNamespace employee = new EmployeeNamespace();
		employee.id = CONTROL_ID;
        dept.emp = employee;
        dept.id = 1;
        return dept;
    }

}

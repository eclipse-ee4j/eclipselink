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
package org.eclipse.persistence.testing.oxm.xmlroot.complex.inheritance;

import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class NullDefaultRootXMLRootTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/inheritance/employeeNullRoot.xml";

    /**
      <?xml version="1.0" encoding="UTF-8"?>
      <oxm:blah xsi:type="oxm:emp" xmlns:oxm="test" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <name>Joe Smith</name>
        <id>15</id>
      </oxm:blah>
     */
    protected final static String CONTROL_PERSON_NAME = "Joe Smith";
    protected final static int CONTROL_ID = 15;
    protected final static String CONTROL_ELEMENT_NAME = "oxm:blah";
    protected final static String CONTROL_NAMESPACE_URI = "test";

    public NullDefaultRootXMLRootTestCases(String name) throws Exception {
        super(name);
        setControlDocument(getXMLResource());
        setProject(getTopLinkProject());
    }

    public Project getTopLinkProject() {
        return new XMLRootComplexInheritanceProject();
    }

    public String getXMLResource() {
        return XML_RESOURCE;
    }

    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.setName(CONTROL_PERSON_NAME);
        emp.setEmpId(CONTROL_ID);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(emp);
        return xmlRoot;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlroot.complex.inheritance.NullDefaultRootXMLRootTestCases" };
        TestRunner.main(arguments);
    }
}

/*******************************************************************************
* Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlreadonly;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlReadOnlyTestCases extends JAXBWithJSONTestCases {

    private static final String XML_READ_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlreadonly/employee_read.xml";
    private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlreadonly/employee_write.xml";
    
    private static final String JSON_READ_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlreadonly/employee_read.json";
    private static final String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlreadonly/employee_write.json";
    public XmlReadOnlyTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Employee.class});
        setControlDocument(XML_READ_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_READ_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
    }
    
    public Object getControlObject() {
        Employee emp = new Employee();
        emp.name = "Jane Doe";
        emp.readOnlyField = "Read Only Data";
        return emp;
    }
    
    @Override
    public void testRoundTrip() {
        //Not Applicable due to use of separate read and write docs
    }
}


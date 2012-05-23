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
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlaccessmethods;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAccessMethodsTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlaccessmethods/employee.xml";
    private static final String WRITE_XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlaccessmethods/employee_write.xml";

    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlaccessmethods/employee.json";
    private static final String WRITE_JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlaccessmethods/employee_write.json";

    public XmlAccessMethodsTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Employee.class});
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(WRITE_XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(WRITE_JSON_RESOURCE);
    }

    public Object getControlObject() {
        Employee emp = new Employee();
        emp.property1 = "Value1";
        emp.property2 = "Value2";

        emp.wasProp1SetCalled = true;
        emp.wasProp2SetCalled = true;
        return emp;
    }

    @Override
    public Object getWriteControlObject() {
        Employee emp = new Employee();
        emp.property1 = "Value1";
        emp.setName(5);
        emp.property2 = "Value2";

        emp.wasProp1SetCalled = true;
        emp.wasProp2SetCalled = true;
        return emp;
    }

    @Override
    public void testRoundTrip() throws Exception {
        return;
    }

}
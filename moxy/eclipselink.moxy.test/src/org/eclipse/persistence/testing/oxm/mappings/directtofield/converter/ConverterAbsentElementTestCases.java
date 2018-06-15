/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Denise Smith - September 22 /2009
package org.eclipse.persistence.testing.oxm.mappings.directtofield.converter;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class ConverterAbsentElementTestCases  extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/converter/employee_absent.xml";

    public ConverterAbsentElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project aProject = new ConverterProject();
        setProject(aProject);
    }

    public void setUp() throws Exception{
        super.setUp();
        MyConverter.HIT_CONVERTER = false;
    }

    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.firstName = "Bill";
        emp.lastName = "Jones";

        return emp;
    }

    public void xmlToObjectTest(Object testObject) throws Exception{
        super.xmlToObjectTest(testObject);
        assertFalse("Converter was  hit" , MyConverter.HIT_CONVERTER);
    }

}

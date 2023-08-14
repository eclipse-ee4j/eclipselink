/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// Denise Smith - September 22 /2009
package org.eclipse.persistence.testing.oxm.mappings.directtofield.converter;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class ConverterEmptyElementTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/converter/employee_empty.xml";

    public ConverterEmptyElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project aProject = new ConverterProject();
        setProject(aProject);
    }

    @Override
    public void setUp() throws Exception{
        super.setUp();
        MyConverter.HIT_CONVERTER = false;
    }

    @Override
    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.firstName = "Bill";
        emp.lastName = "Jones";
        emp.gender ="";

        return emp;
    }

    @Override
    public void xmlToObjectTest(Object testObject) throws Exception{
        super.xmlToObjectTest(testObject);
        assertTrue(MyConverter.HIT_CONVERTER);
    }


}

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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement.positional;

// JDK imports
import java.io.InputStream;

// TopLink imports
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

import org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement.Employee;

public class PositionalRootElementTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/simpletypes/rootelement/positional/PositionalRootElementTest.xml";
    private final static String CONTROL_EMPLOYEE_NAME = "Jane";
    private final static String CONTROL_EMPLOYEE_LAST_NAME = "Doe";
    private final static int CONTROL_EMPLOYEE_AGE = 35;
    private XMLMarshaller xmlMarshaller;

    public PositionalRootElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new PositionalRootElementProject());
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setName(CONTROL_EMPLOYEE_NAME);
        employee.setLastName(CONTROL_EMPLOYEE_LAST_NAME);
        employee.setAge(CONTROL_EMPLOYEE_AGE);

        return employee;
    }

}

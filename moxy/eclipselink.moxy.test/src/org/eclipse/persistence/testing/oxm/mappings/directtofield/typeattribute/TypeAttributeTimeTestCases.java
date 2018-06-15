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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.typeattribute;

import java.io.InputStream;
import java.util.Calendar;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class TypeAttributeTimeTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/typeattribute/TypeAttributeTime.xml";
    private final static Calendar CONTROL_ID = Calendar.getInstance();
    private final static String CONTROL_FIRST_NAME = "Jane";
    private final static String CONTROL_LAST_NAME = "Doe";
    private final static String XML_RESOURCE_INVALID = "org/eclipse/persistence/testing/oxm/mappings/directtofield/typeattribute/InvalidTypeAttributeTime.xml";

    public TypeAttributeTimeTestCases(String name) throws Exception {
        super(name);
        buildCalendar();
        setControlDocument(XML_RESOURCE);
        setProject(new TypeAttributeTimeProject());
    }

    private void buildCalendar() {
        CONTROL_ID.clear();
        CONTROL_ID.set(Calendar.HOUR, 9);
        CONTROL_ID.set(Calendar.HOUR_OF_DAY, 9);
        CONTROL_ID.set(Calendar.AM_PM, Calendar.AM);
        CONTROL_ID.set(Calendar.MINUTE, 30);
        CONTROL_ID.set(Calendar.SECOND, 45);
        CONTROL_ID.set(Calendar.MILLISECOND, 0);
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setIdentifier(CONTROL_ID);
        employee.setFirstName(CONTROL_FIRST_NAME);
        employee.setLastName(CONTROL_LAST_NAME);
        return employee;
    }

    public void testErrorCase() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream(XML_RESOURCE_INVALID);
        try {
            xmlUnmarshaller.unmarshal(in);
            fail("No Exception was thrown");
        } catch (ConversionException ex) {
            System.out.println(ex.getMessage());
            assertTrue("The wrong exception was thrown for invalid time format", ex.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
        }
    }
}

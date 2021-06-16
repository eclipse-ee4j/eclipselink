/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.typeattribute;

import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.exceptions.ConversionException;

public class TypeAttributeDateTimeTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/typeattribute/TypeAttributeDateTime.xml";
    private final static Calendar CONTROL_ID = Calendar.getInstance();
    private final static String CONTROL_FIRST_NAME = "Jane";
    private final static String CONTROL_LAST_NAME = "Doe";
    private final static String XML_RESOURCE_INVALID = "org/eclipse/persistence/testing/oxm/mappings/directtofield/typeattribute/InvalidTypeAttributeDateTime.xml";

    public TypeAttributeDateTimeTestCases(String name) throws Exception {
        super(name);
        buildCalendar();
        setControlDocument(XML_RESOURCE);
        setProject(new TypeAttributeProject());
    }

    private void buildCalendar() {
        CONTROL_ID.clear();
        CONTROL_ID.set(Calendar.YEAR, 2000);
        CONTROL_ID.set(Calendar.MONTH, Calendar.JANUARY);
        CONTROL_ID.set(Calendar.DAY_OF_MONTH, 2);
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
            assertTrue("The incorrect exception was thrown", ex.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
        }
    }
}

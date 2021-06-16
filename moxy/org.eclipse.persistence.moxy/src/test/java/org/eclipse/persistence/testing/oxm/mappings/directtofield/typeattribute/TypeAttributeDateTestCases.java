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

import java.util.Calendar;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import java.io.InputStream;
import org.eclipse.persistence.exceptions.ConversionException;

public class TypeAttributeDateTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/typeattribute/TypeAttributeDate.xml";
    private final static Calendar CONTROL_ID = Calendar.getInstance();
    private final static String CONTROL_FIRST_NAME = "Jane";
    private final static String CONTROL_LAST_NAME = "Doe";
    private final static String XML_RESOURCE_INVALID = "org/eclipse/persistence/testing/oxm/mappings/directtofield/typeattribute/InvalidTypeAttributeDate.xml";

    public TypeAttributeDateTestCases(String name) throws Exception {
        super(name);
        buildCalendar();
        setControlDocument(XML_RESOURCE);
        setProject(new TypeAttributeDateProject());
    }

    private void buildCalendar() {
        CONTROL_ID.clear();
        CONTROL_ID.set(Calendar.YEAR, 2000);
        CONTROL_ID.set(Calendar.MONTH, Calendar.JANUARY);
        CONTROL_ID.set(Calendar.DAY_OF_MONTH, 2);
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
            assertTrue("The wrong exception was thrown for invalid date format", ex.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
        }
    }
}

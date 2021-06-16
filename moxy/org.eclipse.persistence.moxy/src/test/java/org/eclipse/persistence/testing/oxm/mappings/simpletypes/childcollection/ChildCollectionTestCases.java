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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.childcollection;

// JDK imports
import java.io.InputStream;

// TopLink imports
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class ChildCollectionTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/simpletypes/childcollection/SimpleChildCollectionTest.xml";
    private final static String CONTROL_EMPLOYEE_NAME = "Jane Doh";
    private final static String CONTROL_EMPLOYEE_PHONE_1 = "(613)444-1234";
    private final static String CONTROL_EMPLOYEE_PHONE_2 = "(613)555-1234";
    private final static String CONTROL_EMPLOYEE_PHONE_3 = "(613)666-1234";
    private XMLMarshaller xmlMarshaller;

    public ChildCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new EmployeeProject());
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setName(CONTROL_EMPLOYEE_NAME);

        java.util.Vector phoneNumbers = new java.util.Vector();
        phoneNumbers.addElement(new Phone(CONTROL_EMPLOYEE_PHONE_1));
        phoneNumbers.addElement(new Phone(CONTROL_EMPLOYEE_PHONE_2));
        phoneNumbers.addElement(new Phone(CONTROL_EMPLOYEE_PHONE_3));
        employee.setPhones(phoneNumbers);

        return employee;
    }

}

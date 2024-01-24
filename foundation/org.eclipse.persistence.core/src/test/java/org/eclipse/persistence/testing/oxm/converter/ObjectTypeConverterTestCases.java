/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.converter;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;

import java.io.InputStream;

/**
 *  @version $Header: ObjectTypeConverterTestCases.java 24-feb-2005.10:42:56 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class ObjectTypeConverterTestCases extends OXTestCase {
    XMLContext context;
    XMLMarshaller marshaller;
    XMLUnmarshaller unmarshaller;

    public ObjectTypeConverterTestCases(String name) {
        super(name);
        context = getXMLContext("ConverterSession");
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
    }

    public void testWriteEmployeeM() throws Exception {
        Employee emp = new Employee();
        emp.firstName = "Bill";
        emp.lastName = "Jones";
        emp.gender = "Male";

        Document document = marshaller.objectToXML(emp);
        String gender = document.getElementsByTagName("gender").item(0).getFirstChild().getNodeValue();
        assertEquals("The field was incorrectly converted, expected \"M\" found " + gender, "M", gender);
    }

    public void testWriteEmployeeF() throws Exception {
        Employee emp = new Employee();
        emp.firstName = "Bill";
        emp.lastName = "Jones";
        emp.gender = "Female";

        Document document = marshaller.objectToXML(emp);
        String gender = document.getElementsByTagName("gender").item(0).getFirstChild().getNodeValue();
        assertEquals("The field was incorrectly converted, expected \"F\" found " + gender, "F", gender);

    }

    public void testReadEmployeeM() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/converter/employee_male.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        assertEquals("The field was incoreectly converted, expected \"Male\" found " + emp.gender, "Male", emp.gender);
    }

    public void testReadEmployeeF() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/converter/employee_female.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        assertEquals("The field was incoreectly converted, expected \"Female\" found " + emp.gender, "Female", emp.gender);
    }
}

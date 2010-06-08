/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.converter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.w3c.dom.*;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.oxm.*;

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
        assertTrue("The field was incorrectly converted, expected \"M\" found " + gender, gender.equals("M"));
    }

    public void testWriteEmployeeF() throws Exception {
        Employee emp = new Employee();
        emp.firstName = "Bill";
        emp.lastName = "Jones";
        emp.gender = "Female";

        Document document = marshaller.objectToXML(emp);
        String gender = document.getElementsByTagName("gender").item(0).getFirstChild().getNodeValue();
        assertTrue("The field was incorrectly converted, expected \"F\" found " + gender, gender.equals("F"));

    }

    public void testReadEmployeeM() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/converter/employee_male.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        assertTrue("The field was incoreectly converted, expected \"Male\" found " + emp.gender, emp.gender.equals("Male"));
    }

    public void testReadEmployeeF() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/converter/employee_female.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        assertTrue("The field was incoreectly converted, expected \"Female\" found " + emp.gender, emp.gender.equals("Female"));
    }
}

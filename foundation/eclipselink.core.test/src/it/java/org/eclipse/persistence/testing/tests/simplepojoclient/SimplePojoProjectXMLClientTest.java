/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.simplepojoclient;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.lang.reflect.Field;
import java.util.Vector;

public class SimplePojoProjectXMLClientTest extends TestCase {
    private DatabaseSession session = null;
    private SimplePojoProjectXMLClientTest simplePojoProjectXMLClient = null;

    public SimplePojoProjectXMLClientTest() {
        setName("SimplePojoProjectXMLClientTest");
    }

    @Override
    public void setup() throws TestErrorException {
        simplePojoProjectXMLClient = new SimplePojoProjectXMLClientTest();

        System.out.println(System.lineSeparator() + System.lineSeparator() + "Now running Simple POJO test using Project Deployment XML file...");

        try {
            org.eclipse.persistence.sessions.Project project = XMLProjectReader.read("PojoEmployee.xml");
            session = project.createDatabaseSession();
            session.login();
        } catch (Exception e) {
            throw new TestErrorException("Session failed to load properly.  Caught: " + e);
        }
    }

    @Override
    public void test() throws TestErrorException {
        try {
            Vector objects = session.readAllObjects(org.eclipse.persistence.testing.tests.simplepojoclient.PojoEmployee.class);
            for (Object object : objects) {
                simplePojoProjectXMLClient.printObjectAttributes(object);
            }
            System.out.println(System.lineSeparator() + System.lineSeparator() + "Test using Project Deployment XML file is complete" + System.lineSeparator() + System.lineSeparator());
        } catch (Exception e) {
            throw new TestErrorException("" + e);
        }
    }

    private void printObjectAttributes(Object object) throws Exception {
        System.out.println("Object Name: " + object.getClass().getName());
        System.out.println("Fields:");
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field value : fields) {
            try {
                Field field = value;
                field.setAccessible(true);
                System.out.println("    " + field.getName() + " = " + field.get(object));
            } catch (Exception e) {
                throw new Exception(e);
            }

        }
    }
}

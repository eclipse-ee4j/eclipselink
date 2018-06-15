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
package org.eclipse.persistence.testing.tests.simplepojoclient;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.DatabaseSession;

import java.util.Vector;
import java.util.Iterator;

import java.lang.reflect.Field;

public class SimplePojoProjectXMLClientTest extends TestCase {
    private DatabaseSession session = null;
    private SimplePojoProjectXMLClientTest simplePojoProjectXMLClient = null;

    public SimplePojoProjectXMLClientTest() {
        setName("SimplePojoProjectXMLClientTest");
    }

    public void setup() throws TestErrorException {
        simplePojoProjectXMLClient = new SimplePojoProjectXMLClientTest();

        System.out.println(System.getProperty("line.separator") + System.getProperty("line.separator") + "Now running Simple POJO test using Project Deployment XML file...");

        try {
            org.eclipse.persistence.sessions.Project project = XMLProjectReader.read("PojoEmployee.xml");
            session = project.createDatabaseSession();
            session.login();
        } catch (Exception e) {
            throw new TestErrorException("Session failed to load properly.  Caught: " + e);
        }
    }

    public void test() throws TestErrorException {
        try {
            Vector objects = session.readAllObjects(org.eclipse.persistence.testing.tests.simplepojoclient.PojoEmployee.class);
            for (Iterator itr = objects.iterator(); itr.hasNext(); ) {
                simplePojoProjectXMLClient.printObjectAttributes(itr.next());
            }
            System.out.println(System.getProperty("line.separator") + System.getProperty("line.separator") + "Test using Project Deployment XML file is complete" + System.getProperty("line.separator") + System.getProperty("line.separator"));
        } catch (Exception e) {
            throw new TestErrorException("" + e);
        }
    }

    private void printObjectAttributes(Object object) throws Exception {
        System.out.println("Object Name: " + object.getClass().getName());
        System.out.println("Fields:");
        Field fields[] = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                Field field = fields[i];
                field.setAccessible(true);
                System.out.println("    " + field.getName() + " = " + field.get(object));
            } catch (Exception e) {
                throw new Exception(e);
            }

        }
    }
}

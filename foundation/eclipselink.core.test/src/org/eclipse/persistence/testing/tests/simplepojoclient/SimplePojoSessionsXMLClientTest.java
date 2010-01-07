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
package org.eclipse.persistence.testing.tests.simplepojoclient;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.DatabaseSession;

import java.util.Vector;
import java.util.Iterator;

import java.lang.reflect.Field;

public class SimplePojoSessionsXMLClientTest extends TestCase {
    private SessionManager sessionManager = null;
    private SimplePojoSessionsXMLClientTest simplePojoSessionsXMLClient = null;
    private DatabaseSession session = null;

    public SimplePojoSessionsXMLClientTest() {
        setName("SimplePojoSessionsXMLClientTest");
    }

    public void setup() throws TestErrorException {
        simplePojoSessionsXMLClient = new SimplePojoSessionsXMLClientTest();

        System.out.println(System.getProperty("line.separator") + System.getProperty("line.separator") + "Now running Simple POJO test using Sessions XML file...");

        try {
            org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader xmlLoader = new org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader("SimplePojoClientSessions.xml");
            sessionManager = org.eclipse.persistence.sessions.factories.SessionManager.getManager();
            session = (DatabaseSession)sessionManager.getSession(xmlLoader, "Session", simplePojoSessionsXMLClient.getClass().getClassLoader());
        } catch (Exception e) {
            throw new TestErrorException("Session failed to load properly.  Caught: " + e);
        }
    }

    public void test() throws TestErrorException {
        try {
            Vector objects = session.readAllObjects(org.eclipse.persistence.testing.tests.simplepojoclient.PojoEmployee.class);
            for (Iterator itr = objects.iterator(); itr.hasNext(); ) {
                simplePojoSessionsXMLClient.printObjectAttributes(itr.next());
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

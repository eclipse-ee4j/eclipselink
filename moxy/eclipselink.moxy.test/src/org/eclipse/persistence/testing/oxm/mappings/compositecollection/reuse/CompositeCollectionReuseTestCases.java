/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-10-07 13:24:58 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.reuse;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.reuse.Employee;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeCollectionReuseTestCases extends XMLMappingTestCases {

    public CompositeCollectionReuseTestCases(String name) throws Exception {
        super(name);
        setProject(new CompositeCollectionReuseProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/compositecollection/reuse/reuse.xml");
    }

    public Object getControlObject() {
        Employee emp = new Employee();
        emp.setID(123);

        EmailAddress email1 = new EmailAddress();
        email1.setUserID("jondoe");
        email1.setDomain("test.com");
        EmailAddress email2 = new EmailAddress();
        email2.setUserID("elfkillah1984");
        email2.setDomain("everquest.com");

        Vector emails = new Stack();
        emails.add(email1);
        emails.add(email2);

        emp.setEmailAddresses(emails);

        return emp;
    }

    public void testContainerReused() throws Exception {
        URL url = ClassLoader.getSystemResource(resourceName);
        Employee testObject = (Employee) xmlUnmarshaller.unmarshal(url);

        assertEquals("This mapping's container was not reused.", Stack.class, testObject.getEmailAddresses().getClass());
    }

}
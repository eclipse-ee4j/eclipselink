/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class InsertNewObjectTest extends AutoVerifyTestCase {
    public InsertNewObjectTest() {
        setDescription("This model tests some of the complex operation that can be performed in the Unit Of Work.");
    }

    public void verify() {
        // ** note the objects are inserted during the populate.
        Person person = (Person)getSession().readObject(Person.class);
        Vector contacts = person.getContacts();

        if (contacts == null) {
            TestErrorException exception = new TestErrorException("The new non private objects are not inserted.");
            throw exception;
        }

        for (Enumeration all = contacts.elements(); all.hasMoreElements(); ) {
            Contact contact = (Contact)all.nextElement();

            if (contact.getMailAddress() == null) {
                TestErrorException exception = 
                    new TestErrorException("The new non private objects are not inserted.");
                throw exception;
            }
        }
    }
}

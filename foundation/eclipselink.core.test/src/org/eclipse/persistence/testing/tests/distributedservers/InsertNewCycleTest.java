/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.distributedservers;

import java.util.Vector;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.tests.unitofwork.Contact;
import org.eclipse.persistence.testing.tests.unitofwork.Person;
import org.eclipse.persistence.exceptions.RemoteCommandManagerException;


public class InsertNewCycleTest extends TransactionalTestCase {
    public Person originalPerson;
    //Must use a different thread in this test as the test is tezting for an infinite recursion
    public Thread thread;

    public void setup() {
        super.setup();
        //		originalPerson = new Person();
        //	originalPerson.name = "bob";
        UnitOfWork uow = getSession().acquireUnitOfWork();
        originalPerson = (Person)uow.readObject(Person.class);
        uow.commit();
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        // must make the remote session load the object so that it will be merged
        server.getDistributedSession().readObject(originalPerson);

    }

    public void test() {
        try {
            Person person1 = new Person();
            person1.name = "alfred";

            Contact contact = new Contact();
            contact.person = person1;

            Person person2 = new Person();
            person2.name = "William";

            Contact contact1 = new Contact();
            contact1.person = person2;

            Vector vector = new Vector();
            vector.add(contact);
            person2.contacts = vector;
            vector = new Vector();
            vector.add(contact1);
            person1.contacts = vector;

            UnitOfWork uow = getSession().acquireUnitOfWork();
            Person personClone = (Person)uow.readObject(originalPerson);
            vector = new Vector();
            Contact contact2 = new Contact();
            contact2.person = person1;
            vector.add(contact2);
            personClone.contacts = vector;
            uow.commit();
        } catch (RemoteCommandManagerException exception) {
            throw new TestErrorException("New Object Cycles are causing infinite recursion in Cache Synch");
        }
    }

    public void verify() {
    }

    public void reset() {
        super.reset();
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        // must make the remote session load the object so that it will be merged
        server.getDistributedSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }


}

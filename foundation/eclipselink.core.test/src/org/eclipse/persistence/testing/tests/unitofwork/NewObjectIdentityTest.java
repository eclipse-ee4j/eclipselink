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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class NewObjectIdentityTest extends AutoVerifyTestCase {
    public Employee readPerson;
    public Employee origPerson;

    public NewObjectIdentityTest() {
        this.setDescription("Tests that TopLink is correctly using the original new object in the cache.");
    }

    public void setup() {
        if (getSession().isDistributedSession()) {
            throw new TestWarningException("Test unavailable on Remote UnitOfWork because of timing issues");
        }
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        origPerson = new Employee();
        Employee person = (Employee)uow.registerObject(origPerson);
        person.setFirstName("Anthony");
        person.setLastName("Anthony");

        ((UnitOfWorkImpl)uow).issueSQLbeforeCompletion();

        Runnable runnable = new Runnable() {
                public void run() {
                    ReadAllQuery query = new ReadAllQuery(Employee.class);
                    query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal("Anthony").and(query.getExpressionBuilder().get("lastName").equal("Anthony")));
                    readPerson = (Employee)((Vector)getSession().executeQuery(query)).firstElement();
                }
            };
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
        }
        ((UnitOfWorkImpl)uow).mergeClonesAfterCompletion();
        try {
            thread.join();
        } catch (InterruptedException ex) {
        }
    }

    public void verify() {
        if (origPerson != readPerson) {
            throw new TestErrorException("Original New Object not placed in cache");
        }
    }

    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(readPerson);
        uow.commit();
    }
}

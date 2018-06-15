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
package org.eclipse.persistence.testing.tests.simultaneous;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;

public class ConcurrencyTest extends AutoVerifyTestCase {

    Employee emp = null;
    SmallProject project = null;


    public ConcurrencyTest() {
        super();
    }


    @Override
    protected void setup() throws Throwable {
        super.setup();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        project = new SmallProject();
        uow.registerNewObject(project);
        emp = new Employee();
        uow.registerNewObject(emp);
        uow.commit();
    }

    @Override
    protected void test() throws Throwable {
        // TODO Auto-generated method stub
        super.test();
            Integer i = new Integer(5);
            Thread thread1 = new Thread(new Runner1(i,  emp.getId(),project.getId(),getSession()));
            thread1.setName("Runner1");
            Thread thread2 = new Thread(new Runner2(i, emp.getId(),project.getId(),getSession()));
            thread2.setName("Runner2");
            thread2.start();
            thread1.start();
            try {
                Thread.currentThread().sleep(8000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            synchronized (i) {
                i.notifyAll();
            }
            try {
                thread2.join(30000);
                thread1.join(30000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (thread2.isAlive() || thread1.isAlive()){
                thread2.interrupt();
                thread1.interrupt();
                fail("Dead-lock occurred");
           }

    }

    @Override
    public void reset() throws Throwable {
        super.reset();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.emp = (Employee)uow.readObject(emp);
        this.emp.getProjects().clear();
        uow.deleteObject(uow.readObject(project));
        uow.deleteObject(emp);
        uow.commit();
    }
}

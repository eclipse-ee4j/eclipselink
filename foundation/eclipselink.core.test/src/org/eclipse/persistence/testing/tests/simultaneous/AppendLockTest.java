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
package org.eclipse.persistence.testing.tests.simultaneous;

import java.math.BigDecimal;
import java.util.Vector;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;

import org.eclipse.persistence.testing.models.employee.domain.Address;

//see bug 305611

public class AppendLockTest extends AutoVerifyTestCase {
    
    Employee emp = null;
    SmallProject project = null;
    Address address = null;
    

    public AppendLockTest() {
        super();
    }
    

    @Override
    protected void setup() throws Throwable {
        super.setup();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        project = new SmallProject();
        uow.registerNewObject(project);
        emp = new Employee();
        address = new Address();
        uow.registerNewObject(address);
        uow.registerNewObject(emp);
        uow.commit();
    }

    @Override
    protected void test() throws Throwable {
        // TODO Auto-generated method stub
        super.test();
        Thread locker = new Thread(new Locker(getSession(), address));
        Thread writer1 = new Thread(new Writer1(getSession(), emp, project, address));
        Thread writer2 = new Thread (new Writer2(getSession(), project));
        locker.start();
        writer1.start();
        writer2.start();
        writer2.join(60000);
        try{
        if (writer2.isAlive()){
            try{
            writer2.interrupt();
            writer1.interrupt();
            locker.interrupt();
            }catch (Exception e) {
            }
            fail("Bug 9484687 - deadlock occured.");
        }
        
    } catch (Exception e) {
        e.printStackTrace();
    }
          }

    @Override
    public void reset() throws Throwable {
        super.reset();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.project = (SmallProject) uow.refreshObject(project);
        this.emp = (Employee)uow.refreshObject(emp);
        uow.deleteObject(project);
        uow.deleteObject(emp);
        uow.commit();
    }
    
    public static class Writer1 implements Runnable{
        protected Project project;
        protected Employee emp;
        protected Session session;
        protected Address address;
        
        public Writer1(Session session, Employee emp, Project project, Address address){
            this.session = session;
            this.project = project;
            this.emp = emp;
            this.address =address;
        }

        public void run() {
            UnitOfWork uow = session.acquireUnitOfWork();
            Project p = (Project) uow.readObject(project);
            Address address = (Address) uow.readObject(this.address);
            Employee emp = (Employee) uow.readObject(this.emp);
            emp.setAddress(address);
            p.setDescription("SomeBigLongDescription");
            ((AbstractSession)this.session).getIdentityMapAccessorInstance().getCacheKeyForObject(this.project).setObject(null);
            ((UnitOfWorkImpl)uow).getCloneToOriginals().remove(p);
            synchronized (this.session) {
                try {
                    this.session.notifyAll();
                    this.session.wait(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            uow.commit();
            synchronized (this.session) {
                    this.session.notifyAll();
            }
        }
        
    }

    public static class Locker implements Runnable{
        protected Address address;
        protected Session session;
        
        public Locker(Session session, Address address){
            this.session = session;
            this.address = address;
        }

        public void run() {
            CacheKey cacheKey = null;
            synchronized (this.session) {
                try {
                    this.session.wait(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Vector<BigDecimal> pk = new Vector(1);
            pk.add(this.address.getId());
            try{
                 cacheKey = ((AbstractSession)this.session).getIdentityMapAccessorInstance().acquireLock(pk, this.address.getClass(), this.session.getClassDescriptor(this.address));
                synchronized (this.session) {
                    this.session.notifyAll();
                    try {
                        this.session.wait(5000);
                    } catch (InterruptedException e) {
                        //TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }finally{
                cacheKey.release();
            }
        }
        
    }

    public static class Writer2 implements Runnable{
        protected Session session;
        protected Project project;

        
        public Writer2(Session session, Project project){
            this.session = session;
            this.project = project;
        }

        public void run() {
            synchronized (this.session) {
                try {
                    this.session.wait(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (this.session) {
                try {
                    this.session.wait(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (this.session) {
                try {
                    this.session.wait(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            UnitOfWork uow = this.session.acquireUnitOfWork();
            try{
            this.project = (Project) uow.refreshObject(this.project); // should block
            
                synchronized (this.session) {
                        this.session.notifyAll();
                }
                uow.release();
            }catch(Exception ex){
                System.out.println("Thread was interrupted");
            }
        }
        
    }

}

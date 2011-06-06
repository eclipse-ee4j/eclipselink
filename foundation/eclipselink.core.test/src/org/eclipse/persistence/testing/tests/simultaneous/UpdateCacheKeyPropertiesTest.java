/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;

public class UpdateCacheKeyPropertiesTest extends AutoVerifyTestCase {
    
    Employee emp = null;
    SmallProject project = null;
    

    public UpdateCacheKeyPropertiesTest() {
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
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Thread reader = new Thread(new Reader(uow, emp, project));
        Thread writer = new Thread (new Writer(uow, emp, project));
        reader.start();
        writer.start();
        writer.join(60000);
        try{
        if (writer.isAlive()){
            try{
            writer.interrupt();
            reader.interrupt();
            }catch (Exception e) {
                // TODO: handle exception
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
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.project = (SmallProject) uow.readObject(project);
        this.emp = (Employee)uow.readObject(emp);
        uow.deleteObject(project);
        uow.deleteObject(emp);
        uow.commit();
    }
    
    public static class Reader implements Runnable{
        protected UnitOfWork uow;
        protected Project project;
        protected Employee emp;
        
        public Reader(UnitOfWork uow, Employee emp, Project project){
            this.uow = uow;
            this.project = project;
            this.emp = emp;
        }

        public void run() {
            synchronized (uow) {
                try {
                    uow.wait(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Session session = uow.getParent();
            CacheKey zonKey = ((IdentityMapAccessor)session.getIdentityMapAccessor()).getCacheKeyForObject(emp);
            zonKey.acquireDeferredLock();
            synchronized (uow) {
                uow.notifyAll();
                try {
                    uow.wait(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            CacheKey empKey = ((IdentityMapAccessor)session.getIdentityMapAccessor()).getCacheKeyForObject(project);
            empKey.acquireDeferredLock();
            empKey.releaseDeferredLock();
            zonKey.releaseDeferredLock();
            
            
        }
        
    }

    public static class Writer implements Runnable{
        protected UnitOfWork uow;
        protected Project project;
        protected Employee emp;

        
        public Writer(UnitOfWork uow, Employee emp, Project project){
            this.uow = uow;
            this.project = project;
            this.emp = emp;
        }

        public void run() {
            this.project = (Project) uow.readObject(this.project);
            this.project.setTeamLeader((org.eclipse.persistence.testing.models.employee.interfaces.Employee) uow.readObject(emp));
            try{
                synchronized (uow) {
                    try {
                        uow.notifyAll();
                    uow.wait(6000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                uow.commit();
            }catch(Exception ex){
                System.out.println("Thread was interrupted");
            }
            synchronized (uow) {
                    uow.notifyAll();
            }

        }
        
    }

}

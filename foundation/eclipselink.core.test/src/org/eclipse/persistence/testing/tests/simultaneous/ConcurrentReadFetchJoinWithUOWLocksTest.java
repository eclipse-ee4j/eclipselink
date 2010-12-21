/*******************************************************************************
 * Copyright (c) 2010 <Company/individual>. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Chris Delahunt  = 2.2 - 331921: deadlock with uow commit and query using joins
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.simultaneous;

import java.math.BigDecimal;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.unitofwork.ConcurrentLargeProject;
import org.eclipse.persistence.testing.tests.unitofwork.ConcurrentPerson;
import org.eclipse.persistence.testing.tests.unitofwork.ConcurrentAddress;
import org.eclipse.persistence.testing.tests.unitofwork.ConcurrentProject;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.Server;

import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * Test for Bug 262157
 * This test tests to ensure no deadlock occurs in the following scenario:
 * 1) Thread1 gets deferred lock on LargeProject
 * 2) Thread2 gets required locks on Person+Address for updates
 * 3) Thread1 attempts to get an active lock on Address, but has to wait as it is held by thread2
 * 4) Thread2 updates, commits, and during the merge tries to get a lock on LargeProject.  This causes it to transition to deferred locks.
 * 4b) thread2 attempts to release its deferred lock, causing it to waiting on threadB - a deadlock. 
 * 
 */
public class ConcurrentReadFetchJoinWithUOWLocksTest extends AutoVerifyTestCase {
    protected ConcurrentPerson person = null;
    protected ConcurrentLargeProject project;
    
    protected boolean deadlockDetected = false;
    
    public void setup(){     
        deadlockDetected = false;
        this.getExecutor().swapServerSession();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        person = (ConcurrentPerson)uow.registerObject(new ConcurrentPerson());
        person.name = "SomeoneSpecial";
        
        project = (ConcurrentLargeProject)uow.registerObject(new ConcurrentLargeProject());
        project.setName("ConcurrentRFJUOWLock Project");
        
        ConcurrentAddress address = (ConcurrentAddress)uow.registerObject(new ConcurrentAddress());
        address.setStreet("99 Bank St");
        project.setLocation(address);

        uow.commit();
        uow.release();
    }
    
    public void test(){
        Server server = this.getServerSession();
        UnitOfWork uow = server.acquireUnitOfWork();
        ConcurrentLargeProject clonedProject = (ConcurrentLargeProject)uow.registerObject(project);
        clonedProject.getLocation().setPostalCode("K1P 1A4");
        ConcurrentPerson clonedPerson = (ConcurrentPerson)uow.registerObject(person);
        clonedPerson.setHobby(clonedProject);
        uow.writeChanges();
        
        Thread thread1 = new Thread(new ProjectReader(server.acquireClientSession(), project.getId()));
        ConcurrentProject.RUNNING_TEST = ConcurrentProject.READ_WITH_UOW_LOCKS_TESTS;
        //start reading Project, and have the thread wait while building DTF mappings
        thread1.start();
        
        try {
            thread1.join(1000);//wait a token amount to be sure that thread1 isn't starved before getting deferred lock on Project.
        } catch (InterruptedException ex) {
        }
        
        //start uow commit, which will get locks on Person+Address, commit, then merge.  
        //merge should get a deferred lock on Project.  
        Thread thread2 = new Thread(new UOWCommit(uow));
        thread2.start();
        
        //while waiting, thread1 should wake and try to get a lock on Address.  It will deadlock if it
        //tries to get an active lock, since they will be held by the UOW which can't complete until thread1 
        //releases.
        try {
            thread1.join(20000);
            if (thread1.isAlive()){
                try{
                    thread1.interrupt();
                    thread2.interrupt();
                }catch (Exception e) {
                }
                deadlockDetected = true;
            }
        } catch (InterruptedException ex) {
        }
    }
    
    public void verify(){
        if (deadlockDetected){
            throw new TestErrorException("Deadlock detected in UnitOfWork when reading a joined 1-1.");
        }
    }
    
    public void reset(){
        ConcurrentProject.RUNNING_TEST = ConcurrentProject.NONE;
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        uow.deleteObject(person);
        uow.deleteObject(project);
        uow.deleteObject(project.getLocation());
        
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        this.getExecutor().resetSession();
    }
    
    //threads:
    public class ProjectReader implements Runnable{
        private Session session;
        private BigDecimal idToUse;
        
        public ProjectReader(Session session, BigDecimal id){
            this.session = session;
            this.idToUse = id;
        }
        
        public void run(){
            org.eclipse.persistence.queries.ReadAllQuery query = new org.eclipse.persistence.queries.ReadAllQuery(ConcurrentLargeProject.class);
            query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(idToUse));
            query.addJoinedAttribute("location");
            query.refreshIdentityMapResult();
            session.executeQuery(query);
        }
    }
    
    public class UOWCommit implements Runnable{
        private UnitOfWork uow;
        
        public UOWCommit(UnitOfWork uow){
            this.uow = uow;
        }
        
        public void run(){
            uow.commit();
        }
    }

}

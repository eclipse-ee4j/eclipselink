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
 *     tware - test for bug 262157
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.simultaneous;

import java.math.BigDecimal;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.unitofwork.ConcurrentLargeProject;
import org.eclipse.persistence.testing.tests.unitofwork.ConcurrentPerson;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
/**
 * Test for Bug 262157
 * This test tests to ensure no dealock occurs in the following scenario
 * There is a bidirectional realtionship between an object (A) without inheritance and an object (B) with inheritance with a superclass (C)
 * A eagerly maps to C and B eagerly maps to A
 * If the mappings are setup so that A1 maps to B1 and B1 maps to A1, ensure no deadlock will occur
 * This test starts a thread that reads the A and pauses the build after it has acquired its locks.  It then starts
 * a thread that reads B and tests to ensure both threads run to completion
 * @author tware
 *
 */
public class ConcurrentReadOneToOneInheritanceTest extends AutoVerifyTestCase{

    protected BigDecimal personId = null;
    protected BigDecimal projectId = null;
    protected ConcurrentPerson person = null;
    
    protected boolean deadlockDetected = false;
    
    public void setup(){     
        deadlockDetected = false;
        UnitOfWork uow = getSession().acquireUnitOfWork();
        person = new ConcurrentPerson();
        person.name = "Wenger";
        ConcurrentLargeProject project = new ConcurrentLargeProject();
        project.setName("Football");
        person.setHobby(project);
        project.setSupervisor(person);
        uow.registerObject(person);
        uow.registerObject(project);
        uow.commit();
        uow.release();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        personId = person.id;
        projectId = project.getId();
    }
    
    public void test(){
        ConcurrentPerson.RUNNING_TEST = ConcurrentPerson.ONE_TO_ONE_INHERITANCE;
        Thread thread1 = new Thread(new PersonReader(getSession().acquireUnitOfWork(), personId));
        Thread thread2 = new Thread(new LargeProjectReader(getSession().acquireUnitOfWork(), projectId));
        thread1.start();
        thread2.start();
        try {
            thread1.join(30000);
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
            throw new TestErrorException("Deadlock detected when reading a bidirectional 1-1 with Inheritance.");
        }
    }
    
    public void reset(){
        ConcurrentPerson.RUNNING_TEST = ConcurrentPerson.NONE;
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(person.getHobby());
        uow.deleteObject(person);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public class PersonReader implements Runnable{
        private UnitOfWork uow;
        private BigDecimal id;
        
        public PersonReader(UnitOfWork uow, BigDecimal id){
            this.uow = uow;
            this.id = id;
        }
        
        public void run(){
            uow.readObject(ConcurrentPerson.class, (new ExpressionBuilder()).get("id").equal(id));
        }
    }
    
    public class LargeProjectReader implements Runnable{
        private UnitOfWork uow;
        private BigDecimal id;
        
        public LargeProjectReader(UnitOfWork uow, BigDecimal id){
            this.uow = uow;
            this.id = id;
        }
        
        public void run(){
            uow.readObject(ConcurrentLargeProject.class, (new ExpressionBuilder()).get("id").equal(id));
        }
    }
}

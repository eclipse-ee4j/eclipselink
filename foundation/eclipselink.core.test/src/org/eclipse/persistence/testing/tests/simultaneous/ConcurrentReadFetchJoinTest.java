/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - test for bug 324459
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.simultaneous;

import java.math.BigDecimal;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.unitofwork.ConcurrentPerson;
import org.eclipse.persistence.testing.tests.unitofwork.ConcurrentPhoneNumber;

/**
 *  Test for bug 324459
 *  
 *  This test test a bidirectional relationship where one side is EAGER and the other is LAZY
 *  The object with the EAGER relationship does a simple ReadObjectQuery
 *  The object with the LAZY relationahip does a ReadObjectQuery with a fetch join that
 *  traverses the relationship.
 *  This test tests to ensure there is no deadlock in that situation
 * @author tware
 *
 */
public class ConcurrentReadFetchJoinTest extends AutoVerifyTestCase{

    public static final String NUMBER_TYPE = "cell";
    
    protected BigDecimal personId = null;
    protected BigDecimal projectId = null;
    protected ConcurrentPerson person = null;
    
    protected boolean deadlockDetected = false;
    
    public void setup(){
        deadlockDetected = false;
        UnitOfWork uow = getSession().acquireUnitOfWork();
        person = new ConcurrentPerson();
        person.name = "Wenger";
        ConcurrentPhoneNumber phoneNumber = new ConcurrentPhoneNumber();
        phoneNumber.setType(NUMBER_TYPE);
        phoneNumber.setNumber("1234567");
        phoneNumber.setOwner(person);
        person.phoneNumbers.add(phoneNumber);
        uow.registerObject(person);
        uow.registerObject(phoneNumber);

        uow.commit();
        uow.release();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        personId = person.id;
    }
    
    public void test(){
        ConcurrentPerson.RUNNING_TEST = ConcurrentPerson.READ_FETCH_JOIN;
        Thread thread1 = new Thread(new PersonReader(getSession().acquireUnitOfWork(), personId));

        Thread thread2 = new Thread(new PhoneNumberReader(getSession().acquireUnitOfWork(), personId, NUMBER_TYPE));
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
            throw new TestErrorException("Deadlock detected when reading a bidirectional relationship with a fetch join.");
        }
    }
    
    public void reset(){
        ConcurrentPerson.RUNNING_TEST = ConcurrentPerson.NONE;
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(person.getPhoneNumbers().get(0));
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
    
    public class PhoneNumberReader implements Runnable{
        private UnitOfWork uow;
        private BigDecimal id;
        private String type;
        
        public PhoneNumberReader(UnitOfWork uow, BigDecimal id, String type){
            this.uow = uow;
            this.id = id;
            this.type = type;
        }
        
        public void run(){
            ReadObjectQuery query = new ReadObjectQuery(ConcurrentPhoneNumber.class);
            ExpressionBuilder phoneNumberBuilder = query.getExpressionBuilder();
            Expression exp = phoneNumberBuilder.get("type").equal(type).and(phoneNumberBuilder.get("owner").get("id").equal(id));
            query.setSelectionCriteria(exp);
            query.addJoinedAttribute("owner");
            uow.executeQuery(query);
        }
    }
}

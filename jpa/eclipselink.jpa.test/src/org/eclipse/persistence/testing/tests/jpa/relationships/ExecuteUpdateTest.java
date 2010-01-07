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
package org.eclipse.persistence.testing.tests.jpa.relationships;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.*;
import javax.persistence.Query;
import org.eclipse.persistence.testing.models.jpa.relationships.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;
import javax.persistence.RollbackException;

/*
 * Tests for the executeUpdate method on the EJBQueryImpl class
 *   Also tests bugs 4288845 and 4293920, that params are passed in and used correctly
 */
public class ExecuteUpdateTest extends EntityContainerTestBase {

  //reset gets called twice on error
  protected boolean reset = false;
  
  //used for verification
  protected Customer returnedCustomer=null;
  public Integer[] cusIDs = new Integer[2];
  
  public java.net.URL n=null;
  
  protected String nameChange1 = "New Name1";
  protected String nameChange2 = "New Name2";
  protected String nameChange3 = "New Name3";
  protected String returnedName1,returnedName2,returnedName3 = null;
  protected Exception expectedException =null;
  protected RollbackException expectedException2 = null;
  public ExecuteUpdateTest()  {
  }
  
  public void setup (){
    super.setup();
    this.reset = true;
    
    Customer cusClone1 = RelationshipsExamples.customerExample1();
    try {
      beginTransaction();
      getEntityManager().persist(cusClone1);
      commitTransaction();
    } catch (Exception ex) {
          throw new TestException("Unable to setup Test" + ex);
    }
    ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
    cusIDs[0] = cusClone1.getCustomerId();
       
    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();   
  }
    
  public void reset (){
    if (reset){//ensures it is only done once
	  try {
		beginTransaction();
        Customer cus1 = getEntityManager().find(Customer.class,cusIDs[0]);
        getEntityManager().remove(cus1);
		commitTransaction();
		reset = false;
	  }catch (Exception ex) {
          throw new TestException("Unable to reset Test" + ex);
      }
    }
  }
  
  public void test(){

    try {
      beginTransaction();
      Customer cus = getEntityManager().find(Customer.class,cusIDs[0]);
      Query query = getEntityManager().createQuery("UPDATE Customer customer SET customer.name = '"+nameChange1+"' WHERE customer.customerId = "+cusIDs[0]);
      query.executeUpdate();
      
      
      ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
      getSession().getIdentityMapAccessor().initializeAllIdentityMaps();  
      //getEntityManager().refresh(cus);
      cus = getEntityManager().find(Customer.class,cusIDs[0]);
      returnedName1 = cus.getName();
      
        //tests bug 4288845
      Query query2 = getEntityManager().createQuery("UPDATE Customer customer SET customer.name = :name WHERE customer.customerId = "+cusIDs[0]);
      query2.setParameter("name", nameChange2);
      query2.executeUpdate();
      
      ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
      getSession().getIdentityMapAccessor().initializeAllIdentityMaps();  
      //getEntityManager().refresh(cus);
      cus = getEntityManager().find(Customer.class,cusIDs[0]);
      returnedName2 = cus.getName();
      
        //tests bug 4293920
      Query query3 = getEntityManager().createQuery("UPDATE Customer customer SET customer.name = :name WHERE customer.customerId = :id");
      query3.setParameter("name", nameChange3);
      query3.setParameter("id", cusIDs[0]);
      query3.executeUpdate();
      
      ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
      getSession().getIdentityMapAccessor().initializeAllIdentityMaps();  
      //getEntityManager().refresh(cus);
      cus = getEntityManager().find(Customer.class,cusIDs[0]);
      returnedName3 = cus.getName();
      
        //tests bug 4294241
      try{
        Query query4 = getEntityManager().createNamedQuery("findAllCustomers");
        int someValue = query4.executeUpdate();
      }catch(IllegalStateException expected){
        expectedException=expected;
      }
      
      try {
        commitTransaction();
        expectedException2 = null;
      }catch(RollbackException ex){
        expectedException2 = ex;
      }
    
    } catch (Exception ex) {
      throw new TestErrorException("Exception thrown while executing updates" + ex, ex);
    } finally {
      rollbackTransaction();
    }
  }
  
  public void verify(){
    if ( (returnedName1==null || !returnedName1.equals(nameChange1))){
        throw new TestErrorException("Customer name did not get updated correctly should be:"+nameChange1 +" is :"+returnedName1);
    }
    if ( (returnedName2==null || !returnedName2.equals(nameChange2))){
        throw new TestErrorException("Customer name did not get updated correctly should be:"+nameChange2 +" is :"+returnedName2);
    }
    if ( (returnedName3==null || !returnedName3.equals(nameChange3))){
        throw new TestErrorException("Customer name did not get updated correctly should be:"+nameChange3 +" is :"+returnedName3);
    }
    if ( expectedException==null) {
        throw new TestErrorException("excuteUpdate did not result in an exception on findAllCustomers named ReadAllQuery");
    }
    if ( expectedException2==null ){
        throw new TestErrorException("commit did not throw expected RollbackException");
    }
  }
}

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

import java.util.HashMap;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.jpa.relationships.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;
import java.util.Collection;
/*
 * Tests using the 'getSingleResult' api on a Query object obtained from the EntityManager
 * Also tests bugs 4300879 - check non Collection container policy error
 *             and 4297903 - check ReadObjectQuery fails
 */
public class GetResultCollectionTest extends EntityContainerTestBase {

  //reset gets called twice on error
  protected boolean reset = false;
  
  //used for verification
  protected Customer returnedCustomer=null;
  protected Collection returnedCustomers1, returnedCustomers2,returnedCustomers3,returnedCustomers4 = null;
  protected QueryException expectedException1,expectedException2 = null;
  
  protected String searchString = "notAnItemName";
  protected String ejbql1 = "SELECT OBJECT(thecust) FROM Customer thecust WHERE thecust.customerId = :id";
  
  public Integer[] cusIDs = new Integer[3];

  public GetResultCollectionTest()  {
  }
  
  public void setup (){
    super.setup();
    this.reset = true;
    
    Customer cusClone1 = RelationshipsExamples.customerExample1();
    Customer cusClone2 = RelationshipsExamples.customerExample2();
    try {
      beginTransaction();
      getEntityManager().persist(cusClone1);
      getEntityManager().persist(cusClone2);
      commitTransaction();
		} catch (Exception ex) {
          throw new TestException("Unable to setup Test" + ex);
    }
    ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
    cusIDs[0] = cusClone1.getCustomerId();
    cusIDs[1] = cusClone2.getCustomerId();
       
    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();   
  }
    
  public void reset (){
    if (reset){//ensures it is only done once
	  try {
	    beginTransaction();
        Customer cus1 = getEntityManager().find(Customer.class,cusIDs[0]);
        getEntityManager().remove(cus1);
        Customer cus2 = getEntityManager().find(Customer.class,cusIDs[1]);
        getEntityManager().remove(cus2);
	    commitTransaction();
        reset = false;
	  } catch (Exception ex) {
          throw new TestException("Unable to reset Test" + ex);
      }
    }
  }
  
  public void test(){
    try{
      beginTransaction();
      EntityManagerImpl entityManagerImpl = (EntityManagerImpl)getEntityManager();
      
      EJBQueryImpl query1 = (EJBQueryImpl)entityManagerImpl.createNamedQuery("findAllCustomers");
      returnedCustomers1= query1.getResultCollection();
      
      EJBQueryImpl query2 = (EJBQueryImpl) entityManagerImpl.createQuery(ejbql1);
      query2.setParameter("id",new Integer(-10));
      returnedCustomers2 = query2.getResultCollection();
      
      //bug:4297903, check container policy failure
      EJBQueryImpl query3 = (EJBQueryImpl) entityManagerImpl.createQuery(ejbql1);
      ReadAllQuery readAllQuery = new ReadAllQuery(Customer.class);
      MapContainerPolicy mapContainerPolicy = new MapContainerPolicy();
      mapContainerPolicy.setContainerClass(HashMap.class);
      mapContainerPolicy.setKeyName("hashCode");
      readAllQuery.setContainerPolicy(mapContainerPolicy);
      query3.setDatabaseQuery(readAllQuery);
      try{
        returnedCustomers3 = query3.getResultCollection();
      }catch(QueryException exceptionExpected1){//QueryException.INVALID_CONTAINER_CLASS
        expectedException1 = exceptionExpected1;
      }
      
      //bug:4300879, check ReadObjectQuery fails
      EJBQueryImpl query4 = (EJBQueryImpl) entityManagerImpl.createQuery(ejbql1);
      query4.setParameter("id",new Integer(-10));
      ReadObjectQuery readObjectQuery2 = new ReadObjectQuery(Customer.class);
      readObjectQuery2.setEJBQLString(ejbql1);
      query4.setDatabaseQuery(readObjectQuery2);
      try{
        returnedCustomers4 = query4.getResultCollection();
      }catch(QueryException exceptionExpected2){
        expectedException2 = exceptionExpected2;
      }
      
      commitTransaction();
    }catch (Exception unexpectedException){
      try{
        commitTransaction();
      }catch(Exception comitEx){}
      throw new TestErrorException("Problem in GetResultCollectionTest: "+unexpectedException);
    }
  }
  
  public void verify(){
    if ( returnedCustomers1==null || (returnedCustomers1.size()<2) ){
        throw new TestErrorException("Not all customers were returned from findAllCustomers query ");
    }
    if ( returnedCustomers2==null || (returnedCustomers2.size()!=0) ){
        throw new TestErrorException("Customer from ReadObjectQuery was not returned using getResultCollection");
    }
    if ( expectedException1==null || (expectedException1.getErrorCode()!= QueryException.INVALID_CONTAINER_CLASS)){
        throw new TestErrorException("getResultCollection on query returning a hashtable did not throw expected INVALID_CONTAINER_CLASS QueryException");
    }
  }
}

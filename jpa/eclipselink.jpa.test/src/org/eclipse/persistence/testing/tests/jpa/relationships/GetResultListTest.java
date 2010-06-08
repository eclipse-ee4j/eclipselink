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
import javax.persistence.Query;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.jpa.relationships.*;
import java.util.List;
import javax.persistence.RollbackException;
/*
 * Tests using the 'getSingleResult' api on a Query object obtained from the EntityManager
 * Also tests bugs 4300879 - check non List container policy error
 *             and 4297903 - check ReadObjectQuery fails
 */
public class GetResultListTest extends GetResultCollectionTest {

  //reset gets called twice on error
  protected boolean reset = false;
  
  //used for verification
  protected List returnedCustomers1, returnedCustomers2,returnedCustomers3,returnedCustomers4 = null;
  
  public Integer[] cusIDs = new Integer[3];

  protected RollbackException expectedException3 = null;

  public GetResultListTest()  {
  }
  
  
  public void test(){
    try{
      beginTransaction();
      EntityManagerImpl entityManagerImpl = (EntityManagerImpl)getEntityManager();
      
      Query query1 = entityManagerImpl.createNamedQuery("findAllCustomers");
      returnedCustomers1= query1.getResultList();
      
      EJBQueryImpl query2 = (EJBQueryImpl) entityManagerImpl.createQuery(ejbql1);
      query2.setParameter("id",new Integer(-10));
      returnedCustomers2 = query2.getResultList();
      
      //bug:4297903, check container policy failure
      EJBQueryImpl query3 = (EJBQueryImpl) entityManagerImpl.createQuery(ejbql1);
      ReadAllQuery readAllQuery = new ReadAllQuery(Customer.class);
      MapContainerPolicy mapContainerPolicy = new MapContainerPolicy();
      mapContainerPolicy.setContainerClass(HashMap.class);
      mapContainerPolicy.setKeyName("hashCode");
      readAllQuery.setContainerPolicy(mapContainerPolicy);
      query3.setDatabaseQuery(readAllQuery);
      try{
        returnedCustomers3 = query3.getResultList();
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
        returnedCustomers4 = query4.getResultList();
      }catch(QueryException exceptionExpected2){
        expectedException2 = exceptionExpected2;
      }

      try {
        commitTransaction();
        expectedException3 = null;
      }catch(RollbackException ex){
        expectedException3 = ex;
      }
        
    }catch (Exception unexpectedException){
      throw new TestErrorException("Problem in GetResultListTest: "+unexpectedException);
    }finally{
      rollbackTransaction();
    }
  }
  
  public void verify(){
    if ( returnedCustomers1==null || (returnedCustomers1.size()<2) ){
        throw new TestErrorException("Not all customers were returned from findAllCustomers query ");
    }
    if ( returnedCustomers2==null || (returnedCustomers2.size()!=0) ){
        throw new TestErrorException("Customer from ReadObjectQuery was not returned using getResultList");
    }
    if ( expectedException1==null || (expectedException1.getErrorCode()!= QueryException.INVALID_CONTAINER_CLASS)){
        throw new TestErrorException("getResultList on query returning a hashtable did not throw expected INVALID_CONTAINER_CLASS QueryException");
    }
    if ( expectedException3==null ){
        throw new TestErrorException("commit did not throw expected RollbackException");
    }
  }
  
}

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
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;
import org.eclipse.persistence.sessions.Session;

//Bug#4646580  Query arguments are added in EJBQL
public class NamedQueryWithArgumentsTest extends EntityContainerTestBase {

    public Integer[] cusIDs = new Integer[3];
    public Integer[] orderIDs = new Integer[3];
    public Integer[] itemIDs = new Integer[3];
    Exception exception;
    List list;
        
    public NamedQueryWithArgumentsTest() {
        setDescription("Named query with arguments can be executed through Session's executeQuery(String queryName, Vector argumentValues).");
    }

    public void setup (){
      super.setup();
      
      Customer cusClone1 = RelationshipsExamples.customerExample1();
      Item item1 = RelationshipsExamples.itemExample1();
      Order order1 = RelationshipsExamples.orderExample1();
      order1.setCustomer(cusClone1);
      order1.setItem(item1);
      
      try {
        beginTransaction();
        getEntityManager().persist(cusClone1);
        getEntityManager().persist(order1);        
        commitTransaction();
      } catch (Exception ex) {
        throw new TestException("Unable to setup Test: " + ex, ex);
      }          
      cusIDs[0] = cusClone1.getCustomerId();
      orderIDs[0] = order1.getOrderId();
      itemIDs[0] = item1.getItemId();
         
      getSession().getIdentityMapAccessor().initializeAllIdentityMaps();   
    }

    public void reset (){
        try {
          beginTransaction();
          Customer cus1 = getEntityManager().find(Customer.class, cusIDs[0]);
          getEntityManager().remove(cus1);
          Order ord1 = getEntityManager().find(Order.class, orderIDs[0]);
          getEntityManager().remove(ord1);
          Item it1 = getEntityManager().find(Item.class, itemIDs[0]);
          getEntityManager().remove(it1);
          commitTransaction();
        } catch (Exception ex) {
          throw new TestException("Unable to reset Test: " + ex, ex);
        }
      }
    
  public void test(){
      try {
          Session session = ((JpaEntityManager)getEntityManager().getDelegate()).getActiveSession();
          List args = new ArrayList();
          args.add(itemIDs[0]);
          list = (List)session.executeQuery("findAllFieldAccessOrdersByItem", args);
      } catch (Exception ex) {
          exception = ex;
      }
  }
      
    public void verify(){
        if (exception != null) {
            throw new TestErrorException("An exception is thrown: " + exception);
        }
        if (list.size() != 1) {
            throw new TestErrorException("One order is expected but " + list.size() + " was returned");
        }
    }
}

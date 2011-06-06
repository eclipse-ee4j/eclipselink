/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships;

import java.util.Vector;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class RelationshipsExamples 
{
    private PopulationManager populationManager;

    public RelationshipsExamples()
    {
        this.populationManager = PopulationManager.getDefaultManager();
    }

    public void buildExamples(Session session)
    {
        this.populationManager.getRegisteredObjects().remove(Order.class);
        this.populationManager.getRegisteredObjects().remove(Customer.class);
        this.populationManager.getRegisteredObjects().remove(Item.class);
        this.populationManager.getRegisteredObjects().remove(SalesPerson.class);    

        Vector allObjects = new Vector();
        Order orderExample1 = orderExample1();        
        Customer customerExample1 = customerExample1();          
        Customer customerExample2 = customerExample2();
        SalesPerson salesPerson1 = salesPersonExample1();  

        /*
         * Set relationships:
         * Order1 has customer1 as the billed customer
         * Order1 has customer2 as the customer
         * OrdersList contains Order1 and is set as customer2's orders
         * SalesPerson1 is set as salesPerson to Order1
         */
        orderExample1.setBilledCustomer(customerExample1);
        orderExample1.setCustomer(customerExample2);   
        Vector ordersList = new Vector();
        ordersList.add(orderExample1);
        customerExample2.setOrders(ordersList);    
        orderExample1.setSalesPerson(salesPerson1);

        Order orderExample2 = orderExample2();        
        Customer customerExample3 = customerExample3();          
        Customer customerExample4 = customerExample4();   
        SalesPerson salesPerson2 = salesPersonExample2();
        /*
         * Set relationships:
         * Order2 has customer3 as the billed customer
         * Order2 has customer4 as the customer
         * Order2 is added to the OrdersList and is set as customer2's orders
         * SalesPerson2 is set as salesPerson to Order2
         */
        orderExample2.setBilledCustomer(customerExample3);
        orderExample2.setCustomer(customerExample4);
        ordersList.add(orderExample2);
        customerExample4.setOrders(ordersList);
        orderExample2.setSalesPerson(salesPerson2);


        allObjects.add(customerExample1);    
        allObjects.add(customerExample2);
        allObjects.add(orderExample1);
        allObjects.add(customerExample3);
        allObjects.add(customerExample4);
        allObjects.add(orderExample2);

        allObjects.add(itemExample1());
        allObjects.add(itemExample2());
        allObjects.add(itemExample3());
        allObjects.add(itemExample4());    

        allObjects.add(orderExample3());
        allObjects.add(orderExample4());    
        allObjects.add(salesPerson1);
        allObjects.add(salesPerson2);

        UnitOfWork unitOfWork = session.acquireUnitOfWork();
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }

    public static Customer customerExample1(){
        Customer customer1 = new Customer();
        customer1.setName("John Smith");
        customer1.setCity("Ottawa");
        return customer1;
    }
    public static Customer customerExample2(){
        Customer customer2 = new Customer();
        customer2.setName("Jane Smith");
        customer2.setCity("Orleans");
        return customer2;
    }
    public static Customer customerExample3(){
        Customer customer3 = new Customer();
        customer3.setName("Karen McDonald");
        customer3.setCity("Nepean");
        return customer3;
    }
    public static Customer customerExample4(){
        Customer customer4 = new Customer();
        customer4.setName("Robert Sampson");
        customer4.setCity("Manotick");
        return customer4;
    }
    public static Item itemExample1(){
        Item item = new Item();
        item.setName("item1");
        item.setDescription("Item1 description");
        return item;
    }
    public static Item itemExample2(){
        Item item = new Item();
        item.setName("item2");
        item.setDescription("Item2 description");
        return item;
    }
    public static Item itemExample3(){
        Item item = new Item();
        item.setName("item3");
        item.setDescription("Item3 description");
        return item;
    }
    public static Item itemExample4(){
        Item item = new Item();
        item.setName("item4");
        item.setDescription("Item4 description");
        return item;
    }
    public static Order orderExample1(){
        Order order = new Order();
        order.setQuantity(70);
        order.setShippingAddress("100 Argyle Street");
        return order;
    }
    public static Order orderExample2(){
        Order order = new Order();
        order.setQuantity(680);
        order.setShippingAddress("500 Oracle Parkway");
        return order;
    }
    public static Order orderExample3(){
        Order order = new Order();
        order.setQuantity(22);
        order.setShippingAddress("240 Queen Street");
        return order;
    }
    public static Order orderExample4(){
        Order order = new Order();
        order.setQuantity(1);
        order.setShippingAddress("50 O'Connor");
        return order;
    }

    public static SalesPerson salesPersonExample1(){
        SalesPerson salesPerson = new SalesPerson();
        salesPerson.setName("Sales Person 1");
        return salesPerson;
    }

    public static SalesPerson salesPersonExample2(){
        SalesPerson salesPerson = new SalesPerson();
        salesPerson.setName("Sales Person 2");
        return salesPerson;
    } 
}
